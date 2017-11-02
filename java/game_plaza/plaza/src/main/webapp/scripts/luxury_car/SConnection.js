/*
*websocket 客户端通讯单元
*/
var ws = null;
var is_tick_out = false;
function connect(server){
	if (!("WebSocket" in window))
    {
    	alert("WebSocket NOT supported by your Browser!");
    	return;
    }
	this.ws = new WebSocket(server);
	ws.onopen = function(event){
		doLogin();
	};
	ws.onmessage = function (event){
		//msgBox(event.data);
		onNetMessage(event.data);
	};
	ws.onclose = function(event){
		if (is_tick_out == true){
			return;
		}
        msgBox("注意！当前网络环境不佳,3秒后将为您自动刷新。");
		reloadAfter3Second(); 
		ws.close();
	};
	ws.onerror = function(event){
		if (is_tick_out == true){
			return;
		}
		msgBox("注意！当前网络环境不佳,3秒后将为您自动刷新。");
		reloadAfter3Second();
		ws.close();
	}
}
var tmr_reload = null;
function reloadAfter3Second()
{
	tmr_reload = new LTimer(3000, 1);
	tmr_reload.addEventListener(LTimerEvent.TIMER, timerToReload);
	tmr_reload.start();	
}
function timerToReload(e)
{
	window.location.reload();
}
var stream = "";
function onNetMessage(data){
	stream += data;
	var lCount = 0; var rCount = 0; var lOffset = 0; var rOffset = 0;
	for (var i = 0; i < stream.length; i++){
		var character = stream.substr(i, 1);
		if (character == "{"){
			lCount++;
		}
		else if (character == "}"){
			rCount++;
		}
		if (lCount - rCount == 0 && lCount > 0){
			rOffset = i + 1; lCount = rCount = 0;
			var msg = stream.substr(lOffset, rOffset - lOffset);
			try{
				dispatchMsg(msg);
			}
			catch(e){
				alert("NetMsg:"+msg+" Ocurr Error:"+e.message);
			}			
			lOffset = rOffset;
		}
	}
	stream = stream.substr(rOffset, stream.length - rOffset);
	
}
function dispatchMsg(strMsg){
	 var msg = eval("("+strMsg+")");
	 switch(msg.protocol){
		case S2C_PROTOCOL_LOGIN:
			processLogin(msg);
			break;
		case S2C_PROTOCOL_REQUEST_MASTER:
			processRequestMaster(msg);
			break;
		case S2C_PROTOCOL_SET_CHIPS:
			processSetChips(msg);
			break;
		case S2C_PROTOCOL_RESULT:
			processResult(msg);
			break;
		case S2C_PROTOCOL_GAME_STATE:
			processGameState(msg);
			break;
		case S2C_PROTOCOL_LAST_RESULT:
			processLastResult(msg);
			break;
		case S2C_PROTOCOL_QUERY_OPEN_HISTORY:
			processQueryOpenHistory(msg);
			break;
		// case S2C_PROTOCOL_QUERY_SET_HISTORY:
		// 	processQuerySetHistory(msg);
		// 	break;
		case S2C_PROTOCOL_CHAT:
			processChat(msg);
			break;
		case S2C_PROTOCOL_GET_OR_SAVE_MONEY:
			processGetOrSaveMoeny(msg);
			break;
		case S2C_PROTOCOL_RELIEF:
			processRelief(msg);
			break;
		case S2C_PROTOCOL_MASTER_LIST:
			processMasterList(msg);
			break;
		case S2C_PROTOCOL_SETTED_CHIPS:
			processSettedChips(msg);
			break;
		case S2C_PROTOCOL_QUERY_GAME_SETTING:
			processQueryGameSetting(msg);
			break;
		case S2C_PROTOCOL_ONLINE:
			processOnline(msg);
			break;
		case S2C_PROTOCOL_HINT:
			processHintMsg(msg);
			break;
		case S2C_PROTOCOL_NOTIFYCATION:
			processNotifycation(msg);
			break;
		case S2C_PROTOCOL_FORBID:
			processForbid(msg);
			break;
		case S2C_PROTOCOL_CONTINE_SUCC:
			processContinueSucc(msg);
			break;
		case S2C_PROTOCOL_TICKOUT:
			is_tick_out = true;
			alert("账号在其他地方登陆，请确认账号安全！如有异常请联系官方微信公众号\""+gWeiXin+"\"。");
			break;
	 }
}
function GetRequest() { 
	var url = location.search; //获取url中"?"符后的字串
	var theRequest = new Object(); 
	if (url.indexOf("?") != -1) { 
		var str = url.substr(1); 
		strs = str.split("&"); 
		for(var i = 0; i < strs.length; i ++) { 
			theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]); 
		}
	}
	return theRequest; 
} 

function getCookie(sName){
	var sSearch = sName + "=";
    if(document.cookie.length > 0){
		offset = document.cookie.indexOf(sSearch)
		if(offset != -1){
			offset += sSearch.length;
			end = document.cookie.indexOf(";", offset)
			if(end == -1) end = document.cookie.length;
			return unescape(document.cookie.substring(offset, end))
		}
		else return "";
    }

	return "";
}

function doLogin(){
	var cookie = getCookie(gCookieName);
	if (cookie && cookie.length > 0){
		C2SProtocolLogin.cookie = cookie;
		ws.send(JSON.stringify(C2SProtocolLogin));
	}else{
		onMenuItem1Click(null);
	}	
}
var curr_self_chips = 0;
var curr_self_money = 0;
var curr_relief_count = 0;
var curr_self_id = 0;
var curr_login_ok = false;
var curr_self_name = "";
function processLogin(msg){
	if (msg.code == 1){
		curr_self_id = parseInt(msg.user.id);
		curr_self_chips = parseInt(msg.user.chips);	
		curr_self_money = parseInt(msg.user.money);
		curr_relief_count = parseInt(msg.user.reliefCount);
		if (parseInt(msg.user.isNew) == 1){
			showGuide();
		}
		setSelfChips(curr_self_chips);
		curr_self_name = msg.user.name;
		setSelfName(msg.user.name);

		curr_login_ok = true;
		requestGameSetting();
		doQueryOpenHistory();
	}
	else {
		noticeLoginError(msg.errMsg);
	}
}
var curr_game_state = ROOM_STATE_WAITING_FOR_MASTER;
var curr_game_interval = 0;
var curr_time_count = 0;
var curr_timer = null;
function processGameState(msg){
	curr_game_state = msg.state;
	curr_game_interval = msg.interval;
	curr_time_count = 0;
	if (curr_timer == null){
		curr_timer = new LTimer(1000, 0);
		curr_timer.addEventListener(LTimerEvent.TIMER, onTimer);
		curr_timer.start();
	}
	if (curr_game_state == ROOM_STATE_WAITING_FOR_MASTER){
		if (layer_effect){
			layer_effect.stop();
		}
		onNoticeResultClose();
		clearChipsLayer();
		gCurrRelief = 0;
	}
	else if (curr_game_state == ROOM_STATE_WAITING_FOR_SET_CHIPS && 
		curr_self_id != curr_master_id &&
		array_last_setted_chips.length > 0){
		setContinueButtonState(LButton.STATE_ENABLE);
	} 
	else {
		setContinueButtonState(LButton.STATE_DISABLE);
	}
	setOpenTime(curr_game_interval);
	// setHighlightButtons();
	if (curr_game_state == ROOM_STATE_WAITING_FOR_START){
		noticeBox("等待开始", "#ffffff");
		if (gCurrRelief == 0 && curr_relief_count > 0 && curr_self_chips < 1000 && curr_self_money <1000){			msgBox("您的金币不足！请先充值。");//			noticBoxEx21("您的金币不足！", "您可以通过以下方式获取金币。");
		}
	}
	else if (curr_game_state == ROOM_STATE_WAITING_FOR_MASTER){		
		noticeBox("正在排庄", "#ffffff");
	}
	else if (curr_game_state == ROOM_STATE_WAITING_FOR_SET_CHIPS){
		// betonEffect(layer_last_result);
		noticeBox("请投注", "#ffffff");
		startSoundWithSetChips();
	}
	else if (curr_game_state == ROOM_STATE_WAITING_FOR_CACLULATE){
		//noticeBox("准备开奖", "#ffffff");
	}
}
function onTimer(e){
	curr_time_count++;
	if (curr_game_interval - curr_time_count > 0){
		setOpenTime(curr_game_interval - curr_time_count, true);
	}
}
function doRequestMaster(flag){	
	if (flag == true){		//hecm update
		if (curr_self_chips < 10000000){
			msgBox('金币不足一千万,不能上庄');
			return;
		}
		C2SProtocolRequestMaster.up = 1;
	}
	else {
		C2SProtocolRequestMaster.up = 0;
	}	
	ws.send(JSON.stringify(C2SProtocolRequestMaster));
}
var curr_master_id = 0;
var master_state_up = false;
function processRequestMaster(msg){
	if (msg.state == REQEST_MASTER_STATE_SUCCESS){
		msgBox("上庄成功！请等待排庄");
		master_state_up = true;
		setMaster();		
	}
	else if (msg.state == REQEST_MASTER_STATE_DOWN_SUCCESS){
		msgBox("下庄成功！");
		master_state_up = false;
		setMaster();
	}
	else if (msg.state == REQEST_MASTER_STATE_INFOMATION){
		curr_master_id = parseInt(msg.user.id);
		setMasterInfo(msg.user.name, msg.user.chips, msg.score, msg.remainCount);
	}
	else if (msg.state == REQEST_MASTER_STATE_NOT_IN_START_STATE){
		msgBox("请在【等待开始】状态上庄");
	}
	else if (msg.state == REQEST_MASTER_STATE_BE_SETTED_BY_OTHER){
		msgBox("已被别人优先抢庄");
	}
	else if (msg.state == REQEST_MASTER_STATE_NOT_ENOUGH_CHIPS){
		msgBox("金币不足无法上庄");
	}	
}

var gAllChips = new Array();
function doSetChips(index, num){
	if (num > curr_self_chips){
//		noticBoxEx2("您的金币不足！", "您可以通过以下方式获取金币。");		msgBox("您的金币不足！请先充值。");
		return;
	}
	if (-1 == gAllChips.indexOf(index)){
		gAllChips.push(index);
	}
	C2SProtocolSetChips.lotteryIndex = index;
	C2SProtocolSetChips.num = num;
	ws.send(JSON.stringify(C2SProtocolSetChips));
}
var array_curr_chips_layer = new Array();
var array_last_setted_chips = new Array();
function processSetChips(msg){
	if (parseInt(msg.code) != 1){
		msgBox(msg.errMsg);
		return;
	}
	var num = parseInt(msg.num);	
	if (msg.user.id == curr_self_id){
		if (num > curr_self_chips){
			msgBox("金币不足");
			return;
		}
		curr_self_chips -= num;
		var chips = array_last_setted_chips[msg.lotteryIndex];
		if (chips == undefined){
			array_last_setted_chips[msg.lotteryIndex] = num;
		}
		else {
			array_last_setted_chips[msg.lotteryIndex] += num;
		}
	}
	
	setSelfChips(curr_self_chips);
	var layer_chips = array_curr_chips_layer[msg.lotteryIndex];
	if (layer_chips == undefined){
		layer_chips = new SChip(msg.lotteryIndex);
		layer_game_background.addChild(layer_chips);
		array_curr_chips_layer[msg.lotteryIndex] = layer_chips;
	}
	layer_chips.addChips(msg.num, msg.user.id == curr_self_id ? true : false);
	
}
function clearChipsLayer(){
	var array_last_setted_chips_tmp = new Array();
	var ok = false;
	for (var i = IDX_ROLLS; i <= IDX_AUDI; i++){
		var layer_chips = array_curr_chips_layer[i];
		if (layer_chips != undefined){
			array_last_setted_chips_tmp[i] = layer_chips.selfChipsNum;
			if (layer_chips.selfChipsNum > 0){
				ok = true;
			}
			layer_chips.remove();
		}else{
			array_last_setted_chips_tmp[i] = 0;
		}
	}
	if (ok){
		array_last_setted_chips = array_last_setted_chips_tmp;
	}
	array_curr_chips_layer.splice(0, IDX_AUDI);	
}
function clearLastSettedChips(){
	// array_last_setted_chips.splice(0, IDX_AUDI);	
}
function continueLastSettedChips(){
	C2SProtocolContinue.arrSetChips = array_last_setted_chips;
	ws.send(JSON.stringify(C2SProtocolContinue));
	/*
	for (var index = IDX_SX_SHU; index <= IDX_1V88; index++){
		var num = array_last_setted_chips[index];
		if (num != undefined){
			doSetChips(index, num);
		}		
	}
	*/
}
function processSettedChips(msg){
	for (var i = IDX_ROLLS; i <= IDX_AUDI; i++){
		var iTotal = parseInt(msg.total[i]);
		var iSingle = parseInt(msg.single[i]);
		if (iTotal == 0){
			continue;
		}
		var layer_chips = array_curr_chips_layer[i];
		if (layer_chips == undefined){
			layer_chips = new SChip(i);
			layer_game_background.addChild(layer_chips);
			array_curr_chips_layer[i] = layer_chips;
		}
		layer_chips.addChips(iTotal - iSingle, false);		
		if (iSingle > 0){
			layer_chips.addChips(iSingle, true);
		}
				
	}
}
var curr_result = S2CProtocolResult;
function processResult(msg){	
	curr_result = msg;
	//var timer = new LTimer(1000, 1);
	//timer.addEventListener(LTimerEvent.TIMER, doOpenResult);
	//timer.start();	
	doOpenResult(1);
}

var gResultMusic = 0;
function doOpenResult(e){
	if (-1 != gAllChips.indexOf(curr_result.result_wx)){
		gResultMusic = 1;
	}else{
		gResultMusic = 0;
	}
	openLottery(curr_result.result_sx, curr_result.result_wx, curr_result.result_td);
	gAllChips = new Array();	
}
function processLastResult(msg){
	setLastResult(msg.result_td, msg.result_wx, msg.result_sx);
}

function doQueryOpenHistory(){
	ws.send(JSON.stringify(C2SProtocolQueryOpenHistory));
}
function processQueryOpenHistory(msg){
	if (layer_history){
		if (layer_history.isInit()){
			showOpenList(msg);
		}else{
			layer_history.init(msg);
		}
	}else{
		showOpenList(msg);
	}
	
	//alert(JSON.stringify(msg.list));
}
function doQuerySetHistory(){
	ws.send(JSON.stringify(C2SProtocolQuerySetHistory));
}
function processQuerySetHistory(msg){
	msgBox(JSON.stringify(msg.list));
}
function doChat(content){
	C2SProtocolChat.msg = content;
	ws.send(JSON.stringify(C2SProtocolChat));
}
function processChat(msg){
	try{
		addContent(msg.userName, msg.msg);
	}catch(e){
		window.location.reload();
	}
}
function doGetOrSaveMoeny(getNum, saveNum){
	C2SProtocolGetOrSaveMoney.getNum = getNum;
	C2SProtocolGetOrSaveMoney.saveNum = saveNum;
	ws.send(JSON.stringify(C2SProtocolGetOrSaveMoney));
}
function processGetOrSaveMoeny(msg){
	if (parseInt(msg.code) != 1){
		msgBox(msg.msg);
		return;
	}
	curr_self_chips = parseInt(msg.chips);
	curr_self_money = parseInt(msg.money);
	setSelfChips(curr_self_chips);
	if (layer_self_chips != null){
		layer_self_chips.setText("可存金币:"+splitNumber(curr_self_chips));
	}
	if (layer_self_money != null){
		layer_self_money.setText("可取金币:"+splitNumber(curr_self_money));
	}
	msgBox("操作成功！");
}
var gCurrRelief = 0;
function processRelief(msg){
	gCurrRelief = 1;
	curr_self_chips = parseInt(msg.chips);
	curr_relief_count = parseInt(msg.reliefCount);

	setSelfChips(curr_self_chips);
}
function doQueryMasterList(){
	ws.send(JSON.stringify(C2SProtocolGetMasterList));
	showMasterList(new Array());
}
function processMasterList(msg){
	reflashMasterList(msg.users);
}
function processSetChipsError(msg){
	msgBox(msg.msg);
}

function processQueryGameSetting(msg){
	if (layer_sound_mgr){
		msg.bgMusic = 0;
		if (msg.bgMusic == 1){
			layer_sound_mgr.setBgPlay(true);
			layer_sound_mgr.playBg();
		}else{
			layer_sound_mgr.setBgPlay(false);
		}
		msg.music = 1;
		if (isSafariBrowser()){
			msg.music = 0;
		}
		if (msg.music == 1){
			layer_sound_mgr.setMusicPlay(true);
		}else{
			layer_sound_mgr.setMusicPlay(false);
		}
	}
}

function processOnline(msg){
	showOnlineListWindow(msg);
}

function processHintMsg(msg){	
	var content = "";
	content += msg.time + " 在游戏[" + msg.gamename + "]中\n";
	content += "获得金币[" + msg.gold + "]";
	msgBox(content);
}

function processNotifycation(msg){
	addContent('系统', msg.msg);
}

function processForbid(msg){
	chat_room_fobid = msg.state;
}

function processContinueSucc(msg){
	clearLastSettedChips();
	setContinueButtonState(LButton.STATE_DISABLE);
}

function requestSetGameSetting(bgMusic, music){
	C2SProtocolSetGameSetting.bgMusic = bgMusic;
	C2SProtocolSetGameSetting.music = music;
	ws.send(JSON.stringify(C2SProtocolSetGameSetting));
}

function requestGameSetting(){
	ws.send(JSON.stringify(C2SProtocolQueryGameSetting));
}

function requestOnline(){
	ws.send(JSON.stringify(C2SProtocolOnline));
}


function requestRelief(){
	ws.send(JSON.stringify(C2SProtocolRelief));
}

function requestTick(){
	ws.send(JSON.stringify(C2SProtocolTick));	
}














/*
*websocket 客户端通讯单元
*/
var ws = null;
var is_tick_out = false;
function connect(server){
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
		case S2C_PROTOCOL_QUERY_YILOU_TIPS:
            processShowYiLouTips(msg);
			break;
		case S2C_PROTOCOL_QUERY_OPEN_HISTORY:
			processQueryOpenHistory(msg);
			break;
		case S2C_PROTOCOL_QUERY_SET_HISTORY:
			processQuerySetHistory(msg);
			break;
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
			alert("账号在其他地方登陆，请确认账号安全！如有异常请联系官方微信公众号\"九州微云手\"。");
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

function doLogin(){	//hecm
	var cookie = getCookie('shake_home_login_auth');	if (cookie && cookie.length > 0){
		C2SProtocolLogin.cookie = cookie;
		ws.send(JSON.stringify(C2SProtocolLogin));
	}else{
		onMenuItem1Click(null);
	}
}
var curr_self_chips = 0;
var curr_self_money = 0;
var curr_self_id = 0;
var curr_relief_count = 0;
var curr_login_ok = false;
function processLogin(msg){
	if (msg.code == 1){
		curr_self_id = parseInt(msg.user.id);
		curr_self_chips = parseInt(msg.user.chips);	
		curr_self_money = parseInt(msg.user.money);
		curr_relief_count = parseInt(msg.user.reliefCount);
		setSelfChips(curr_self_chips);

		curr_login_ok = true;
		requestGameSetting();
	}
	else {
		noticeLoginError(msg.errMsg);
	}
}
var curr_game_state = ROOM_STATE_WAITING_FOR_MASTER;
var curr_game_interval = 0;
var curr_time_count = 0;
var curr_timer = null;
var have_use_continue = true;
function processGameState(msg){
	curr_game_state = msg.state;
	curr_game_interval = msg.interval;
	curr_time_count = 0;
	if (curr_timer == null){
		curr_timer = new LTimer(1000, 0);
		curr_timer.addEventListener(LTimerEvent.TIMER, onTimer);
		curr_timer.start();
	}
	if (curr_game_state == ROOM_STATE_WAITING_FOR_MASTER || curr_game_state == ROOM_STATE_WAITING_FOR_SET_CHIPS || curr_game_state == ROOM_STATE_WAITING_FOR_START){
		noticeBoxResultExClose();
		clearChipsLayer();
		gCurrRelief = 0;
        // 设置续压
        have_use_continue = false;

        for (var i = IDX_POINT_SMALL; i <= IDX_APPEAR_6; i++){
            array_curr_total_set_chips[i] = 0;
            array_last_setted_chips[i] = array_curr_my_set_chips[i];
            array_curr_my_set_chips[i] = 0;
        }
	}
		if (curr_game_state == ROOM_STATE_WAITING_FOR_SET_CHIPS && curr_self_id != curr_master_id){
        for (var i = IDX_POINT_SMALL; i <= IDX_APPEAR_6; i++){
            if(array_last_setted_chips[i] > 0){
                setContinueButtonState(LButton.STATE_ENABLE);
                i = IDX_APPEAR_6+1;
            }
        }
	}
	else {
		setContinueButtonState(LButton.STATE_DISABLE);
	}
	setOpenTime(curr_game_interval);
	//setHighlightButtons();
	if (curr_game_state == ROOM_STATE_WAITING_FOR_START){
		noticeBox("等待开始", "#ffffff");
		if (gCurrRelief == 0 && /*curr_relief_count > 0 &&*/ curr_self_chips < 1000 && curr_self_money <1000){			msgBox("您的金币不足！请先充值。");//			noticBoxEx21("您的金币不足！", "您可以通过以下方式获取金币。");
		}
	}
	else if (curr_game_state == ROOM_STATE_WAITING_FOR_MASTER){
		noticeBox("正在排庄", "#ffffff");
	}
	else if (curr_game_state == ROOM_STATE_WAITING_FOR_SET_CHIPS){
		noticeBox("请投注", "#ffffff");
        // 设置遗漏标签
        queryYilouTips();
	}
	else if (curr_game_state == ROOM_STATE_WAITING_FOR_CACLULATE){
		//noticeBox("准备开奖", "#ffffff");
	}
}
function onTimer(e){
	curr_time_count++;
	if (curr_game_interval - curr_time_count > 0){
		setOpenTime(curr_game_interval - curr_time_count);
	}
}
function doRequestMaster(flag){
	if (flag == true){		//hecm update
		if (curr_self_chips < 50000000){
			msgBox('金币不足五千万,不能上庄');
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
var array_curr_chips_layer = new Array();       // 当前页面临时保存chips
var array_curr_my_set_chips = new Array();  // 我的押注
var array_curr_total_set_chips = new Array();       // 总押注
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

        array_curr_my_set_chips[parseInt(msg.lotteryIndex)]+= num;
	}
	
	setSelfChips(curr_self_chips);

    var beton_index = parseInt(msg.lotteryIndex);
    if(now_gam_lab_type == "1" && beton_index <= IDX_SAME_TWO_ONE_6){
        var layer_chips = array_curr_chips_layer[beton_index];
        if (layer_chips == undefined){
            layer_chips = new SChip(beton_index);
            layer_now_gam_lab.addChild(layer_chips);
            array_curr_chips_layer[beton_index] = layer_chips;
        }
        layer_chips.addChips(msg.num, msg.user.id == curr_self_id ? true : false);

    }else if(now_gam_lab_type == "2" && beton_index > IDX_SAME_TWO_ONE_6){
        var layer_chips = array_curr_chips_layer[beton_index];
        if (layer_chips == undefined){
            layer_chips = new SChip(beton_index);
            layer_now_gam_lab.addChild(layer_chips);
            array_curr_chips_layer[beton_index] = layer_chips;
        }
        layer_chips.addChips(msg.num, msg.user.id == curr_self_id ? true : false);
    }

    array_curr_total_set_chips[parseInt(msg.lotteryIndex)]+= num;
}
function clearChipsLayer(){
	for (var i = IDX_POINT_SMALL; i <= IDX_APPEAR_6; i++){
		var layer_chips = array_curr_chips_layer[i];
		if (layer_chips != undefined){
			layer_chips.remove();
		}else{
		}
	}
	array_curr_chips_layer.splice(1, IDX_APPEAR_6);
}
function clearChipsLayer_2(type){
    var index_min = IDX_POINT_SMALL;
    var index_max = IDX_SAME_TWO_ONE_6;
    if(type == "1"){
        index_min = IDX_COUNT_4;
        index_max = IDX_APPEAR_6;
    }
    for (var i = index_min; i <= index_max; i++){
        var layer_chips = array_curr_chips_layer[i];
        if (layer_chips != undefined){
            layer_chips.remove();
        }
    }
    array_curr_chips_layer.splice(index_min, index_max-index_min + 1);
}

function clearLastSettedChips(){
	// array_last_setted_chips.splice(1, IDX_APPEAR_6);
}
function continueLastSettedChips(){
	C2SProtocolContinue.arrSetChips = array_last_setted_chips;
	ws.send(JSON.stringify(C2SProtocolContinue));
}
function queryYilouTips(){
    ws.send(JSON.stringify(C2SProtocolQueryYilouTips));
}
function processSettedChips(msg){
    for (var i = IDX_POINT_SMALL; i <= IDX_APPEAR_6; i++){
        var iTotal = parseInt(msg.total[i]);
        var iSingle = parseInt(msg.single[i]);
        if (iTotal == 0){
            continue;
        }
        //if(now_gam_lab_type == "1" && i <= IDX_SAME_TWO_ONE_6){
        //    var layer_chips = array_curr_chips_layer[i];
        //    if (layer_chips == undefined){
        //        layer_chips = new SChip(i);
        //        layer_now_gam_lab.addChild(layer_chips);
        //        array_curr_chips_layer[i] = layer_chips;
        //    }
        //    layer_chips.addChips(iTotal - iSingle, false);
        //    if (iSingle > 0){
        //        layer_chips.addChips(iSingle, true);
        //    }
        //}else if(now_gam_lab_type == "2" && i > IDX_SAME_TWO_ONE_6){
        //    var layer_chips = array_curr_chips_layer[i];
        //    if (layer_chips == undefined){
        //        layer_chips = new SChip(i);
        //        layer_now_gam_lab.addChild(layer_chips);
        //        array_curr_chips_layer[i] = layer_chips;
        //    }
        //    layer_chips.addChips(iTotal - iSingle, false);
        //    if (iSingle > 0){
        //        layer_chips.addChips(iSingle, true);
        //    }
        //}
        array_curr_total_set_chips[i] += iTotal;
        array_curr_my_set_chips[i] += iSingle;
    }
    setGambleLab();
}

var curr_result = S2CProtocolResult;
function processResult(msg){
	curr_result = msg;
	var timer = new LTimer(2000, 1);
	timer.addEventListener(LTimerEvent.TIMER, doOpenResult);
	timer.start();	
}

var gResultMusic = 0;
function doOpenResult(e){
	if (-1 != gAllChips.indexOf(curr_result.result_1_num) ||
		-1 != gAllChips.indexOf(curr_result.result_2_num) ||
		-1 != gAllChips.indexOf(curr_result.result_3_num)){
		gResultMusic = 1;
	}else{
		gResultMusic = 0;
	}
	openLottery(curr_result.result_1_num, curr_result.result_2_num, curr_result.result_3_num);
	gAllChips = new Array();	
}
function processLastResult(msg){
	setLastResult(msg.result_1_num, msg.result_2_num, msg.result_3_num, msg.open_index);
}
//function processTema(msg){
//	set1v18Result(msg.tm_lm_td, msg.tm_lm_sx);
//	set1v88Result(msg.tm_sm_td, msg.tm_sm_wx, msg.tm_sm_sx);
//}
function doQueryOpenHistory(){
	ws.send(JSON.stringify(C2SProtocolQueryOpenHistory));
}
function processQueryOpenHistory(msg){
	showOpenList(msg);
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
	setChatMsg(msg.userName, msg.msg);
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

function requestSetGameSetting(bgMusic, music){
	C2SProtocolSetGameSetting.bgMusic = bgMusic;
	C2SProtocolSetGameSetting.music = music;
	ws.send(JSON.stringify(C2SProtocolSetGameSetting));
}

function requestGameSetting(){
	ws.send(JSON.stringify(C2SProtocolQueryGameSetting));
}

function requestRelief(){
	ws.send(JSON.stringify(C2SProtocolRelief));
}


function processHintMsg(msg){	
	var content = "";
	content += msg.time + " 在游戏[" + msg.gamename + "]中\n";
	content += "获得金币[" + msg.gold + "]";
	msgBox(content);
}

function processNotifycation(msg){
	setChatMsg('系统', msg.msg);
}


function processForbid(msg){
	chat_room_fobid = msg.state;
}

function processContinueSucc(msg){
    have_use_continue = true;
	clearLastSettedChips();
	setContinueButtonState(LButton.STATE_DISABLE);
}


function requestTick(){
	ws.send(JSON.stringify(C2SProtocolTick));	
}







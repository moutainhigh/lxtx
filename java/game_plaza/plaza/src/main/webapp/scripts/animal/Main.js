

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
///  游戏初始化设定
var basePath = global_util.getAppPath();
window._requestAF = (function() {
  return window.requestAnimationFrame ||
         window.webkitRequestAnimationFrame ||
         window.mozRequestAnimationFrame ||
         window.oRequestAnimationFrame ||
         window.msRequestAnimationFrame ||
         function(/* function FrameRequestCallback */ callback, /* DOMElement Element */ element) {
           window.setTimeout(callback, 1000/150);
         };
})();
var SCREEN_WIDTH = 640;//
var SCREEN_HEIGHT = 1003;//window.innerHeight * SCREEN_WIDTH / window.innerWidth;
LInit(60,"shiershengxiao",SCREEN_WIDTH,SCREEN_HEIGHT,main);
/**
 * 层变量
 * */
//显示进度条所用层
var loadingLayer;
//游戏底层
var baseLayer;
var layer_helper;
//页面背景层
var layer_background = null; 
var layer_msg_box_bg = null;
function main(){	
	//预读取音频
	var loadsound = true;
	var protocol = location.protocol;
	if (protocol == "http:" || protocol == "https:"){
		if (LGlobal.ios && !LSound.webAudioEnabled){
			//如果IOS环境，并且不支持WebAudio，则无法预先读取
			loadsound = false;
		}
	} else if (LGlobal.mobile) {
		//如果是移动浏览器本地访问，则无法预先读取
		loadsound = false;
	}
	if(loadsound){		
		if(LGlobal.os == OS_PC){
			//浏览器支持WebAudio，或者环境为PC，则预先读取所有音频
			imgData.push({name : "sound_bg",path : basePath+"/animal/medias/bg.mp3",type:"sound"});
			imgData.push({name : "sound_click",path : basePath+"/animal/medias/click.mp3",type:"sound"});
			imgData.push({name : "sound_in",path : basePath+"/animal/medias/in.mp3",type:"sound"});
			imgData.push({name : "sound_out",path : basePath+"/animal/medias/out.mp3",type:"sound"});
			imgData.push({name : "sound_roulette",path : basePath+"/animal/medias/roulette.mp3",type:"sound"});
			imgData.push({name : "sound_select",path : basePath+"/animal/medias/select.mp3",type:"sound"});
		}
	}
	LGlobal.stage.addEventListener(LEvent.WINDOW_RESIZE, function(){
		fullScreen();
	});	

	// LGlobal.stage.addEventListener(LEvent.WINDOW_ORIENTATIONCHANGE,function(){alert(e.orientation)});
	
	fullScreen();
	var loader = new LLoaderEx(loadBitmapdata);
}

function fullScreen(){
	LGlobal.stageScale = LStageScaleMode.SHOW_ALL ;
	LSystem.screen(LStage.FULL_SCREEN);
}

// 加载游戏用图片
function loadBitmapdata(bitmaps){
	loadingLayer = new LoadingSampleEx(bitmaps);
	addChild(loadingLayer);
	LLoadManage.load(
			imgData,
			function(progress){
				loadingLayer.setProgress(progress);
			},
			gameInit
		);
		
}

function gameInit(result){
	//状态:STATE_WAIT_FOR_START
	imglist = result;
	removeChild(loadingLayer);
	loadingLayer = null;
	
	baseLayer = new LSprite();
	addChild(baseLayer);

	layer_helper = new LSprite();
	addChild(layer_helper);	
	
	//背景
	layer_background = new LSprite();//spriteWithImage(IMG_BACKGROUND);
	baseLayer.addChild(layer_background);
	layer_background.graphics.drawRect(1, "#0e2f50", [0, 0, SCREEN_WIDTH, SCREEN_HEIGHT], true,  "#0e2f50");
	
	initTop();
	initCenter();
	initBottom();
	setSelfChips('');

	layer_msg_box_bg = new LSprite();
	baseLayer.addChild(layer_msg_box_bg);
	layer_msg_box_bg.graphics.drawRect(0, "#0e2f50", [0, 0, SCREEN_WIDTH,SCREEN_HEIGHT], false,  "#0e2f50");

	connectServer();
	initSound(playSoundBG);

	var myTimer = new LTimer(10000, 1000000000);
	myTimer.addEventListener(LTimerEvent.TIMER, requestTick);
    myTimer.start();
}

function connectServer(){
	connect(gServerAddress);
}

function playSoundBG(){
	if (!curr_login_ok){
		requestGameSetting();
	}
}

/******************************************************************************************************
* 顶部区域
*******************************************************************************************************/
var layer_top_background = null;
function initTop(){
	layer_top_background = spriteWithImage(IMG_TOP_BACKGROUND);
	layer_background.addChild(layer_top_background);
	setMasterInfo("", 0, 0, 0);
	setOpenTime(0);
	setLastResult(IDX_TIAN, IDX_WX_JIN, IDX_SX_SHU);
	//菜单按钮
	var layer_menu = new LButton(bitmapWithImage(IMG_MENU));
	layer_top_background.addChild(layer_menu);
	layer_menu.x = SCREEN_WIDTH - imgSize[IMG_MENU].w - 10;
	layer_menu.y = 10;	
	layer_menu.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuClicked);	
	
}
var layer_menu_items = null;
var layer_clear_event = null;
function onMenuClicked(event){	
	layer_clear_event = new LSprite();
	baseLayer.addChild(layer_clear_event);
	layer_clear_event.graphics.drawRect(1, "#ffffff", [0, 0, SCREEN_WIDTH, SCREEN_HEIGHT], true,  "#ffffff");
	layer_clear_event.alpha = 0;
	layer_clear_event.addEventListener(LMouseEvent.MOUSE_DOWN, onClearEvent);
	
	if (layer_menu_items != null){
		layer_menu_items.remove();
	}
	layer_menu_items = new LSprite();
	baseLayer.addChild(layer_menu_items);
	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 70;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuHelperClicked);	
	var text = "规则说明";
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;
	
	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 120;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuGuideClicked);	
	var text = "新手引导";
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;

	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 170;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuMusicBgClicked);
	var text = "音乐：" + ((layer_sound_mgr && layer_sound_mgr.getBgPlay()) ? '开' : '关');
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;

	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 220;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuMusicClicked);	
	var text = "音效：" + ((layer_sound_mgr && layer_sound_mgr.getMusicPlay()) ? '开' : '关');
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;

	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 270;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuItem1Click);	
	var itemText1 = labelWithText("返回大厅", 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;
}
function onClearEvent(event){
	layer_clear_event.remove();
	if (layer_menu_items != null){
		layer_menu_items.remove();
		layer_menu_items = null;
	}
}
function onMenuItem1Click(event){	//返回大厅
	var wxid = document.getElementById("wxid").value;	var chnno =document.getElementById("chnno").value;	self.location = global_util.getAppPath()+"/game_center/"+wxid+"/"+chnno;
}

function onMenuMusicBgClicked(event){
	var target = event.currentTarget;
	if (target){
		var lb = target.getChildAt(0);
		if (lb && layer_sound_mgr){
			layer_sound_mgr.setBgPlay(!layer_sound_mgr.getBgPlay());
			lb.setText("音乐：" + ((layer_sound_mgr && layer_sound_mgr.getBgPlay()) ? '开' : '关'));
			if (layer_sound_mgr.getBgPlay()){
				layer_sound_mgr.playBg();
			}else{
				layer_sound_mgr.stop("bg");
			}

			requestSetGameSetting(layer_sound_mgr.getBgPlay() ? 1 :0,layer_sound_mgr.getMusicPlay() ? 1 : 0);
		}
	}
}

function onMenuMusicClicked(event){
	var target = event.currentTarget;
	if (target){
		var lb = target.getChildAt(0);
		if (lb && layer_sound_mgr){
			layer_sound_mgr.setMusicPlay(!layer_sound_mgr.getMusicPlay());
			lb.setText("音效：" + ((layer_sound_mgr && layer_sound_mgr.getMusicPlay()) ? '开' : '关'));

			if (isSafariBrowser()){
				initSafari();
			}
			requestSetGameSetting(layer_sound_mgr.getBgPlay() ? 1 :0,layer_sound_mgr.getMusicPlay() ? 1 : 0);
		}
	}
}

function onMenuHelperClicked(event){
	showHelper();
}

function onMenuGuideClicked(event){
	onClearEvent(event);
	showGuide();
}

var layer_master_background = null;
function setMasterInfo(name, chips, score, count){
	if (layer_master_background != null){
		layer_master_background.remove();
	}
	layer_master_background = spriteWithImage(IMG_USER_INFO_BACKGROUND);
	layer_top_background.addChild(layer_master_background);
	layer_master_background.x = 10; layer_master_background.y = 5;
	
	var layer_name_tip = labelWithText("庄家:", 22, "#292355");
	layer_master_background.addChild(layer_name_tip);
	layer_name_tip.x = 10; layer_name_tip.y = 10;
	
	var layer_chips_tip = labelWithText("财富:", 22, "#292355");
	layer_master_background.addChild(layer_chips_tip);
	layer_chips_tip.x = 10; layer_chips_tip.y = 45;
	
	var layer_score_tip = labelWithText("成绩:", 22, "#292355");
	layer_master_background.addChild(layer_score_tip);
	layer_score_tip.x = 10; layer_score_tip.y = 80;
	
	var layer_count_tip = labelWithText("连庄:", 22, "#292355");
	layer_master_background.addChild(layer_count_tip);
	layer_count_tip.x = 10; layer_count_tip.y = 115;
	
	var layer_name = labelWithText(name, 22, "#ffffff");
	layer_master_background.addChild(layer_name);
	layer_name.x = 70; layer_name.y = 10;
	
	var outChips = splitNumber(chips);
	var layer_chips = labelWithText(outChips, 22, "#ffcf3d");
	layer_master_background.addChild(layer_chips);
	layer_chips.x = 70; layer_chips.y = 45;
	
	var outScore = splitNumber(score);
	var layer_score;
	if (score >= 0 ){
		layer_score = labelWithText(outScore, 22, "#ff0000");
	}
	else {
		layer_score = labelWithText(outScore, 22, "#00ff00");
	}
	layer_master_background.addChild(layer_score);
	layer_score.x = 70; layer_score.y = 80;
	
	var layer_count = labelWithText(count, 30, "#a6ebf1");
	layer_master_background.addChild(layer_count);
	layer_count.x = 70; layer_count.y = 110;

    add_head_img();
}

var headIconloader = null;
function init_user_head(){
    headIconloader = new LLoader();
    headIconloader.addEventListener(LEvent.COMPLETE, loadheadIcon);    //庄家头像
    headIconloader.load("http://www.ld12.com/upimg358/allimg/c150627/14353W345a130-Q2B.jpg", "bitmapData");
}

var layer_head_portrait = null;
var head_img_data = null;
function loadheadIcon(event){
    head_img_data = event.target;
    add_head_img();
}

function add_head_img(){
    var bitmapdata_head = new LBitmapData(head_img_data, 0,0,130,130);
    var bitmap_head = new LBitmap(bitmapdata_head);
    if(layer_head_portrait != null){
        layer_head_portrait.remove();
    }
    layer_head_portrait = new LButton(bitmap_head);
    layer_master_background.addChild(layer_head_portrait);
    layer_head_portrait.x = 250; layer_head_portrait.y = 10;
    layer_head_portrait.addEventListener(LMouseEvent.MOUSE_DOWN, onMasterListClick);
    var layer_master_icon = spriteWithImage(IMG_MASTER);
    layer_master_background.addChild(layer_master_icon);
    layer_master_icon.x = 330; layer_master_icon.y = 0;
}

//设置开奖时间
var layer_open_time_background = null;
function setOpenTime(iValue){
	if (layer_open_time_background != null){
		layer_open_time_background.remove();
	}
	layer_open_time_background = spriteWithImage(IMG_OPEN_TIME_BACKGROUND);
	layer_top_background.addChild(layer_open_time_background);
	layer_open_time_background.x = 420; layer_open_time_background.y = 5;	
	var tip;
	switch(curr_game_state){
	case ROOM_STATE_WAITING_FOR_MASTER:
		tip = "正在排庄";
		break;
	case ROOM_STATE_WAITING_FOR_START:
		tip = "等待开始";
		break;	
	case ROOM_STATE_WAITING_FOR_SET_CHIPS:
		tip = "投注时间还剩";
		break;
	case ROOM_STATE_WAITING_FOR_CACLULATE:
		tip = "正在开奖";
		break;
	case ROOM_STATE_WAITING_FOR_SERVER:
		tip = "正在结算";
		break;
	default:
		break;
	}
	var content = String(iValue);
	if (curr_game_state == ROOM_STATE_WAITING_FOR_MASTER ||
		curr_game_state == ROOM_STATE_WAITING_FOR_CACLULATE ||
		curr_game_state == ROOM_STATE_WAITING_FOR_SERVER){
		content = "...";	
	}
	var layer_open_time_tip = labelWithText(tip, 22, "#292355");
	layer_open_time_background.addChild(layer_open_time_tip);
	layer_open_time_tip.x = 5;
	layer_open_time_tip.y = 10;	
	var layer_open_time = labelWithText(content, 30, "#a6ebf1");
	layer_open_time_background.addChild(layer_open_time);
	layer_open_time.x = 60;
	layer_open_time.y = 40;		
}
//设置上期开奖结果
var layer_last_result_background = null;
function setLastResult(result_td, result_wx, result_sx){
	if (layer_last_result_background != null){
		layer_last_result_background.remove();
	}
	layer_last_result_background = spriteWithImage(IMG_HISTORY_BACKGROUND);
	layer_top_background.addChild(layer_last_result_background);
	layer_last_result_background.x = 420; layer_last_result_background.y = 82;
	layer_last_result_background.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectHistoryResult);
	
	var layer_last_result_tip = labelWithText("上期:", 22, "#292355");
	layer_last_result_background.addChild(layer_last_result_tip);
	layer_last_result_tip.x = 5; layer_last_result_tip.y = 5;
	
	var content = getNameFromLotteryResult(result_td) + "  " + getNameFromLotteryResult(result_wx) + "  " + getNameFromLotteryResult(result_sx);
	var layer_last_result = labelWithText(content, 22, "#292355");
	layer_last_result_background.addChild(layer_last_result);
	layer_last_result.x = 5; layer_last_result.y = 40;
	
	var layer_history_tip = labelWithText("往期", 22, "#b2b5e0");
	layer_last_result_background.addChild(layer_history_tip);
	layer_history_tip.x = 120; layer_history_tip.y = 5;
	
	
	var layer_history_icon = spriteWithImage(IMG_HISTORY_DRAG_ICON);
	layer_last_result_background.addChild(layer_history_icon);
	layer_history_icon.x = 165; layer_history_icon.y = 5;
	layer_history_icon.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectHistoryResult);
	
}
//查看往期开奖结果
function onSelectHistoryResult(event){
	doQueryOpenHistory();
}
function onMasterListClick(event){
	doQueryMasterList();	
}
/******************************************************************************************************
* 中心区域
*******************************************************************************************************/
var layer_game_background = null;

function initCenter(){
	layer_game_background = spriteWithImage(IMG_GAME_BACKGROUND);
	layer_background.addChild(layer_game_background);
	layer_game_background.y = 165;
	layer_game_background.addEventListener(LMouseEvent.MOUSE_DOWN, onSetChip);
	layer_game_background.addEventListener(LEvent.ENTER_FRAME, onGameFrame);
	is_ready_to_rotate = false;
	//1v18
	var layer_set_chip_1v18 = spriteWithImage(IMG_SET_CHIP_1V18);
	layer_game_background.addChild(layer_set_chip_1v18);
	layer_set_chip_1v18.x = 10;
	layer_set_chip_1v18.y = 5;
	//1v88
	var layer_set_chip_1v88 = spriteWithImage(IMG_SET_CHIP_1V88);
	layer_game_background.addChild(layer_set_chip_1v88);
	layer_set_chip_1v88.x = SCREEN_WIDTH - imgSize[IMG_SET_CHIP_1V88].w - 10;
	layer_set_chip_1v88.y = 5;
	//转盘背景
	var layer_rotate_background = spriteWithImage(IMG_ROTATE_BACKGROUND);
	layer_game_background.addChild(layer_rotate_background);
	layer_rotate_background.x = convertToScreenCenterX(imgSize[IMG_ROTATE_BACKGROUND].w);
	layer_rotate_background.y = 5;
	//生肖转盘
	var layer_rotate_shengxiao = spriteWithImage(IMG_ROTATE_SHENGXIAO);
	layer_rotate_background.addChild(layer_rotate_shengxiao);
	layer_rotate_shengxiao.x = converToParentX(imgSize[IMG_ROTATE_SHENGXIAO].w,imgSize[IMG_ROTATE_BACKGROUND].w);
	layer_rotate_shengxiao.y = converToParentY(imgSize[IMG_ROTATE_SHENGXIAO].h,imgSize[IMG_ROTATE_BACKGROUND].h);
	//五行转盘
	var layer_rotate_wuxing = spriteWithImage(IMG_ROTATE_WUXING);
	layer_rotate_background.addChild(layer_rotate_wuxing);
	layer_rotate_wuxing.x = converToParentX(imgSize[IMG_ROTATE_WUXING].w,imgSize[IMG_ROTATE_BACKGROUND].w);
	layer_rotate_wuxing.y = converToParentY(imgSize[IMG_ROTATE_WUXING].h,imgSize[IMG_ROTATE_BACKGROUND].h);
	//天地转盘
	var layer_rotate_tiandi = spriteWithImage(IMG_ROTATE_TIANDI);
	layer_rotate_background.addChild(layer_rotate_tiandi);
	layer_rotate_tiandi.x = converToParentX(imgSize[IMG_ROTATE_TIANDI].w,imgSize[IMG_ROTATE_BACKGROUND].w);
	layer_rotate_tiandi.y = converToParentY(imgSize[IMG_ROTATE_TIANDI].h,imgSize[IMG_ROTATE_BACKGROUND].h);
	//赔率1:18文字
	var label_1v18 = labelWithText("1赔24", 18, "#699ae7");
	layer_game_background.addChild(label_1v18);
	label_1v18.x = 65;
	label_1v18.y = 92;
	label_1v18.rotate = -45;
	//赔率1:88文字
	var label_1v88 = labelWithText("1赔120", 18, "#699ae7");
	layer_game_background.addChild(label_1v88);
	label_1v88.x = 535;
	label_1v88.y = 50;
	label_1v88.rotate = 45;
	//赔率1:0.8底图+文字
	var layer_1v0d8_background = spriteWithImage(IMG_SET_CHIP_1V0D8);
	layer_game_background.addChild(layer_1v0d8_background);
	layer_1v0d8_background.x = convertToScreenCenterX(imgSize[IMG_SET_CHIP_1V0D8].w);
	layer_1v0d8_background.y = 300;
	var label_1v0d8 = labelWithText("1赔1.96", 18, "#699ae7");
	layer_1v0d8_background.addChild(label_1v0d8);
	label_1v0d8.x = 30;
	label_1v0d8.y = 12;
	//赔率1:3.5底图+文字
	var layer_1v3d5_background = spriteWithImage(IMG_SET_CHIP_1V3D5);
	layer_game_background.addChild(layer_1v3d5_background);
	layer_1v3d5_background.x = 265;
	layer_1v3d5_background.y = 148;
	var label_1v3d5 = labelWithText("1赔5", 18, "#699ae7");
	layer_1v3d5_background.addChild(label_1v3d5);
	label_1v3d5.x = 35;
	label_1v3d5.y = 5;
	//赔率1:9底图+文字
	var layer_1v9_background = spriteWithImage(IMG_SET_CHIP_1V9);
	layer_game_background.addChild(layer_1v9_background);
	layer_1v9_background.x = 274;
	layer_1v9_background.y = 15;
	var label_1v9 = labelWithText("1赔12", 18, "#699ae7");
	layer_1v9_background.addChild(label_1v9);
	label_1v9.x = 20;
	label_1v9.y = 3;	
	//我要上庄
	setMaster();
	//我要取款
	var layer_get_chips = spriteWithImage(IMG_GET_CHIPS);
	layer_game_background.addChild(layer_get_chips);
	layer_get_chips.x = SCREEN_WIDTH - imgSize[IMG_GET_CHIPS].w - 10; layer_get_chips.y = 458;
	
	setChatMsg("", "");
	//设置当天1:18赔率的开奖结果
	set1v18Result(IDX_TIAN, IDX_SX_SHU);
	//设置当天1:88赔率的开奖结果
	set1v88Result(IDX_TIAN, IDX_WX_JIN, IDX_SX_SHU);
}
var layer_set_master = null;
function setMaster(){
	if (layer_set_master != null){
		layer_set_master.remove();
	}
	if (master_state_up == false){
		layer_set_master = spriteWithImage(IMG_SET_MASTER);	
	}
	else {
		layer_set_master = spriteWithImage(IMG_SET_MASTER_DOWN);
	}
	layer_game_background.addChild(layer_set_master);
	layer_set_master.x = 10; layer_set_master.y = 458;
}
//设置当天1:18赔率的开奖结果
var layer_1v18_result = null;
function set1v18Result(result_td, result_sx){
	var content = getNameFromLotteryResult(result_td) + "|" + getNameFromLotteryResult(result_sx);
	if (layer_1v18_result == null){
		layer_1v18_result = labelWithText(content, 22, "#325782");
		layer_game_background.addChild(layer_1v18_result);
		layer_1v18_result.x = 45;
		layer_1v18_result.y = 65;
		layer_1v18_result.rotate = -45;
	}
	else{
		layer_1v18_result.setText(content);
	}
}
//设置当天1:88赔率的开奖结果
var layer_1v88_result = null;
function set1v88Result(result_td, result_wx, result_sx){
	var content = getNameFromLotteryResult(result_td) + "|" + getNameFromLotteryResult(result_wx) + "|" + getNameFromLotteryResult(result_sx);
	if (layer_1v88_result == null){
		layer_1v88_result = labelWithText(content, 22, "#744d3e");
		layer_game_background.addChild(layer_1v88_result);
		layer_1v88_result.x = 550;
		layer_1v88_result.y = 20;
		layer_1v88_result.rotate = 45;
	}
	else{
		layer_1v88_result.setText(content);
	}
}
//下注金币
function onSetChip(event){
	if (is_chatting == true){
		return false;
	}
	posX = event.offsetX - 320;
	posY = 485 - event.offsetY;
	var angle = posAngle(new SPoint(0, 0), new SPoint(posX, posY));
	var distance = posDistance(new SPoint(0, 0), new SPoint(posX, posY));
	var tag = -1;
	if (posY < -310){
		return;
	}
	if (distance < 92){
		tag = TAG_RANGE_TD;
	}
	else if (distance < 188){
		tag = TAG_RANGE_WX;
	}
	else if (distance < 304){
		tag = TAG_RANGE_SX;
	}
	else if (distance < 485){
		tag = TAG_RANGE_TS;
	}
	if (tag == -1){
		return false;
	}

	var index = getIndexByAngle(angle, tag);	
	if (index == -1){
		return false;
	}

	if (index == IDX_SET_MASTER){
		playSoundWithSelect();

		if (master_state_up == false){
			doRequestMaster(true);
		}
		else {
			doRequestMaster(false);
		}
		
	}
	else if (index == IDX_GET_CHIP){
		playSoundWithSelect();

		if (curr_game_state != ROOM_STATE_WAITING_FOR_CACLULATE && 
			curr_game_state != ROOM_STATE_WAITING_FOR_SERVER){
			onGetChipsClicked();	
		}
		else {
			msgBox("提示：【开奖】和【结算】状态不能取款操作！");
		}
	}
	else {
		playSoundWithClick();

		if (curr_master_id == curr_self_id){
			msgBox("提示：当前自己是庄家，不能下注！");
		}
		else if (curr_game_state != ROOM_STATE_WAITING_FOR_SET_CHIPS){
			msgBox("提示：请在【投注】状态下注")			
		}
		else {
			if (continue_button_state == LButton.STATE_ENABLE){
				clearLastSettedChips();
			}
			setContinueButtonState(LButton.STATE_DISABLE);
			doSetChips(index, curr_seleted_chips);
		}
	}	
}
function onGetChipsClicked(){	//进入充值界面	var wxid = document.getElementById("wxid").value;	self.location = global_util.getAppPath()+"/shop/"+wxid;
//	showBank();
}
//闪烁按钮
var layer_highlight_set_master = null;
var layer_highlight_get_chips = null;
var MIN_SET_MASTER_CHIPS = 1000000;
var MIN_GET_CHIPS = 10000;
function setHighlightButtons(){
	if (curr_game_state == ROOM_STATE_WAITING_FOR_MASTER && 
		curr_self_chips >= MIN_SET_MASTER_CHIPS && 
		layer_highlight_set_master == null){
		layer_highlight_set_master = spriteWithImage(IMG_SET_MASTER_HIGHLIGHT);
		layer_game_background.addChild(layer_highlight_set_master);
		layer_highlight_set_master.x = 0; layer_highlight_set_master.y = 450;
		layer_highlight_set_master.addEventListener(LEvent.ENTER_FRAME, onSetMasterHighlight);
	}
	else if (layer_highlight_set_master != null){
		layer_highlight_set_master.remove();
		layer_highlight_set_master = null;
	}
	if (curr_game_state != ROOM_STATE_WAITING_FOR_CACLULATE && 
		curr_game_state != ROOM_STATE_WAITING_FOR_SERVER && 
		curr_self_chips < MIN_GET_CHIPS && 
		layer_highlight_get_chips == null){
		layer_highlight_get_chips = spriteWithImage(IMG_GET_CHIPS_HIGHLIGHT);
		layer_game_background.addChild(layer_highlight_get_chips);
		layer_highlight_get_chips.x = SCREEN_WIDTH - imgSize[IMG_GET_CHIPS_HIGHLIGHT].w; layer_highlight_get_chips.y = 450;
		layer_highlight_get_chips.addEventListener(LEvent.ENTER_FRAME, onGetChipsHighlight);
	}
	else if (layer_highlight_get_chips != null){
		layer_highlight_get_chips.remove();
		layer_highlight_get_chips = null;
	}
}
function onSetMasterHighlight(){
	layer_highlight_set_master.alpha -= 0.05;
	if (layer_highlight_set_master.alpha <= 0){
		layer_highlight_set_master.alpha = 1;
	}
}
function onGetChipsHighlight(){
	layer_highlight_get_chips.alpha -= 0.05;
	if (layer_highlight_get_chips.alpha <= 0){
		layer_highlight_get_chips.alpha = 1;
	}
}
var layer_open_lottery = null;
var layer_mask = null;
var bitmap_rotate_shengxiao = null;
var bitmap_rotate_wuxing = null;
var bitmap_rotate_tiandi = null;
function openLottery(sxResult, wxResult, tdResult){
	if (layer_open_lottery != null){
		location.reload();
		return;
	}
	setRotateAngle(getAngleFromLotteryResult(sxResult), getAngleFromLotteryResult(wxResult), getAngleFromLotteryResult(tdResult));
	layer_open_lottery = new LSprite();
	layer_background.addChild(layer_open_lottery);
	layer_mask = new LSprite();
	layer_open_lottery.addChild(layer_mask);
	layer_mask.graphics.drawRect(1, "#000000", [0, 0, SCREEN_WIDTH, SCREEN_HEIGHT], true, "#000000");
	layer_mask.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	
	layer_mask.alpha = 0;
	rotate_frame_index = 0;
	
	var layer_arrow = spriteWithImage(IMG_ROTATE_ARROW);
	layer_open_lottery.addChild(layer_arrow);
	layer_arrow.x = convertToScreenCenterX(imgSize[IMG_ROTATE_ARROW].w);
	layer_arrow.y = 90;
		
	//转盘背景
	var layer_rotate_background = spriteWithImage(IMG_ROTATE_BACKGROUND);
	layer_open_lottery.addChild(layer_rotate_background);
	layer_rotate_background.x = convertToScreenCenterX(imgSize[IMG_ROTATE_BACKGROUND].w); 
	layer_rotate_background.y = 170; 
	//生肖转盘
	bitmap_rotate_shengxiao = bitmapWithImage(IMG_ROTATE_SHENGXIAO_EX);
	layer_rotate_background.addChild(bitmap_rotate_shengxiao);
	bitmap_rotate_shengxiao.x = converToParentX(imgSize[IMG_ROTATE_SHENGXIAO_EX].w,imgSize[IMG_ROTATE_BACKGROUND].w);
	bitmap_rotate_shengxiao.y = converToParentY(imgSize[IMG_ROTATE_SHENGXIAO_EX].h,imgSize[IMG_ROTATE_BACKGROUND].h);
	//五行转盘
	bitmap_rotate_wuxing = bitmapWithImage(IMG_ROTATE_WUXING_EX);
	layer_rotate_background.addChild(bitmap_rotate_wuxing);
	bitmap_rotate_wuxing.x = converToParentX(imgSize[IMG_ROTATE_WUXING_EX].w,imgSize[IMG_ROTATE_BACKGROUND].w);
	bitmap_rotate_wuxing.y = converToParentY(imgSize[IMG_ROTATE_WUXING_EX].h,imgSize[IMG_ROTATE_BACKGROUND].h);
	//天地转盘
	bitmap_rotate_tiandi = bitmapWithImage(IMG_ROTATE_TIANDI_EX);
	layer_rotate_background.addChild(bitmap_rotate_tiandi);
	bitmap_rotate_tiandi.x = converToParentX(imgSize[IMG_ROTATE_TIANDI_EX].w,imgSize[IMG_ROTATE_BACKGROUND].w);
	bitmap_rotate_tiandi.y = converToParentY(imgSize[IMG_ROTATE_TIANDI_EX].h,imgSize[IMG_ROTATE_BACKGROUND].h);
}
var rotate_angle_shengxiao;
var rotate_angle_wuxing;
var	rotate_angle_tiandi;
function setRotateAngle(sxAngle, wxAngle, tdAngle){
	rotate_angle_shengxiao = sxAngle + rotate_angle_base;
	rotate_angle_wuxing = wxAngle - 720 - rotate_angle_base;
	rotate_angle_tiandi = tdAngle + rotate_angle_base;
}
var blink_count = 0;
var rotate_angle_base = 360 * 5;
var rotate_speed_shengxiao = 42;
var rotate_speed_wuxing = 51;
var rotate_speed_tiandi = 60;
var rotate_speed_begin = 10;
var is_ready_to_rotate = false;
var is_ready_to_blink = false;
var is_play_roulette = false;
var is_play_result = false;
function onGameFrame(){
	/**
	* 开奖时背景渐灰
	*/
	if (layer_mask !== null){
		if (layer_mask.alpha < 0.8){
			layer_mask.alpha += 0.1;
		}
		else{
			is_ready_to_rotate = true;		
		}
	}
	/**
	* 转动转盘
	*/
	if (is_ready_to_rotate == true){
		if (!is_play_roulette){
			playSoundWithRoulette();
			is_play_roulette = true;
		}
		
		var speed1 = getRotateIncreaseSpeed(bitmap_rotate_shengxiao.rotate, 
														rotate_angle_shengxiao,
														rotate_speed_begin,
														rotate_speed_shengxiao);
		var speed2 = getRotateDecreaseSpeed(bitmap_rotate_wuxing.rotate, 
														rotate_angle_wuxing,
														rotate_speed_begin,
														rotate_speed_wuxing);
		var speed3 = getRotateIncreaseSpeed(bitmap_rotate_tiandi.rotate, 
														rotate_angle_tiandi,
														rotate_speed_begin,
														rotate_speed_tiandi);
		bitmap_rotate_shengxiao.rotate += speed1;
		bitmap_rotate_wuxing.rotate -= speed2;
		bitmap_rotate_tiandi.rotate += speed3;
		if (speed1 == 0 && speed2 == 0 && speed3 == 0){ //停止了转动
			stopSoundWithRoulette();
			is_ready_to_rotate = false;
			is_play_roulette = false;
			layer_mask = null;
			//显示结果
			is_ready_to_blink = true;
			showBlink(IDX_SX_SHU, 0, IDX_TIAN);
		}
	}
	/**
	* 闪烁开奖结果动画
	*/
	if (is_ready_to_blink == true){
		if (!is_play_result){
			playSoundWithResult(gResultMusic);
			is_play_result = true;
		}
		layer_blink_shengxiao.alpha -= 0.03;
		if (layer_blink_shengxiao.alpha <= 0)
			layer_blink_shengxiao.alpha = 1;
		layer_blink_wuxing.alpha -= 0.03;
		if (layer_blink_wuxing.alpha <= 0)
			layer_blink_wuxing.alpha = 1;
		layer_blink_tiandi.alpha -= 0.03;
		if (layer_blink_tiandi.alpha <= 0)
			layer_blink_tiandi.alpha = 1;
		if (layer_blink_tiandi.alpha == 1){
			blink_count++;
		}
			
		if (blink_count == 3 && layer_blink_tiandi.alpha == 1){//闪烁3次可以了 
			//可以移掉了
			if (layer_open_lottery !== null){
				layer_open_lottery.remove();   
				layer_open_lottery = null;
			}
			if (layer_blink_background !== null){
				layer_blink_background.remove();
				layer_blink_background = null;
			}	
			//回到压注界面显示
			showBlink(curr_result.result_sx, curr_result.result_wx, curr_result.result_td);
		}
		if (blink_count == 4 && layer_blink_tiandi.alpha == 1){ //压注界面的结果显示也完了
			blink_count = 0;
			is_ready_to_blink = false;
			is_play_result = false;
			layer_blink_background.remove();
			layer_blink_background = null;
			//进行下一回合
			curr_game_state = ROOM_STATE_WAITING_FOR_SERVER;				
			setLastResult(curr_result.result_td, curr_result.result_wx, curr_result.result_sx);	
			//结算提示
			var masterScore = parseInt(curr_result.master_score);
			var selfScore = parseInt(curr_result.win_num) - parseInt(curr_result.setted_num);
			if (curr_self_id == curr_master_id){
				selfScore = masterScore;
				curr_self_chips += selfScore;
			}
			else {
				curr_self_chips += curr_result.win_num;				
			}
			setSelfChips(curr_self_chips);
			var color1 = "#ff0000";
			if (masterScore < 0){
				color1 = "#00ff00";
			}
			var color2 = "#ff0000";
			if (selfScore < 0){
				color2 = "#00ff00";
			}
			noticeBoxResultEx("庄家："+splitNumber(masterScore), color1,
				"本家："+splitNumber(selfScore), color2);
		}
	}
}
function getRotateIncreaseSpeed(angle, maxAngle, minSpeed, maxSpeed){
	var speed = 0; 
	if (angle == 0){
		speed = minSpeed;
	}
	else if (angle < maxAngle / 8){
		speed = maxSpeed * (angle / (maxAngle / 8));
	}
	else if (angle < maxAngle / 8 * 6){
		speed = maxSpeed;			
	}
	else if (angle < maxAngle){
		speed = maxSpeed * ((maxAngle -angle) / (maxAngle / 8 * 2));
		if (speed + angle > maxAngle)
			speed = maxAngle - angle;
		if (speed < 0.01)
			speed = 0;
	}
	return speed;
}
function getRotateDecreaseSpeed(angle, maxAngle, minSpeed, maxSpeed){
	var speed = 0; 
	if (angle == 0){
		speed = minSpeed;
	}
	else if (angle > maxAngle / 8){
		speed = maxSpeed * (angle / (maxAngle / 8));
	}
	else if (angle > maxAngle / 8 * 6){
		speed = maxSpeed;			
	}
	else if (angle > maxAngle){
		speed = maxSpeed * ((maxAngle -angle) / (maxAngle / 8 * 2));
		if (angle - speed < maxAngle)
			speed = angle - maxAngle;
		if (speed < 0.01)
			speed = 0;
	}
	return speed;
}
var layer_blink_background = null;
var layer_blink_shengxiao = null;
var layer_blink_wuxing = null;
var layer_blink_tiandi = null;
function showBlink(sxResult, wxResult, tdResult){
	layer_blink_background = new LSprite();
	layer_background.addChild(layer_blink_background);
	//生肖
	var layer_blink_shengxiao_background = new LSprite(); 
	layer_blink_background.addChild(layer_blink_shengxiao_background);
	layer_blink_shengxiao_background.rotatex = SCREEN_WIDTH / 2; //转盘中心点宽度
	layer_blink_shengxiao_background.rotatey = 485;   //转盘中心点高度
	layer_blink_shengxiao_background.rotate = 360 - getAngleFromLotteryResult(sxResult);	
	layer_blink_shengxiao = spriteWithImage(IMG_BLINK_SHENGXIAO);
	layer_blink_shengxiao_background.addChild(layer_blink_shengxiao);
	layer_blink_shengxiao.x =convertToScreenCenterX(imgSize[IMG_BLINK_SHENGXIAO].w);
	layer_blink_shengxiao.y = 160; //250半径
	
	//五行
	var layer_blink_wuxing_background = new LSprite(); 
	layer_blink_background.addChild(layer_blink_wuxing_background);
	layer_blink_wuxing_background.rotatex = SCREEN_WIDTH / 2; //转盘中心点宽度
	layer_blink_wuxing_background.rotatey = 485;   //转盘中心点高度
	if (wxResult !== 0 )
		layer_blink_wuxing_background.rotate = 360 - getAngleFromLotteryResult(wxResult);	
	layer_blink_wuxing = spriteWithImage(IMG_BLINK_WUXING);
	layer_blink_wuxing_background.addChild(layer_blink_wuxing);
	layer_blink_wuxing.x =convertToScreenCenterX(imgSize[IMG_BLINK_WUXING].w);
	layer_blink_wuxing.y = 285; //150半径
	
	//天地
	var layer_blink_tiandi_background = new LSprite(); 
	layer_blink_background.addChild(layer_blink_tiandi_background);
	layer_blink_tiandi_background.rotatex = SCREEN_WIDTH / 2; //转盘中心点宽度
	layer_blink_tiandi_background.rotatey = 485;   //转盘中心点高度
	layer_blink_tiandi_background.rotate = 360 - getAngleFromLotteryResult(tdResult);	
	layer_blink_tiandi = spriteWithImage(IMG_BLINK_TIANDI);
	layer_blink_tiandi_background.addChild(layer_blink_tiandi);
	layer_blink_tiandi.x =convertToScreenCenterX(imgSize[IMG_BLINK_TIANDI].w);
	layer_blink_tiandi.y = 375; //84半径
}
/******************************************************************************************************
* 底部区域
*******************************************************************************************************/
var layer_bottom_background = null; //操作栏目背景层
var button_chip_continue = null;
var button_chip_100 = null;
var button_chip_1000 = null;
var button_chip_10000 = null;
var button_chip_100000 = null;
function initBottom(){
	//背景
	layer_bottom_background = spriteWithImage(IMG_CHIP_OPERATION_BACKGROUND);
	layer_background.addChild(layer_bottom_background);
	layer_bottom_background.y = 892;
	//我的金币标题
	var label_my_chips_tip = labelWithText("我的金币", 22, "#3e4f6b");
	layer_bottom_background.addChild(label_my_chips_tip);
	label_my_chips_tip.x = 20;
	label_my_chips_tip.y = 25;
	//我的金币数量
	setSelfChips(curr_self_chips);
	//下注一百按钮
	button_chip_100 = spriteWithImage(IMG_CHIP_10000);
	layer_bottom_background.addChild(button_chip_100);
	button_chip_100.x = 140;
	button_chip_100.y = 5;
	button_chip_100.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip100);
	//下注一千按钮
	button_chip_1000 = spriteWithImage(IMG_CHIP_100000);
	layer_bottom_background.addChild(button_chip_1000);
	button_chip_1000.x = 230;
	button_chip_1000.y = 15;
	button_chip_1000.alpha = 0.5;
	button_chip_1000.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip1000);
	//下注一万按钮
	button_chip_10000 = spriteWithImage(IMG_CHIP_1000000);
	layer_bottom_background.addChild(button_chip_10000);
	button_chip_10000.x = 320;
	button_chip_10000.y = 15;
	button_chip_10000.alpha = 0.5;
	button_chip_10000.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip10000);
	//下注一万按钮
	button_chip_100000 = spriteWithImage(IMG_CHIP_10000000);
	layer_bottom_background.addChild(button_chip_100000);
	button_chip_100000.x = 410;
	button_chip_100000.y = 15;
	button_chip_100000.alpha = 0.5;
	button_chip_100000.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip100000);
	
	button_chip_continue = new LButton(bitmapWithImage(IMG_CONTINUE_SET_CHIP), bitmapWithImage(IMG_CONTINUE_SET_CHIP), bitmapWithImage(IMG_CONTINUE_SET_CHIP), bitmapWithImage(IMG_CONTINUE_SET_CHIP_DISABLE));
	layer_bottom_background.addChild(button_chip_continue);
	button_chip_continue.x = 520; button_chip_continue.y = 15;
	button_chip_continue.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueSetChips);	
	setContinueButtonState(LButton.STATE_DISABLE);
	
	/*
	var button_add_chips = new LButton(bitmapWithImage(IMG_ADD_CHIPS));
	layer_bottom_background.addChild(button_add_chips);
	button_add_chips.x = 100; button_add_chips.y = 50;
	button_add_chips.addEventListener(LMouseEvent.MOUSE_DOWN, onAddChipsClicked);	
	*/
	setLayerChipSelected(500);
}
//我的金币数量
var layer_my_chips = null; //我的金币数量
var layer_my_chips_width = 127;
function setSelfChips(num){
	var content = splitNumber(String(num));//String(Math.floor(num / 10000)) + "万" ;//
	if (layer_my_chips == null){
		layer_my_chips = labelWithText(content, 35, "#ffcf3d");
		layer_bottom_background.addChild(layer_my_chips);
		layer_my_chips.x = 20;
		layer_my_chips.y = 70;
	}
	else{
		layer_my_chips.setText(content);

		if (layer_my_chips.getWidth() * layer_my_chips.scaleX > layer_my_chips_width){
			var scale = layer_my_chips_width / layer_my_chips.getWidth();
			layer_my_chips.scaleX = scale;
			layer_my_chips.scaleY = scale;
		}
	}
}
//100下注金币被选择
var curr_seleted_chips = 500;
function onSelectChip100(event){
	/*
	if (curr_game_state != ROOM_STATE_WAITING_FOR_SET_CHIPS){
		msgBox("提示：【投注】状态操作才有效！");
		return;
	}
	*/
	playSoundWithSelect();

	curr_seleted_chips = 500;
	button_chip_100.alpha = 1.0; button_chip_100.y = 5;
	button_chip_1000.alpha = 0.5; button_chip_1000.y = 15;
	button_chip_10000.alpha = 0.5; button_chip_10000.y = 15;
	button_chip_100000.alpha = 0.5; button_chip_100000.y = 15;
	setLayerChipSelected(curr_seleted_chips);
}
//1000下注金币被选择
function onSelectChip1000(event){
	/*
	if (curr_game_state != ROOM_STATE_WAITING_FOR_SET_CHIPS){
		msgBox("提示：【投注】状态操作才有效！");
		return;
	}
	*/
	playSoundWithSelect();

	curr_seleted_chips = 10000;
	button_chip_100.alpha = 0.5; button_chip_100.y = 15;
	button_chip_1000.alpha  = 1.0; button_chip_1000.y = 5;
	button_chip_10000.alpha = 0.5; button_chip_10000.y = 15;
	button_chip_100000.alpha = 0.5; button_chip_100000.y = 15;
	setLayerChipSelected(curr_seleted_chips);
}
//10000下注金币被选择
function onSelectChip10000(event){
	/*
	if (curr_game_state != ROOM_STATE_WAITING_FOR_SET_CHIPS){
		msgBox("提示：【投注】状态操作才有效！");
		return;
	}
	*/
	playSoundWithSelect();

	curr_seleted_chips = 100000;
	button_chip_100.alpha = 0.5; button_chip_100.y = 15;
	button_chip_1000.alpha  = 0.5; button_chip_1000.y = 15;
	button_chip_10000.alpha = 1.0; button_chip_10000.y = 5;
	button_chip_100000.alpha = 0.5; button_chip_100000.y = 15;
	setLayerChipSelected(curr_seleted_chips);
}
//10000下注金币被选择
function onSelectChip100000(event){
	/*
	if (curr_game_state != ROOM_STATE_WAITING_FOR_SET_CHIPS){
		msgBox("提示：【投注】状态操作才有效！");
		return;
	}
	*/
	playSoundWithSelect();

	curr_seleted_chips = 500000;
	button_chip_100.alpha = 0.5; button_chip_100.y = 15;
	button_chip_1000.alpha = 0.5; button_chip_1000.y = 15;
	button_chip_10000.alpha= 0.5; button_chip_10000.y = 15;
	button_chip_100000.alpha= 1.0; button_chip_100000.y = 5;
	setLayerChipSelected(curr_seleted_chips);
}
function onContinueSetChips(event){
	if (curr_game_state != ROOM_STATE_WAITING_FOR_SET_CHIPS){
		msgBox("提示：【投注】状态操作才有效！");
		return;
	}
	playSoundWithClick();
	continueLastSettedChips();
	// clearLastSettedChips();
	// setContinueButtonState(LButton.STATE_DISABLE);
}
var continue_button_state = LButton.STATE_DISABLE;
function setContinueButtonState(state){
	continue_button_state = state;
	button_chip_continue.setState(state);
}
//选择下注金币
var layer_chip_selected = null;
function setLayerChipSelected(num){
	if (layer_chip_selected == null){
		layer_chip_selected = spriteWithImage(IMG_CHIPS_SELECTED);
		layer_bottom_background.addChild(layer_chip_selected);
	}	
	switch(num){
		case 500: 
			layer_chip_selected.x = 136.5; layer_chip_selected.y = 1;
			break;
		case 10000:
			layer_chip_selected.x = 226.5; layer_chip_selected.y = 1;
			break;
		case 100000:
			layer_chip_selected.x = 316; layer_chip_selected.y = 1;
			break;
		case 500000:
			layer_chip_selected.x = 406; layer_chip_selected.y = 2;
			break;
		default:
			break;		
	}
}
//撤销下注被选择
function onAddChipsClicked(event){
	msgBox("充值！");
}

var layer_guide_master = null;
var layer_guide = null;
function showGuide()
{
	layer_guide_master = new LSprite();
	baseLayer.addChild(layer_guide_master);
	layer_guide_master.graphics.drawRect(1, "#000000", [0, 0, SCREEN_WIDTH, SCREEN_HEIGHT], true,  "#000000");
	layer_guide_master.alpha = 0;
	layer_guide_master.addEventListener(LMouseEvent.MOUSE_DOWN, cancleEvent);
	
	layer_guide = new LSprite();
	baseLayer.addChild(layer_guide);
	var layer_help1 = spriteWithImage(IMG_HELP1)
	layer_guide.addChild(layer_help1);
	var tip1 = labelWithText("欢迎您来到乐玩九州！", 20,"#ffffff");
	layer_guide.addChild(tip1);
	tip1.x = 150; tip1.y = 450;
	var tip2 = labelWithText("接下来将为您进行简单的游戏介绍！", 20,"#ffffff");
	layer_guide.addChild(tip2);
	tip2.x = 150; tip2.y = 480;
	var btn1 = labelWithText("我是老手不需要！", 20,"#cccccc");
	layer_guide.addChild(btn1);
	btn1.x = 230; btn1.y = 550;
	btn1.addEventListener(LMouseEvent.MOUSE_DOWN, onCancleGuide);
	var tip3 = labelWithText("_______________", 20,"#cccccc");
	layer_guide.addChild(tip3);
	tip3.x = 230; tip3.y = 550;
	var btn2 = spriteWithImage(IMG_HELP0);
	layer_guide.addChild(btn2);
	btn2.x = 240; btn2.y = 630;
	btn2.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueGuide2);
	var tip4 = labelWithText("开始引导", 20,"#333333");
	layer_guide.addChild(tip4);
	tip4.x = 260; tip4.y = 640;
	
}
function cancleEvent(event){
	
}
function onCancleGuide(event){
	if (layer_guide_master != null){
		layer_guide_master.remove();
		layer_guide_master =null;
	}
	if (layer_guide != null){
		layer_guide.remove();
		layer_guide =null;
	}
}
function onContinueGuide2(event){
	layer_guide.remove();
	layer_guide = spriteWithImage(IMG_HELP2);
	baseLayer.addChild(layer_guide);
	layer_guide.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueGuide3);
}

function onContinueGuide3(event){
	layer_guide.remove();
	layer_guide = spriteWithImage(IMG_HELP3);
	baseLayer.addChild(layer_guide);
	layer_guide.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueGuide4);
}

function onContinueGuide4(event){
	layer_guide.remove();
	layer_guide = spriteWithImage(IMG_HELP4);
	baseLayer.addChild(layer_guide);
	layer_guide.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueGuide5);
}

function onContinueGuide5(event){
	layer_guide.remove();
	layer_guide = new LSprite();
	baseLayer.addChild(layer_guide);
	var layer_help1 = spriteWithImage(IMG_HELP1)
	layer_guide.addChild(layer_help1);
	var tip1 = labelWithText("新手引导结束！", 20,"#ffffff");
	layer_guide.addChild(tip1);
	tip1.x = 150; tip1.y = 450;
	var tip2 = labelWithText("请开启您精彩的竞猜之旅吧！", 20,"#ffffff");
	layer_guide.addChild(tip2);
	tip2.x = 150; tip2.y = 480;
	var btn1 = labelWithText("重新观看！", 20,"#cccccc");
	layer_guide.addChild(btn1);
	btn1.x = 230; btn1.y = 550;
	btn1.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueGuide2);
	var tip3 = labelWithText("_______", 20,"#cccccc");
	layer_guide.addChild(tip3);
	tip3.x = 230; tip3.y = 550;
	var btn2 = spriteWithImage(IMG_HELP0);
	layer_guide.addChild(btn2);
	btn2.x = 240; btn2.y = 630;
	btn2.addEventListener(LMouseEvent.MOUSE_DOWN, onCancleGuide);
	var tip4 = labelWithText("回到游戏", 20,"#333333");
	layer_guide.addChild(tip4);
	tip4.x = 260; tip4.y = 640;
}



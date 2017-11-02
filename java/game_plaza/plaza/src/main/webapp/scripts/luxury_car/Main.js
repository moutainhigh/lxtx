
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
///  游戏初始化设定
window._requestAF = (function() {
  return window.requestAnimationFrame ||
         window.webkitRequestAnimationFrame ||
         window.mozRequestAnimationFrame ||
         window.oRequestAnimationFrame ||
         window.msRequestAnimationFrame ||
         function(/* function FrameRequestCallback */ callback, /* DOMElement Element */ element) {
           window.setTimeout(callback, 1000/25);
         };
})();
var SCREEN_WIDTH = 1136;//
var SCREEN_HEIGHT = 640;//window.innerHeight * SCREEN_WIDTH / window.innerWidth;
// LGlobal.aspectRatio = LANDSCAPE;
window.onload = function(){
	LInit(60,"car",SCREEN_WIDTH,SCREEN_HEIGHT,main);
}

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
var layer_fps = null;
var layer_msg_box_bg = null;
var baseLayerTimer = null;
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
	}	var basePath = global_util.getAppPath();
	if(loadsound){		
		if(LGlobal.os == OS_PC){
			//浏览器支持WebAudio，或者环境为PC，则预先读取所有音频
			imgData.push({name : "sound_bg",path : basePath+"/luxury_car/medias/bg.mp3",type:"sound"});
			imgData.push({name : "sound_circle",path : basePath+"/luxury_car/medias/circle.mp3",type:"sound"});
			imgData.push({name : "sound_click",path : basePath+"/luxury_car/medias/click.mp3",type:"sound"});
			imgData.push({name : "sound_roulette5",path : basePath+"/luxury_car/medias/roulette5.mp3",type:"sound"});
			imgData.push({name : "sound_roulette20",path : basePath+"/luxury_car/medias/roulette20.mp3",type:"sound"});
			imgData.push({name : "sound_roulette20f",path : basePath+"/luxury_car/medias/roulette20f.mp3",type:"sound"});
			imgData.push({name : "sound_select",path : basePath+"/luxury_car/medias/select.mp3",type:"sound"});
			imgData.push({name : "sound_setchips",path : basePath+"/luxury_car/medias/setchips.mp3",type:"sound"});
			imgData.push({name : "sound_start",path : basePath+"/luxury_car/medias/start.mp3",type:"sound"});
		}
	}

	baseLayerTimer = new LTimer(200,1);
	baseLayerTimer.addEventListener(LTimerEvent.TIMER, refresh);
	baseLayer = new LSprite();
	baseLayer.graphics.drawRect(0, "#ff0000", [0, 0, SCREEN_WIDTH,SCREEN_HEIGHT], false,  "#0e2f50");
	
	if (LGlobal.mobile){
		// LGlobal.align = LStageAlign.TOP_LEFT;
		// LGlobal.stageScale = LStageScaleMode.EXACT_FIT ;

		LGlobal.align = LStageAlign.MIDDLE;
		LGlobal.stageScale = LStageScaleMode.SHOW_ALL;
	}else{
		LGlobal.align = LStageAlign.MIDDLE;
		LGlobal.stageScale = LStageScaleMode.NO_SCALE
		LSystem.screen(LStage.FULL_SCREEN);
	}	

	LGlobal.stage.addEventListener(LEvent.WINDOW_RESIZE, function(){
		baseLayerTimer.reset();
		baseLayerTimer.start();
	});	

	refresh();
	if (LGlobal.mobile){
		LGlobal.stage.addEventListener(LEvent.WINDOW_ORIENTATIONCHANGE,function(e){	
			baseLayerTimer.reset();
			baseLayerTimer.start();
		});
	}	
		
	addChild(baseLayer);

	layer_helper = new LSprite();
	addChild(layer_helper);
	
	var loader = new LLoaderEx(loadBitmapdata);
}

function fullScreen(){
	return;//暂时不用
	if (LGlobal.mobile){		
		LGlobal.stageScale = LStageScaleMode.EXACT_FIT ;
		LSystem.screen(LStage.FULL_SCREEN);
		if (LGlobal.os == OS_ANDROID){
			var ua = window.navigator.userAgent;
			// alert(ua);
			if(ua.indexOf("SM-A7009") >= 0){ //三星UC
				var scale = 0.82;
				LGlobal.stage.scaleX = scale;
				LGlobal.stage.scaleY = scale;
				LGlobal.stage.y = 110;
			}else if(ua.indexOf("MZ-m3") >= 0) {//MZ QQ
				var scale = 0.85;
				LGlobal.stage.scaleX = scale;
				LGlobal.stage.scaleY = scale;
			}
		}		
	}else{
		LGlobal.stageScale = LStageScaleMode.NO_SCALE;
		LSystem.screen(LStage.FULL_SCREEN);
	}
}

// 加载游戏用图片
function loadBitmapdata(bitmaps){
	loadingLayer = new LoadingSampleEx(bitmaps);	
	baseLayer.addChild(loadingLayer);

	LLoadManage.load(
			imgData,
			function(progress){
				loadingLayer.setProgress(progress);
			},
			function(result){
				imglist = result;

				loadingLayer.remove();
				loadingLayer = null;
				gameInit()
			}
			
		);
}

function gameInit(result){
	layer_fps = new FPS();
	layer_fps.alpha = 0;
	addChild(layer_fps);
	
	//背景
	layer_background = new LSprite();//spriteWithImage(IMG_BACKGROUND);
	baseLayer.addChild(layer_background);
	layer_background.graphics.drawRect(0, "#ff0000", [0, 0, SCREEN_WIDTH,SCREEN_HEIGHT], false,  "#0e2f50");

	initTop();
	initCenter();
	initRight();
	initBottom();	

	layer_msg_box_bg = new LSprite();
	baseLayer.addChild(layer_msg_box_bg);
	layer_msg_box_bg.graphics.drawRect(0, "#0e2f50", [0, 0, SCREEN_WIDTH,SCREEN_HEIGHT], false,  "#0e2f50");

	setSelfChips('');
	connectServer();
	initSound(playSoundBG);    
}
function getScreenWidth(){
	return LGlobal.width < LGlobal.height ? LGlobal.height : LGlobal.width;
}
function getScreenHeight(){
	return LGlobal.width < LGlobal.height ? LGlobal.width : LGlobal.height;
}
function refresh(){	
	if (!LGlobal.mobile){
		LGlobal.stageScale = LStageScaleMode.NO_SCALE;
		LSystem.screen(LStage.FULL_SCREEN);
		return;
	}
	if(window.innerWidth < window.innerHeight){
		LGlobal.width = SCREEN_HEIGHT;
		LGlobal.height = SCREEN_WIDTH

		baseLayer.y = SCREEN_WIDTH;
		baseLayer.rotate = -90;	
		if (layer_helper){
			layer_helper.rotate = -90;	
			layer_helper.y = LGlobal.height;
		}
	}else{
		LGlobal.height = SCREEN_HEIGHT;
		LGlobal.width = SCREEN_WIDTH

		baseLayer.rotate = 0;
		baseLayer.y = 0;

		if (layer_helper){
			layer_helper.rotate = 0;
			layer_helper.y = 0;	
		}
	}
	closeHelper(null);

	LGlobal.canvasObj.width  = LGlobal.width;
	LGlobal.canvasObj.height  = LGlobal.height;

	LSystem.screen(LStage.FULL_SCREEN);
	resetInputTextBox()

	myScrollTop();
}

function connectServer(){
	connect(gServerAddress);

	var myTimer = new LTimer(10000, 1000000000);
	myTimer.addEventListener(LTimerEvent.TIMER, requestTick);
    myTimer.start();
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
	layer_top_background = spriteWithImage(IMG_BACKGROUND);
	layer_background.addChild(layer_top_background);

	var img = spriteWithImage(IMG_TOP_LEFT_BG);
	layer_top_background.addChild(img);

	var img2 = spriteWithImage(IMG_MASTER_SMALL);
	img2.x = 30;
	img.addChild(img2);

	var img3 = spriteWithImage(IMG_HELP);
	img3.x = 120;
	img3.addEventListener(LMouseEvent.MOUSE_UP, onHelpMenuClicked)
	img.addChild(img3);

	var img = spriteWithImage(IMG_TOP_RIGHT_BG);
	img.x = layer_top_background.getWidth() - img.getWidth();
	layer_top_background.addChild(img);

	var img2 = spriteWithImage(IMG_TOP_RIGHT_SOUND)
	img2.x = 70;
	img2.addEventListener(LMouseEvent.MOUSE_UP, onMenuClicked);
	img.addChild(img2);
	var img2 = spriteWithImage(IMG_TOP_RIGHT_BACK)
	img2.x = 150;
	img2.addEventListener(LMouseEvent.MOUSE_UP, onMenuItem1Click);
	img.addChild(img2);

	var img = spriteWithImage(IMG_LEFT_BG);
	img.x = 13;
	img.y = 42;
	layer_top_background.addChild(img);

	var img = spriteWithImage(IMG_MASTER_BG);
	img.x = 20;
	img.y = 60;
	layer_top_background.addChild(img);

	var img2 = spriteWithImage(IMG_HEAD_SMALL);
	img2.x = 15;
	img2.y = converToParentY(img2.getHeight(), img.getHeight());
	img.addChild(img2);

	var lb = labelWithText('昵称:', 18, '#1fd7ff');
	lb.x = 60;lb.y = 40;
	img.addChild(lb);
	var lb = labelWithText('金币:', 18, '#1fd7ff');
	lb.x = 60;lb.y = 70;
	img.addChild(lb);
	var lb = labelWithText('成绩:', 18, '#1fd7ff');
	lb.x = 60;lb.y = 100;
	img.addChild(lb);
	var lb = labelWithText('当前庄数:', 18, '#1fd7ff');
	lb.x = 24;lb.y = 125;
	img.addChild(lb);

	var img = spriteWithImage(IMG_APPLY);
	img.x = 28;
	img.y = 215;
	img.addEventListener(LMouseEvent.MOUSE_UP, onRequestMasterClicked);
	layer_top_background.addChild(img);

	var img = spriteWithImage(IMG_UP);
	img.x = 142;
	img.y = 215;
	img.addEventListener(LMouseEvent.MOUSE_UP, onMasterListClick);
	layer_top_background.addChild(img);

	showChatRoom();
}

var layer_help_menu_items = null;
var layer_help_clear_event = null;
function onHelpMenuClicked(event){
	layer_help_clear_event = new LSprite();
	baseLayer.addChild(layer_help_clear_event);
	layer_help_clear_event.graphics.drawRect(1, "#ffffff", [0, 0, SCREEN_WIDTH, SCREEN_HEIGHT], true,  "#ffffff");
	layer_help_clear_event.alpha = 0;
	layer_help_clear_event.addEventListener(LMouseEvent.MOUSE_DOWN, onHelpClearEvent);
	
	if (layer_help_menu_items != null){
		layer_help_menu_items.remove();
	}
	layer_help_menu_items = new LSprite();
	layer_help_menu_items.x = 0;
	baseLayer.addChild(layer_help_menu_items);

	var item1 = new LSprite();
	layer_help_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 125; item1.y = 70;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuHelperClicked);
	var text = "规则说明";
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;
	
	var item2 = new LSprite();
	layer_help_menu_items.addChild(item2);
	item2.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item2.x = 125; item2.y = 118;
	item2.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuGuideClicked);
	var text = "新手引导";
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item2.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;
}

function onHelpClearEvent(event){
	layer_help_clear_event.remove();
	if (layer_help_menu_items != null){
		layer_help_menu_items.remove();
		layer_help_menu_items = null;
	}
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
	layer_menu_items.x = 500;
	baseLayer.addChild(layer_menu_items);

	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 70;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuMusicBgClicked);
	var text = "音乐：" + ((layer_sound_mgr && layer_sound_mgr.getBgPlay()) ? '开' : '关');
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;

	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 120;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuMusicClicked);	
	var text = "音效：" + ((layer_sound_mgr && layer_sound_mgr.getMusicPlay()) ? '开' : '关');
	var itemText1 = labelWithText(text, 24, "#ffffff");
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
function onMenuItem1Click(event){	var wxid = document.getElementById("wxid").value;	var chnno =document.getElementById("chnno").value;	self.location = global_util.getAppPath()+"/game_center/"+wxid+"/"+chnno;
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

			stopSoundMusic();
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
	onHelpClearEvent(event);
	showGuide();
}

var layer_master_background = null;
function setMasterInfo(name, chips, score, count){
	if (layer_master_background != null){
		layer_master_background.remove();
	}

	layer_master_background = panelWithSprite(120,110);
	layer_background.addChild(layer_master_background);
	layer_master_background.x = 130; layer_master_background.y = 100;
	
	var layer_name = labelWithText(name, 18, "#ffedd9");
	layer_master_background.addChild(layer_name);
	layer_name.x = 0; layer_name.y = 0;
	
	var outChips = splitNumberNotPlus(chips);
	var layer_chips = labelWithText(outChips, 18, "#ffd368");
	layer_master_background.addChild(layer_chips);
	layer_chips.x = 0; layer_chips.y = 30;
	
	var outScore = splitNumber(score);
	var layer_score;
	if (score >= 0 ){
		layer_score = labelWithText(outScore, 18, "#ff0000");
	}
	else {
		layer_score = labelWithText(outScore, 18, "#16bf22");
	}
	layer_master_background.addChild(layer_score);
	layer_score.x = 0; layer_score.y = 60;
	
	var layer_count = labelWithText(count, 18, "#16bf22");
	layer_master_background.addChild(layer_count);
	layer_count.x = 0; layer_count.y = 85;
}
//设置开奖时间
var layer_open_time_background = null;
var layer_open_time = null;
function setOpenTime(iValue, sound){
	if (layer_open_time_background != null){
		layer_open_time_background.remove();
	}
	layer_open_time_background = spriteWithImage(IMG_OPEN_TIME_BACKGROUND);
	layer_game_background.addChild(layer_open_time_background);
	layer_open_time_background.alpha = 0;
	layer_open_time_background.x = 460; layer_open_time_background.y = 5;
	var tip;
	switch(curr_game_state){
	case ROOM_STATE_WAITING_FOR_MASTER:
		tip = "正在排庄";
		break;
	case ROOM_STATE_WAITING_FOR_START:
		tip = "等待开始";
		if (sound){
			startSoundWithStart();
		}		
		break;
	case ROOM_STATE_WAITING_FOR_SET_CHIPS:
		tip = "投注时间还剩";
		if (iValue <= 5){
			startSoundWithStart();
		}
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
	if (curr_game_state == ROOM_STATE_WAITING_FOR_MASTER || curr_game_state == ROOM_STATE_WAITING_FOR_SERVER){
		content = " ...";
	}else if (curr_game_state == ROOM_STATE_WAITING_FOR_CACLULATE){
		content = " ...";
		if (layer_effect){
			if (!layer_effect.isShowText()){
				content = "";
			}
		}
	}

	var layer_open_time_tip = labelWithText(tip, 22, "#292355");
	layer_open_time_background.addChild(layer_open_time_tip);
	layer_open_time_tip.x = 5;
	layer_open_time_tip.y = 10;

	if (!layer_open_time){
		layer_open_time = labelWithText(content, 35, "#ffffff");
		layer_game_background.addChild(layer_open_time);		
		layer_open_time.x = converToParentX(layer_open_time.getWidth(), layer_game_background.getWidth());
		layer_open_time.y = converToParentX(35, layer_game_background.getHeight());		
	}else{
		layer_open_time.setText(content);
		layer_open_time.x = converToParentX(layer_open_time.getWidth(), layer_game_background.getWidth());
		layer_open_time.y = converToParentX(35, layer_game_background.getHeight());
	}
}
//设置上期开奖结果
var layer_last_result_background = null;
var layer_last_result_flag = 0;
var layer_last_result = 0;
function setLastResult(result_flag, result){
	if (layer_last_result_background != null){
		layer_last_result_background.remove();
	}
	layer_last_result_flag = result_flag;
	layer_last_result = result;
	
	effectLastResult(layer_last_result_flag);	
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
var layer_game_beton = null;
var layer_game_background_result = null;
var layer_circle = null;
var layer_circle_last = null;
function initCenter(){
	// var scale = window.innerHeight / window.innerWidth;

	layer_game_background = panelWithSprite(imgSize[IMG_GAME_BACKGROUND_CAR].w,imgSize[IMG_GAME_BACKGROUND_CAR].h);
	layer_game_background.x = 266;
	// layer_game_background.scaleX = scale;
	// layer_game_background.scaleY = scale;
	layer_background.addChild(layer_game_background);

	var img = spriteWithImage(IMG_GAME_BACKGROUND);
	img.x = converToParentX(img.getWidth(), imgSize[IMG_GAME_BACKGROUND_CAR].w);
	img.y = converToParentY(img.getHeight(), imgSize[IMG_GAME_BACKGROUND_CAR].h);
	layer_game_background.addChild(img);

	layer_game_beton = panelWithSprite(imgSize[IMG_GAME_BACKGROUND_CAR].w,imgSize[IMG_GAME_BACKGROUND_CAR].h);
	layer_game_background.addChild(layer_game_beton);

	var bgTime = panelWithSprite(imgSize[IMG_GAME_BACKGROUND_CAR].w,imgSize[IMG_GAME_BACKGROUND_CAR].h);
	layer_game_background.addChild(bgTime);

	layer_game_background_result = panelWithSprite(imgSize[IMG_GAME_BACKGROUND_CAR].w,imgSize[IMG_GAME_BACKGROUND_CAR].h);
	layer_game_background.addChild(layer_game_background_result);

	var img = spriteWithImage(IMG_GAME_BACKGROUND_CAR);
	img.x = converToParentX(img.getWidth(), layer_game_background.getWidth());
	img.y = converToParentY(img.getHeight(), layer_game_background.getHeight());
	layer_game_background.addChild(img);

	layer_circle = panelWithSprite(layer_game_background.getWidth(), layer_game_background.getHeight());
	layer_circle_last = panelWithSprite(layer_game_background.getWidth(), layer_game_background.getHeight());
	layer_circle_last.x = layer_game_background.x
	layer_circle.x = layer_game_background.x
	layer_background.addChild(layer_circle_last);
	layer_background.addChild(layer_circle);
	// layer_game_background.y = 165;
	// layer_game_background.addEventListener(LMouseEvent.MOUSE_DOWN, onSetChip);
	// layer_game_background.addEventListener(LEvent.ENTER_FRAME, onGameFrame);
	// is_ready_to_rotate = false;

	var myPoint = new Array(
		new Array(new Array(240,112), new Array(240,240), new Array(117,240), IDX_BENZ),
		new Array(new Array(371,112), new Array(371,240), new Array(494,240), IDX_BMW),
		new Array(new Array(240,372), new Array(240,500), new Array(117,372), IDX_VM),
		new Array(new Array(371,372), new Array(371,500), new Array(494,372), IDX_AUDI)
		);
	for (var i=0,len=myPoint.length; i< len; ++i){
		var tmp = myPoint[i];
		var tri = panelWithSpriteForTriangle(tmp[0], tmp[1], tmp[2]);
		tri.myIndex = tmp[3];
		layer_game_background.addChild(tri);
		tri.addEventListener(LMouseEvent.MOUSE_DOWN, onSetChip);
	}

	var myPoint = new Array(
		new Array(130,90,110,260, IDX_LAMBORGHINI),		
		new Array(130,90,373,260, IDX_ROLLS),
		new Array(90,128,260,110, IDX_FERRARI),
		new Array(90,128,260,370, IDX_BENTLEY)
		);
	for (var i=0,len=myPoint.length; i< len; ++i){
		var tmp = myPoint[i];
		var tri = panelWithSprite(tmp[0], tmp[1]);
		tri.myIndex = tmp[4];
		tri.x = tmp[2];
		tri.y = tmp[3];
		layer_game_background.addChild(tri);
		tri.addEventListener(LMouseEvent.MOUSE_DOWN, onSetChip);
	}

	var img_bg_time = spriteWithImage(IMG_BG_TIME);
	img_bg_time.x = converToParentX(img_bg_time.getWidth(), layer_game_background.getWidth());
	img_bg_time.y = converToParentX(img_bg_time.getHeight(), layer_game_background.getHeight());
	bgTime.addChild(img_bg_time);	
}

var layer_set_master = null;
function setMaster(){
	if (layer_set_master != null){
		layer_set_master.remove();
	}
	if (master_state_up == false){
		layer_set_master = spriteWithImage(IMG_APPLY);	
	}
	else {
		layer_set_master = spriteWithImage(IMG_SET_MASTER_DOWN);
	}
	layer_top_background.addChild(layer_set_master);
	layer_set_master.x = 28; 
	layer_set_master.y = 215;
}

//下注金币
function onSetChip(event){
	if (is_chatting == true){
		return false;
	}	
	// var posX = (event.offsetX - LGlobal.stage.x) / LGlobal.stage.scaleX - event.currentTarget.x - event.currentTarget.getWidth() / 2;
	// var posY = event.currentTarget.getHeight() / 2 - ((event.offsetY - LGlobal.stage.y) / LGlobal.stage.scaleY - event.currentTarget.y);
	// var angle = posAngle(new SPoint(0, 0), new SPoint(posX, posY));

	// var dis = Math.sqrt(posX * posX + posY * posY);
	// if (dis < 52 || dis > 194){
	// 	return;
	// }

	// var index = getIndexByPos(posX, posY);
	var index = event.currentTarget.myIndex;
	if (index == 0){
		return;
	}

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

function onRequestMasterClicked(){
	if (master_state_up == false){
			doRequestMaster(true);
		}
		else {
			doRequestMaster(false);
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
	if (curr_game_state == ROOM_STATE_WAITING_FOR_START && 
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

var layer_effect = null;
function openLottery(sxResult, wxResult, tdResult){
	if (layer_effect == null){
		layer_effect = new EffectResult();
	}else{
		layer_effect.stop();
		layer_effect.resetInit();
	}
	
	layer_effect.moveFromIndex(layer_last_result_flag,tdResult, wxResult);
}

var layer_right_background = null;
function initRight(){
	//背景
	layer_right_background = spriteWithImage(IMG_RIGHT_BG);
	layer_background.addChild(layer_right_background);
	layer_right_background.x = layer_background.getWidth() - layer_right_background.getWidth() - 5;
	layer_right_background.y = 42;

	var space = 27;
	var posX = 13;
	var posY = 15;
	var font = 17;
	var color = "#efb810";

	var lb  = getMultiText("兰", font, color);
	lb.x = posX + space * 0;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("博", font, color);
	lb.x = posX + space * 0;lb.y = posY + 20;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("基", font, color);
	lb.x = posX + space * 0;lb.y = posY + 40;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("尼", font, color);
	lb.x = posX + space * 0;lb.y = posY + 60;
	layer_right_background.addChild(lb);

	var lb  = getMultiText("宝", font, color);
	lb.x = posX + space * 1;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("马", font, color);
	lb.x = posX + space * 1;lb.y = posY + 60;
	layer_right_background.addChild(lb);

	var lb  = getMultiText("法", font, color);
	lb.x = posX + space * 2;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("拉", font, color);
	lb.x = posX + space * 2;lb.y = posY + 30;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("利", font, color);
	lb.x = posX + space * 2;lb.y = posY + 60;
	layer_right_background.addChild(lb);

	var lb  = getMultiText("奥", font, color);
	lb.x = posX + space * 3;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("迪", font, color);
	lb.x = posX + space * 3;lb.y = posY + 60;
	layer_right_background.addChild(lb);

	var lb  = getMultiText("劳", font, color);
	lb.x = posX + space * 4;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("斯", font, color);
	lb.x = posX + space * 4;lb.y = posY + 20;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("莱", font, color);
	lb.x = posX + space * 4;lb.y = posY + 40;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("斯", font, color);
	lb.x = posX + space * 4;lb.y = posY + 60;
	layer_right_background.addChild(lb);	

	var lb  = getMultiText("奔", font, color);
	lb.x = posX + space * 5;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("驰", font, color);
	lb.x = posX + space * 5;lb.y = posY + 60;
	layer_right_background.addChild(lb);

	var lb  = getMultiText("宾", font, color);
	lb.x = posX + space * 6;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("利", font, color);
	lb.x = posX + space * 6;lb.y = posY + 60;
	layer_right_background.addChild(lb);	

	var lb  = getMultiText("大", font, color);
	lb.x = posX + space * 7;lb.y = posY;
	layer_right_background.addChild(lb);
	var lb  = getMultiText("众", font, color);
	lb.x = posX + space * 7;lb.y = posY + 60;
	layer_right_background.addChild(lb);

	showHistory();
}

function getMultiText(text,size, color){
	labelText = new LTextField();
	labelText.color = color;
	labelText.font = "HG行書体";
	labelText.size = size;
	labelText.x = 0;
	labelText.y = 0;
	labelText.text = text;
	labelText.setMultiline(true);
	return labelText;
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
	layer_bottom_background = spriteWithImage(IMG_BOTTOM_BG);
	layer_background.addChild(layer_bottom_background);
	layer_bottom_background.y = baseLayer.getHeight() - layer_bottom_background.getHeight();

	var button_head = spriteWithImage(IMG_HEAD_BIG);
	layer_background.addChild(button_head);
	button_head.x = 15;button_head.y = baseLayer.getHeight() - button_head.getHeight() - 20;

	var chips_bg = spriteWithImage(IMG_CHIPS_BG);
	layer_bottom_background.addChild(chips_bg);
	chips_bg.x = 125;chips_bg.y = 70;

	var img = spriteWithImage(IMG_CHIPS);
	img.x = 22;img.y = 5;
	chips_bg.addChild(img);

	var img = spriteWithImage(IMG_BOTTOM_MAIN);
	img.x = layer_background.getWidth() - 300;img.y = 50;	//hecm update old is showbank
	img.addEventListener(LMouseEvent.MOUSE_UP, onGetChipsClicked);
	layer_bottom_background.addChild(img);
	//hecm update
//	var img = spriteWithImage(IMG_BOTTOM_FRIEND);//
//	img.x = layer_background.getWidth() - 200;img.y = 50;//
//	img.addEventListener(LMouseEvent.MOUSE_UP, requestOnline);//
//	layer_bottom_background.addChild(img);

	var img = spriteWithImage(IMG_BOTTOM_HISTORY);	//hecm update
//	img.x = layer_background.getWidth() -100;img.y = 50;	img.x = layer_background.getWidth() - 200;img.y = 50;
	img.addEventListener(LMouseEvent.MOUSE_UP, onSelectHistoryResult);
	layer_bottom_background.addChild(img);

	//我的金币数量
	setSelfChips(1000);

	var scaleX = baseLayer.getWidth()  / SCREEN_WIDTH;
	var scaleY = baseLayer.getHeight() / SCREEN_HEIGHT;

	// var scale = Math.min(scaleX, scaleY);
	// alert(scale)

	var pl = panelWithSprite(325,150);
	// pl.scaleX = scale;
	// pl.scaleY = scale;

	pl.x = baseLayer.getWidth() - 325;
	pl.y = baseLayer.getHeight() - (150 + 70);
	
	layer_background.addChild(pl);
	//下注一百按钮
	button_chip_100 = spriteWithImage(IMG_CHIP_10000);
	pl.addChild(button_chip_100);
	button_chip_100.x = 0;
	button_chip_100.y = 80;
	button_chip_100.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip100);
	// //下注一千按钮
	button_chip_1000 = spriteWithImage(IMG_CHIP_100000);
	pl.addChild(button_chip_1000);
	button_chip_1000.x = 63;
	button_chip_1000.y = 74;
	// button_chip_1000.alpha = 0.5;
	button_chip_1000.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip1000);
	// //下注一万按钮
	button_chip_10000 = spriteWithImage(IMG_CHIP_1000000);
	pl.addChild(button_chip_10000);
	button_chip_10000.x = 126;
	button_chip_10000.y = 68;
	// button_chip_10000.alpha = 0.5;
	button_chip_10000.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip10000);
	// //下注一万按钮
	button_chip_100000 = spriteWithImage(IMG_CHIP_10000000);
	pl.addChild(button_chip_100000);
	button_chip_100000.x = 189;
	button_chip_100000.y = 62;
	// button_chip_100000.alpha = 0.5;
	button_chip_100000.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectChip100000);
	
	button_chip_continue = new LButton(bitmapWithImage(IMG_CONTINUE_SET_CHIP), bitmapWithImage(IMG_CONTINUE_SET_CHIP), bitmapWithImage(IMG_CONTINUE_SET_CHIP), bitmapWithImage(IMG_CONTINUE_SET_CHIP_DISABLE));
	pl.addChild(button_chip_continue);
	button_chip_continue.x = 252;
	button_chip_continue.y = 56;
	button_chip_continue.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueSetChips);	
	setContinueButtonState(LButton.STATE_DISABLE);
	
	// /*
	// var button_add_chips = new LButton(bitmapWithImage(IMG_ADD_CHIPS));
	// layer_bottom_background.addChild(button_add_chips);
	// button_add_chips.x = 100; button_add_chips.y = 50;
	// button_add_chips.addEventListener(LMouseEvent.MOUSE_DOWN, onAddChipsClicked);	
	// */
	onSelectChip100(null);
}
//我的金币数量
var layer_my_chips = null; //我的金币数量
var layer_my_chips_width = 127;
function setSelfChips(num){
	var content = splitNumberNotPlus(String(num));//String(Math.floor(num / 10000)) + "万" ;//
	if (layer_my_chips == null){
		layer_my_chips = labelWithText(content, 20, "#00f0ff");
		layer_bottom_background.addChild(layer_my_chips);
		layer_my_chips.x = 180;
		layer_my_chips.y = 77;
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

var layer_self_name = null;
var layer_self_name_width = 180;
var layer_self_name_height = 25;
function setSelfName(name){
	if (layer_self_name){
		layer_self_name.setText(name);
	}else{
		var pl = panelWithSprite(layer_self_name_width, layer_self_name_height);
		pl.x = 128;pl.y = 42;
		layer_bottom_background.addChild(pl);

		layer_self_name = labelWithText(name, 20,"#a5e2ff");
		layer_self_name.x = converToParentX(layer_self_name.getWidth(), layer_self_name_width);
		layer_self_name.y = converToParentX(20, layer_self_name_height);
		pl.addChild(layer_self_name);
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
	button_chip_100.alpha = 1.0; button_chip_100.y = 70;
	button_chip_1000.alpha = 0.5; button_chip_1000.y = 74;
	button_chip_10000.alpha = 0.5; button_chip_10000.y = 68;
	button_chip_100000.alpha = 0.5; button_chip_100000.y = 62;
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
	button_chip_100.alpha = 0.5; button_chip_100.y = 80;
	button_chip_1000.alpha  = 1.0; button_chip_1000.y = 64;
	button_chip_10000.alpha = 0.5; button_chip_10000.y = 68;
	button_chip_100000.alpha = 0.5; button_chip_100000.y = 62;
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
	button_chip_100.alpha = 0.5; button_chip_100.y = 80;
	button_chip_1000.alpha  = 0.5; button_chip_1000.y = 74;
	button_chip_10000.alpha = 1.0; button_chip_10000.y = 58;
	button_chip_100000.alpha = 0.5; button_chip_100000.y = 62;
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
	button_chip_100.alpha = 0.5; button_chip_100.y = 80;
	button_chip_1000.alpha = 0.5; button_chip_1000.y = 74;
	button_chip_10000.alpha= 0.5; button_chip_10000.y = 68;
	button_chip_100000.alpha= 1.0; button_chip_100000.y = 52;
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
	var tip1 = labelWithText("欢迎您来到"+ gGameName + "！", 20,"#ffffff");
	layer_guide.addChild(tip1);
	tip1.x = 400; tip1.y = 350;
	var tip2 = labelWithText("接下来将为您进行简单的游戏介绍！", 20,"#ffffff");
	layer_guide.addChild(tip2);
	tip2.x = 400; tip2.y = 380;
	var btn1 = labelWithText("我是老手不需要！", 20,"#cccccc");
	layer_guide.addChild(btn1);
	btn1.x = 470; btn1.y = 450;
	btn1.addEventListener(LMouseEvent.MOUSE_DOWN, onCancleGuide);
	var tip3 = labelWithText("_______________", 20,"#cccccc");
	layer_guide.addChild(tip3);
	tip3.x = 470; tip3.y = 450;
	var btn2 = spriteWithImage(IMG_HELP0);
	layer_guide.addChild(btn2);
	btn2.x = 480; btn2.y = 530;
	btn2.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueGuide2);
	var tip4 = labelWithText("开始引导", 20,"#333333");
	layer_guide.addChild(tip4);
	tip4.x = 500; tip4.y = 540;
	
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
	tip1.x = 400; tip1.y = 350;
	var tip2 = labelWithText("请开启您精彩的竞猜之旅吧！", 20,"#ffffff");
	layer_guide.addChild(tip2);
	tip2.x = 400; tip2.y = 380;
	var btn1 = labelWithText("重新观看！", 20,"#cccccc");
	layer_guide.addChild(btn1);
	btn1.x = 500; btn1.y = 450;
	btn1.addEventListener(LMouseEvent.MOUSE_DOWN, onContinueGuide2);
	var tip3 = labelWithText("_______", 20,"#cccccc");
	layer_guide.addChild(tip3);
	tip3.x = 500; tip3.y = 450;
	var btn2 = spriteWithImage(IMG_HELP0);
	layer_guide.addChild(btn2);
	btn2.x = 480; btn2.y = 530;
	btn2.addEventListener(LMouseEvent.MOUSE_DOWN, onCancleGuide);
	var tip4 = labelWithText("回到游戏", 20,"#333333");
	layer_guide.addChild(tip4);
	tip4.x = 500; tip4.y = 540;
}


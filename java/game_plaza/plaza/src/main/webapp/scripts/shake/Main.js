
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
var SCREEN_WIDTH = 640;//
var SCREEN_HEIGHT = 1003;//window.innerHeight * SCREEN_WIDTH / window.innerWidth;
LInit(1000/50,"huanleyaoyiyao",SCREEN_WIDTH,SCREEN_HEIGHT,main);
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
			imgData.push({name : "sound_bg",path : global_util.getAppPath()+"/shake/medias/bg.mp3",type:"sound"});
			imgData.push({name : "sound_click",path : global_util.getAppPath()+"/shake/medias/click.mp3",type:"sound"});
			imgData.push({name : "sound_in",path : global_util.getAppPath()+"/shake/medias/in.mp3",type:"sound"});
			imgData.push({name : "sound_out",path : global_util.getAppPath()+"/shake/medias/out.mp3",type:"sound"});
			imgData.push({name : "sound_roulette",path : global_util.getAppPath()+"/shake/medias/roulette.mp3",type:"sound"});
			imgData.push({name : "sound_select",path : global_util.getAppPath()+"/shake/medias/select.mp3",type:"sound"});
			imgData.push({name : "sound_win",path : global_util.getAppPath()+"/shake/medias/win.mp3",type:"sound"});
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
	layer_msg_box_bg = new LSprite();
	baseLayer.addChild(layer_msg_box_bg);
	layer_msg_box_bg.graphics.drawRect(0, "#0e2f50", [0, 0, SCREEN_WIDTH,SCREEN_HEIGHT], false,  "#0e2f50");
	setSelfChips('');	
	connectServer();
	initSound(playSoundBG);	

	var myTimer = new LTimer(10000, 1000000000);
	myTimer.addEventListener(LTimerEvent.TIMER, requestTick);
    myTimer.start();
}

function connectServer(){
	connect(gServerAddress);
    //openLottery(1,2,3);
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
	layer_top_background = new LSprite();
	layer_background.addChild(layer_top_background);
	setMasterInfo("", 0, 0, 0);
	setOpenTime(0);
	setLastResult(1, 2, 3, 1);
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
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuMusicBgClicked);

	var text = "音乐：" + ((layer_sound_mgr && layer_sound_mgr.getBgPlay()) ? '开' : '关');
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;

	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 170;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuMusicClicked);	
	var text = "音效：" + ((layer_sound_mgr && layer_sound_mgr.getMusicPlay()) ? '开' : '关');
	var itemText1 = labelWithText(text, 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;

	var item1 = new LSprite();
	layer_menu_items.addChild(item1);
	item1.graphics.drawRect(1, "#ffffff", [0, 0, 120, 44], true,  "#395384");
	item1.x = 465; item1.y = 220;
	item1.addEventListener(LMouseEvent.MOUSE_DOWN, onMenuItem1Click);	
	var itemText1 = labelWithText("返回大厅", 24, "#ffffff");
	item1.addChild(itemText1);
	itemText1.x = 10; itemText1.y = 10;
}

function onMenuHelperClicked(event){
    showHelper();
}

function onClearEvent(event){
	layer_clear_event.remove();
	if (layer_menu_items != null){
		layer_menu_items.remove();
		layer_menu_items = null;
	}
}
function onMenuItem1Click(event){	var wxid = document.getElementById("wxid").value;	var chnno =document.getElementById("chnno").value;	self.location = global_util.getAppPath()+"/game_center/"+wxid+"/"+chnno;}

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

var layer_master_background = null;
function setMasterInfo(name, chips, score, count){
	if (layer_master_background != null){
		layer_master_background.remove();
	}
	layer_master_background = spriteWithImage(IMG_YIY_TOP_USER_INFO);
	layer_top_background.addChild(layer_master_background);
	layer_master_background.x = 10; layer_master_background.y = 7;
	
	var layer_name_tip = labelWithText("庄家：", 20, "#75daf0");
	layer_master_background.addChild(layer_name_tip);
	layer_name_tip.x = 15; layer_name_tip.y = 10;
	
	var layer_chips_tip = labelWithText("财富：", 20, "#75daf0");
	layer_master_background.addChild(layer_chips_tip);
	layer_chips_tip.x = 15; layer_chips_tip.y = 45;
	
	var layer_score_tip = labelWithText("成绩：", 20, "#75daf0");
	layer_master_background.addChild(layer_score_tip);
	layer_score_tip.x = 15; layer_score_tip.y = 80;
	
	var layer_count_tip = labelWithText("连庄：", 20, "#75daf0");
	layer_master_background.addChild(layer_count_tip);
	layer_count_tip.x = 15; layer_count_tip.y = 115;
	
	var layer_name = labelWithText(name, 20, "#ffedd9");
	layer_master_background.addChild(layer_name);
	layer_name.x = 75; layer_name.y = 9;
	
	var outChips = splitNumber(chips);
	var layer_chips = labelWithText(outChips, 20, "#ffd368");
	layer_master_background.addChild(layer_chips);
	layer_chips.x = 75; layer_chips.y = 45;
	
	var outScore = splitNumber(score);
	var layer_score;
	if (score >= 0 ){
		layer_score = labelWithText(outScore, 20, "#ff0000");
	}
	else {
		layer_score = labelWithText(outScore, 20, "#00ff00");
	}
	layer_master_background.addChild(layer_score);
	layer_score.x = 75; layer_score.y = 81;
	
	var layer_count = labelWithText(count, 20, "#27f7ff");
	layer_master_background.addChild(layer_count);
	layer_count.x = 75; layer_count.y = 115;


}
//设置开奖时间
var layer_open_time_background = null;
function setOpenTime(iValue){
	if (layer_open_time_background != null){
		layer_open_time_background.remove();
	}
	layer_open_time_background = spriteWithImage(IMG_YIY_TOP_TIMER_STATUS);
	layer_top_background.addChild(layer_open_time_background);
	layer_open_time_background.x = 405; layer_open_time_background.y = 5;
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
	var layer_open_time_tip = labelWithText(tip, 20, "#75daf0");
	layer_open_time_background.addChild(layer_open_time_tip);
	layer_open_time_tip.x = 12;
	layer_open_time_tip.y = 12;
	var layer_open_time = labelWithText(content, 25, "#ecbe5a");
	layer_open_time_background.addChild(layer_open_time);
	layer_open_time.x = 60;
	layer_open_time.y = 35;
}
//设置上期开奖结果
var layer_last_result_background = null;
var dice_1_pic = null;
var dice_2_pic = null;
var dice_3_pic = null;
function setLastResult(result_1, result_2, result_3, open_index){
	if (layer_last_result_background != null){
		layer_last_result_background.remove();
	}
	layer_last_result_background = spriteWithImage(IMG_YIY_TOP_LAST_DICE_INFO);
	layer_top_background.addChild(layer_last_result_background);
	layer_last_result_background.x = 405; layer_last_result_background.y = 70;
	layer_last_result_background.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectHistoryResult);
	
	var layer_last_result_tip = labelWithText("上期: ", 20, "#75daf0");
	layer_last_result_background.addChild(layer_last_result_tip);
	layer_last_result_tip.x = 20; layer_last_result_tip.y = 15;

    var layer_name_tip = labelWithText(String(result_1+result_2+result_3), 25, "#ff4d30");
    layer_last_result_background.addChild(layer_name_tip);
    layer_name_tip.x = 69; layer_name_tip.y = 13;
    // 设置结果
    getDicePicFromLotteryResult(result_1, result_2, result_3, dice_1_pic, dice_2_pic, dice_3_pic);

    // 设置大小豹子
    setLastResultTypeStr(result_1, result_2, result_3);

	var layer_history_tip = labelWithText("往期", 20, "#75daf0");
	layer_last_result_background.addChild(layer_history_tip);
	layer_history_tip.x = 135; layer_history_tip.y = 15;
	
	
	var layer_history_icon = spriteWithImage(IMG_HISTORY_DRAG_ICON);
	layer_last_result_background.addChild(layer_history_icon);
	layer_history_icon.x = 185; layer_history_icon.y = 20;
	layer_history_icon.addEventListener(LMouseEvent.MOUSE_DOWN, onSelectHistoryResult);
	
}
//查看往期开奖结果
function onSelectHistoryResult(event){
	doQueryOpenHistory();
}
function onMasterListClick(event){
	doQueryMasterList();	
}

function onRequestMaster(event){
    if (master_state_up == false){
        doRequestMaster(true);
    }
    else {
        doRequestMaster(false);
    }
    playSoundWithQuestMaster();
}
/******************************************************************************************************
* 中心区域
*******************************************************************************************************/
var layer_game_background = null;
var now_gam_lab_type = "1";     // 赌盘类型
var layer_now_gam_lab = null;       // 赌盘
var switch_button_outlet = null;    // 按钮边框

var layer_now_open_word = null;    // 开启文字
var layer_now_close_word = null;    // 关闭文字
var layer_Switch_button_2 = null;    // 切换按钮
var layer_lab_yilou_tips = null;        // 赌盘遗漏标签
function initCenter(){
	layer_game_background = spriteWithImage(IMG_YIY_CENTER_BACKGROUND);
	layer_background.addChild(layer_game_background);
	layer_game_background.y = 145;

	//我要上庄
	setMaster();
	//我要取款
	var layer_get_chips = spriteWithImage(IMG_YIY_BANK_BUTTON);
	layer_game_background.addChild(layer_get_chips);
	layer_get_chips.x = SCREEN_WIDTH - imgSize[IMG_YIY_BANK_BUTTON].w - 10; layer_get_chips.y = 550;
    layer_get_chips.addEventListener(LMouseEvent.MOUSE_DOWN, onGetChipsClicked);

    // 美女
    var layer_lady_body = spriteWithImage(IMG_YIY_LADY_BODY);
    layer_game_background.addChild(layer_lady_body);
    layer_lady_body.y = -140;
    layer_lady_body.x = 213;
    layer_lady_body.addEventListener(LMouseEvent.MOUSE_DOWN, onMasterListClick);

    // 切换按钮
    switch_button_outlet = spriteWithImage(IMG_YIY_SWITCH_BUTTON_OUTLET);
    layer_game_background.addChild(switch_button_outlet);
    switch_button_outlet.y = 520;
    switch_button_outlet.x = 229;
    // 设置状态
    clearChipsLayer();
    for (var i = IDX_POINT_SMALL; i <= IDX_APPEAR_6; i++){
        array_curr_total_set_chips[i] = 0;
        array_curr_my_set_chips[i] = 0;
    }

    setSwitchButton_type0();
    // 聊天
	setChatMsg("", "");
}
var layer_set_master = null;
function setMaster(){
	if (layer_set_master != null){
		layer_set_master.remove();
	}
	if (master_state_up == false){
		layer_set_master = spriteWithImage(IMG_YIY_UP_MASTER_BUTTON);
	}
	else {
		layer_set_master = spriteWithImage(IMG_YIY_DOWN_MASTER_BUTTON);
	}
    layer_set_master.addEventListener(LMouseEvent.MOUSE_DOWN, onRequestMaster);
	layer_game_background.addChild(layer_set_master);
	layer_set_master.x = 10; layer_set_master.y = 550;
}

//下注金币
function onSetChip(event){
    // 获取点击位置
    var index = 0;
    posX = event.offsetX;
    posY = event.offsetY;
    // 判断位置
    if(posX >= RANGE_OFFSET_X_MIN && posX <= RANGE_OFFSET_X_MAX){
        if(now_gam_lab_type == "1"){
            if(posY >= RANGE_OFFSET_Y_BS1_MIN && posY <= RANGE_OFFSET_Y_BS1_MAX){
                // 大小 豹子通选
                if(posX < RANGE_OFFSET_X_2){
                    index = IDX_POINT_SMALL;
                }else if(posX < RANGE_OFFSET_X_4){
                    index = IDX_SAME_THREE_ALL;
                }else{
                    index = IDX_POINT_BIG;
                }
            }else if(posY >= RANGE_OFFSET_Y_THREE2_MIN && posY <= RANGE_OFFSET_Y_THREE2_MAX){
                if(posX < RANGE_OFFSET_X_3){
                    if(posX < RANGE_OFFSET_X_1){
                        index = IDX_SAME_THREE_ONE_1;
                    }else if(posX < RANGE_OFFSET_X_2){
                        index = IDX_SAME_THREE_ONE_2;
                    }else{
                        index = IDX_SAME_THREE_ONE_3;
                    }
                }else{
                    if(posX < RANGE_OFFSET_X_4){
                        index = IDX_SAME_THREE_ONE_4;
                    }else if(posX < RANGE_OFFSET_X_5){
                        index = IDX_SAME_THREE_ONE_5;
                    }else{
                        index = IDX_SAME_THREE_ONE_6;
                    }
                }
            }else if(posY >= RANGE_OFFSET_Y_TWO3_MIN && posY <= RANGE_OFFSET_Y_TWO3_MAX){
                if(posX < RANGE_OFFSET_X_3){
                    if(posX < RANGE_OFFSET_X_1){
                        index = IDX_SAME_TWO_ONE_1;
                    }else if(posX < RANGE_OFFSET_X_2){
                        index = IDX_SAME_TWO_ONE_2;
                    }else{
                        index = IDX_SAME_TWO_ONE_3;
                    }
                }else{
                    if(posX < RANGE_OFFSET_X_4){
                        index = IDX_SAME_TWO_ONE_4;
                    }else if(posX < RANGE_OFFSET_X_5){
                        index = IDX_SAME_TWO_ONE_5;
                    }else{
                        index = IDX_SAME_TWO_ONE_6;
                    }
                }
            }else{
                return;
            }
        }else {
            if (posY >= RANGE_OFFSET_Y_COUNT4_MIN && posY <= RANGE_OFFSET_Y_COUNT4_MID) {
                if(posX < RANGE_OFFSET_X_2_3){
                    if(posX < RANGE_OFFSET_X_2_1){
                        index = IDX_COUNT_4;
                    }else if(posX < RANGE_OFFSET_X_2_2){
                        index = IDX_COUNT_5;
                    }else{
                        index = IDX_COUNT_6;
                    }
                }else{
                    if(posX < RANGE_OFFSET_X_2_4){
                        index = IDX_COUNT_7;
                    }else if(posX < RANGE_OFFSET_X_2_5){
                        index = IDX_COUNT_8;
                    }else if(posX < RANGE_OFFSET_X_2_6){
                        index = IDX_COUNT_9;
                    }else{
                        index = IDX_COUNT_10;
                    }
                }
            } else if(posY >= RANGE_OFFSET_Y_COUNT4_MID && posY <= RANGE_OFFSET_Y_COUNT4_MAX){
                if(posX < RANGE_OFFSET_X_2_3){
                    if(posX < RANGE_OFFSET_X_2_1){
                        index = IDX_COUNT_17;
                    }else if(posX < RANGE_OFFSET_X_2_2){
                        index = IDX_COUNT_16;
                    }else{
                        index = IDX_COUNT_15;
                    }
                }else{
                    if(posX < RANGE_OFFSET_X_2_4){
                        index = IDX_COUNT_14;
                    }else if(posX < RANGE_OFFSET_X_2_5){
                        index = IDX_COUNT_13;
                    }else if(posX < RANGE_OFFSET_X_2_6){
                        index = IDX_COUNT_12;
                    }else{
                        index = IDX_COUNT_11;
                    }
                }
            }else if (posY >= RANGE_OFFSET_Y_APPEAR5_MIN && posY <= RANGE_OFFSET_Y_APPEAR5_MAX) {
                if(posX < RANGE_OFFSET_X_3){
                    if(posX < RANGE_OFFSET_X_1){
                        index = IDX_APPEAR_1;
                    }else if(posX < RANGE_OFFSET_X_2){
                        index = IDX_APPEAR_2;
                    }else{
                        index = IDX_APPEAR_3;
                    }
                }else{
                    if(posX < RANGE_OFFSET_X_4){
                        index = IDX_APPEAR_4;
                    }else if(posX < RANGE_OFFSET_X_5){
                        index = IDX_APPEAR_5;
                    }else{
                        index = IDX_APPEAR_6;
                    }
                }
            }else{
                return;
            }
        }
    }else{
        return;
    }

    if(index == 0){
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
function onGetChipsClicked(){		//进入充值界面	var wxid = document.getElementById("wxid").value;	self.location = global_util.getAppPath()+"/shop/"+wxid;//hecm
//    if (curr_game_state != ROOM_STATE_WAITING_FOR_CACLULATE &&//
//        curr_game_state != ROOM_STATE_WAITING_FOR_SERVER){//
//        playSoundWithBank();//
//        showBank();//
//    }else {//
//        msgBox("提示：【开奖】和【结算】状态不能取款操作！");//
//    }
}

var LayerOpenResult = null;
var layer_mask;     // 黑底层
var Result_1_end;
var Result_2_end;
var Result_3_end;
function openLottery(Result_1, Result_2, Result_3){
    if(LayerOpenResult != null){
        LayerOpenResult.remove();
        LayerOpenResult = null;
    }
    if(layer_lab_yilou_tips != null){
        layer_lab_yilou_tips.remove();
        layer_lab_yilou_tips = null;
    }
    LayerOpenResult = new LSprite();
    layer_background.addChild(LayerOpenResult);

    layer_mask = new LSprite();
    LayerOpenResult.addChild(layer_mask);
    layer_mask.graphics.drawRect(1, "#000000", [0, 0, SCREEN_WIDTH, SCREEN_HEIGHT], true, "#000000");
    layer_mask.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);
    layer_mask.alpha = 0;

    // 黑底
    Result_1_end = Result_1;
    Result_2_end = Result_2;
    Result_3_end = Result_3;

    LayerOpenResult.addEventListener(LEvent.ENTER_FRAME, onResultFrame);
    //ShowRollPad();
}

function onResultFrame(){
    if (layer_mask !== null){
        if (layer_mask.alpha < 0.8){
            layer_mask.alpha += 0.05;
        }
        else{
            LayerOpenResult.removeAllEventListener();
             //显示摇奖盘
            var timerRoll = new LTimer(1000, 1);
            timerRoll.addEventListener(LTimerEvent.TIMER, ShowRollPad);
            timerRoll.start();
        }
    }
}

// 显示一格色子
var have_rolling_dice_num = 0;       // 当前已转动过的色子数量
function ShowRollingDice(){
    have_rolling_dice_num ++;
    var playerLayer = new LSprite();
    LayerOpenResult.addChild(playerLayer);
    //初始化动画属性
    initAnimate(playerLayer);
}

var speed_1 = DICE_1_SPEED_INIT;    // 初始速度 之后++
var _speed_1 = 0;
var acc_1 = DICE_1_ACC;      // 走x Frame +1速度
var _acc_1 = 0;      // 帧数记录

var speed_2 = DICE_2_SPEED_INIT;    // 初始速度 之后++
var _speed_2 = 0;
var acc_2 = DICE_2_ACC;      // 走x Frame +1速度
var _acc_2 = 0;      // 帧数记录

var speed_3 = DICE_3_SPEED_INIT;    // 初始速度 之后++
var _speed_3 = 0;
var acc_3 = DICE_3_ACC;      // 走x Frame +1速度
var _acc_3 = 0;      // 帧数记录
function onframe(){
    //控制走帧的速度
    //_speed自增不到speed，不动，继续自增
    //if(_speed++<speed){
    //    return ;
    //}
    ////等于speed的时候，重置，animate走一帧
    //_speed = 0;

    if(animate_dice_1 != null && STOP_SPEED_1 > _speed_1){
        if(_speed_1++>speed_1){
            _acc_1++;
            _speed_1 = 0;
            if(_acc_1 == acc_1){
                _acc_1 = 0;
                speed_1 ++;
            }
            animate_dice_1.onframe();
            playSoundWithRoulette();
        }
    }else if(STOP_SPEED_1 == _speed_1){
        // 关闭动画 显示数字
        closeAnimate1();
        showDiceResult1();
    }

    if(animate_dice_2 != null && STOP_SPEED_2 > _speed_2){
        if(_speed_2++>speed_2){
            _acc_2++;
            _speed_2 = 0;
            if(_acc_2 == acc_2){
                _acc_2 = 0;
                speed_2 ++;
            }
            animate_dice_2.onframe();
            playSoundWithRoulette();
        }
    }else if(STOP_SPEED_2 == _speed_2){
        closeAnimate2();
        showDiceResult2();
    }

    if(animate_dice_3 != null && STOP_SPEED_3 > _speed_3){
        if(_speed_3++>speed_3){
            _acc_3++;
            _speed_3 = 0;
            if(_acc_3 == acc_3){
                _acc_3 = 0;
                speed_3 ++;
            }
            animate_dice_3.onframe();
            playSoundWithRoulette();
        }
    }else if(STOP_SPEED_3 == _speed_3){
        closeAnimate3();
        showDiceResult3();

        // 显示文字描述
        showResultLab();
        have_rolling_dice_num = 0;

        var timerClose = new LTimer(3000, 1);
        timerClose.addEventListener(LTimerEvent.TIMER, CloseLayerOpen);
        timerClose.start();
        stopSoundWithRoulette();
        playSoundWithResult();
    }
}

function CloseLayerOpen(){
    LayerOpenResult.removeAllEventListener();
    ShowWin();
}

var Result_Img_Array = [0, IMG_YIY_DICE_RESULT_1,IMG_YIY_DICE_RESULT_2,IMG_YIY_DICE_RESULT_3,
    IMG_YIY_DICE_RESULT_4,IMG_YIY_DICE_RESULT_5,IMG_YIY_DICE_RESULT_6];
//关闭动画
function closeAnimate1(){
    animate_dice_1.remove();
    animate_dice_1 = null;
    speed_1 = DICE_1_SPEED_INIT;    // 初始速度 之后++
    _speed_1 = 0;
    acc_1 = DICE_1_ACC;      // 走x Frame +1速度
    _acc_1 = 0;      // 帧数记录
}
// 显示结果色子
function showDiceResult1(){
    var Result1_Dice_Img = spriteWithImage(Result_Img_Array[Result_1_end]);
    LayerOpenResult.addChild(Result1_Dice_Img);
    Result1_Dice_Img.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w);
    Result1_Dice_Img.y = 340;
}

//关闭动画
function closeAnimate2(){
    animate_dice_2.remove();
    animate_dice_2 = null;
    speed_2 = DICE_2_SPEED_INIT;    // 初始速度 之后++
    _speed_2 = 0;
    acc_2 = DICE_2_ACC;      // 走x Frame +1速度
    _acc_2 = 0;      // 帧数记录
}
// 显示结果色子
function showDiceResult2(){
    var Result2_Dice_Img = spriteWithImage(Result_Img_Array[Result_2_end]);
    LayerOpenResult.addChild(Result2_Dice_Img);
    Result2_Dice_Img.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w) - 90;
    Result2_Dice_Img.y = 500;
}

//关闭动画
function closeAnimate3(){
    animate_dice_3.remove();
    animate_dice_3 = null;
    speed_3 = DICE_3_SPEED_INIT;    // 初始速度 之后++
    _speed_3 = 0;
    acc_3 = DICE_3_ACC;      // 走x Frame +1速度
    _acc_3 = 0;      // 帧数记录
}
// 显示结果色子
function showDiceResult3(){
    var Result3_Dice_Img = spriteWithImage(Result_Img_Array[Result_3_end]);
    LayerOpenResult.addChild(Result3_Dice_Img);
    Result3_Dice_Img.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w) + 90;
    Result3_Dice_Img.y = 500;
}

function ShowRollPad(){
    var tileResultLab = spriteWithImage(IMG_YIY_TITLE_RESULT);
    LayerOpenResult.addChild(tileResultLab);
    tileResultLab.x = convertToScreenCenterX(imgSize[IMG_YIY_TITLE_RESULT].w);
    tileResultLab.y = 10;

    var circlePadLayer = spriteWithImage(IMG_YIY_CIRCLE_PAD);
    LayerOpenResult.addChild(circlePadLayer);
    circlePadLayer.x = convertToScreenCenterX(imgSize[IMG_YIY_CIRCLE_PAD].w);
    circlePadLayer.y = 300;

    ShowRollingDice();
    ShowRollingDice();
    ShowRollingDice();
    //进入图之后，按帧速率调用onframe函数
    LayerOpenResult.addEventListener(LEvent.ENTER_FRAME,onframe);
    // 显示第一个 过n秒后显示第二个
    //var timerRoll_Dice_1 = new LTimer(1000, 1);
    //timerRoll_Dice_1.addEventListener(LTimerEvent.TIMER, ShowRollingDice);
    //timerRoll_Dice_1.start();
//
    //var timerRoll_Dice_2 = new LTimer(3000, 1);
    //timerRoll_Dice_2.addEventListener(LTimerEvent.TIMER, ShowRollingDice);
    //timerRoll_Dice_2.start();
//
    //var timerRoll_Dice_3 = new LTimer(5000, 1);
    //timerRoll_Dice_3.addEventListener(LTimerEvent.TIMER, ShowRollingDice);
    //timerRoll_Dice_3.start();



    //var Result_Img_Array = [0, IMG_YIY_DICE_RESULT_1,IMG_YIY_DICE_RESULT_2,IMG_YIY_DICE_RESULT_3,
    //    IMG_YIY_DICE_RESULT_4,IMG_YIY_DICE_RESULT_5,IMG_YIY_DICE_RESULT_6];

    //var Result1_Dice_Img = spriteWithImage(Result_Img_Array[Result_1_end]);
    //LayerOpenResult.addChild(Result1_Dice_Img);
    //Result1_Dice_Img.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w);
    //Result1_Dice_Img.y = 340;
//
    //var Result2_Dice_Img = spriteWithImage(Result_Img_Array[Result_2_end]);
    //LayerOpenResult.addChild(Result2_Dice_Img);
    //Result2_Dice_Img.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w) - 90;
    //Result2_Dice_Img.y = 500;
//
    //var Result3_Dice_Img = spriteWithImage(Result_Img_Array[Result_3_end]);
    //LayerOpenResult.addChild(Result3_Dice_Img);
    //Result3_Dice_Img.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w) + 90;
    //Result3_Dice_Img.y = 500;
//
    //noticeBox("结果:" + Result_1_end + ","+Result_2_end+","+Result_3_end, "#00ff00");
}


function ShowWin(){
	//进行下一回合
	curr_game_state = ROOM_STATE_WAITING_FOR_SERVER;
	setLastResult(curr_result.result_1_num, curr_result.result_2_num, curr_result.result_3_num, curr_result.open_index);
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

    LayerOpenResult.remove();
    LayerOpenResult = null;
}


//function onGameFrame(){
//    /**
//     * 开奖时背景渐灰
//     */
//    if (layer_mask !== null){
//        if (layer_mask.alpha < 0.8){
//            layer_mask.alpha += 0.1;
//        }
//        else{
//            is_ready_to_rotate = true;
//        }
//    }
//
//	if (is_play_result){ //压注界面的结果显示也完了
//		is_play_result = false;
//		//进行下一回合
//		curr_game_state = ROOM_STATE_WAITING_FOR_SERVER;
//		setLastResult(curr_result.result_1_num, curr_result.result_2_num, curr_result.result_3_num, curr_result.open_index);
//		//结算提示
//		var masterScore = parseInt(curr_result.master_score);
//		var selfScore = parseInt(curr_result.win_num) - parseInt(curr_result.setted_num);
//		if (curr_self_id == curr_master_id){
//			selfScore = masterScore;
//			curr_self_chips += selfScore;
//		}
//		else {
//			curr_self_chips += curr_result.win_num;
//		}
//		setSelfChips(curr_self_chips);
//		var color1 = "#ff0000";
//		if (masterScore < 0){
//			color1 = "#00ff00";
//		}
//		var color2 = "#ff0000";
//		if (selfScore < 0){
//			color2 = "#00ff00";
//		}
//		noticeBoxEx("庄家："+splitNumber(masterScore), color1,
//			"本家："+splitNumber(selfScore), color2);
//	}
//}

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
	num = Math.floor(num);
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
		//case 1000:
		//	layer_chip_selected.x = 135; layer_chip_selected.y = 0;
		//	break;
		//case 10000:
		//	layer_chip_selected.x = 225; layer_chip_selected.y = 0;
		//	break;
		//case 100000:
		//	layer_chip_selected.x = 315; layer_chip_selected.y = 0;
		//	break;
		//case 1000000:
		//	layer_chip_selected.x = 405; layer_chip_selected.y = 0;
		//	break;
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


// 设置赌盘界面
function setGambleLab(){
    if(layer_now_gam_lab != null){
        layer_now_gam_lab.remove();
        layer_now_gam_lab = null;
    }
    if(now_gam_lab_type == "1"){
        layer_now_gam_lab = spriteWithImage(IMG_YIY_BIG_SMALL_PAGE);
    }else{
        layer_now_gam_lab = spriteWithImage(IMG_YIY_COUNT_PAGE);
    }

    layer_game_background.addChild(layer_now_gam_lab);
    layer_now_gam_lab.y = 110;
    layer_now_gam_lab.x = 15;
    layer_now_gam_lab.addEventListener(LMouseEvent.MOUSE_DOWN, onSetChip);
    //layer_now_gam_lab.addEventListener(LEvent.ENTER_FRAME, onGameFrame);
    // 设置已押注点
    clearChipsLayer();
    showSetChips(now_gam_lab_type);
    showYiLouTips();
}

// 设置切换按钮
function setSwitchButton_type0(e){
    if(layer_now_open_word != null){
        layer_now_open_word.remove();
        layer_now_open_word = null;
    }
    if(layer_Switch_button_2 != null){
        layer_Switch_button_2.remove();
        layer_Switch_button_2 = null;
    }
    if(layer_now_close_word != null){
        layer_now_close_word.remove();
        layer_now_close_word = null;
    }
    layer_now_open_word = spriteWithImage(IMG_YIY_BS_WORD_BLUE);
    switch_button_outlet.addChild(layer_now_open_word);
    layer_now_open_word.x = 22;
    layer_now_open_word.y = 17;

    layer_Switch_button_2 = new LButton(bitmapWithImage(IMG_YIY_SWITCH_BLOCK));
    switch_button_outlet.addChild(layer_Switch_button_2);
    layer_Switch_button_2.x = 88;
    layer_Switch_button_2.y = 6;

    layer_now_close_word = spriteWithImage(IMG_YIY_COUNT_WORD_BROWN);
    layer_Switch_button_2.addChild(layer_now_close_word);
    layer_now_close_word.x = 22;
    layer_now_close_word.y = 12;

    layer_Switch_button_2.addEventListener(LMouseEvent.MOUSE_DOWN, setSwitchButton_type2);
    // 赌盘更新
    now_gam_lab_type = "1";
    setGambleLab();
}

// 设置切换按钮
function setSwitchButton_type1(e){
    if(layer_now_open_word != null){
        layer_now_open_word.remove();
        layer_now_open_word = null;
    }
    if(layer_Switch_button_2 != null){
        layer_Switch_button_2.remove();
        layer_Switch_button_2 = null;
    }
    if(layer_now_close_word != null){
        layer_now_close_word.remove();
        layer_now_close_word = null;
    }
    layer_now_open_word = spriteWithImage(IMG_YIY_BS_WORD_BLUE);
    switch_button_outlet.addChild(layer_now_open_word);
    layer_now_open_word.x = 22;
    layer_now_open_word.y = 17;

    layer_Switch_button_2 = new LButton(bitmapWithImage(IMG_YIY_SWITCH_BLOCK));
    switch_button_outlet.addChild(layer_Switch_button_2);
    layer_Switch_button_2.x = 88;
    layer_Switch_button_2.y = 6;

    layer_now_close_word = spriteWithImage(IMG_YIY_COUNT_WORD_BROWN);
    layer_Switch_button_2.addChild(layer_now_close_word);
    layer_now_close_word.x = 22;
    layer_now_close_word.y = 12;

    layer_Switch_button_2.addEventListener(LMouseEvent.MOUSE_DOWN, setSwitchButton_type2);
    // 赌盘更新
    now_gam_lab_type = "1";
    playSoundWithChangeLab();
    setGambleLab();
}

// 设置切换按钮 按2
function setSwitchButton_type2(e){
    if(layer_now_open_word != null){
        layer_now_open_word.remove();
        layer_now_open_word = null;
    }
    if(layer_Switch_button_2 != null){
        layer_Switch_button_2.remove();
        layer_Switch_button_2 = null;
    }
    if(layer_now_close_word != null){
        layer_now_close_word.remove();
        layer_now_close_word = null;
    }

    layer_now_open_word = spriteWithImage(IMG_YIY_COUNT_WORD_BLUE);
    switch_button_outlet.addChild(layer_now_open_word);
    layer_now_open_word.x = 110;
    layer_now_open_word.y = 17;

    layer_Switch_button_2 = new LButton(bitmapWithImage(IMG_YIY_SWITCH_BLOCK));
    switch_button_outlet.addChild(layer_Switch_button_2);
    layer_Switch_button_2.x = 3;
    layer_Switch_button_2.y = 6;

    layer_now_close_word = spriteWithImage(IMG_YIY_BS_WORD_BROWN);
    layer_Switch_button_2.addChild(layer_now_close_word);
    layer_now_close_word.x = 22;
    layer_now_close_word.y = 12;

    layer_Switch_button_2.addEventListener(LMouseEvent.MOUSE_DOWN, setSwitchButton_type1);
    // 赌盘更新
    now_gam_lab_type = "2";
    playSoundWithChangeLab();
    setGambleLab();
}

// 显示已押注
function showSetChips(type){
    var index_min = IDX_POINT_SMALL;
    var index_max = IDX_SAME_TWO_ONE_6;
    if(type == "2"){
        index_min = IDX_COUNT_4;
        index_max = IDX_APPEAR_6;
    }
    for(var i = index_min; i <= index_max; i++) {
        var my_set_num = array_curr_my_set_chips[i];
        var total_set_num = array_curr_total_set_chips[i];
        if (total_set_num > 0){
            var layer_chips = new SChip(i);
            array_curr_chips_layer[i] = layer_chips;
            layer_now_gam_lab.addChild(layer_chips);
            layer_chips.addChips(total_set_num-my_set_num, false);
            if(my_set_num > 0){
                layer_chips.addChips(my_set_num, true);
            }
        }
    }
}

var animate_dice_1;
var animate_dice_2;
var animate_dice_3;
function initAnimate(playerLayer){
    var datas = [];
    var listChild = [];
    var Img_Dice_Rolling_list = [IMG_YIY_DICE_ROLLING_1, IMG_YIY_DICE_ROLLING_2, IMG_YIY_DICE_ROLLING_3, IMG_YIY_DICE_ROLLING_4];
    for (var i = 0; i < 4; i++) {
        datas.push(new LBitmapData(imglist[Img_Dice_Rolling_list[i]]));
        listChild.push({dataIndex : i, x : 0, y : 0, width : 142, height : 142, sx : 0, sy : 0});
    }
    get_animate_by_index(datas, listChild, playerLayer);
}

// 获取位置
function get_animate_by_index(datas, listChild, playerLayer){
    if(have_rolling_dice_num == 1){
        animate_dice_1 =new LAnimation(playerLayer,datas,[listChild]);
        animate_dice_1.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w);
        animate_dice_1.y = 340;
        animate_dice_1.setAction(0);
    }else if(have_rolling_dice_num == 2){
        animate_dice_2 =new LAnimation(playerLayer,datas,[listChild]);
        animate_dice_2.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w) - 90;
        animate_dice_2.y = 500;
        animate_dice_2.setAction(0);
    }else{
        animate_dice_3 =new LAnimation(playerLayer,datas,[listChild]);
        animate_dice_3.x = convertToScreenCenterX(imgSize[IMG_YIY_DICE_RESULT_1].w) + 90;
        animate_dice_3.y = 500;
        animate_dice_3.setAction(0);
    }
}

function showResultLab(){
    var layer_dice_num_result = labelWithText(Result_1_end+" "+Result_2_end+" "+Result_3_end+" ", 55, "#ffffff");
    LayerOpenResult.addChild(layer_dice_num_result);
    layer_dice_num_result.y = 180; layer_dice_num_result.x = 105;

    var layer_dice_count_result = labelWithText(String(Result_1_end+Result_2_end+Result_3_end), 55, "#f5863e");
    LayerOpenResult.addChild(layer_dice_count_result);
    layer_dice_count_result.y = 180; layer_dice_count_result.x = 300;


    var layer_result_type_result = labelWithText(getResultText(Result_1_end,Result_2_end,Result_3_end), 55, "#ec3839");
    LayerOpenResult.addChild(layer_result_type_result);
    layer_result_type_result.y = 180; layer_result_type_result.x = 430;
}

var yilou_tips_data = new Array(); //
function processShowYiLouTips(msg){
    yilou_tips_data = msg.yilou_pos_list;
    console.log(yilou_tips_data);
    showYiLouTips();
}
function showYiLouTips(){
    if(layer_lab_yilou_tips != null){
        layer_lab_yilou_tips.remove();
    }
    layer_lab_yilou_tips = new LSprite();
    layer_now_gam_lab.addChild(layer_lab_yilou_tips);
    for(i = 0; i < yilou_tips_data.length; i++){
        addYilouTips(yilou_tips_data[i])
    }
}
function addYilouTips(pos){
    if( (now_gam_lab_type == "1" && pos <= IDX_SAME_TWO_ONE_6) || (now_gam_lab_type == "2" && pos > IDX_SAME_TWO_ONE_6)) {
        var itemText1 = new SYilouTips(pos);
        layer_lab_yilou_tips.addChild(itemText1);
    }
}

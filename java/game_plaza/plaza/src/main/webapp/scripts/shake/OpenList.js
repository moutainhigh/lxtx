var layer_open_list_background = null;
var layer_zhkj = null;
var layer_wx = null;
var layer_sxtm = null;
var layer_open_list_selected = null;
var data_list = null;
var data_yilou = null;
function showOpenList(msg){
	if (layer_open_list_background != null){
		return;
	}
	data_list = msg.list;
	data_yilou = msg.yilou_data;
	layer_open_list_background = new LSprite();
	baseLayer.addChild(layer_open_list_background);
	layer_open_list_background.graphics.drawRect(1, "#000000", [0, 0, SCREEN_WIDTH, 765], true, "#000000");
	layer_open_list_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);
	//layer_open_list_background.alpha = 0.5;
	
	var layer_open_list_title = new LSprite();
	layer_open_list_background.addChild(layer_open_list_title);
	layer_open_list_title.graphics.drawRect(1, "#162737", [0, 0, SCREEN_WIDTH, 90], true, "#162737");
	layer_open_list_title.addEventListener(LMouseEvent.MOUSE_DOWN, onOpenListSelectedPage);
	
	layer_zhkj = labelWithText("综合开奖", 26, "#6fd5fb");
	layer_open_list_title.addChild(layer_zhkj);
	layer_zhkj.x = 38; layer_zhkj.y = 30;
	
	layer_wx = labelWithText("单骰", 26, "#455971");
	layer_open_list_title.addChild(layer_wx);
	layer_wx.x = 245; layer_wx.y = 30;
	
	layer_sxtm = labelWithText("和值&豹子&对子", 26, "#455971");
	layer_open_list_title.addChild(layer_sxtm);
	layer_sxtm.x = 400; layer_sxtm.y = 30;
	
	var layer_split = spriteWithImage(IMG_OPEN_LIST_2);
	layer_open_list_title.addChild(layer_split);
	layer_split.x = 180; layer_split.y = 23;
	
	var layer_split2 = spriteWithImage(IMG_OPEN_LIST_2);
	layer_open_list_title.addChild(layer_split2);
	layer_split2.x = 360; layer_split2.y = 23;
	
	layer_open_list_selected = spriteWithImage(IMG_OPEN_LIST_1);
	layer_open_list_title.addChild(layer_open_list_selected);
	layer_open_list_selected.x = 0; layer_open_list_selected.y = 83;
	
	var layer_split3 = spriteWithImage(IMG_OPEN_LIST_3);
	layer_open_list_title.addChild(layer_split3);
	layer_split3.x = 0; layer_split3.y = 90;
	
	var layer_close = spriteWithImage(IMG_OPEN_LIST_4);
	layer_open_list_background.addChild(layer_close);
	layer_close.x = 0; layer_close.y = 765;
	layer_close.addEventListener(LMouseEvent.MOUSE_DOWN, onOpenListClose);
	
	showZHKJPage(data_list);
}
var layer_zhkj_page = null;
var layer_wx_page = null;
var layer_sxtm_page = null;
function onOpenListClose(event){
	layer_open_list_background.remove();
	layer_open_list_background = null;
	layer_zhkj_page = null;
	layer_wx_page = null;
	layer_sxtm_page = null;
}
function closeAllPage(){
	if (layer_zhkj_page != null){
		layer_zhkj_page.remove();
		layer_zhkj_page = null;
	}
	if (layer_wx_page != null){
		layer_wx_page.remove();
		layer_wx_page = null;
	}
	if (layer_sxtm_page != null){
		layer_sxtm_page.remove();
		layer_sxtm_page = null;
	}
}
function onOpenListSelectedPage(event){
	if (event.offsetY > 90){
		return false;
	}
	closeAllPage();
	if (event.offsetX < 213){
		layer_zhkj.setColor("#6fd5fb");
		layer_wx.setColor("#455971");
		layer_sxtm.setColor("#455971");
		layer_open_list_selected.x = 0;
		showZHKJPage(data_list);
	}
	else if (event.offsetX < 426){
		layer_zhkj.setColor("#455971");
		layer_wx.setColor("#6fd5fb");
		layer_sxtm.setColor("#455971");
		layer_open_list_selected.x = 213;
        showOnePage(data_list);
	}
	else {
		layer_zhkj.setColor("#455971");
		layer_wx.setColor("#455971");
		layer_sxtm.setColor("#6fd5fb");
		layer_open_list_selected.x = 426;
		showYLPage(data_yilou);
	}
}
//////////////////////////////////////////////////////////////////////////////////
//
function showZHKJPage(list){
	layer_zhkj_page = new LSprite();
	layer_open_list_background.addChild(layer_zhkj_page);
	
	var layer_title = new LSprite();
	layer_zhkj_page.addChild(layer_title);
	layer_title.graphics.drawRect(1, "#162737", [0, 93, SCREEN_WIDTH, 90], true, "#162737");
	
	var layer_qishu = labelWithText("期数", 26, "#455971");
	layer_zhkj_page.addChild(layer_qishu);
	layer_qishu.x = 50; layer_qishu.y = 125;

	var layer_tian = labelWithText("点数", 26, "#455971");
	layer_zhkj_page.addChild(layer_tian);
	layer_tian.x = 200; layer_tian.y = 125;
	
	var layer_di = labelWithText("和值", 26, "#455971");
	layer_zhkj_page.addChild(layer_di);
	layer_di.x = 350; layer_di.y = 125;

	var layer_wuxing = labelWithText("大", 26, "#455971");
	layer_zhkj_page.addChild(layer_wuxing);
	layer_wuxing.x = 470; layer_wuxing.y = 125;

	var layer_shengxiao = labelWithText("小", 26, "#455971");
	layer_zhkj_page.addChild(layer_shengxiao);
	layer_shengxiao.x = 560; layer_shengxiao.y = 125;
	
	showZHKJList(list);
}

function showZHKJList(list){
	var lv = new LListView();
	layer_zhkj_page.addChild(lv);
	lv.x = 0;
	lv.y = 165;
	lv.maxPerLine = 1;
	lv.cellWidth = 640;
	lv.cellHeight = 60;
	lv.resize(640,600);
	lv.arrangement = LListView.Direction.Horizontal;
	lv.movement = LListView.Direction.Vertical;
	lv.graphics.drawRect(1, "#000000", [0, 0, lv.clipping.width,lv.clipping.height]);
	for (i = 0; i < list.length; i++){
		var color = "#1c324a";
		if (i % 2 === 1){
			color = "#223850";
		}
		var ele = eval("("+list[i]+")");
		lv.insertChildView(zhkjListItem(ele.open_index, color, ele.result_num_1, ele.result_num_2, ele.result_num_3));
	}
}

function zhkjListItem(index, color, result_1, result_2, result_3){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 60], true,color);
    var count = result_1+result_2+result_3;

	//期数
	var layer_index = labelWithText(String(index), 26, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 55; layer_index.y = 18;
    // 点数
    var dice_1_pic = null;
    var dice_2_pic = null;
    var dice_3_pic = null;
    open_getDicePicFromLotteryResult(child, result_1, result_2, result_3, dice_1_pic, dice_2_pic, dice_3_pic);

    // 和值
    var layer_count = labelWithText(String(result_1+result_2+result_3), 26, "#aad3e7");
    child.addChild(layer_count);
    layer_count.x = 350; layer_count.y = 18;

    if(count > 3 && count < 18){
        if(count > 10){
            // 大
            var layer_big = labelWithText("大", 26, "#aad3e7");
            child.addChild(layer_big);
            layer_big.x = 470; layer_big.y = 18;
        }else{
            // 小
            var layer_small = labelWithText("小", 26, "#aad3e7");
            child.addChild(layer_small);
            layer_small.x = 560; layer_small.y = 18;
        }
    }

	return child;
}
//////////////////////////////////////////////////////////////////////////////////
//
function showOnePage(list){
	layer_wx_page = new LSprite();
	layer_open_list_background.addChild(layer_wx_page);
	var layer_title = new LSprite();
	layer_wx_page.addChild(layer_title);
	layer_title.graphics.drawRect(1, "#162737", [0, 93, SCREEN_WIDTH, 90], true, "#162737");
	var layer_qishu = labelWithText("期数", 26, "#455971");
	layer_wx_page.addChild(layer_qishu);
	layer_qishu.x = 50; layer_qishu.y = 125;
	var layer_jin = labelWithText("1", 26, "#cb9b45");
	layer_wx_page.addChild(layer_jin);
	layer_jin.x = 160; layer_jin.y = 125;
	var layer_mu = labelWithText("2", 26, "#5f896f");
	layer_wx_page.addChild(layer_mu);
	layer_mu.x = 240; layer_mu.y = 125;
	var layer_shui = labelWithText("3", 26, "#5f896f");
	layer_wx_page.addChild(layer_shui);
	layer_shui.x = 320; layer_shui.y = 125;
	var layer_huo = labelWithText("4", 26, "#ad5750");
	layer_wx_page.addChild(layer_huo);
	layer_huo.x = 400; layer_huo.y = 125;
	var layer_tu = labelWithText("5", 26, "#524643");
	layer_wx_page.addChild(layer_tu);
	layer_tu.x = 480; layer_tu.y = 125;
    var layer_liu = labelWithText("6", 26, "#524643");
    layer_wx_page.addChild(layer_liu);
    layer_liu.x = 560; layer_liu.y = 125;
	showOneList(list);
}

function showOneList(list){
	var lv = new LListView();
	layer_wx_page.addChild(lv);
	lv.x = 0;
	lv.y = 165;
	lv.maxPerLine = 1;
	lv.cellWidth = 640;
	lv.cellHeight = 60;
	lv.resize(640,600);
	lv.arrangement = LListView.Direction.Horizontal;
	lv.movement = LListView.Direction.Vertical;
	lv.graphics.drawRect(1, "#000000", [0, 0, lv.clipping.width,lv.clipping.height]);

    for (i = 0; i < list.length; i++){
        var color = "#1c324a";
        if (i % 2 === 1){
            color = "#223850";
        }
        var ele = eval("("+list[i]+")");
		lv.insertChildView(wxListItem(ele.open_index, color,ele.result_num_1, ele.result_num_2, ele.result_num_3));
	}
}

var time_list = new Array();
function wxListItem(index, color, result_1, result_2, result_3){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 60], true,color);

    //期数
    var layer_index = labelWithText(String(index), 26, "#aad3e7");
    child.addChild(layer_index);
    layer_index.x = 60; layer_index.y = 18;
    time_list[1] = 0;
    time_list[2] = 0;
    time_list[3] = 0;
    time_list[4] = 0;
    time_list[5] = 0;
    time_list[6] = 0;
    time_list[parseInt(result_1)] = time_list[parseInt(result_1)]+1;
    time_list[parseInt(result_2)] = time_list[parseInt(result_2)]+1;
    time_list[parseInt(result_3)] = time_list[parseInt(result_3)]+1;

    //次数
    var layer_times_1 = labelWithText(String(time_list[1]), 26, "#aad3e7");
    child.addChild(layer_times_1);
    layer_times_1.x = 160; layer_times_1.y = 18;

    var layer_times_2 = labelWithText(String(time_list[2]), 26, "#aad3e7");
    child.addChild(layer_times_2);
    layer_times_2.x = 240; layer_times_2.y = 18;

    var layer_times_3 = labelWithText(String(time_list[3]), 26, "#aad3e7");
    child.addChild(layer_times_3);
    layer_times_3.x = 320; layer_times_3.y = 18;

    var layer_times_4 = labelWithText(String(time_list[4]), 26, "#aad3e7");
    child.addChild(layer_times_4);
    layer_times_4.x = 400; layer_times_4.y = 18;

    var layer_times_5 = labelWithText(String(time_list[5]), 26, "#aad3e7");
    child.addChild(layer_times_5);
    layer_times_5.x = 480; layer_times_5.y = 18;

    var layer_times_6 = labelWithText(String(time_list[6]), 26, "#aad3e7");
    child.addChild(layer_times_6);
    layer_times_6.x = 560; layer_times_6.y = 18;

	return child;
}
//////////////////////////////////////////////////////////////////////////////////
//

function showYLPage(data){
	layer_sxtm_page = new LSprite();
	layer_open_list_background.addChild(layer_sxtm_page);
	
	var layer_title = new LSprite();
	layer_sxtm_page.addChild(layer_title);
	layer_title.graphics.drawRect(1, "#162737", [0, 93, SCREEN_WIDTH, 90], true, "#162737");
	
	var layer_count_yl = labelWithText("和值遗漏值", 26, "#455971");
	layer_sxtm_page.addChild(layer_count_yl);
    layer_count_yl.x = 265; layer_count_yl.y = 115;

	var layer_title2 = new LSprite();
	layer_sxtm_page.addChild(layer_title2);
	layer_title2.graphics.drawRect(1, "#162737", [0, 400, SCREEN_WIDTH, 90], true, "#162737");
	
	var layer_baozi_yl = labelWithText("豹子遗漏值", 26, "#455971");
	layer_sxtm_page.addChild(layer_baozi_yl);
    layer_baozi_yl.x = 265; layer_baozi_yl.y = 430;

    var layer_title3 = new LSprite();
    layer_sxtm_page.addChild(layer_title3);
    layer_title3.graphics.drawRect(1, "#162737", [0, 590, SCREEN_WIDTH, 90], true, "#162737");

    var layer_duizi_yl = labelWithText("对子遗漏值", 26, "#455971");
    layer_sxtm_page.addChild(layer_duizi_yl);
    layer_duizi_yl.x = 265; layer_duizi_yl.y = 617;
	showYLList(data);
}

function showYLList(data){
	var lv = new LListView();
	layer_sxtm_page.addChild(lv);
	lv.x = 0;
	lv.y = 160;
	lv.maxPerLine = 7;
	lv.cellWidth = 640 / 7;
	lv.cellHeight = 120;
	lv.resize(640,240);
	lv.arrangement = LListView.Direction.Horizontal;
	lv.movement = LListView.Direction.Vertical;
	lv.graphics.drawRect(1, "#000000", [0, 0, lv.clipping.width,lv.clipping.height]);

	for (i = 4; i < 11; i++){
		var value = eval("("+data[i-4]+")");
		lv.insertChildView(ylListItem(i, value));
        //lv.insertChildView(ylListItem(i, 11));
	}

    var i2 = 7;
    for (i = 17; i >10; i--){
        //lv.insertChildView(ylListItem(i, 11));
        var value = eval("("+data[i2]+")");
        lv.insertChildView(ylListItem(i, value));
        i2 ++;
    }

	var lv2 = new LListView();
	layer_sxtm_page.addChild(lv2);
	lv2.x = 0;
	lv2.y = 490;
	lv2.maxPerLine = 6;
	lv2.cellWidth = 640 / 6;
	lv2.cellHeight = 100;
	lv2.resize(640,100);
	lv2.arrangement = LListView.Direction.Horizontal;
	lv2.movement = LListView.Direction.Vertical;
	lv2.graphics.drawRect(1, "#000000", [0, 0, lv2.clipping.width,lv2.clipping.height]);
	for (i = 1; i < 7; i++){
        //lv2.insertChildView(ylListItem2(i, 22));
		var value = eval("("+data[i+13]+")");
		lv2.insertChildView(ylListItem2(i, value));
	}

    var lv3 = new LListView();
    layer_sxtm_page.addChild(lv3);
    lv3.x = 0;
    lv3.y = 670;
    lv3.maxPerLine = 6;
    lv3.cellWidth = 640 / 6;
    lv3.cellHeight = 100;
    lv3.resize(640,100);
    lv3.arrangement = LListView.Direction.Horizontal;
    lv3.movement = LListView.Direction.Vertical;
    lv3.graphics.drawRect(1, "#000000", [0, 0, lv3.clipping.width,lv3.clipping.height]);
    for (i = 1; i < 7; i++){
        //lv3.insertChildView(ylListItem3(i, 33));
        var value = eval("("+data[i+19]+")");
        lv3.insertChildView(ylListItem3(i, value));
    }
}

// 和值遗漏
function ylListItem(i, index){	
	var child = new LListChildView();
	var layer1 = new LSprite();
	layer1.graphics.drawRect(1, "#1c324a", [0, 0, 640 / 7, 60], true, "#1c324a");
	child.addChild(layer1);

    //count
    var layer_count = labelWithText(String(i), 36, "#aad3e7");
    layer1.addChild(layer_count);
    layer_count.x = 30; layer_count.y = 18;

	var layer2 = new LSprite();
	layer2.graphics.drawRect(1, "#223850", [0, 60, 640 / 7, 60], true, "#223850");
	child.addChild(layer2);

	//期数
	var layer_index = labelWithText(String(index) + "期", 26, "#aad3e7");
    layer2.addChild(layer_index);
	layer_index.x = 15; layer_index.y = 75;
	return child;
}

// 豹子遗漏
function ylListItem2(i, index){	
	var child = new LListChildView();
	var layer1 = new LSprite();
	layer1.graphics.drawRect(1, "#1c324a", [0, 0, 640 / 6, 50], true, "#1c324a");
	child.addChild(layer1);

    //数字
    var layer_count = labelWithText(String(i), 36, "#aad3e7");
    layer1.addChild(layer_count);
    layer_count.x = 40; layer_count.y = 15;

	var layer2 = new LSprite();
	layer2.graphics.drawRect(1, "#223850", [0, 50, 640 / 6, 50], true, "#223850");
	child.addChild(layer2);

	// 期数
	var layer_index = labelWithText(String(index) + "期", 26, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 20; layer_index.y = 60;
	return child;
}

// 对子遗漏
function ylListItem3(i, index){
    var child = new LListChildView();
    var layer1 = new LSprite();
    layer1.graphics.drawRect(1, "#1c324a", [0, 0, 640 / 6, 50], true, "#1c324a");
    child.addChild(layer1);

    //数字
    var layer_count = labelWithText(String(i), 36, "#aad3e7");
    layer1.addChild(layer_count);
    layer_count.x = 40; layer_count.y = 15;


    var layer2 = new LSprite();
    layer2.graphics.drawRect(1, "#223850", [0, 50, 640 / 6, 50], true, "#223850");
    child.addChild(layer2);

    //期数
    var layer_index = labelWithText(String(index) + "期", 26, "#aad3e7");
    child.addChild(layer_index);
    layer_index.x = 20; layer_index.y = 60;
    return child;
}

function open_getDicePicFromLotteryResult(child, result1, result2, result3, dice_1_pic, dice_2_pic, dice_3_pic){
    if(dice_1_pic != null){
        dice_1_pic.remove();
        dice_1_pic = null;
    }
    if(dice_2_pic != null){
        dice_2_pic.remove();
        dice_2_pic = null;
    }
    if(dice_3_pic != null){
        dice_3_pic.remove();
        dice_3_pic = null;
    }
    var  map_array = [0, IMG_YIY_DICE_LOW_1, IMG_YIY_DICE_LOW_2, IMG_YIY_DICE_LOW_3, IMG_YIY_DICE_LOW_4, IMG_YIY_DICE_LOW_5, IMG_YIY_DICE_LOW_6];
    dice_1_pic = spriteWithImage(map_array[result1]);
    child.addChild(dice_1_pic);
    dice_1_pic.x = 180; dice_1_pic.y = 15;

    dice_2_pic = spriteWithImage(map_array[result2]);
    child.addChild(dice_2_pic);
    dice_2_pic.x = 215; dice_2_pic.y = 15;

    dice_3_pic = spriteWithImage(map_array[result3]);
    child.addChild(dice_3_pic);
    dice_3_pic.x = 250; dice_3_pic.y = 15;
}
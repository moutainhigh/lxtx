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
	var list = msg.list;
	data_list = list;
	data_yilou = msg.tema_yilou;
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
	layer_zhkj.x = 40; layer_zhkj.y = 30;
	
	layer_wx = labelWithText("五行", 26, "#455971");
	layer_open_list_title.addChild(layer_wx);
	layer_wx.x = 292; layer_wx.y = 30;
	
	layer_sxtm = labelWithText("生效&特码", 26, "#455971");
	layer_open_list_title.addChild(layer_sxtm);
	layer_sxtm.x = 496; layer_sxtm.y = 30;
	
	var layer_split = spriteWithImage(IMG_OPEN_LIST_2);
	layer_open_list_title.addChild(layer_split);
	layer_split.x = 213; layer_split.y = 23;
	
	var layer_split2 = spriteWithImage(IMG_OPEN_LIST_2);
	layer_open_list_title.addChild(layer_split2);
	layer_split2.x = 426; layer_split2.y = 23;
	
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
	
	showZHKJPage(list);
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
		showWXPage(data_list);
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
	layer_qishu.x = 52; layer_qishu.y = 125;
	
	var layer_tian = labelWithText("天", 26, "#455971");
	layer_zhkj_page.addChild(layer_tian);
	layer_tian.x = 197; layer_tian.y = 125;
	
	var layer_di = labelWithText("地", 26, "#455971");
	layer_zhkj_page.addChild(layer_di);
	layer_di.x = 296; layer_di.y = 125;
	
	var layer_wuxing = labelWithText("五行", 26, "#455971");
	layer_zhkj_page.addChild(layer_wuxing);
	layer_wuxing.x = 398; layer_wuxing.y = 125;
	
	var layer_shengxiao = labelWithText("生肖", 26, "#455971");
	layer_zhkj_page.addChild(layer_shengxiao);
	layer_shengxiao.x = 535; layer_shengxiao.y = 125;
	
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
		lv.insertChildView(zhkjListItem(ele.open_index, color, ele.result_td, ele.result_wx, ele.result_sx));
	}
}

function zhkjListItem(index, color, result_td, result_wx, result_sx){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 60], true,color);
	
	//期数
	var layer_index = labelWithText(String(index), 26, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 40; layer_index.y = 18;
	//天地
	if (result_td == IDX_TIAN){
		var layer_tian = spriteWithImage(IMG_WX_SHUI);
		child.addChild(layer_tian);
		layer_tian.x = 200; layer_tian.y = 18;
	}
	else {
		var layer_di = spriteWithImage(IMG_WX_MU);
		child.addChild(layer_di);
		layer_di.x = 299; layer_di.y = 18;
	}
	//五行
	var layer_wx_icon; var layer_wx_name;
	if (result_wx == IDX_WX_JIN){
		layer_wx_icon = spriteWithImage(IMG_WX_JIN);
		layer_wx_name = labelWithText("金", 26, "#aad3e7");
	}
	else if (result_wx == IDX_WX_MU){
		layer_wx_icon = spriteWithImage(IMG_WX_MU);
		layer_wx_name = labelWithText("木", 26, "#aad3e7");
	}
	else if (result_wx == IDX_WX_SHUI){
		layer_wx_icon = spriteWithImage(IMG_WX_SHUI);
		layer_wx_name = labelWithText("水", 26, "#aad3e7");
	}
	else if (result_wx == IDX_WX_HUO){
		layer_wx_icon = spriteWithImage(IMG_WX_HUO);
		layer_wx_name = labelWithText("火", 26, "#aad3e7");
	}
	else if (result_wx == IDX_WX_TU){
		layer_wx_icon = spriteWithImage(IMG_WX_TU);
		layer_wx_name = labelWithText("土", 26, "#aad3e7");
	}
	child.addChild(layer_wx_icon);
	layer_wx_icon.x = 394; layer_wx_icon.y = 18;
	child.addChild(layer_wx_name); 
	layer_wx_name.x = 419; layer_wx_name.y = 18;
	//生肖
	var layer_sx_icon; var layer_sx_name;
	if (result_sx == IDX_SX_SHU){
		layer_sx_icon = spriteWithImage(IMG_SX_SHU);
		layer_sx_name = labelWithText("鼠", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_NIU){
		layer_sx_icon = spriteWithImage(IMG_SX_NIU);
		layer_sx_name = labelWithText("牛", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_HU){
		layer_sx_icon = spriteWithImage(IMG_SX_HU);
		layer_sx_name = labelWithText("虎", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_TU){
		layer_sx_icon = spriteWithImage(IMG_SX_TU);
		layer_sx_name = labelWithText("兔", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_LONG){
		layer_sx_icon = spriteWithImage(IMG_SX_LONG);
		layer_sx_name = labelWithText("龙", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_SHE){
		layer_sx_icon = spriteWithImage(IMG_SX_SHE);
		layer_sx_name = labelWithText("蛇", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_MA){
		layer_sx_icon = spriteWithImage(IMG_SX_MA);
		layer_sx_name = labelWithText("马", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_YANG){
		layer_sx_icon = spriteWithImage(IMG_SX_YANG);
		layer_sx_name = labelWithText("羊", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_HOU){
		layer_sx_icon = spriteWithImage(IMG_SX_HOU);
		layer_sx_name = labelWithText("猴", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_JI){
		layer_sx_icon = spriteWithImage(IMG_SX_JI);
		layer_sx_name = labelWithText("鸡", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_GOU){
		layer_sx_icon = spriteWithImage(IMG_SX_GOU);
		layer_sx_name = labelWithText("狗", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_ZHU){
		layer_sx_icon = spriteWithImage(IMG_SX_ZHU);
		layer_sx_name = labelWithText("猪", 26, "#aad3e7");
	}
	child.addChild(layer_sx_icon);
	layer_sx_icon.x = 530; layer_sx_icon.y = 2;
	child.addChild(layer_sx_name); 
	layer_sx_name.x = 585; layer_sx_name.y = 18;
	
	return child;
}
//////////////////////////////////////////////////////////////////////////////////
//
function showWXPage(list){
	layer_wx_page = new LSprite();
	layer_open_list_background.addChild(layer_wx_page);
	var layer_title = new LSprite();
	layer_wx_page.addChild(layer_title);
	layer_title.graphics.drawRect(1, "#162737", [0, 93, SCREEN_WIDTH, 90], true, "#162737");
	var layer_qishu = labelWithText("期数", 26, "#455971");
	layer_wx_page.addChild(layer_qishu);
	layer_qishu.x = 52; layer_qishu.y = 125;
	var layer_jin = labelWithText("金", 26, "#cb9b45");
	layer_wx_page.addChild(layer_jin);
	layer_jin.x = 159; layer_jin.y = 125;
	var layer_mu = labelWithText("木", 26, "#5f896f");
	layer_wx_page.addChild(layer_mu);
	layer_mu.x = 266; layer_mu.y = 125;
	var layer_shui = labelWithText("水", 26, "#5f896f");
	layer_wx_page.addChild(layer_shui);
	layer_shui.x = 373; layer_shui.y = 125;
	var layer_huo = labelWithText("火", 26, "#ad5750");
	layer_wx_page.addChild(layer_huo);
	layer_huo.x = 480; layer_huo.y = 125;
	var layer_tu = labelWithText("土", 26, "#524643");
	layer_wx_page.addChild(layer_tu);
	layer_tu.x = 587; layer_tu.y = 125;
	showWXList(list);
}

function showWXList(list){
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
		lv.insertChildView(wxListItem(ele.open_index, color,ele.result_wx));
	}
}

function wxListItem(index, color, result_wx){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 60], true,color);
	
	//期数
	var layer_index = labelWithText(String(index), 26, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 40; layer_index.y = 18;
	//五行
	var layer_wx_icon; var layer_wx_name;
	if (result_wx == IDX_WX_JIN){
		layer_wx_icon = spriteWithImage(IMG_WX_JIN);
		layer_wx_icon.x = 159; layer_wx_icon.y = 18;
	}
	else if (result_wx == IDX_WX_MU){
		layer_wx_icon = spriteWithImage(IMG_WX_MU);
		layer_wx_icon.x = 266; layer_wx_icon.y = 18;
	}
	else if (result_wx == IDX_WX_SHUI){
		layer_wx_icon = spriteWithImage(IMG_WX_SHUI);
		layer_wx_icon.x = 373; layer_wx_icon.y = 18;
	}
	else if (result_wx == IDX_WX_HUO){
		layer_wx_icon = spriteWithImage(IMG_WX_HUO);
		layer_wx_icon.x = 480; layer_wx_icon.y = 18;
	}
	else if (result_wx == IDX_WX_TU){
		layer_wx_icon = spriteWithImage(IMG_WX_TU);
		layer_wx_icon.x = 587; layer_wx_icon.y = 18;
	}
	child.addChild(layer_wx_icon);	
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
	
	var layer_sxyl = labelWithText("生效遗漏值", 26, "#455971");
	layer_sxtm_page.addChild(layer_sxyl);
	layer_sxyl.x = 265; layer_sxyl.y = 125;
	
	
	var layer_title2 = new LSprite();
	layer_sxtm_page.addChild(layer_title2);
	layer_title2.graphics.drawRect(1, "#162737", [0, 555, SCREEN_WIDTH, 90], true, "#162737");
	
	var layer_tmyl = labelWithText("特码遗漏值", 26, "#455971");
	layer_sxtm_page.addChild(layer_tmyl);
	layer_tmyl.x = 265; layer_tmyl.y = 590;
	
	showYLList(data);
}

function showYLList(data){
	var lv = new LListView();
	layer_sxtm_page.addChild(lv);
	lv.x = 0;
	lv.y = 165;
	lv.maxPerLine = 3;
	lv.cellWidth = 640 / 3;
	lv.cellHeight = 100;
	lv.resize(640,400);
	lv.arrangement = LListView.Direction.Horizontal;
	lv.movement = LListView.Direction.Vertical;
	lv.graphics.drawRect(1, "#000000", [0, 0, lv.clipping.width,lv.clipping.height]);
	for (i = 0; i < 12; i++){		
		var value = eval("("+data[i]+")");
		lv.insertChildView(ylListItem(i, value));
	}
	var lv2 = new LListView();
	layer_sxtm_page.addChild(lv2);
	lv2.x = 0;
	lv2.y = 635;
	lv2.maxPerLine = 2;
	lv2.cellWidth = 640 / 2;
	lv2.cellHeight = 130;
	lv2.resize(640,130);
	lv2.arrangement = LListView.Direction.Horizontal;
	lv2.movement = LListView.Direction.Vertical;
	lv2.graphics.drawRect(1, "#000000", [0, 0, lv2.clipping.width,lv2.clipping.height]);
	for (i = 12; i < 14; i++){		
		var value = eval("("+data[i]+")");
		lv2.insertChildView(ylListItem2(i, value));
	}
	
}

function ylListItem(i, index){	
	var child = new LListChildView();
	var layer1 = new LSprite();
	layer1.graphics.drawRect(1, "#1c324a", [0, 0, 640 / 3, 50], true, "#1c324a");
	child.addChild(layer1);
	var layer2 = new LSprite();
	layer2.graphics.drawRect(1, "#223850", [0, 50, 640 / 3, 50], true, "#223850");	
	child.addChild(layer2);
	//生肖
	var layer_sx_icon; var layer_sx_name;
	layer_sx_icon = spriteWithImage(IMG_SX_SHU);
	var result_sx = i + 1;
	if (result_sx == IDX_SX_SHU){
		layer_sx_icon = spriteWithImage(IMG_SX_SHU);
		layer_sx_name = labelWithText("鼠", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_NIU){
		layer_sx_icon = spriteWithImage(IMG_SX_NIU);
		layer_sx_name = labelWithText("牛", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_HU){
		layer_sx_icon = spriteWithImage(IMG_SX_HU);
		layer_sx_name = labelWithText("虎", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_TU){
		layer_sx_icon = spriteWithImage(IMG_SX_TU);
		layer_sx_name = labelWithText("兔", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_LONG){
		layer_sx_icon = spriteWithImage(IMG_SX_LONG);
		layer_sx_name = labelWithText("龙", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_SHE){
		layer_sx_icon = spriteWithImage(IMG_SX_SHE);
		layer_sx_name = labelWithText("蛇", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_MA){
		layer_sx_icon = spriteWithImage(IMG_SX_MA);
		layer_sx_name = labelWithText("马", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_YANG){
		layer_sx_icon = spriteWithImage(IMG_SX_YANG);
		layer_sx_name = labelWithText("羊", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_HOU){
		layer_sx_icon = spriteWithImage(IMG_SX_HOU);
		layer_sx_name = labelWithText("猴", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_JI){
		layer_sx_icon = spriteWithImage(IMG_SX_JI);
		layer_sx_name = labelWithText("鸡", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_GOU){
		layer_sx_icon = spriteWithImage(IMG_SX_GOU);
		layer_sx_name = labelWithText("狗", 26, "#aad3e7");
	}
	else if (result_sx == IDX_SX_ZHU){
		layer_sx_icon = spriteWithImage(IMG_SX_ZHU);
		layer_sx_name = labelWithText("猪", 26, "#aad3e7");
	}	
	child.addChild(layer_sx_icon);
	layer_sx_icon.x = 70;
	layer_sx_icon.y = 0;
	child.addChild(layer_sx_name);
	layer_sx_name.x = 130;
	layer_sx_name.y = 18;
	//期数
	var layer_index = labelWithText(String(index) + "期", 26, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 90; layer_index.y = 65;	
	return child;
}

function ylListItem2(i, index){	
	var child = new LListChildView();
	var layer1 = new LSprite();
	layer1.graphics.drawRect(1, "#1c324a", [0, 0, 640 / 2, 65], true, "#1c324a");
	child.addChild(layer1);
	var layer2 = new LSprite();
	layer2.graphics.drawRect(1, "#223850", [0, 65, 640 / 2, 65], true, "#223850");	
	child.addChild(layer2);
	
	var ele = eval("("+data_list[0]+")");
	var layer_name;
	if (i == 12){
		layer_name = labelWithText(getNameFromLotteryResult(ele.result_lm_td) +"|"+ getNameFromLotteryResult(ele.result_lm_sx), 26, "#aad3e7");
		layer_name.x = 115; layer_name.y = 18;
	}
	else {
		layer_name = labelWithText(getNameFromLotteryResult(ele.result_sm_td) +"|"+ getNameFromLotteryResult(ele.result_sm_wx) +"|"+ getNameFromLotteryResult(ele.result_sm_sx), 26, "#aad3e7");
		layer_name.x = 105; layer_name.y = 18;
	}
	child.addChild(layer_name);
	
	
	var layer_index = labelWithText(String(index) + "期", 26, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 120; layer_index.y = 83;	
	return child;
}



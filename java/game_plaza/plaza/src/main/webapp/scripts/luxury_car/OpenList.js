var layer_open_list_background = null;
var layer_zhkj = null;
var layer_open_list_selected = null;
function showOpenList(msg){
	if (layer_open_list_background != null){
		return;
	}
	var list = msg.list;
	data_list = list;
	data_yilou = msg.tema_yilou;
	layer_open_list_background = new LSprite();
	layer_open_list_background.x = converToParentX(640, SCREEN_WIDTH);
	baseLayer.addChild(layer_open_list_background);
	layer_open_list_background.graphics.drawRect(0, "#000000", [0, 0, 640, 565], false, "#000000");
	layer_open_list_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);
	//layer_open_list_background.alpha = 0.5;
	
	var layer_open_list_title = new LSprite();
	layer_open_list_background.addChild(layer_open_list_title);
	layer_open_list_title.graphics.drawRect(1, "#162737", [0, 0, 640, 70], true, "#162737");
	
	layer_zhkj = labelWithText("开奖", 23, "#6fd5fb");
	layer_open_list_title.addChild(layer_zhkj);
	layer_zhkj.x = 40; layer_zhkj.y = 20;
	
	
	var layer_split = spriteWithImage(IMG_OPEN_LIST_2);
	layer_open_list_title.addChild(layer_split);
	layer_split.x = 213; layer_split.y = 23;
	
	var layer_split2 = spriteWithImage(IMG_OPEN_LIST_2);
	layer_open_list_title.addChild(layer_split2);
	layer_split2.x = 426; layer_split2.y = 23;
	
	var img = new LSprite();
	layer_open_list_title.addChild(img);
	img.y = 63	
	img.graphics.drawRect(1, "#000000", [0, 0, 640, 20], true, "#000000");

	layer_open_list_selected = spriteWithImage(IMG_OPEN_LIST_1);
	img.addChild(layer_open_list_selected);
	layer_open_list_selected.x = 0; layer_open_list_selected.y = 5;
	
	var layer_split3 = spriteWithImage(IMG_OPEN_LIST_3);
	layer_open_list_title.addChild(layer_split3);
	layer_split3.x = 0; layer_split3.y = 80;
	
	var layer_close = spriteWithImage(IMG_OPEN_LIST_4);
	layer_open_list_background.addChild(layer_close);
	layer_close.x = 0; layer_close.y = 550;
	layer_close.addEventListener(LMouseEvent.MOUSE_DOWN, onOpenListClose);
	
	showZHKJPage(list);
}
var layer_zhkj_page = null;
function onOpenListClose(event){
	layer_open_list_background.remove();
	layer_open_list_background = null;
	layer_zhkj_page = null;
}
function closeAllPage(){
	if (layer_zhkj_page != null){
		layer_zhkj_page.remove();
		layer_zhkj_page = null;
	}
}
//////////////////////////////////////////////////////////////////////////////////
//
function showZHKJPage(list){
	layer_zhkj_page = new LSprite();
	layer_open_list_background.addChild(layer_zhkj_page);
	
	var layer_title = new LSprite();
	layer_zhkj_page.addChild(layer_title);
	layer_title.graphics.drawRect(1, "#162737", [0, 83, 640, 70], true, "#162737");
	
	var layer_qishu = labelWithText("期数", 26, "#6fd5fb");
	layer_zhkj_page.addChild(layer_qishu);
	layer_qishu.x = 52; layer_qishu.y = 105;
	
	var layer_tian = labelWithText("索引", 26, "#6fd5fb");
	layer_zhkj_page.addChild(layer_tian);
	layer_tian.x = 197; layer_tian.y = 105;
	
	var layer_di = labelWithText("豪车", 26, "#6fd5fb");
	layer_zhkj_page.addChild(layer_di);
	layer_di.x = 296; layer_di.y = 105;
	
	showZHKJList(list);
}

function showZHKJList(list){
	var lv = new LListView();
	layer_zhkj_page.addChild(lv);
	lv.x = 0;
	lv.y = 153;
	lv.maxPerLine = 1;
	lv.cellWidth = 640;
	lv.cellHeight = 40;
	lv.resize(640,400);
	lv.arrangement = LListView.Direction.Horizontal;
	lv.movement = LListView.Direction.Vertical;
	lv.graphics.drawRect(1, "#162737", [0, 0, lv.clipping.width,lv.clipping.height], true, "#162737");

	for (i = 0; i < list.length; i++){
		var color = "#1c324a";
		if (i % 2 === 1){
			color = "#223850";
		}
		var ele = eval("("+list[i]+")");
		lv.insertChildView(zhkjListItem(ele.open_index, color, ele.result_flag, ele.result));
	}
}

function zhkjListItem(index, color, result_flag, result){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 40], true,color);
	
	var posY = 5;
	// 期数
	var layer_index = labelWithText(String(index), 25, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 40; layer_index.y = posY;

	// 索引
	var flag = '';
	if (result_flag < 10){
		flag = '0'+result_flag;
	}else{
		flag = String(result_flag)
	}
	var layer_index = labelWithText(flag, 22, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 200; layer_index.y = posY;

	// 开奖
	var layer_index = labelWithText(gCarName[result - 1], 22, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 300; layer_index.y = posY;
	
	return child;
}
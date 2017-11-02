var layer_online_list_background = null;
var layer_online = null;
var layer_online_list_selected = null;
function showOnlineListWindow(msg){
	if (layer_online_list_background != null){
		return;
	}
	var list = msg.list;
	data_list = list;
	data_yilou = msg.tema_yilou;
	layer_online_list_background = new LSprite();
	layer_online_list_background.x = converToParentX(640, SCREEN_WIDTH);
	baseLayer.addChild(layer_online_list_background);
	layer_online_list_background.graphics.drawRect(0, "#000000", [0, 0, 640, 565], false, "#000000");
	layer_online_list_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);
	//layer_online_list_background.alpha = 0.5;
	
	var layer_open_list_title = new LSprite();
	layer_online_list_background.addChild(layer_open_list_title);
	layer_open_list_title.graphics.drawRect(1, "#162737", [0, 0, 640, 70], true, "#162737");
	
	layer_online = labelWithText("玩家列表", 23, "#6fd5fb");
	layer_open_list_title.addChild(layer_online);
	layer_online.x = 40; layer_online.y = 20;
	
	
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

	layer_online_list_selected = spriteWithImage(IMG_OPEN_LIST_1);
	img.addChild(layer_online_list_selected);
	layer_online_list_selected.x = 0; layer_online_list_selected.y = 5;
	
	var layer_split3 = spriteWithImage(IMG_OPEN_LIST_3);
	layer_open_list_title.addChild(layer_split3);
	layer_split3.x = 0; layer_split3.y = 80;
	
	var layer_close = spriteWithImage(IMG_OPEN_LIST_4);
	layer_online_list_background.addChild(layer_close);
	layer_close.x = 0; layer_close.y = 550;
	layer_close.addEventListener(LMouseEvent.MOUSE_DOWN, onOnlineListClose);
	
	showOnlinePage(list);
}
var layer_online_page = null;
function onOnlineListClose(event){
	layer_online_list_background.remove();
	layer_online_list_background = null;
	layer_online_page = null;
}
function closeAllPage(){
	if (layer_online_page != null){
		layer_online_page.remove();
		layer_online_page = null;
	}
}

//////////////////////////////////////////////////////////////////////////////////
//
function showOnlinePage(list){
	layer_online_page = new LSprite();
	layer_online_list_background.addChild(layer_online_page);
	
	var layer_title = new LSprite();
	layer_online_page.addChild(layer_title);
	layer_title.graphics.drawRect(1, "#162737", [0, 83, 640, 70], true, "#162737");
	
	var layer_qishu = labelWithText("玩家名", 26, "#6fd5fb");
	layer_online_page.addChild(layer_qishu);
	layer_qishu.x = 52; layer_qishu.y = 105;
	
	showOnlineList(list);
}

function showOnlineList(list){
	var lv = new LListView();
	layer_online_page.addChild(lv);
	lv.x = 0;
	lv.y = 153;
	lv.maxPerLine = 1;
	lv.cellWidth = 640;
	lv.cellHeight = 40;
	lv.resize(640,400);
	lv.arrangement = LListView.Direction.Horizontal;
	lv.movement = LListView.Direction.Vertical;
	var scrollBarVertical = new LListScrollBar(new LPanel("#9370DB", 0, 0), new LPanel("#9400D3", 0, 0), LListView.ScrollBarCondition.OnlyIfNeeded);
	lv.setVerticalScrollBar(scrollBarVertical);

	lv.graphics.drawRect(1, "#162737", [0, 0, lv.clipping.width,lv.clipping.height], true, "#162737");

	for (i = 0; i < list.length; i++){
		var color = "#1c324a";
		if (i % 2 === 1){
			color = "#223850";
		}
		var ele = list[i];
		lv.insertChildView(onlineListItem(ele.open_index, color, ele));
	}
}

function onlineListItem(index, color, name){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 40], true,color);
	
	var posY = 5;
	// 名称
	var layer_index = labelWithText(name, 25, "#aad3e7");
	child.addChild(layer_index);
	layer_index.x = 40; layer_index.y = posY;
	
	return child;
}
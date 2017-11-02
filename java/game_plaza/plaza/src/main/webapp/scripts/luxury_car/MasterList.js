var layer_master_list_background = null;
function showMasterList(masterList){
	if (layer_master_list_background != null){
		return;
	}
	layer_master_list_background = new LSprite();
	baseLayer.addChild(layer_master_list_background);
	layer_master_list_background.x = converToParentX(640,SCREEN_WIDTH);
	layer_master_list_background.graphics.drawRect(1, "#162737", [0, 0, 640, 450], true, "#162737");
	layer_master_list_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	
	
	var layer_master_title = new LSprite();
	layer_master_list_background.addChild(layer_master_title);
	layer_master_title.graphics.drawRect(1, "#162737", [0, 0, 640, 90], true, "#162737");
	
	var layer_split = spriteWithImage(IMG_OPEN_LIST_2);
	layer_master_title.addChild(layer_split);
	layer_split.x = 213; layer_split.y = 23;
	
	var layer_selected = spriteWithImage(IMG_OPEN_LIST_1);
	layer_master_title.addChild(layer_selected);
	layer_selected.x = 0; layer_selected.y = 83;
	
	var layer_lts = labelWithText("排庄信息", 26, "#6fd5fb");
	layer_master_title.addChild(layer_lts);
	layer_lts.x = 40; layer_lts.y = 30;
	
	var layer_section = new LSprite();
	layer_master_list_background.addChild(layer_section);
	layer_section.graphics.drawRect(1, "#162737", [0, 0, 640, 90], true, "#162737");
	layer_section.y = 91;
	
	var layer_paiwei = labelWithText("排位", 26, "#455971");
	layer_section.addChild(layer_paiwei);
	layer_paiwei.x = 80; layer_paiwei.y = 30;
	
	var layer_tian = labelWithText("财富", 26, "#455971");
	layer_section.addChild(layer_tian);
	layer_tian.x = 297; layer_tian.y = 30;
	
	var layer_di = labelWithText("昵称", 26, "#455971");
	layer_section.addChild(layer_di);
	layer_di.x = 496; layer_di.y = 30;	
	
	var layer_conetnt_masterlist = new LListView();
	layer_master_list_background.addChild(layer_conetnt_masterlist);
	layer_conetnt_masterlist.x = 0;
	layer_conetnt_masterlist.y = 180;
	layer_conetnt_masterlist.maxPerLine = 1;
	layer_conetnt_masterlist.cellWidth = 640;
	layer_conetnt_masterlist.cellHeight = 45;
	layer_conetnt_masterlist.resize(SCREEN_WIDTH, 600);
	layer_conetnt_masterlist.arrangement = LListView.Direction.Horizontal;
	layer_conetnt_masterlist.movement = LListView.Direction.Vertical;
	layer_conetnt_masterlist.graphics.drawRect(0, "#000000", [0, 0, layer_conetnt_masterlist.clipping.width,layer_conetnt_masterlist.clipping.height]);
	
	var rank = 0;
	for (var i = 0; i < masterList.length; i++){
		var color = "#1c324a";
		if (i % 2 === 1){
			color = "#223850";
		}
		var user = masterList[i];
		if (curr_master_id <= 0 || user.id == curr_master_id){
			continue;
		}

		rank += 1;
		layer_conetnt_masterlist.insertChildView(masterItem(rank, user.chips, user.name, color));
	}
	
	var layer_close = spriteWithImage(IMG_OPEN_LIST_4);
	layer_master_list_background.addChild(layer_close);
	layer_close.x = 0; layer_close.y = 450;
	layer_close.addEventListener(LMouseEvent.MOUSE_DOWN, onMasterListClose);
}
function onMasterListClose(event){
	closeMasterList();
}
function closeMasterList(){
	if (layer_master_list_background != null){
		layer_master_list_background.remove();
		layer_master_list_background = null;
	}
}
function reflashMasterList(masterList){
	if (layer_master_list_background != null){
		layer_master_list_background.remove();
		layer_master_list_background = null;
		showMasterList(masterList);
	}
}
function masterItem(rank, chips, name, color){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 45], true,color);
	var layer_rank = labelWithText(rank, 22, "#aad3e7");
	child.addChild(layer_rank);
	layer_rank.x = 100;
	layer_rank.y = 9;
	var layer_chips = labelWithText(splitNumber(chips), 22, "#aad3e7");
	child.addChild(layer_chips);
	layer_chips.x = 270;
	layer_chips.y = 9;
	var layer_name = labelWithText(name, 22, "#aad3e7");
	child.addChild(layer_name);
	layer_name.x = 450;
	layer_name.y = 9;
	return child;
}
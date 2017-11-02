var layer_helper_background = null;
var layer_webView = null;
function showHelper(){
	if (layer_helper_background != null){
		return;
	}
	layer_helper_background = new LSprite();
	layer_helper.addChild(layer_helper_background);
	layer_helper_background.graphics.drawRect(1, "#223850", [0, 0, SCREEN_WIDTH, 585], true, "#223850");
	layer_helper_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	
	
	var layer_bank_title = new LSprite();
	layer_helper_background.addChild(layer_bank_title);
	layer_bank_title.graphics.drawRect(1, "#162737", [0, 0, SCREEN_WIDTH, 90], true, "#162737");
	
	var layer_split = spriteWithImage(IMG_OPEN_LIST_2);
	layer_bank_title.addChild(layer_split);
	layer_split.x = 213; layer_split.y = 23;
	
	var layer_selected = spriteWithImage(IMG_OPEN_LIST_1);
	layer_bank_title.addChild(layer_selected);
	layer_selected.x = 0; layer_selected.y = 83;
	
	var layer_yh = labelWithText("规则说明", 26, "#6fd5fb");
	layer_bank_title.addChild(layer_yh);
	layer_yh.x = 40; layer_yh.y = 30;

	var width = 600
	layer_webView = new LStageWebView();
	layer_webView.setViewPort(new LRectangle(30 ,100,width,450));
	layer_webView.loadURL(global_util.getAppPath()+"/shake_helper.html");
	layer_webView.show();
	layer_webView.display.style.border = 0;
	layer_webView.iframe.style.border = 0;

	var layer_close = spriteWithImage(IMG_OPEN_LIST_4);
	layer_helper_background.addChild(layer_close);
	layer_close.x = 0; layer_close.y = 585;
	layer_close.addEventListener(LMouseEvent.MOUSE_DOWN, closeHelper);
}

function closeHelper(event){
	if (layer_helper_background != null){
		layer_helper_background.remove();
		layer_helper_background = null;

		layer_webView.die();
	}
}












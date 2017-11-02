var layer_msg_box = null;
function msgBox(content){
	if (layer_msg_box != null){
		layer_msg_box.remove();
	}
	layer_msg_box = spriteWithImage(IMG_MSG_BOX);
	layer_msg_box_bg.addChild(layer_msg_box);
	layer_msg_box.x = convertToScreenCenterX(imgSize[IMG_MSG_BOX].w);
	layer_msg_box.y = convertToScreenCenterY(70);
	layer_msg_box.addEventListener(LEvent.ENTER_FRAME,onMsgBoxFrame);
	var layer_msg = labelWithText(content, 25, "#ff0000");
	layer_msg_box.addChild(layer_msg);
	layer_msg.x = converToParentX(layer_msg.getWidth(), imgSize[IMG_MSG_BOX].w);
	layer_msg.y = 18;
	layer_msg.lineColor = "#000000";
	layer_msg.lineWidth = 2;
	
}
function msgBox2(content){
	if (layer_msg_box != null){
		layer_msg_box.remove();
	}
	layer_msg_box = spriteWithImage(IMG_MSG_BOX);
	baseLayer.addChild(layer_msg_box);
	layer_msg_box.x = convertToScreenCenterX(imgSize[IMG_MSG_BOX].w);
	layer_msg_box.y = convertToScreenCenterY(70);
	layer_msg_box.addEventListener(LEvent.ENTER_FRAME,onMsgBoxFrame);
	var layer_msg = labelWithText(content, 25, "#ff0000");
	layer_msg_box.addChild(layer_msg);
	layer_msg.x = converToParentX(layer_msg.getWidth(), imgSize[IMG_MSG_BOX].w);
	layer_msg.y = 18;
	layer_msg.lineColor = "#000000";
	layer_msg.lineWidth = 2;
	
}
function onMsgBoxFrame(event){
	//var layer = event.currentTarget;
		layer_msg_box.y -= 2;
	if (layer_msg_box.y < 400){
		layer_msg_box.remove();
		layer_msg_box = null;
	}
}
var layer_notice_box = null;
var notice_frame_count = 0;
function noticeBox(content, color){
	if (layer_notice_box != null){
		layer_notice_box.remove();
	}
	notice_frame_count = 0;
	layer_notice_box = spriteWithImage(IMG_NOTICE_BOX);
	layer_msg_box_bg.addChild(layer_notice_box);
	layer_notice_box.x = convertToScreenCenterX(imgSize[IMG_NOTICE_BOX].w);
	layer_notice_box.y = convertToScreenCenterY(imgSize[IMG_NOTICE_BOX].h);
	layer_notice_box.addEventListener(LEvent.ENTER_FRAME,onNoticeBoxFrame);
	var layer_msg = labelWithText(content, 25, color);
	layer_notice_box.addChild(layer_msg);
	layer_msg.x = converToParentX(layer_msg.getWidth(), imgSize[IMG_NOTICE_BOX].w);
	layer_msg.y = 36;
	layer_msg.lineColor = "#000000";
	layer_msg.lineWidth = 2;
}
function noticeBoxEx(content1, color1, content2, color2){
	if (layer_notice_box != null){
		layer_notice_box.remove();
	}
	notice_frame_count = 0;
	layer_notice_box = spriteWithImage(IMG_NOTICE_BOX);
	layer_msg_box_bg.addChild(layer_notice_box);
	layer_notice_box.x = convertToScreenCenterX(imgSize[IMG_NOTICE_BOX].w);
	layer_notice_box.y = convertToScreenCenterY(imgSize[IMG_NOTICE_BOX].h);
	layer_notice_box.addEventListener(LEvent.ENTER_FRAME,onNoticeBoxFrame);
	var layer_msg1 = labelWithText(content1, 25, color1);
	layer_notice_box.addChild(layer_msg1);
	layer_msg1.x = 200;//converToParentX(layer_msg.getWidth(), imgSize[IMG_NOTICE_BOX].w);
	layer_msg1.y = 20;
	layer_msg1.lineColor = "#000000";
	layer_msg1.lineWidth = 2;
	var layer_msg2 = labelWithText(content2, 25, color2);
	layer_notice_box.addChild(layer_msg2);
	layer_msg2.x = 200;//converToParentX(layer_msg.getWidth(), imgSize[IMG_NOTICE_BOX].w);
	layer_msg2.y = 55;
	layer_msg2.lineColor = "#000000";
	layer_msg2.lineWidth = 2;
}

var layer_notice_box_login_error = null;
function noticeLoginError(content){
	if (layer_notice_box_login_error != null){
		layer_notice_box_login_error.remove();
		layer_notice_box_login_error = null;
	}
	layer_notice_box_login_error = new LSprite();
	layer_notice_box_login_error.graphics.drawRect(0, "#223850", [0, 0, SCREEN_WIDTH, SCREEN_HEIGHT], true, "#000000");	
	layer_notice_box_login_error.alpha = 0.5;
	baseLayer.addChild(layer_notice_box_login_error);
	var bg = spriteWithImage(IMG_RELIEF);
	baseLayer.addChild(bg);
	bg.x = converToParentX(imgSize[IMG_RELIEF].w, SCREEN_WIDTH);
	bg.y = convertToScreenCenterY(imgSize[IMG_RELIEF].h);
	layer_notice_box_login_error.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	

	var lb = labelWithText('开 奖 中', 22, '#ffffff');
	lb.x = converToParentX(lb.getWidth(), imgSize[IMG_RELIEF].w);
	lb.y = 25;
	bg.addChild(lb);

	var x = 60;
	var lb = labelWithText('你参与的 ', 22, '#ffffff');
	lb.x = x;
	lb.y = 105;
	bg.addChild(lb);
	x += lb.getWidth();

	var lb = labelWithText(content, 22, '#ffff00');
	lb.x = x;
	lb.y = 105;
	bg.addChild(lb);
	x += lb.getWidth();

	var lb = labelWithText(' 正在开奖中，', 22, '#ffffff');
	lb.x = x;
	lb.y = 105;
	bg.addChild(lb);

	var lb = labelWithText('暂时不能参与当前游戏，', 22, '#ffffff');
	lb.x = 60;
	lb.y = 135;
	bg.addChild(lb);

	var lb = labelWithText('请耐心等待开奖完成。', 22, '#ffffff');
	lb.x = 60;
	lb.y = 165;
	bg.addChild(lb);

	var btn_close = panelWithSprite(134,65);
	bg.addChild(btn_close);
	btn_close.x = converToParentX(134, imgSize[IMG_RELIEF].w);
	btn_close.y = imgSize[IMG_RELIEF].h - 75;
	btn_close.addEventListener(LMouseEvent.MOUSE_DOWN, onNoticeBoxLoginErrorClose);

	var lb = new LTextField();
	lb.text = '返回首页';
	lb.size = 26;
	lb.color = '#000000';
	lb.lineColor = "#000000";
	lb.lineWidth = 1;
	lb.x = converToParentX(lb.getWidth(), btn_close.getWidth());
	lb.y = converToParentY(lb.getHeight(), btn_close.getHeight()) - 6;
	btn_close.addChild(lb);
}
function onNoticeBoxLoginErrorClose(){
	if (layer_notice_box_login_error != null){
		layer_notice_box_login_error.remove();
		layer_notice_box_login_error = null;
	}

	onMenuItem1Click(null);
}

function noticBoxEx21(title, content){
	if (layer_notice_box_ex2 != null){
		layer_notice_box_ex2.remove();
		layer_notice_box_ex2 = null;
	}	
	layer_notice_box_ex2 = spriteWithImage(IMG_RELIEF);
	baseLayer.addChild(layer_notice_box_ex2);
	layer_notice_box_ex2.x = converToParentX(imgSize[IMG_RELIEF].w, SCREEN_WIDTH);
	layer_notice_box_ex2.y = convertToScreenCenterY(imgSize[IMG_RELIEF].h);
	layer_notice_box_ex2.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	

	var lb = labelWithText('救济', 22, '#ffffff');
	lb.x = converToParentX(lb.getWidth(), imgSize[IMG_RELIEF].w);
	lb.y = 25;
	layer_notice_box_ex2.addChild(lb);

	var lb = labelWithText('系统赠送给您 3000金币，好好把握机会!', 22, '#ffffff');
	lb.x = converToParentX(lb.getWidth(), imgSize[IMG_RELIEF].w);
	lb.y = 155;
	layer_notice_box_ex2.addChild(lb);

	var btn_close = panelWithSprite(134,65);
	layer_notice_box_ex2.addChild(btn_close);
	btn_close.x = converToParentX(134, imgSize[IMG_RELIEF].w);
	btn_close.y = imgSize[IMG_RELIEF].h - 75;
	btn_close.addEventListener(LMouseEvent.MOUSE_DOWN, onNoticeBoxEx21Close);

	var lb = new LTextField();
	lb.text = '可领取';
	lb.size = 26;
	lb.stroke = true;
	lb.color = '#ffffff';
	lb.lineColor = "#fdc418";
	lb.lineWidth = 2;
	lb.x = converToParentX(lb.getWidth(), btn_close.getWidth());
	lb.y = converToParentY(lb.getHeight(), btn_close.getHeight()) - 6;
	btn_close.addChild(lb);
}

function onNoticeBoxEx21Close(){
	requestRelief();
	if (layer_notice_box_ex2 != null){
		layer_notice_box_ex2.remove();
		layer_notice_box_ex2 = null;
	}
}

var layer_notice_box_ex2 = null;
function noticBoxEx2(title, content){
	if (layer_notice_box_ex2 != null){
		layer_notice_box_ex2.remove();
		layer_notice_box_ex2 = null;
	}
	layer_notice_box_ex2 = new LSprite();
	layer_msg_box_bg.addChild(layer_notice_box_ex2);
	layer_notice_box_ex2.x = 0; layer_notice_box_ex2.y = convertToScreenCenterY(320);
	layer_notice_box_ex2.graphics.drawRect(1, "#223850", [0, 0, SCREEN_WIDTH, 320], true, "#223850");
	layer_notice_box_ex2.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	
	var layer_split = new LSprite();
	layer_notice_box_ex2.addChild(layer_split);
	layer_split.x = 0; layer_split.y = 90;
	layer_split.graphics.drawRect(1, "#6bd2f9", [0, 0, SCREEN_WIDTH, 7], true, "#6bd2f9");	
	var layer_content_background = new LSprite();
	layer_notice_box_ex2.addChild(layer_content_background);
	layer_content_background.x = 0; layer_content_background.y = 97;
	layer_content_background.graphics.drawRect(1, "#1c324a", [0, 0, SCREEN_WIDTH, 203], true, "#1c324a");
	var layer_title = labelWithText(title, 26, "#6fd5fb");
	layer_notice_box_ex2.addChild(layer_title);
	layer_title.x = 20; layer_title.y = 32;
	var layer_content = labelWithText(content, 24, "#6fd5fb");
	layer_notice_box_ex2.addChild(layer_content);
	layer_content.x = converToParentX(layer_content.getWidth(), SCREEN_WIDTH);
	layer_content.y = 126;
	var btn_close = new LButton(bitmapWithImage(IMG_CLOSE));
	layer_notice_box_ex2.addChild(btn_close);
	btn_close.x = 590; btn_close.y = 20;
	btn_close.addEventListener(LMouseEvent.MOUSE_DOWN, onNoticeBoxClose);
	var btn_show_bank = new LButton(bitmapWithImage(IMG_BANK));
	layer_notice_box_ex2.addChild(btn_show_bank);
	btn_show_bank.x = 75; btn_show_bank.y = 200;
	btn_show_bank.addEventListener(LMouseEvent.MOUSE_DOWN, onNoticeBoxShowBank);
	var btn_add_chips = new LButton(bitmapWithImage(IMG_RECHARGE));
	layer_notice_box_ex2.addChild(btn_add_chips);
	btn_add_chips.x = 390; btn_add_chips.y = 200;
	btn_add_chips.addEventListener(LMouseEvent.MOUSE_DOWN, onNoticeBoxShowAddChips);
	
}
function onNoticeBoxClose(){
	if (layer_notice_box_ex2 != null){
		layer_notice_box_ex2.remove();
		layer_notice_box_ex2 = null;
	}
}
function onNoticeBoxShowBank(){
	showBank();
	onNoticeBoxClose();
}
function onNoticeBoxShowAddChips(){
	var wxid = document.getElementById("wxid").value;	window.location =  global_util.getAppPath()+"/shop/"+wxid;	// 外网1 平台商城地址
}
var max_message_box_frame_count = 60;
function onNoticeBoxFrame(event){
	if (++notice_frame_count > max_message_box_frame_count){
		layer_notice_box.remove();
		layer_notice_box = null;
	}
}

var layer_notice_box_result = null;
function noticeBoxResultEx(content1, color1, content2, color2){
	noticeBoxResultExClose();
	
	notice_frame_count = 0;
	layer_notice_box_result = spriteWithImage(IMG_NOTICE_BOX);
	layer_msg_box_bg.addChild(layer_notice_box_result);
	layer_notice_box_result.x = convertToScreenCenterX(imgSize[IMG_NOTICE_BOX].w);
	layer_notice_box_result.y = convertToScreenCenterY(imgSize[IMG_NOTICE_BOX].h);
	var layer_msg1 = labelWithText(content1, 25, color1);
	layer_notice_box_result.addChild(layer_msg1);
	layer_msg1.x = 200;//converToParentX(layer_msg.getWidth(), imgSize[IMG_NOTICE_BOX].w);
	layer_msg1.y = 20;
	layer_msg1.lineColor = "#000000";
	layer_msg1.lineWidth = 2;
	var layer_msg2 = labelWithText(content2, 25, color2);
	layer_notice_box_result.addChild(layer_msg2);
	layer_msg2.x = 200;//converToParentX(layer_msg.getWidth(), imgSize[IMG_NOTICE_BOX].w);
	layer_msg2.y = 55;
	layer_msg2.lineColor = "#000000";
	layer_msg2.lineWidth = 2;
}

function noticeBoxResultExClose(){
	if (layer_notice_box_result != null){
		layer_notice_box_result.remove();
		layer_notice_box_result = null;
	}
}

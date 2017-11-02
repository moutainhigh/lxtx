var MAX_CHAT_MSG_COUNT = 10;
var arr_chat_name = new Array();
var arr_chat_msg = new Array();
var chat_name1 = "";
var chat_name2 = "";
var chat_msg1 = "";
var chat_msg2 = "";
var layer_chat_background = null;
var layer_chat_room_background = null;
var chat_room_fobid = 0;
function setChatMsg(userName, msg){
	chat_name2 = chat_name1;
	chat_msg2 = chat_msg1;
	chat_name1 = userName;
	chat_msg1 = msg;
	if (layer_chat_background != null){
		layer_chat_background.remove();
	}
	layer_chat_background = spriteWithImage(IMG_CHAT_BACKGROUND);
	layer_game_background.addChild(layer_chat_background);
	layer_chat_background.x = convertToScreenCenterX(imgSize[IMG_CHAT_BACKGROUND].w); layer_chat_background.y = 648;
	
	var layer_chat_button = new LButton(bitmapWithImage(IMG_CHAT));
	layer_chat_background.addChild(layer_chat_button);
	layer_chat_button.x = 10; layer_chat_button.y = 15;
	layer_chat_button.addEventListener(LMouseEvent.MOUSE_DOWN, onChatClicked);
	
	var next = false;
	var arrNext = [];
	if (chat_msg1.length > 0){
		arrNext = getNeedStr(chat_msg1);
		if (arrNext.length > 1){
			next = true;
		}
	}
	if (chat_msg1.length > 0){
		arr_chat_msg.unshift(chat_msg1);
		if (arr_chat_msg.length > MAX_CHAT_MSG_COUNT){
			arr_chat_msg.splice(MAX_CHAT_MSG_COUNT, arr_chat_msg.length - MAX_CHAT_MSG_COUNT);
		}
		arr_chat_name.unshift(chat_name1);
		if (arr_chat_name.length > MAX_CHAT_MSG_COUNT){
			arr_chat_name.splice(MAX_CHAT_MSG_COUNT, arr_chat_name.length - MAX_CHAT_MSG_COUNT);
		}
		var layer_chat1 = labelWithText(chat_name1 + "：", 24, "#ffa900");
		layer_chat_background.addChild(layer_chat1);			
		if (arrNext.length <= 1){
			layer_chat1.x = 80; layer_chat1.y = 50;
			var layer_msg1 = labelWithText(arrNext[0], 24, "#ffffff");
			layer_chat_background.addChild(layer_msg1);
			layer_msg1.x = layer_chat1.x + layer_chat1.getWidth(); layer_msg1.y = 50;
		}else{
			layer_chat1.x = 80; layer_chat1.y = 20;
			var layer_msg1 = labelWithText(arrNext[1], 24, "#ffffff");
			layer_chat_background.addChild(layer_msg1);
			layer_msg1.x = layer_chat1.x + layer_chat1.getWidth(); layer_msg1.y = 50;

			var layer_msg2 = labelWithText(getNeedStr(arrNext[0]), 24, "#ffffff");
			layer_chat_background.addChild(layer_msg2);
			layer_msg2.x = layer_chat1.x + layer_chat1.getWidth(); layer_msg2.y = 20;
		}
	}
	if (!next && chat_msg2.length > 0){
		var layer_chat2 = labelWithText(chat_name2 + "：", 24, "#ffa900");
		layer_chat_background.addChild(layer_chat2);
		layer_chat2.x = 80; layer_chat2.y = 20;
		var str = '';
		var arr = getNeedStr(chat_msg2);
		if (arr.length<=1){
			str = arr[0];
		}else{
			str = arr[1];
		}
		var layer_msg2 = labelWithText(str, 24, "#ffffff");
		layer_chat_background.addChild(layer_msg2);
		layer_msg2.x = layer_chat2.x + layer_chat2.getWidth(); layer_msg2.y = 20;		
	}
	if (layer_chat_room_background !== null){
		layer_chat_room_background.remove();
		layer_chat_room_background = null;
		layer_input = null;
		layer_input2 = null;
		showChatRoom();
	}
}
var is_chatting = false;
function onChatClicked(event){
	is_chatting = true;
	showChatRoom();
}
function onChatClose(event){
	is_chatting = false;
	closeChatRoom();
}

var layer_input = null;
var layer_input2 = null;
var input_text = ""; // 保存的上次文本
function showChatRoom(){
	if (layer_chat_room_background != null){
		return;
	}
	layer_chat_room_background = new LSprite();
	baseLayer.addChild(layer_chat_room_background);
	layer_chat_room_background.graphics.drawRect(1, "#162737", [0, 0, SCREEN_WIDTH, 755], true, "#162737");
	layer_chat_room_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	
	
	
	var layer_chat_title = new LSprite();
	layer_chat_room_background.addChild(layer_chat_title);
	layer_chat_title.graphics.drawRect(1, "#162737", [0, 0, SCREEN_WIDTH, 90], true, "#162737");
	
	var layer_split = spriteWithImage(IMG_OPEN_LIST_2);
	layer_chat_title.addChild(layer_split);
	layer_split.x = 213; layer_split.y = 23;
	
	var layer_selected = spriteWithImage(IMG_OPEN_LIST_1);
	layer_chat_title.addChild(layer_selected);
	layer_selected.x = 0; layer_selected.y = 83;

	
	var layer_lts = labelWithText("聊天室", 26, "#6fd5fb");
	layer_chat_title.addChild(layer_lts);
	layer_lts.x = 40; layer_lts.y = 30;
	
	layer_conetnt = new LListView();
	layer_chat_room_background.addChild(layer_conetnt);
	layer_conetnt.x = 0;
	layer_conetnt.y = 90;
	layer_conetnt.maxPerLine = 1;
	layer_conetnt.cellWidth = 640;
	layer_conetnt.cellHeight = 60;
	layer_conetnt.resize(SCREEN_WIDTH, 600);
	layer_conetnt.arrangement = LListView.Direction.Horizontal;
	layer_conetnt.movement = LListView.Direction.Vertical;
	layer_conetnt.graphics.drawRect(0, "#000000", [0, 0, layer_conetnt.clipping.width,layer_conetnt.clipping.height]);
	
	var chat_count = 0;
	for (var i = arr_chat_msg.length - 1; i >= 0; i--){
		var color = "#1c324a";
		if (i % 2 === 1){
			color = "#223850";
		}
		var len = getLength(arr_chat_name[i]+':');
		var arr = getMsgArray(arr_chat_msg[i], len);
		layer_conetnt.insertChildView(chatMsgItem(arr_chat_name[i]+':', arr[0], color));
		chat_count += arr.length;
		for (var j=1,maxLen=arr.length; j<maxLen; ++j){
			layer_conetnt.insertChildView(chatMsgItem('', arr[j], color));
		}		
	}

	var count = chat_count - 10;
	if (count > 0){
		layer_conetnt.clipping.y = layer_conetnt.cellHeight * count;
	}
	
	layer_input = new LTextField();	
	layer_input2 = new LTextField();	
	layer_chat_room_background.addChild(layer_input);
	layer_input2.visible = false;
	layer_chat_room_background.addChild(layer_input2);
	layer_input.x = 10; layer_input.y = 690; layer_input.size = 30;
	layer_input2.x = 10; layer_input2.y = 690; layer_input2.size = 30;
	var labelLayer = new LSprite();
	labelLayer.graphics.drawRoundRect(1, "#ffffff", [0, 0, 437, 63, 10], true, "#ffffff");
	layer_input.setType(LTextFieldType.INPUT, labelLayer);

	var labelLayer = new LSprite();
	labelLayer.graphics.drawRoundRect(0, "#ffffff", [0, 0, 0, 0, 0], false, "#ffffff");
	layer_input2.setType(LTextFieldType.INPUT, labelLayer);
	
	layer_input.addEventListener(LTextEvent.TEXT_INPUT, function (e) {
		if (e.currentTarget.text.length > 39){
			e.currentTarget.text = input_text;
			e.currentTarget.updateInput();
			e.preventDefault();
			msgBox("提示：发送字数限制为40个字!");
		}else{
			input_text = e.currentTarget.text;
		}

		if(e.keyCode == 13){
			layer_input2.focus();
			onSendClicked(e);
		}
	})
	
	var layer_send = spriteWithImage(IMG_CHAT_SEND);
	layer_chat_room_background.addChild(layer_send);
	layer_send.x = 455; layer_send.y = 688;
	layer_send.addEventListener(LMouseEvent.MOUSE_DOWN, onSendClicked);
	
	var layer_close = spriteWithImage(IMG_OPEN_LIST_4);
	layer_chat_room_background.addChild(layer_close);
	layer_close.x = 0; layer_close.y = 755;
	layer_close.addEventListener(LMouseEvent.MOUSE_DOWN, onChatClose);
}

function getNeedStr(str){
	var fontLimit = 350;
	var retArr = new Array();
	var currLen = 0;
	var j = 0;
	var end = false;
	for(var i=0,len=str.length; i < len;i++)
	{
		currLen += getLength(str[i]);

		if (currLen >= fontLimit){
			var subString = str.substr(j, i - j + 1)
			if (retArr.length == 1){
				retArr.push(subString.substr(0, subString.length - 1) + '...');
				end = true;
				break;
			}
			retArr.push(subString);
			j = i + 1;
			currLen = 0;
		}
	}

	if (!end){
		var subString = str.substr(j);
		if (subString.length > 0){
			retArr.push(str.substr(j));
		}		
	}	

	return retArr;
}
function getMsgArray(str, nameLen){
	var fontLimit = 600;
	var retArr = new Array();
	var currLen = nameLen;
	var j = 0;
	var end = false;
	for(var i=0,len=str.length; i < len;i++)
	{
		currLen += getLength(str[i]);

		if (currLen >= fontLimit){
			var subString = str.substr(j, i - j + 1)
			if (retArr.length == 1){
				retArr.push(subString.substr(0, subString.length - 1) + '...');
				end = true;
				break;
			}
			retArr.push(subString);
			j = i + 1;
			currLen = 0;
		}
	}

	if (!end){
		var subString = str.substr(j);
		if (subString.length > 0){
			retArr.push(str.substr(j));
		}		
	}	

	return retArr;
}

function getLength(str){
	var len = 0;
	var otx = LGlobal.canvas
	var oldFont = otx.font;
	trace(otx.font)
	otx.font = '24px HG行書体';
	len = otx.measureText(str).width;
	otx.font = oldFont;
	
	return len;
}
function onSendClicked(event){
	if (chat_room_fobid > 0){
		msgBox("提示：您已被禁言！");
		return;
	}
	if (gCdFlag){
		msgBox("提示：发送太频繁了,请稍后再发！");
	}else{
		if (layer_input != null && layer_input.text.length > 0){
			doChat(layer_input.text);
		}	
		else {
			msgBox("提示：请先输入聊天内容！");
		}
	}
}

// 启动发送倒计时
var gCdFlag = false; // 是否在cd标识
function startChatCD(){
	gCdFlag = true;
	var cdTimer = new LTimer(10000, 1);
	cdTimer.addEventListener(LTimerEvent.TIMER, cdTimeout);
    cdTimer.start();
}

// cd 到
function cdTimeout(){
	gCdFlag = false;
}

function closeChatRoom(){
	if (layer_chat_room_background != null){
		layer_chat_room_background.remove();
		layer_chat_room_background = null;
		layer_input = null;
		layer_input2 = null;
	}
}
function chatMsgItem(name, msg, color){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 640, 60], true,color);
	var layer_name = labelWithText(name, 24, "#ffa900");
	child.addChild(layer_name);
	layer_name.x = 10;	layer_name.y = 20;	
	var layer_msg = labelWithText(msg, 24, "#ffffff");
	child.addChild(layer_msg);
	layer_msg.x = layer_name.x + layer_name.getWidth() + 5; layer_msg.y = 20;
	
	return child;
}
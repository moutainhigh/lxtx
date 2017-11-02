var MAX_CHAT_MSG_COUNT = 10;
var arr_chat_name = new Array();
var arr_chat_msg = new Array();
var chat_name1 = "";
var chat_name2 = "";
var chat_msg1 = "";
var chat_msg2 = "";
var layer_chat_room_background = null;
var chat_room_fobid = 0;
var layer_conetnt = null;
function setChatMsg(userName, msg){
	chat_name2 = chat_name1;
	chat_msg2 = chat_msg1;
	chat_name1 = userName;
	chat_msg1 = msg;

	if (chat_msg1.length > 0){
		arr_chat_msg.unshift(chat_msg1);
		if (arr_chat_msg.length > MAX_CHAT_MSG_COUNT){
			arr_chat_msg.splice(MAX_CHAT_MSG_COUNT, arr_chat_msg.length - MAX_CHAT_MSG_COUNT);
		}
		arr_chat_name.unshift(chat_name1);
		if (arr_chat_name.length > MAX_CHAT_MSG_COUNT){
			arr_chat_name.splice(MAX_CHAT_MSG_COUNT, arr_chat_name.length - MAX_CHAT_MSG_COUNT);
		}
	}

	if (layer_chat_room_background !== null){
		if (layer_input2){
			if(LGlobal.inputTextBox){
				LGlobal.inputTextBox.blur();
				LGlobal.inputTextBox.style.display = NONE;
			}
		}
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
	layer_chat_room_background.x = 20;
	layer_chat_room_background.y = 255;
	layer_background.addChild(layer_chat_room_background);
	layer_chat_room_background.graphics.drawRect(0, "#ff0000", [0, 0, 250, 270], false, "#162737");
	layer_chat_room_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	
	
	var img = spriteWithImage(IMG_MASTER_CHAT);
	img.x = 0;img.y = 0;
	layer_chat_room_background.addChild(img);

	var img = spriteWithImage(IMG_CHAT_BG);
	img.x = 10;img.y = layer_chat_room_background.getHeight() - 28;
	layer_chat_room_background.addChild(img);
	
	layer_conetnt = new LListView();
	layer_chat_room_background.addChild(layer_conetnt);
	layer_conetnt.x = 7;
	layer_conetnt.y = 7;
	layer_conetnt.maxPerLine = 1;
	layer_conetnt.cellWidth = 230;
	layer_conetnt.cellHeight = 30;
	layer_conetnt.resize(230, 220);
	layer_conetnt.arrangement = LListView.Direction.Horizontal;
	layer_conetnt.movement = LListView.Direction.Vertical;
	layer_conetnt.graphics.drawRect(0, "#000000", [0, 0, layer_conetnt.clipping.width,layer_conetnt.clipping.height]);
	var scrollBarVertical = new LListScrollBar(new LPanel("#9370DB", 0, 0), new LPanel("#9400D3", 0, 0), LListView.ScrollBarCondition.OnlyIfNeeded);
	layer_conetnt.setVerticalScrollBar(scrollBarVertical);

	for (var i = arr_chat_msg.length - 1; i >= 0; i--){
		var color = "#1c324a";
		if (i % 2 === 1){
			color = "#223850";
		}
		var len = getLength(arr_chat_name[i]);
		var arr = getMsgArray(arr_chat_msg[i], len);
		layer_conetnt.insertChildView(chatMsgItem(arr_chat_name[i]+':', arr[0], color));

		for (var j=1,maxLen=arr.length; j<maxLen; ++j){
			layer_conetnt.insertChildView(chatMsgItem('', arr[j], color));
		}
	}

	var count = layer_conetnt._ll_items.length - 7;
	if (count > 0){
		layer_conetnt.clipping.y = layer_conetnt.cellHeight * count;
	}	
	
	layer_input = new LTextField();	
	layer_input.color = "#ffffff";
	layer_input2 = new LTextField();
	img.addEventListener(LMouseEvent.MOUSE_UP, function(){
		if (layer_input){
			layer_input.focus();
		}
	})
	img.addChild(layer_input);
	layer_input2.visible = false;
	img.addChild(layer_input2);
	
	var labelLayer = new LSprite();
	labelLayer.graphics.drawRoundRect(0, "#ffffff", [0, 0, 120, 20, 10], false, "#ffffff");
	layer_input.setType(LTextFieldType.INPUT, labelLayer);

	var labelLayer = new LSprite();
	labelLayer.graphics.drawRoundRect(0, "#ffffff", [0, 0, 0, 0, 0], false, "#ffffff");
	layer_input2.setType(LTextFieldType.INPUT, labelLayer);
	
	layer_input.addEventListener(LTextEvent.TEXT_INPUT, function (e) {
		if (e.currentTarget.text.length > 32){
			e.currentTarget.text = input_text;
			e.currentTarget.updateInput();
			e.preventDefault();
			msgBox("提示：发送字数限制为33个字!");
		}else{
			input_text = e.currentTarget.text;
		}

		if(e.keyCode == 13){
			if (layer_input2){
				layer_input2.focus();
			}

			if(LGlobal.inputTextBox){
				LGlobal.inputTextBox.blur();
				LGlobal.inputTextBox.style.display = NONE;
			}
			onSendClicked(e);
		}
	})

	// layer_input.addEventListener(LFocusEvent.FOCUS_IN, onChatFocus);
	// layer_input.addEventListener(LFocusEvent.FOCUS_OUT, outChatFocus);
	
	var sendBg = panelWithSprite(imgSize[IMG_CHAT_SEND].w * 1.1, imgSize[IMG_CHAT_SEND].h * 2)
	sendBg.x = layer_chat_room_background.getWidth() - sendBg.getWidth();
	sendBg.y = layer_chat_room_background.getHeight() - sendBg.getHeight();
	layer_chat_room_background.addChild(sendBg);

	var layer_send = spriteWithImage(IMG_CHAT_SEND);
	layer_send.y = sendBg.getHeight() - layer_send.getHeight();
	sendBg.addChild(layer_send);
	sendBg.addEventListener(LMouseEvent.MOUSE_UP, onSendClicked);
	
	layer_input.focus()
	resetInputTextBox()

	if(LGlobal.inputTextBox){
		LGlobal.inputTextBox.blur();
		LGlobal.inputTextBox.style.display = NONE;
	}
}

function resetInputTextBox(){
	if(window.innerWidth < window.innerHeight){
		if (LGlobal.inputTextBox){
			LGlobal.inputTextBox.style.setProperty("transform", "rotate(-90deg)");
			LGlobal.inputTextBox.style.setProperty("transform-origin", "0% 0%");	
			LGlobal.inputTextBox.style.setProperty("position", "absolute");
			LGlobal.inputTextBox.style.setProperty("top", "0px");
			LGlobal.inputTextBox.style.setProperty("left", "0px");			
		}
	}else{
		if (LGlobal.inputTextBox){
			LGlobal.inputTextBox.style.setProperty("transform", "rotate(0deg)");
			LGlobal.inputTextBox.style.setProperty("transform-origin", "0% 0%");
			LGlobal.inputTextBox.style.setProperty("position", "absolute");
			LGlobal.inputTextBox.style.setProperty("top", "0px");
			LGlobal.inputTextBox.style.setProperty("left", "0px");
		}
	}
}

//保留最新30条聊天记录,多余的忽略
var maxMsgArrAmountSize = 30;
var arrMsgAmount = new Array();
function addContent(name, msg){
	var color = "#1c324a";
	if (layer_conetnt._ll_items.length % 2 === 1){
		color = "#223850";
	}
	if (name == curr_self_name){
		if(LGlobal.inputTextBox){
			LGlobal.inputTextBox.blur();
			LGlobal.inputTextBox.style.display = NONE;
		}
	}
	name = name.replace(/[\r\n]/g, "");
	msg = msg.replace(/[\r\n]/g, "")
	var len = getLength(name+':');
	var arr = getMsgArray(msg, len);
	//超过30条，先把旧的那条完整的聊天信息删除
	if (arrMsgAmount.length >= maxMsgArrAmountSize)
	{
		var msgAmount = arrMsgAmount[0];
		arrMsgAmount.splice(0, 1);
		var items = layer_conetnt.getItems();
		for (var k = Math.min(msgAmount,items.length) - 1; k >= 0 ; k--)
		{
			layer_conetnt.deleteChildView(items[k]);
		}
	}
	layer_conetnt.insertChildView(chatMsgItem(name+':', arr[0], color));
	for (var j=1,maxLen=arr.length; j<maxLen; ++j){
		layer_conetnt.insertChildView(chatMsgItem('', arr[j], color));
	}
	//记录一下这条聊天记录有多少行
	arrMsgAmount.push(arr.length);
	
	var count = layer_conetnt._ll_items.length - 7;
	if (count > 0){
		layer_conetnt.clipping.y = layer_conetnt.cellHeight * count;
	}
}

function getMsgArray(str, nameLen){
	var fontLimit = 216;
	var retArr = new Array();
	var currLen = nameLen;
	var j = 0;
	var end = false;
	for(var i=0,len=str.length; i < len;i++)
	{
		var tmpLen = getLength(str[i]);
		currLen += tmpLen;

		if (currLen >= fontLimit){
			if (currLen > fontLimit){
				var subString = str.substr(j, i - j)
				if (retArr.length == 2){
					retArr.push(subString.substr(0, subString.length - 1) + '...');
					end = true;
					break;
				}
				retArr.push(subString);
				j = i;
				currLen = tmpLen;
			}else{
				var subString = str.substr(j, i - j + 1)
				if (retArr.length == 2){
					retArr.push(subString.substr(0, subString.length - 1) + '...');
					end = true;
					break;
				}
				retArr.push(subString);
				j = i + 1;
				currLen = 0;
			}			
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
	otx.font = '18px HG行書体';
	len = otx.measureText(str).width;
	otx.font = oldFont;
	
	return len;
}

// 输入控件焦点获得
function onChatFocus(e){
	var text = e.currentTarget;	
	if (LGlobal.os.toLowerCase() != 'iPAD'.toLowerCase() && LGlobal.mobile && !text.diffY){
		text.y -= 20;
		text.diffY = true;
	}
}

// 输入控件焦点失去
function outChatFocus(e){
	var text = e.currentTarget;		
	if (LGlobal.os.toLowerCase() != 'iPAD'.toLowerCase() && LGlobal.mobile && text.diffY){
		text.y += 20;
		text.diffY = false;
	}

	myScrollTop();
}
// 删除左右空格
function trimStr(str){
	return str.replace(/(^\s*)|(\s*$)/g,"");
}
function onSendClicked(event){
	if (layer_input2){
		layer_input2.focus();
	}	
	if (chat_room_fobid > 0){
		msgBox("提示：您已被禁言！");
		return;
	}
	if (gCdFlag){
		msgBox("提示：发送太频繁了,请稍后再发！");
	}else{		
		if (layer_input != null && layer_input.text.length > 0){
			var msg = layer_input.text;
			msg = trimStr(msg);
			if (msg.length > 0){			
				doChat(msg);
				layer_input.text = '';

				// startChatCD();
			}else{
				layer_input.text = '';
				msgBox("提示：请先输入聊天内容！");
			}
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

function chatMsgItem(name, msg, color){
	var child = new LListChildView();
	child.graphics.drawRect(1, color, [0, 0, 230, 30 ], false,color);
	var layer_name = labelWithText(name, 18, "#efb810");
	child.addChild(layer_name);
	layer_name.y = 5;	

	var layer_msg = labelWithText(msg, 18, "#ffffff");
	child.addChild(layer_msg);
	layer_msg.x = layer_name.x + layer_name.getWidth() + 2; 
	layer_msg.y = 5;
	
	return child;
}
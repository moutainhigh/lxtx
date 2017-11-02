var layer_get_chips_background = null;
var layer_self_chips = null;
var layer_self_money = null;
var layer_get_input = null;
var layer_save_input = null;
function showBank(){
	if (layer_get_chips_background != null){
		return;
	}
	layer_get_chips_background = new LSprite();
	baseLayer.addChild(layer_get_chips_background);
	layer_get_chips_background.graphics.drawRect(1, "#223850", [0, 0, SCREEN_WIDTH, 585], true, "#223850");
	layer_get_chips_background.addEventListener(LMouseEvent.MOUSE_DOWN, onClear);	
	
	
	var layer_bank_title = new LSprite();
	layer_get_chips_background.addChild(layer_bank_title);
	layer_bank_title.graphics.drawRect(1, "#162737", [0, 0, SCREEN_WIDTH, 90], true, "#162737");
	
	var layer_split = spriteWithImage(IMG_OPEN_LIST_2);
	layer_bank_title.addChild(layer_split);
	layer_split.x = 213; layer_split.y = 23;
	
	var layer_selected = spriteWithImage(IMG_OPEN_LIST_1);
	layer_bank_title.addChild(layer_selected);
	layer_selected.x = 0; layer_selected.y = 83;
	
	var layer_yh = labelWithText("银行", 26, "#6fd5fb");
	layer_bank_title.addChild(layer_yh);
	layer_yh.x = 40; layer_yh.y = 30;
	
	/*
	存款
	*/
	var layer_save_chips_title_background = new LSprite();
	layer_get_chips_background.addChild(layer_save_chips_title_background);
	layer_save_chips_title_background.graphics.drawRect(1, "#1c324a", [0, 90, SCREEN_WIDTH, 70], true, "#1c324a");
	var layer_save_chips_title = labelWithText("我要存款", 24, "#6fd5fb");
	layer_save_chips_title_background.addChild(layer_save_chips_title);
	layer_save_chips_title.x = 20; layer_save_chips_title.y = 113;
	
	layer_self_chips = labelWithText("可存金币:"+splitNumber(curr_self_chips), 24, "#ffffff");
	layer_get_chips_background.addChild(layer_self_chips);
	layer_self_chips.x = 20; layer_self_chips.y = 184;
	
	var btn_save_all = labelWithText("全部存入", 26, "#6fd5fb");
	layer_get_chips_background.addChild(btn_save_all);
	btn_save_all.x = 330;  btn_save_all.y = 182;
	btn_save_all.addEventListener(LMouseEvent.MOUSE_DOWN, onSaveAllClicked);
	var btn_save_all_underline = labelWithText("_________", 26, "#6fd5fb");
	layer_get_chips_background.addChild(btn_save_all_underline);
	btn_save_all_underline.x = 330;  btn_save_all_underline.y = 182;
	
	layer_save_input = new LTextField();	
	layer_get_chips_background.addChild(layer_save_input);
	layer_save_input.x = 20; layer_save_input.y = 232; layer_save_input.size = 30;
	var labelLayer1 = new LSprite();
	labelLayer1.graphics.drawRoundRect(1, "#ffffff", [0, 0, 437, 63, 10], true, "#ffffff");
	layer_save_input.setType(LTextFieldType.INPUT, labelLayer1);
	
	var btn_save = spriteWithImage(IMG_SAVE_CHIPS);
	layer_get_chips_background.addChild(btn_save);
	btn_save.x = 465; btn_save.y = 232;
	btn_save.addEventListener(LMouseEvent.MOUSE_DOWN, onSaveClicked);
	
	/**
	取款
	*/
	var layer_get_money_title_background = new LSprite();
	layer_get_chips_background.addChild(layer_get_money_title_background);
	layer_get_money_title_background.graphics.drawRect(1, "#1c324a", [0, 330, SCREEN_WIDTH, 70], true, "#1c324a");
	var layer_save_chips_title = labelWithText("", 24, "#6fd5fb");
	layer_get_money_title_background.addChild(layer_save_chips_title);
	layer_save_chips_title.x = 20; layer_save_chips_title.y = 353;
	
	layer_self_money = labelWithText("可取金币:"+splitNumber(curr_self_money), 24, "#ffffff");
	layer_get_chips_background.addChild(layer_self_money);
	layer_self_money.x = 20; layer_self_money.y = 424;
	
	var btn_get_all = labelWithText("全部取出", 26, "#6fd5fb");
	layer_get_chips_background.addChild(btn_get_all);
	btn_get_all.x = 330;  btn_get_all.y = 422;
	btn_get_all.addEventListener(LMouseEvent.MOUSE_DOWN, onGetAllClicked);
	var btn_get_all_underline = labelWithText("_________", 26, "#6fd5fb");
	layer_get_chips_background.addChild(btn_get_all_underline);
	btn_get_all_underline.x = 330;  btn_get_all_underline.y = 422;
	
	layer_get_input = new LTextField();	
	layer_get_chips_background.addChild(layer_get_input);
	layer_get_input.x = 20; layer_get_input.y = 472; layer_get_input.size = 30;
	var labelLayer2 = new LSprite();
	labelLayer2.graphics.drawRoundRect(1, "#ffffff", [0, 0, 437, 63, 10], true, "#ffffff");
	layer_get_input.setType(LTextFieldType.INPUT, labelLayer2);
	
	var btn_get = spriteWithImage(IMG_GET_MONEY);
	layer_get_chips_background.addChild(btn_get);
	btn_get.x = 465; btn_get.y = 472;
	btn_get.addEventListener(LMouseEvent.MOUSE_DOWN, onGetClicked);
	
	
	var layer_close = spriteWithImage(IMG_OPEN_LIST_4);
	layer_get_chips_background.addChild(layer_close);
	layer_close.x = 0; layer_close.y = 585;
	layer_close.addEventListener(LMouseEvent.MOUSE_DOWN, closeBank);
}
function IsNum(s)
{
    if(s!=null){
        var r,re;
        re = /\d*/i; //\d表示数字,*表示匹配多个数字
        r = s.match(re);
        return (r==s)?true:false;
    }
    return false;
}
function onSaveAllClicked(event){
	layer_save_input.text = String(curr_self_chips);
}
function onGetAllClicked(event){
	layer_get_input.text = String(curr_self_money);
}
function closeBank(event){
	if (layer_get_chips_background != null){
		layer_get_chips_background.remove();
		layer_get_chips_background = null;
		layer_self_chips = null;
		layer_self_money = null;
		layer_save_chips = null;
		layer_get_money = null;
	}
}
function onSaveClicked(event){
	var saveNum = 0; 
	if (layer_save_input == null){
		return;
	}
	if (curr_self_id == curr_master_id){
		msgBox2("提示：当前自己是庄家不能存款！");
		return;
	}
	if (layer_save_input.text.length == 0){
		msgBox2("提示：请输入存款金额！");
		return;
	}	
	if (false == IsNum(layer_save_input.text)){
		msgBox2("提示：只能输入整数！");
		return;
	}
	saveNum = parseInt(layer_save_input.text);
	if (saveNum > curr_self_chips){
		msgBox2("提示：存款金额不能大于剩余金币!");
		return;
	}
	doGetOrSaveMoeny(0, saveNum);
}
function onGetClicked(event){
	var getNum = 0;
	if (layer_get_input == null){
		return;
	}
	if (layer_get_input.text.length == 0){
		msgBox2("提示：请输入取款金额！");
		return;
	}
	if (false == IsNum(layer_get_input.text)){
		msgBox2("提示：只能输入整数！");
		return;
	}
	getNum = parseInt(layer_get_input.text);
	if (getNum > curr_self_money){
		msgBox2("取款金额不能大于银行金币!");
		return;
	}	
	doGetOrSaveMoeny(getNum, 0);
}













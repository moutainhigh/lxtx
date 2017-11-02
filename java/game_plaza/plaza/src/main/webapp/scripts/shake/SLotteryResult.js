/*
*  抽奖结果参数定义
*/
var IDX_SX_SHU 		= 1;
var IDX_SX_NIU 		= 2;
var IDX_SX_HU 		= 3;
var IDX_SX_TU 		= 4;
var IDX_SX_LONG 	= 5;
var IDX_SX_SHE 		= 6;	
var IDX_SX_MA 		= 7;
var IDX_SX_YANG 	= 8;
var IDX_SX_HOU 		= 9;
var IDX_SX_JI 		= 10;
var IDX_SX_GOU 		= 11;
var IDX_SX_ZHU 		= 12;

var IDX_WX_JIN 		= 13;
var IDX_WX_MU 		= 14;
var IDX_WX_SHUI 	= 15;
var IDX_WX_HUO 		= 16;
var IDX_WX_TU 		= 17;

var IDX_TIAN 		= 18;
var IDX_DI			= 19;

var IDX_1V18		= 20;
var IDX_1V88		= 21;

var IDX_SET_MASTER  = 22;
var IDX_GET_CHIP	= 23;

var TAG_RANGE_SX	= 0;
var TAG_RANGE_WX	= 1;
var TAG_RANGE_TD	= 2;
var TAG_RANGE_TS	= 3;


var IDX_DICE_1 		= 1;
var IDX_DICE_2 		= 2;
var IDX_DICE_3 		= 3;
var IDX_DICE_4 		= 4;
var IDX_DICE_5 		= 5;
var IDX_DICE_6 		= 6;

function SLotteryResult(index, name, numPos,  angle){
	this.index = index; 	//索引，用于对其服务端协议
	this.name = name; 		//名称
	this.numPos = numPos; //自己下注金币数量
	this.angle = angle;		//转盘转至该结果时所需要的角度
}
var lotteryResult = new Array(
	new SLotteryResult(IDX_1V18, 		"1赔18", 	new SPoint(36, 23), 		0),
	new SLotteryResult(IDX_1V88, 		"1赔88", 	new SPoint(535, 23), 		0),

	new SLotteryResult(IDX_SX_SHU, 		"鼠", 		new SPoint(283, 61), 	 	360),
	new SLotteryResult(IDX_SX_NIU, 		"牛", 		new SPoint(409, 91), 		330),
	new SLotteryResult(IDX_SX_HU, 		"虎", 		new SPoint(493, 175), 	 	300),
	new SLotteryResult(IDX_SX_TU, 		"兔", 		new SPoint(522, 306), 	 	270),
	new SLotteryResult(IDX_SX_LONG, 	"龙", 		new SPoint(491, 413), 	 	240),
	new SLotteryResult(IDX_SX_SHE, 		"蛇", 		new SPoint(400, 507), 	 	210),
	new SLotteryResult(IDX_SX_MA, 		"马", 		new SPoint(282, 529), 	 	180),
	new SLotteryResult(IDX_SX_YANG, 	"羊", 		new SPoint(161, 494), 		150),
	new SLotteryResult(IDX_SX_HOU, 		"猴", 		new SPoint(76, 418), 		120),
	new SLotteryResult(IDX_SX_JI, 		"鸡", 		new SPoint(36, 290), 	 	90),
	new SLotteryResult(IDX_SX_GOU, 		"狗", 		new SPoint(76, 172), 		60),
	new SLotteryResult(IDX_SX_ZHU, 		"猪", 		new SPoint(169, 79), 	 	30),

	new SLotteryResult(IDX_TIAN, 		"天",		new SPoint(277, 260), 	 	360),
	new SLotteryResult(IDX_DI, 			"地", 		new SPoint(277, 349), 		180),

	new SLotteryResult(IDX_WX_JIN, 		"金", 		new SPoint(203, 203), 		396),
	new SLotteryResult(IDX_WX_MU, 		"木", 		new SPoint(360, 190), 		324),
	new SLotteryResult(IDX_WX_SHUI, 	"水", 		new SPoint(413, 328), 	 	252),
	new SLotteryResult(IDX_WX_HUO, 		"火", 		new SPoint(274, 426), 	 	180),
	new SLotteryResult(IDX_WX_TU, 		"土", 		new SPoint(153, 338), 	 	108)
);
function getAngleFromLotteryResult(result){
	for(var i = 0; i < lotteryResult.length; i++){
		var temp = lotteryResult[i];
		if (temp.index == result)
			return temp.angle;
	}
	return 0;
}
function getNameFromLotteryResult(result){
	for(var i = 0; i < lotteryResult.length; i++){
		var temp = lotteryResult[i];
		if (temp.index == result)
			return temp.name;
	}
	return 0;
}
function getNumPosFromLotteryResult(result){
	return ChipsSetPos[result];
}
function getIndexByAngle(angle, tag){
	var idx = -1;
	if (tag == TAG_RANGE_TS){
		if (angle > 290 && angle < 340){
			idx = IDX_1V18;
		}			
		else if (angle > 20 && angle < 70){
			idx = IDX_1V88;
		}			
		else if (angle > 110 && angle < 150){
			idx = IDX_GET_CHIP;
		}
		else if (angle > 200 && angle < 250){
			idx = IDX_SET_MASTER;
		}			
	}
	else if (tag == TAG_RANGE_SX){
		if (angle > 345){
			idx = IDX_SX_SHU;
		}
		else if (angle < 15){
			idx = IDX_SX_SHU;
		}
		else if (angle > 15 && angle < 45){
			idx = IDX_SX_NIU;
		}
		else if (angle > 45 && angle < 75){
			idx = IDX_SX_HU;
		}
		else if (angle > 75 && angle < 105){
			idx = IDX_SX_TU;
		}
		else if (angle > 105 && angle < 135){
			idx = IDX_SX_LONG;
		}
		else if (angle > 135 && angle < 165){
			idx = IDX_SX_SHE;
		}
		else if (angle > 165 && angle < 195){
			idx = IDX_SX_MA;
		}
		else if (angle > 195 && angle < 225){
			idx = IDX_SX_YANG;
		}
		else if (angle > 225 && angle < 255){
			idx = IDX_SX_HOU;
		}
		else if (angle > 255 && angle < 285){
			idx = IDX_SX_JI;
		}
		else if (angle > 285 && angle < 315){
			idx = IDX_SX_GOU;
		}
		else if (angle > 315 && angle < 345){
			idx = IDX_SX_ZHU;
		}
	}
	else if (tag == TAG_RANGE_WX){
		if (angle > 288){
			idx = IDX_WX_JIN;
		}
		else if (angle < 72){
			idx = IDX_WX_MU;
		}
		else if (angle > 72 && angle < 144){
			idx = IDX_WX_SHUI;
		}
		else if (angle > 144 && angle < 216){
			idx = IDX_WX_HUO;
		}
		else if (angle > 216 && angle < 288){
			idx = IDX_WX_TU;
		}		
	}
	else if (tag == TAG_RANGE_TD){
		if (angle > 90 && angle < 270){
			idx = IDX_DI;
		}			
		else {
			idx = IDX_TIAN;
		}			
	}
	return idx;
}


function getDicePicFromLotteryResult(result1, result2, result3, dice_1_pic, dice_2_pic, dice_3_pic){
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
    layer_last_result_background.addChild(dice_1_pic);
    dice_1_pic.x = 20; dice_1_pic.y = 45;

    dice_2_pic = spriteWithImage(map_array[result2]);
    layer_last_result_background.addChild(dice_2_pic);
    dice_2_pic.x = 55; dice_2_pic.y = 45;

    dice_3_pic = spriteWithImage(map_array[result3]);
    layer_last_result_background.addChild(dice_3_pic);
    dice_3_pic.x = 90; dice_3_pic.y = 45;
}

function getResultText(result_1, result_2, result_3) {
    var use_text = "";
    var count = result_1 + result_2 + result_3;
    if (result_1 == result_2 && result_2 == result_3) {
        use_text = "豹子";
    } else if (count > 10 && count < 18) {
        use_text = "大";
    } else if (count > 3 && count < 11) {
        use_text = "小";
    }
    return use_text;
}

function setLastResultTypeStr(result_1, result_2, result_3){
    var use_text = getResultText(result_1, result_2, result_3);
    var layer_last_result_tip_1 = labelWithText(use_text, 24, "#ff4d30");
    layer_last_result_background.addChild(layer_last_result_tip_1);
    layer_last_result_tip_1.x = 160; layer_last_result_tip_1.y = 45;
}
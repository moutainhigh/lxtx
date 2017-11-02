/**
 * Created by wodong_0013 on 2016/10/15.
 */
/*
 *金币类
 */
function SYilouTips(index){
    base(this,LSprite,[]);
    this.selfBackground = null;
    this.selfChipsLabel = null;
    this.index = index;

    this.selfBackground = spriteWithImage(IMG_YIY_YILOU_TIPS_BACK);
    this.addChild(this.selfBackground);

    var pos = YiLouTipsPosConfig[this.index];
    this.selfBackground.x = pos.x; this.selfBackground.y = pos.y + 20;

    this.selfChipsLabel = labelWithText(getPosYilouText(index), 13, "#000000");
    this.selfBackground.addChild(this.selfChipsLabel);
    this.selfChipsLabel.x = 15; this.selfChipsLabel.y = 4;
}

function getPosYilouText(pos){
    if(pos <= IDX_SAME_TWO_ONE_6 && IDX_SAME_TWO_ONE_1 <= pos){
        return "对子 "+ String(pos-IDX_SAME_TWO_ONE_1+1) + " 已900期未出";
    }
    if(pos <= IDX_SAME_THREE_ONE_6&& IDX_SAME_THREE_ONE_1 <= pos){
        return "豹子 "+ String(pos-IDX_SAME_THREE_ONE_1+1) + " 已900期未出";
    }
    if(pos <= IDX_COUNT_17&& IDX_COUNT_4 <= pos){
        return "和值 "+ String(pos-IDX_COUNT_4+4) + " 已900期未出";
    }
    return "null";
}

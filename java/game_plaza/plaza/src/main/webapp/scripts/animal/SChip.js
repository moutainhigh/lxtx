/*
*金币类
*/
function SChip(index){
	base(this,LSprite,[]);
	this.selfBackground = null;
	this.selfChipsLabel = null;
	this.selfChipsNum = 0;
	this.otherBackground = null;
	this.otherChipsLabel = null;
	this.otherChipsNum = 0;
	this.index = index;
}
SChip.prototype.addChips = function(num, isSelf){
	if (isSelf == true){
		this.selfChipsNum += num;
		
		var strNum = splitNumberNotPlus(this.selfChipsNum);
		if (this.selfBackground == null){
			this.selfBackground = spriteWithImage(IMG_NUMBERS_BACKGROUND);
			this.addChild(this.selfBackground);
			var pos = getNumPosFromLotteryResult(this.index);
			this.selfBackground.x = pos.x; this.selfBackground.y = pos.y + 24;
				
			this.selfChipsLabel = labelWithText(strNum, 18, "#80dc6d");
			this.selfBackground.addChild(this.selfChipsLabel);
			this.selfChipsLabel.x = 5; this.selfChipsLabel.y = 2;
		}
		else {
			this.selfChipsLabel.setText(strNum);
		}
	}
	this.otherChipsNum += num;
	var oStrNum = splitNumberNotPlus(this.otherChipsNum);
	if (this.otherBackground == null){
		this.otherBackground = spriteWithImage(IMG_NUMBERS_BACKGROUND);
		this.addChild(this.otherBackground);
		var oPos = getNumPosFromLotteryResult(this.index);
		this.otherBackground.x = oPos.x; this.otherBackground.y = oPos.y;
				
		this.otherChipsLabel = labelWithText(oStrNum, 18, "#ffffff");
		this.otherBackground.addChild(this.otherChipsLabel);
		this.otherChipsLabel.x = 5; this.otherChipsLabel.y = 2;
	}
	else {
			this.otherChipsLabel.setText(oStrNum);
	}
}
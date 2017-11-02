/*
* ä¾¿æ·æ“ä½œæ–¹æ³•: imglist\imgSize\SCREEN_WIDTH\SCREEN_HEIGHTéœ€é¢„å®šä¹‰
*/

//åˆ›å»ºbitmap
function bitmapWithImage(imgName){
	var data = new LBitmapData(imglist[imgName]);
	var bitmap = new LBitmap(data); 
	return bitmap;
}
//åˆ›å»ºsprite
function spriteWithImage(imgName){
	var sprite = new LSprite();
	sprite.addChild(bitmapWithImage(imgName));
	return sprite;
}
//åˆ›å»ºsprite
function spriteWithBitmap(bitmap){
	var sprite = new LSprite();
	sprite.addChild(bitmap);
	return sprite;
}
//åˆ›å»ºlabel
function labelWithText(text, size, color)
{
	return new SLabel(text, size, color)
}
//ä¸Žå±å¹•ä¸­å¿ƒç‚¹çš„åè·X
function convertToScreenCenterX(w){
	return (SCREEN_WIDTH - w) / 2;	
}
//ä¸Žå±å¹•ä¸­å¿ƒç‚¹çš„åè·Y
function convertToScreenCenterY(h){
	return (SCREEN_HEIGHT - h) / 2;
}
//ä¸Žçˆ¶èŠ‚ç‚¹ä¸­å¿ƒç‚¹çš„åè·X
function converToParentX(w, parentW){
	return (parentW - w) / 2;
}
//ä¸Žçˆ¶èŠ‚ç‚¹ä¸­å¿ƒç‚¹çš„åè·Y
function converToParentY(h, parentH){
	return (parentH - h) / 2;
}
//ç”¨é€—å·åˆ†éš”çš„æ•´æ•° 
function splitNumber(num)
{
	var nStr = String(num);
	if (num > 0){
		nStr = "+"+String(num);
	}
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}
//ç”¨é€—å·åˆ†éš”çš„æ•´æ•°(æ— å‰ç¼€+)
function splitNumberNotPlus(num)
{
	var nStr = String(num);
	if (num > 0){
		nStr = String(num);
	}
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}
function onClear(event){
	
}

// 创建空的区域
function panelWithSprite(width, height, fillColor){
	var color = "#000000";
	var bFill= false;
	if (fillColor != null){
		color = fillColor;
		bFill = true;
	}
	var panel = new LSprite();
	panel.graphics.drawRect(0, "#ff0000", [0, 0, width, height], bFill,  color);

	return panel;
}

// 创建空的区域(三角形)
function panelWithSpriteForTriangle(first, second, third, fillColor){
	var color = "#000000";
	var bFill= false;
	if (fillColor != null){
		color = fillColor;
		bFill = true;
	}

	var panel = new LSprite();
	panel.graphics.drawVertices(0, "#ff0000", [first, second, third], bFill, color);

	return panel;
}

// 置顶窗口
function myScrollTop(){
	setTimeout(function(){
		window.scrollTo(0,0);
	},200);
}

function isSafariBrowser(){
	if (myBrowser() == 'Safari'){
		return true;
	}

	return false;
}

function myBrowser(){
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isOpera = userAgent.indexOf("Opera") > -1;
	if (isOpera) {
		return "Opera"
	}; //判断是否Opera浏览器
	if (userAgent.indexOf("Firefox") > -1) {
		return "FF";
	} //判断是否Firefox浏览器
	if (userAgent.indexOf("Chrome") > -1){
		return "Chrome";
	}
	if (userAgent.indexOf("Safari") > -1) {
		return "Safari";
	} //判断是否Safari浏览器
	if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
		return "IE";
	}; //判断是否IE浏览器

	return '';
}
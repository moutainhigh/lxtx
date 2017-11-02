/*
* 便捷操作方法: imglist\imgSize\SCREEN_WIDTH\SCREEN_HEIGHT需预定义
*/

//创建bitmap
function bitmapWithImage(imgName){
	var data = new LBitmapData(imglist[imgName],0,0,imgSize[imgName].w,imgSize[imgName].h);
	var bitmap = new LBitmap(data); 
	return bitmap;
}
//创建sprite
function spriteWithImage(imgName){
	var sprite = new LSprite();
	sprite.addChild(bitmapWithImage(imgName));
	return sprite;
}
//创建sprite
function spriteWithBitmap(bitmap){
	var sprite = new LSprite();
	sprite.addChild(bitmap);
	return sprite;
}
//创建label
function labelWithText(text, size, color)
{
	return new SLabel(text, size, color)
}
//与屏幕中心点的偏距X
function convertToScreenCenterX(w){
	return (SCREEN_WIDTH - w) / 2;	
}
//与屏幕中心点的偏距Y
function convertToScreenCenterY(h){
	return (SCREEN_HEIGHT - h) / 2;
}
//与父节点中心点的偏距X
function converToParentX(w, parentW){
	return (parentW - w) / 2;
}
//与父节点中心点的偏距Y
function converToParentY(h, parentH){
	return (parentH - h) / 2;
}
//用逗号分隔的整数 
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
//用逗号分隔的整数(无前缀+)
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
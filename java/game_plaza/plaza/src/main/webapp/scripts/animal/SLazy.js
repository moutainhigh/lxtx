/*
* ��ݲ�������: imglist\imgSize\SCREEN_WIDTH\SCREEN_HEIGHT��Ԥ����
*/

//����bitmap
function bitmapWithImage(imgName){
	var data = new LBitmapData(imglist[imgName],0,0,imgSize[imgName].w,imgSize[imgName].h);
	var bitmap = new LBitmap(data); 
	return bitmap;
}
//����sprite
function spriteWithImage(imgName){
	var sprite = new LSprite();
	sprite.addChild(bitmapWithImage(imgName));
	return sprite;
}
//����sprite
function spriteWithBitmap(bitmap){
	var sprite = new LSprite();
	sprite.addChild(bitmap);
	return sprite;
}
//����label
function labelWithText(text, size, color)
{
	return new SLabel(text, size, color)
}
//����Ļ���ĵ��ƫ��X
function convertToScreenCenterX(w){
	return (SCREEN_WIDTH - w) / 2;	
}
//����Ļ���ĵ��ƫ��Y
function convertToScreenCenterY(h){
	return (SCREEN_HEIGHT - h) / 2;
}
//�븸�ڵ����ĵ��ƫ��X
function converToParentX(w, parentW){
	return (parentW - w) / 2;
}
//�븸�ڵ����ĵ��ƫ��Y
function converToParentY(h, parentH){
	return (parentH - h) / 2;
}
//�ö��ŷָ������� 
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
//�ö��ŷָ�������(��ǰ׺+)
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
// �����յ�����
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
// �ö�����
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
    var userAgent = navigator.userAgent; //ȡ���������userAgent�ַ���
    var isOpera = userAgent.indexOf("Opera") > -1;
	if (isOpera) {
		return "Opera"
	}; //�ж��Ƿ�Opera�����
	if (userAgent.indexOf("Firefox") > -1) {
		return "FF";
	} //�ж��Ƿ�Firefox�����
	if (userAgent.indexOf("Chrome") > -1){
		return "Chrome";
	}
	if (userAgent.indexOf("Safari") > -1) {
		return "Safari";
	} //�ж��Ƿ�Safari�����
	if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
		return "IE";
	}; //�ж��Ƿ�IE�����

	return '';
}
// 加载接口
function LLoaderEx(fun){
	this.res = [["bg", global_util.getAppPath()+"/luxury_car/rs/load_bg.png"], ["front", global_util.getAppPath()+"/luxury_car/rs/load_front.png"]];
	this.fun = fun;
	this.ok = 0;
	this.bitmap = [];

	for(var i = 0; i < this.res.length; ++i){
		var loader = new LLoader();
		loader.parent = this;
		loader.resName = this.res[i][0];
		loader.addEventListener(LEvent.COMPLETE, this.loadBitmapdata);
		loader.load(this.res[i][1], "bitmapData");
	}
}

// 检测时否加载完成
LLoaderEx.prototype.checkCom = function(){
	return this.ok >= this.res.length;
}

// 加载完的图片转为bigmap
LLoaderEx.prototype.addBitmap = function(resName, data){
	var bitmapdata = new LBitmapData(data);  
	var bitmap = new LBitmap(bitmapdata);

	this.bitmap[resName] = bitmap;
	this.ok += 1;
	if (this.ok == this.res.length){
		this.fun(this.bitmap);
	}
}

// 单张图片加载完成
LLoaderEx.prototype.loadBitmapdata = function(event){
	event.currentTarget.parent.addBitmap(event.currentTarget.resName, event.target);
}

// 进度条扩展
function LoadingSampleEx(bitmap, size, background, color){
	base(this,LSprite,[]);
	var s = this;
	s.initX = 268;
	s.initY = 130;
	s.backgroundColor = "#0b1830";
	s.graphics.drawRect(1,s.backgroundColor,[0,0,SCREEN_WIDTH,SCREEN_HEIGHT],true,s.backgroundColor);

	s.bg = bitmap["bg"];
	s.front = bitmap["front"];

	s.bg.x = converToParentX(s.bg.getWidth(), SCREEN_WIDTH);
	s.bg.y = converToParentY(s.bg.getHeight(), SCREEN_HEIGHT) - 100;
	s.addChild(s.bg);
	
	s.front.x = s.initX;
	s.front.y = s.initY;
	s.addChild(s.front);
	var mask = new LGraphics();
	mask.x = s.initX;
	mask.y = s.initY;
	s.front.mask = mask;

	s.progress = 0;
	s.setProgress(s.progress);
}
LoadingSampleEx.prototype.setProgress = function (value){
	var s = this;
	var maskObj = new LSprite();
	maskObj.x = s.initX;
	maskObj.y = s.initY;
	maskObj.graphics.drawRect(0, "#ff0000", [0, 0, s.front.getWidth() * (value / 100), s.front.getHeight()]);
	s.front.mask = maskObj;
};
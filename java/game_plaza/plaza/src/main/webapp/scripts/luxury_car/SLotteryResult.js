/*
*  抽奖结果参数定义
*/
var IDX_ROLLS = 1;
var IDX_BMW = 2;
var IDX_FERRARI  = 3;
var IDX_BENZ = 4;
var IDX_LAMBORGHINI = 5;
var IDX_VM  = 6;
var IDX_BENTLEY = 7;
var IDX_AUDI = 8;

var gCarName = new Array(
	'劳斯莱斯',
	'宝    马',
	'法 拉 利',
	'奔    驰',
	'兰博基尼',
	'大    众',
	'宾    利',
	'奥    迪'
	);

// 图标坐标
var gCirclePos = new Array(
	new SPoint(557,305),
	new SPoint(537,209),
	new SPoint(482,127),
	new SPoint(401,74),
	new SPoint(305,54.5),
	new SPoint(211,75),
	new SPoint(131,126),
	new SPoint(77,209),
	new SPoint(53,307),
	new SPoint(75,399),
	new SPoint(129,480),
	new SPoint(211,537),
	new SPoint(306,555),
	new SPoint(403,537),
	new SPoint(483,482),
	new SPoint(538,400)
	);

// 下注区坐标
var gCircleChipsPos = new Array(
	new SPoint(404,260),
	new SPoint(364,160),
	new SPoint(262,130),
	new SPoint(155,160),
	new SPoint(115,260),
	new SPoint(155,375),
	new SPoint(259,390),
	new SPoint(361,375)
	);

var gBetonPos = new Array(
	new SPoint(240,80),// ferrari
	new SPoint(240,305), // bentley
	new SPoint(305,240), // lolls
	new SPoint(85,240), // lambor
	new SPoint(95,355), // vm
	new SPoint(355,355),	// audi
	new SPoint(355,95), // bmw
	new SPoint(90,95) // benz
	);

var gEffectBeton = null;
var gEffectBetonTween = null;
function betonEffect(index){
	return; // 弃用

	var map = [3,7,1,8,4,5,2,6];

	if (index >= 1 && index <= 8){
		var value = map[index - 1];
		if (gEffectBeton == null){
			gEffectBeton = spriteWithImage('IMG_BETON_'+value);
			var p = gBetonPos[value - 1];
			gEffectBeton.x = p.x,gEffectBeton.y = p.y;
			layer_game_beton.addChild(gEffectBeton);
		}else{
			var p = gBetonPos[value - 1];
			gEffectBeton.removeAllChild();
			gEffectBeton.addChild(bitmapWithImage('IMG_BETON_'+value))
			gEffectBeton.x = p.x,gEffectBeton.y = p.y;
			gEffectBeton.alpha = 1;
		}

		if (gEffectBetonTween){
			LTweenLite.remove(gEffectBetonTween);
		}

		var time = 0.2;
		gEffectBetonTween = LTweenLite.to(gEffectBeton,time,{alpha:0,loop:false,ease:LEasing.Sine.easeInOut,
			tweenTimeline:LTweenLite.TYPE_FRAME}).to(gEffectBeton,time,{alpha:1});
	}
}

function betonEffectStop(){
	// layer_game_beton.removeAllChild();
	if (gEffectBeton){
		gEffectBeton.alpha = 0;
	}
}

function getNumPosFromLotteryResult(index) {
	return gCircleChipsPos[index - 1];
}

function getIndexByPos(x, y, dis){
	if (x < 50 && x > -50){
		if (y > 60 ){
			return IDX_FERRARI;
		}else if (y < -60){
			return IDX_BENTLEY;
		}
	}else if (x > 60){
		if (y < 50 && y > -50){
			return IDX_ROLLS;
		}else if (y > 60){
			return IDX_BMW;
		}else if (y < -60){
			return IDX_AUDI;
		}
	}else if (x < -60){
		if (y < 50 && y > -50){
			return IDX_LAMBORGHINI;
		}else if (y > 60){
			return IDX_BENZ;
		}else if (y < -60){
			return IDX_VM;
		}
	}

	return 0;
}

var gEffectLast = null;
var gEffectLastTween = null;
var gEffectLastTimer = null;
function effectLastResultClear(){
	// layer_circle_last.removeAllChild();
	if (gEffectLast){
		gEffectLast.alpha = 0;
	}
}
function effectLastResult(index){
	effectLastResultClear();

	if (gEffectLast == null){
		gEffectLast = spriteWithImage(IMG_HIGHLIGHT);
		gEffectLast.x = gCirclePos[index - 1].x - gEffectLast.getWidth() / 2;
		gEffectLast.y = gCirclePos[index - 1].y - gEffectLast.getHeight() / 2;
		layer_circle_last.addChild(gEffectLast);
		
		gEffectLastTimer = new LTimer(500, 0);
		gEffectLastTimer.addEventListener(LTimerEvent.TIMER, blinkEffect);
		gEffectLastTimer.start();
		
	}else{
		gEffectLast.x = gCirclePos[index - 1].x - gEffectLast.getWidth() / 2;
		gEffectLast.y = gCirclePos[index - 1].y - gEffectLast.getHeight() / 2;
		gEffectLast.alpha = 1;
	}

	// if (gEffectLastTween){
	// LTweenLite.remove(gEffectLastTween);
	// }
	// var time = 0.25;
	// gEffectLastTween =	// LTweenLite.to(gEffectLast,time,{alpha:0,loop:false,ease:LEasing.Sine.easeInOut,
	// tweenTimeline:LTweenLite.TYPE_FRAME}).to(gEffectLast,0.5,{alpha:1});
}

function blinkEffect(e)
{
	if (gEffectLast.alpha == 1)
	{
		gEffectLast.alpha = 0.5;
	}
	else if (gEffectLast.alpha == 0.5)
	{
		gEffectLast.alpha = 1;
	}
}



function Enum() {}
Enum.EffectStatus = {begin:0, starting:1, run:2,endFirst:3,ending:4,ended:5,showAll:10,showAllRun:11};
function EffectResult(){
	var self = this;

	self.diffFps = 15;
	if (layer_fps.fps[0].text.length > 5){
		self.diffFps = parseFloat(layer_fps.fps[0].text.substring(4));
	}

	// self.moveTimeCfg = [48, 28, 12, 6, 3];
	self.moveTimeCfg = [self.diffFps, self.diffFps / 2, self.diffFps/ 3, self.diffFps / 4,  
						self.diffFps / 5, self.diffFps / 6, 
						self.diffFps / 7, 1];

	self.moveTimeEndCfg = [self.diffFps, self.diffFps / 2, self.diffFps/ 3, self.diffFps / 4,  
						self.diffFps / 5, self.diffFps / 6, 
						self.diffFps / 7,self.diffFps / 8,self.diffFps / 9, 1];
	self.loop = 3;

	self.arrImg = [];
	for(var i=0; i<gCirclePos.length; ++i){
		var img = spriteWithImage(IMG_HIGHLIGHT);
		img.x = gCirclePos[i].x - img.getWidth() / 2;
		img.y = gCirclePos[i].y - img.getHeight() / 2;
		img.alpha = 0;
		layer_circle.addChild(img);

		self.arrImg.push(img);
	}
	
	self.resetInit();
}

EffectResult.prototype.resetInit = function(){
	var self = this;

	self.index = 0;
	self.value = 0;
	self.moveIndex = 0;
	self.moveTimeIndex = 0;
	self.moveTime = 0;
	self.from = 0;
	self.currLoop = 0;
	self.moveStatus = Enum.EffectStatus.begin;
	self.endTarget = 0;
	self.target = 0;
	self.timer = null;
	self.circleCount = 0;

	self.decelerate = false;
	self.showText = true;	
	self.isAddChips = false;
}

EffectResult.prototype.moveFromIndex = function(index, target, value){
	var self = this;

	betonEffectStop();

	self.index = index;
	self.value = value;
	self.moveIndex = index;
	self.from = index;
	self.target = target;

	if (target <= 7){
		self.endTarget = target + 9;
	}else{
		self.endTarget = 9 - (16 - target)
	}

	if (layer_sound_mgr != null){
		layer_sound_mgr.pauseBg();
	}

	self.fpsCount = 0;
	self.t = (new Date()).getTime();
	self.effect = new LSprite();
	self.effect.myParent = self;
	layer_circle.addChild(self.effect);
	self.effect.addEventListener(LEvent.ENTER_FRAME, self.onGameFrame);
}

EffectResult.prototype.onGameFrame = function(event){
	var self = event.currentTarget.myParent;

	try{
		self.fpsCount++;

		switch(self.moveStatus){
			case Enum.EffectStatus.begin:
				self.fpsCount  = 0;
				if (0 == (self.target - 1) % 4){
					self.moveStatus = Enum.EffectStatus.showAll;
				}else{
					self.moveStatus = Enum.EffectStatus.starting;
					playSoundWithRoulette5();
				}
				break;
			case Enum.EffectStatus.showAll:
				self.showAll();
				break;
			case Enum.EffectStatus.showAllRun:
				break;
			case Enum.EffectStatus.starting:
				if (self.moveTimeIndex >= self.moveTimeCfg.length){
					self.moveStatus = Enum.EffectStatus.run;
				}else{				
					if (self.fpsCount < self.moveTimeCfg[self.moveTimeIndex]){
						return;
					}

					self.moveTimeIndex++;
					self.fpsCount = 0;
					self.createLight();
					if (self.moveTimeIndex == 1){
						effectLastResultClear();
					}
				}
				break;
			case Enum.EffectStatus.run:
				if (self.decelerate){
					if (self.fpsCount < 4){
						return;
					}
				}else{
					if (self.fpsCount < 1){
						return;
					}
				}
				
				self.createLight(true);
				self.circleCount++;
				self.fpsCount = 0;

				if (self.moveIndex == self.from){
					self.currLoop++;
				}

				if (self.currLoop == self.loop - 1){
					// self.decelerate = true;
				}

				if (self.currLoop >= self.loop){
					if (self.moveIndex == self.endTarget){
						self.moveStatus = Enum.EffectStatus.endFirst;
					}
				}
				break;
			case Enum.EffectStatus.endFirst:
				self.fpsCount  = 0;
				self.moveStatus = Enum.EffectStatus.ending;
				self.moveTimeIndex = self.moveTimeEndCfg.length - 1;
				break;
			case Enum.EffectStatus.ending:
				if (self.moveTimeIndex < 0){
					self.moveStatus = Enum.EffectStatus.ended;
					return;
				}
				if (self.fpsCount < self.moveTimeEndCfg[self.moveTimeIndex]){
					return;
				}

				self.createLight();

				self.moveTimeIndex--;
				self.fpsCount = 0;			
				break;
			case Enum.EffectStatus.ended:
				self.clearEnd();
				break;
		}
	}catch(e){
		window.location.reload();
	}
}

EffectResult.prototype.showCalc = function(){
	var self = this;

	setLastResult(curr_result.result_td, curr_result.result_wx, curr_result.result_sx);	
	// 结算提示
	var masterScore = parseInt(curr_result.master_score);
	var selfScore = parseInt(curr_result.win_num) - parseInt(curr_result.setted_num);
	if (!self.isAddChips){
		self.isAddChips = true;
		if (curr_self_id == curr_master_id){
			selfScore = masterScore;
			if (typeof(curr_result.chips) != "undefined"){
				curr_self_chips = curr_result.chips
			}else{
				curr_self_chips += selfScore;
			}			
		}
		else {
			if (typeof(curr_result.chips) != "undefined"){
				curr_self_chips = curr_result.chips
			}else{
				curr_self_chips += curr_result.win_num;
			}			
		}
	}
	setSelfChips(curr_self_chips);

	
	
	var color1 = "#ff0000";
	if (masterScore < 0){
		color1 = "#00ff00";
	}
	var color2 = "#ff0000";
	if (selfScore < 0){
		color2 = "#00ff00";
	}

	self.clearChipsLayer();

	self.showText = false;

	if (curr_self_id == curr_master_id){
		noticeResultEx(splitNumber(masterScore), color1,splitNumber(masterScore), color2, curr_result.result_wx);
	}else{
		noticeResultEx(splitNumber(masterScore), color1,splitNumber(selfScore), color2, curr_result.result_wx);
	}
	

	if (layer_history){
		layer_history.drawHistory(curr_result.result_wx);
	}
}

EffectResult.prototype.clearChipsLayer = function(){
	for (var i = IDX_ROLLS; i <= IDX_AUDI; i++){
		var layer_chips = array_curr_chips_layer[i];
		if (layer_chips != undefined){
			 layer_chips.alpha = 0;
		}
	}
}

EffectResult.prototype.clearEnd = function(){
	var self = this;

	if (self.effect){
		self.effect.removeEventListener(LTimerEvent.TIMER, self.onGameFrame);
		self.effect.remove();
		self.effect = null;
	}
	if (self.isAddChips == false)
	{
		self.showCalc();
	}
	
}

EffectResult.prototype.stop = function(){
	var self = this;

	if (self.status != Enum.EffectStatus.ended){
		self.status = Enum.EffectStatus.ended;
		self.clearEnd();
	}	

	stopSoundWithRoulette20();
	stopSoundWithRoulette5();

	if (layer_sound_mgr != null){
		layer_sound_mgr.playBg();
	}
}

EffectResult.prototype.isShowText = function(){
	var self = this;
	
	return self.showText;
}

EffectResult.prototype.createLight = function(bSoundStop){
	var self = this;

	// var img = spriteWithImage(IMG_HIGHLIGHT);
	// img.x = gCirclePos[self.moveIndex - 1].x - img.getWidth() / 2;
	// img.y = gCirclePos[self.moveIndex - 1].y - img.getHeight() / 2;
	// layer_circle.addChild(img);	if (self.moveIndex == 0) {		self.moveIndex = 1;	}	
	var img = self.arrImg[self.moveIndex - 1];
	if (self.moveStatus == Enum.EffectStatus.starting || self.moveStatus == Enum.EffectStatus.ending){
		var time = 0.2;
		LTweenLite.to(img,0.8,{alpha:1,loop:false,ease:LEasing.Sine.easeInOut,
		tweenTimeline:LTweenLite.TYPE_FRAME}).to(img,time,{alpha:0, onComplete:onLightOver})
	}else{
		var time = 0.3;
		LTweenLite.to(img,time,{alpha:1,loop:false,ease:LEasing.Sine.easeInOut,
		tweenTimeline:LTweenLite.TYPE_FRAME}).to(img,0.2,{alpha:0, onComplete:onLightOver})
	}	

	if (self.moveIndex == 1){
		self.moveIndex = 16;
	}else{
		self.moveIndex--;
	}

	if (bSoundStop != true){
		startSoundWithCircle();
	}else{
		if (self.circleCount % 2 == 0){
			startSoundWithCircle();
		}
	}
}

EffectResult.prototype.showAll = function(){
	var self = this;

	self.moveStatus = Enum.EffectStatus.showAllRun;

	playSoundWithRoulette20f();
	
	var arr = self.arrImg;
	for(var i=0,len=arr.length; i<len;++i){
		var img = arr[i];
		LTweenLite.to(img,0.2,{alpha:1,loop:false,ease:LEasing.Sine.easeInOut,
		tweenTimeline:LTweenLite.TYPE_FRAME}).to(img,0.5,
		{alpha:0}).to(img,0.2,{alpha:1}).to(img,0.5,{alpha:0}).to(img,0.2,
		{alpha:1}).to(img,0.5,{alpha:0, onComplete:onLightOver})
	}

	var timer = new LTimer(3000, 1);
	timer.myParent = self;
	timer.addEventListener(LTimerEvent.TIMER, self.timerHandler2);
	timer.start();
}

EffectResult.prototype.timerHandler2 = function(event){
	var self = event.currentTarget.myParent;
	self.moveStatus = Enum.EffectStatus.starting;

	stopSoundWithRoulette20f();
	playSoundWithRoulette20();
}

function onLightOver(event){
	// if (event.currTarget){
	// event.currentTarget.remove();
	// }
}

var layer_history = null;
function showHistory(){
	if (layer_history == null){
		layer_history = new History();
	}	
}

function History(){
	var self = this;

	self.cellWidth = 216;

	self.rectW = 27
	self.rectH = 28

	self.bg = panelWithSprite(216, 270);
	self.bg.x = 9;
	self.bg.y = 95;
	layer_right_background.addChild(self.bg);

	self.layer_conetnt = null;
	self.map = [5,2,3,6,1,8,7,4];
	self.history = [];

	self.img = [IMG_STAR_1,IMG_STAR_2,IMG_STAR_4,IMG_STAR_3,IMG_STAR_6,IMG_STAR_5,IMG_STAR_8,IMG_STAR_7];
	self.initOk = false;

	self.last = null;

	self.initUI();	
	self.initList();
}

History.prototype.initUI = function(){
	var self = this;

	var x = imgSize[IMG_RIGHT_BG].w - imgSize[IMG_BTN_1].w - 5;

	var img = spriteWithImage(IMG_BTN_1);
	img.x = x; img.y = 100;
	img.myParent = self;
	img.addEventListener(LMouseEvent.MOUSE_UP, self.onScrollBegin)
	layer_right_background.addChild(img);

	var img = spriteWithImage(IMG_BTN_4);
	img.x = x; img.y = 130;
	img.myParent = self;
	img.addEventListener(LMouseEvent.MOUSE_UP, self.onScrollUp)
	layer_right_background.addChild(img);
	
	var img = spriteWithImage(IMG_BTN_3);
	img.x = x; img.y = 325;
	img.myParent = self;
	img.addEventListener(LMouseEvent.MOUSE_UP, self.onScrollDown)
	layer_right_background.addChild(img);

	var img = spriteWithImage(IMG_BTN_2);
	img.x = x; img.y = 355;
	img.myParent = self;
	img.addEventListener(LMouseEvent.MOUSE_UP, self.onScrollEnd)
	layer_right_background.addChild(img);
}

History.prototype.onScrollBegin= function(event){
	var self = event.currentTarget.myParent;

	if (self.layer_conetnt){
		var count = self.layer_conetnt._ll_items.length - 10;
		if (count > 0){
			self.layer_conetnt.clipping.y  = 0;
		}		
	}	
}

History.prototype.onScrollUp = function(event){
	var self = event.currentTarget.myParent;

	if (self.layer_conetnt){
		var count = self.layer_conetnt._ll_items.length - 10;
		if (count > 0){
			if (self.layer_conetnt.clipping.y - self.rectH >= 0){
				self.layer_conetnt.clipping.y -= self.rectH;
			}
		}		
	}	
}

History.prototype.onScrollDown = function(event){
	var self = event.currentTarget.myParent;

	if (self.layer_conetnt){
		var count = self.layer_conetnt._ll_items.length - 10;
		if (count > 0){
			if (self.layer_conetnt.clipping.y + self.rectH <= count * self.rectH){
				self.layer_conetnt.clipping.y += self.rectH;
			}
		}		
	}	
}

History.prototype.onScrollEnd = function(event){
	var self = event.currentTarget.myParent;

	if (self.layer_conetnt){
		var count = self.layer_conetnt._ll_items.length - 10;
		
		if (count > 0){
			self.layer_conetnt.clipping.y = count * self.rectH;
		}	
	}	
}

History.prototype.getImg = function(value){
	var self = this;

	return self.img[value - 1];
}

History.prototype.drawHistory = function(value){
	var self = this;

	if (value < 1 || value > 8){
		return;
	}

	self.history.unshift(self.map[value - 1]);

	self.addList();

	var count = self.history.length - 10;
	if (count > 0){
		self.layer_conetnt.clipping.y = self.layer_conetnt.cellHeight * count;
	}
}

History.prototype.isInit = function(){
	var self = this;

	return self.initOk;
}

History.prototype.init = function(msg){
	var self = this;

	self.initOk = true;
	var list = msg.list;

	for (var i = 0, len = list.length; i < len; ++i){
		var ele = eval("("+list[i]+")");
		self.history.push(self.map[ele.result - 1]);
	}

	self.initList();
}

History.prototype.initList = function(){
	var self = this;

	if (self.layer_conetnt){
		self.layer_conetnt.remove();
		self.layer_conetnt = null;
	}

	self.layer_conetnt = new LListView();
	self.bg.addChild(self.layer_conetnt);
	self.layer_conetnt.maxPerLine = 1;
	self.layer_conetnt.cellWidth = self.cellWidth;
	self.layer_conetnt.cellHeight = self.rectH;
	self.layer_conetnt.resize(216, 290);
	self.layer_conetnt.arrangement = LListView.Direction.Horizontal;
	self.layer_conetnt.movement = LListView.Direction.Vertical;
	self.layer_conetnt.graphics.drawRect(0, "#000000", [0, 0, self.layer_conetnt.clipping.width,self.layer_conetnt.clipping.height]);
	var scrollBarVertical = new LListScrollBar(new LPanel("#9370DB", 0, 0), new LPanel("#9400D3", 0, 0), LListView.ScrollBarCondition.OnlyIfNeeded);
	self.layer_conetnt.setVerticalScrollBar(scrollBarVertical);

	var len = self.history.length;

	if (len == 1){
		var curr = self.history[0];
		var preX = self.rectW / 2 + (curr) * self.rectW;
		self.layer_conetnt.insertChildView(self.historyItemImg(new SPoint(preX, self.rectH/2)), curr);
		return
	}

	var pre = null;
	for(var i = len-1; i>=0; --i){
		if (pre == null){
			pre = self.history[i];
			second = self.history[i - 1];
			if ( i != 0){
				var preX = self.rectW / 2 + (pre - 1) * self.rectW;
				var secondX = self.rectW / 2 + (second - 1) * self.rectW
				var arr = self.getPoint(preX, secondX);
				self.layer_conetnt.insertChildView(self.historyItem(arr[0], arr[1], null, 1, pre));
			}
		}else{
			second = self.history[i];
			if (i != 0){
				next = self.history[i - 1];

				var preX = self.rectW / 2 + (pre - 1) * self.rectW;
				var secondX = self.rectW / 2 + (second - 1) * self.rectW
				var nextX = self.rectW / 2 + (next - 1) * self.rectW

				var arr = self.getPoint(preX, secondX);
				var arr2 = self.getPoint(secondX, nextX);

				self.layer_conetnt.insertChildView(self.historyItem(arr[2], arr[3], arr2[1], 2, second));
			}else{
				var preX = self.rectW / 2 + (pre - 1) * self.rectW;
				var secondX = self.rectW / 2 + (second - 1) * self.rectW
				var arr = self.getPoint(preX, secondX);
			
				self.layer_conetnt.insertChildView(self.historyItem(arr[2], arr[3], null, 2, second));
			}

			pre = second;
		}
	}
}

History.prototype.addList = function(){
	var self = this;

	if (self.history.length <= 2){
		self.initList();
	}else{
		if (self.last){
			self.last.remove();
			self.layer_conetnt.deleteChildView(self.last);
		}

		var pre = self.history[2];
		var second = self.history[1];
		var next = self.history[0];

		var preX = self.rectW / 2 + (pre - 1) * self.rectW;
		var secondX = self.rectW / 2 + (second - 1) * self.rectW
		var nextX = self.rectW / 2 + (next - 1) * self.rectW
		var arr = self.getPoint(preX, secondX);
		var arr2 = self.getPoint(secondX, nextX);

		self.layer_conetnt.insertChildView(self.historyItem(arr[2], arr[3], arr2[1], 2, second));
		self.layer_conetnt.insertChildView(self.historyItem(arr2[2], arr2[3], null, 2, next));
	}
}

History.prototype.getPoint = function(preX, secondX){
	var self = this;
	var ret = []
	var diff = Math.abs(preX - secondX) / 2;

	var p1 = new SPoint(preX,self.rectH / 2);
	var p2 = new SPoint(0,self.rectH);	
	var p3 = new SPoint(0,0);
	var p4 = new SPoint(secondX,self.rectH / 2);	

	if (preX < secondX){
		p2.x = preX + diff;
		p3.x = preX + diff;
	}else if (preX > secondX){
		p2.x = preX - diff;
		p3.x = preX - diff;
	}else{
		p2.x = p1.x;
		p3.x = p4.x;
	}

	ret.push(p1);
	ret.push(p2);
	ret.push(p3);
	ret.push(p4);

	return ret;
}

History.prototype.historyItemImg = function(p1, value){
	var self = this;
	var child = new LListChildView();
	child.graphics.drawRect(0, "#ff0000", [0, 0, self.cellWidth, self.rectH], false,"#ff0000");

	var img = spriteWithImage(self.getImg(value))
	img.x = p1.x - img.getWidth() / 2;
	img.y = self.rectH / 2 - img.getHeight() / 2;
	child.addChild(img);

	return child;
}

History.prototype.historyItem = function(p1, p2, p3, flag, value){
	var self = this;
	var child = new LListChildView();
	child.graphics.drawRect(0, "#ff0000", [0, 0, self.cellWidth, self.rectH], false,"#ff0000");

	child.graphics.beginPath();
	child.graphics.strokeStyle("#ffffff");
	child.graphics.moveTo(p1.x, p1.y);
	child.graphics.lineTo(p2.x, p2.y);
	child.graphics.stroke();

	if (p3 != null){
		child.graphics.beginPath();
		child.graphics.strokeStyle("#ffffff");
		child.graphics.moveTo(p2.x, p2.y);
		child.graphics.lineTo(p3.x, p3.y);
		child.graphics.stroke();
	}

	var x = 0;
	if (flag == 2){
		x = p2.x;
	}else{
		x = p1.x;
	}

	var img = spriteWithImage(self.getImg(value))
	img.x = x - img.getWidth() / 2;
	img.y = self.rectH / 2 - img.getHeight() / 2;
	child.addChild(img);

	self.last = child;

	return child;
}

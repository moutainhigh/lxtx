// 音效管理层
var layer_sound_mgr = null;
function initSound(fun){
	layer_sound_mgr = new SoundMgr(fun);
	baseLayer.addChild(layer_sound_mgr);
}

function stopSoundBg(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.pauseBg();
	}
}

function stopSoundMusic(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.stopMusic();
	}
}

// 金币选择音效
function playSoundWithSelect(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("select");
	}
}

// 金币点击音效
function playSoundWithClick(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("click");
	}
}

// 转盘音效5
function playSoundWithRoulette5(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.playByCount("roulette5");
	}
}

function stopSoundWithRoulette5(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.stop("roulette5");
	}
}

// 转盘音效20 闪
function playSoundWithRoulette20f(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.playByCount("roulette20f", 1);
	}
}

function stopSoundWithRoulette20f(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.stop("roulette20f");
	}
}

// 转盘音效20
function playSoundWithRoulette20(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.playByCount("roulette20", 3);
	}
}

function stopSoundWithRoulette20(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.stop("roulette20");
	}
}

function startSoundWithStart(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("start");
	}
}

function startSoundWithSetChips(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("setchips");
	}
}

function startSoundWithCircle(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("circle");
	}
}

function initSafari(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.initSafari();
	}
}

function SoundMgr(fun){
	base(this,LSprite,[]);
	this.fun = fun;
	this.cfg = ["click", "select", "roulette5", 'roulette20','roulette20f', 'start','setchips','circle', "bg"];
	this.sound = [];
	this.loaded = 0;
	this.bgPlay = false;
	this.initSafariFlag = false;
	if (!LSound.webAudioEnabled){
		this.bgPlay = false;
	}
	this.musicPlay = false;

	this.loadSound();
}

SoundMgr.prototype.initSafari = function(){
	if (!this.initSafariFlag){
		var sound = this.sound["circle"];
		if (sound){
			sound.play();
			sound.setVolume(0.1);
			sound.stop();
		}
		var sound = this.sound["start"];
		if (sound){
			sound.play();
			sound.setVolume(0.1);
			sound.stop();
		}
		var sound = this.sound["roulette20f"];
		if (sound){
			sound.play();
			sound.setVolume(0.1);
			sound.stop();
		}
		var sound = this.sound["roulette5"];
		if (sound){
			sound.play();
			sound.setVolume(0.1);
			sound.stop();
		}
		var sound = this.sound["roulette20"];
		if (sound){
			sound.play();
			sound.setVolume(0.1);
			sound.stop();
		}

		this.initSafariFlag = true;
	}
}

SoundMgr.prototype.loadSound = function (){
	for(var i = 0; i < this.cfg.length; ++i){
		var s = new LSound();
		var url = global_util.getAppPath()+"/luxury_car/medias/" + this.cfg[i];
		if (imgData['sound' + this.cfg[i]]){
			s.load(imgData['sound' + this.cfg[i]]);			
		}else{
			s.load(url+".mp3");
		}
		
		this.sound[this.cfg[i]] = s;

		s.addEventListener(LEvent.COMPLETE, this.soundLoadOver);
	}
}

SoundMgr.prototype.loadOver = function(){
	this.loaded += 1;
	if (this.loaded == this.cfg.length){
		if (this.fun != null){
			this.fun();
		}
	}
}

SoundMgr.prototype.playBg = function(){
	var sound = this.sound["bg"];
	if (sound != null){
		if (!sound.playing){
			if (this.bgPlay){
				sound.play(0, 10000000);
				sound.setVolume(0.5);
			}
		}else{
			sound.close();
			if (this.bgPlay){
				sound.play(0, 10000000);
				sound.setVolume(0.5);
			}
		}
	}
}

SoundMgr.prototype.pauseBg = function(){
	var sound = this.sound["bg"];
	if (sound != null){
		if (sound.playing){
			sound.stop();
		}
	}
}

SoundMgr.prototype.play = function(name){
	var sound = this.sound[name];
	if (sound != null){
		if (!sound.playing){
			if (this.musicPlay){
				sound.play();
			}
		}else{
			sound.close();
			if (this.musicPlay){
				sound.play();
			}
		}
	}
}

SoundMgr.prototype.playByCount = function(name, count){
	var sound = this.sound[name];
	if (sound != null){
		if (!sound.playing){
			if (this.musicPlay){
				sound.play(0,count);
			}
		}else{
			sound.close();
			if (this.musicPlay){
				sound.play(0, count);
			}
		}
	}
}

SoundMgr.prototype.stop = function(name){
	var sound = this.sound[name];
	if (sound != null){
		sound.stop();
		sound.close();
	}
}

SoundMgr.prototype.stopMusic = function(){
	for(var i = 0; i < this.cfg.length; ++i){
		var soundName = this.cfg[i];
		if (soundName != 'bg'){
			this.stop(soundName);
		}		
	}
}

SoundMgr.prototype.soundLoadOver = function(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.loadOver();
	}
}

SoundMgr.prototype.setBgPlay = function(value){
	this.bgPlay = value;
}

SoundMgr.prototype.setMusicPlay = function(value){
	this.musicPlay = value;
}

SoundMgr.prototype.getBgPlay = function(){
	return this.bgPlay;
}

SoundMgr.prototype.getMusicPlay = function(){
	return this.musicPlay;
}
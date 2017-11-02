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

// 下注音效
function playSoundWithClick(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("click");
	}
}

// 上下庄点击音效
function playSoundWithQuestMaster(){
    if (layer_sound_mgr != null){
        layer_sound_mgr.play("select");
    }
}

// 点击银行音效
function playSoundWithBank(){
    if (layer_sound_mgr != null){
        layer_sound_mgr.play("select");
    }
}

// 赌盘按钮音效
function playSoundWithChangeLab(){
    if (layer_sound_mgr != null){
        layer_sound_mgr.play("select");
    }
}

// 结果音效
function playSoundWithResult(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("win");
	}
}

// 转色子音效
function playSoundWithRoulette(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.play("roulette");
	}
}

function stopSoundWithRoulette(){
	if (layer_sound_mgr != null){
		layer_sound_mgr.stop("roulette");
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
	this.cfg = ["click", "select", "in", "out", "roulette", "win","bg"];
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
		var sound = this.sound["in"];
		if (sound){
			sound.play();
			sound.setVolume(0.1);
			sound.stop();
		}
		var sound = this.sound["out"];
		if (sound){
			sound.play();
			sound.setVolume(0.1);
			sound.stop();
		}
		var sound = this.sound["roulette"];
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
		var url = global_util.getAppPath()+"/shake/medias/" + this.cfg[i];
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
	if (this.loaded >= this.cfg.length){
		if (this.fun != null){
			this.fun();
		}
	}
}



SoundMgr.prototype.playBg = function(){
	var sound = this.sound["bg"];
	if (sound != null){
		if (!sound.playing){
			sound.play(0, 10000000);
		}else{
			sound.close();
			sound.play();
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
			sound.play();
		}
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

SoundMgr.prototype.pauseBg = function(){
	var sound = this.sound["bg"];
	if (sound != null){
		if (sound.playing){
			sound.stop();
		}
	}
}

SoundMgr.prototype.stop = function(name){
	var sound = this.sound[name];
	if (sound != null){
		sound.close();
		sound.stop();
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
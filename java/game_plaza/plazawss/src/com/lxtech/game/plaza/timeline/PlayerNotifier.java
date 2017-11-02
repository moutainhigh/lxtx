package com.lxtech.game.plaza.timeline;

/**
 * notify the users about the game process
 * @author wangwei
 *
 */
public interface PlayerNotifier {
	void waitingForMaster();
	
	void waitingForStart(int seconds);
	
	void waitingForChipset(int seconds);
	
	void waitingForCalculate(int seconds);
}

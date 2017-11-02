package com.lxtx.robot.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.net.KestrelConnector;
import com.lxtx.robot.config.RobotQueueConfig;
import com.lxtx.robot.service.RoomConfig;

public class Main {
	private final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		if (args.length <= 0) {
			System.out.println("options: animal dice car");
		} else if (args[0].equals("car")) {
			KestrelConnector.CHIP_QUEUE = RobotQueueConfig.CAR_CHIP_QUEUE;
			KestrelConnector.BOT_CTRL_QUEUE = RobotQueueConfig.CAR_BOT_CTRL_QUEUE;
			GlobalScheduler.makeInstance(RoomConfig.getCarDialRoomConfig());
		} else if (args[0].equals("dice")) {
			KestrelConnector.CHIP_QUEUE = RobotQueueConfig.DICE_CHIP_QUEUE;
			KestrelConnector.BOT_CTRL_QUEUE = RobotQueueConfig.DICE_BOT_CTRL_QUEUE;
			GlobalScheduler.makeInstance(RoomConfig.getDiceRoomConfig());
		} else if (args[0].equals("animal")) {
			KestrelConnector.CHIP_QUEUE = RobotQueueConfig.ANIMAL_CHIP_QUEUE;
			KestrelConnector.BOT_CTRL_QUEUE = RobotQueueConfig.ANIMAL_BOT_CTRL_QUEUE;
			GlobalScheduler.makeInstance(RoomConfig.getAnimalDialRoomConfig());
		} else {
			System.out.println("options: animal dice car");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						GlobalScheduler.getInstance().tick();
						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("scheduler thread exception", e);
					}
				}
			}
		}).start();
	}
}

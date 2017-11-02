package com.lxtx.robot.service;

import junit.framework.TestCase;

public class RoomConfigTest extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public RoomConfigTest(String testName) {
		super(testName);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		RoomConfig roomConfig = RoomConfig.getDiceRoomConfig();
		long ct = System.currentTimeMillis();
		int count = roomConfig.getRandomBetCount(ct);
	}
}

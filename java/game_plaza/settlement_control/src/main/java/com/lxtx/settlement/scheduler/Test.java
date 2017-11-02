package com.lxtx.settlement.scheduler;

import com.lxtx.settlement.service.AnimalDialSettlementService;
import com.lxtx.settlement.service.BaseSettlementService;
import com.lxtx.settlement.service.CarDialSettlementService;
import com.lxtx.settlement.service.DiceSettlementService;
import com.lxtx.settlement.service.RobotManager;

public class Test {
	public static void main(String[] args) {
		BaseSettlementService.__TEST__ = true;
		RobotManager.getInstance();

		{
			BaseSettlementService<?, ?, ?> service = new DiceSettlementService();
			try {
				service.load();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			service.settlement(2459);
		}
//		{
//			BaseSettlementService<?, ?, ?> service = new CarDialSettlementService();
//			try {
//				service.load();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(-1);
//			}
//			service.settlement(9372);
//		}
	}
}

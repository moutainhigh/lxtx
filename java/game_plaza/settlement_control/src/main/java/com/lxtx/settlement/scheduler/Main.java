package com.lxtx.settlement.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.settlement.service.AnimalDialSettlementService;
import com.lxtx.settlement.service.BaseSettlementService;
import com.lxtx.settlement.service.CarDialSettlementService;
import com.lxtx.settlement.service.DiceSettlementService;
import com.lxtx.settlement.service.RobotManager;
import com.lxtx.settlement.utils.CarDialSettlement;

public class Main {
	private final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		BaseSettlementService.__TEST__ = false;

		BaseSettlementService<?, ?, ?> settlementService = null;
		if (args.length <= 0) {
			settlementService = null;
		} else if (args[0].equals("car")) {
			settlementService = new CarDialSettlementService();
		} else if (args[0].equals("dice")) {
			settlementService = new DiceSettlementService();
		} else if (args[0].equals("animal")) {
			settlementService = new AnimalDialSettlementService();
		}

		if (null == settlementService) {
			System.out.println("options: animal dice car");
		} else {
			logger.info("start");
			RobotManager.getInstance();
			try {
				settlementService.load();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error("scheduler thread exception", e1);
				System.exit(-1);
			}
			final BaseSettlementService<?, ?, ?> service = settlementService;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							service.tick();
							Thread.sleep(500);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("scheduler thread exception", e);
						}
					}
				}
			};
			new Thread(runnable).start();
		}
	}
}

package com.lxtech.cloud.algorithm;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.cloud.db.CloudOrderHandler;
import com.lxtech.cloud.db.model.CloudOrder;
import com.lxtech.cloud.util.GeneratorConfig;
import com.lxtech.cloud.util.TimeUtil;
import com.lxtech.cloud.util.cache.GlobalCacheUtil;

public class IndexChangeAdjuster {
	private final static Logger logger = LoggerFactory.getLogger(IndexChangeAdjuster.class);
	//BU and AG
	private static final double SMALL_ADJUST_VAL = 0.5;
	//CU
	private static final double LARGE_ADJUST_VAL = 10;
	
	private static final double COMM_RATE_LOW = 0.15; //对应于较低的止盈点，如刚玉的止盈点5
	private static final double COMM_RATE_MID = 0.15;
	private static final double COMM_RATE_HIGH = 0.15;
	
	/**
	 * 
	 * @param subject
	 * @param baseIndex
	 * @param currentDiff - should be with -2 and 2 or -15 and 15
	 * This is used to control the range of change created for this operation
	 * @return
	 */
	public static double calculateNewIndex(String subject, double baseIndex, double currentDiff, int currentStep) {
		double currentIndex = baseIndex + currentDiff;
		
		double[] possiblePoints = getNextPossiblePoints(subject, currentDiff, currentStep);
		if (possiblePoints == null) {
			return currentIndex; 
		} else {
			if (possiblePoints.length == 1) {
				return baseIndex + possiblePoints[0];
			} else {
				List<CloudOrder> orderList = null;
				try {
					orderList = CloudOrderHandler.getUnprocessedOrderList(subject);
					if (orderList.size() == 0) { 
						//make a mini variance
						if (whetherGenerateVariance()) {
							//int vari = getVariance(getAdjustSize(subject));
							int pos = (int)(Math.random()*possiblePoints.length);
							return currentIndex + possiblePoints[pos];
						} else {
							return currentIndex;
						}
					}
					logger.info("now calculate new index for "+subject+" , order count is:" + orderList.size());
					return calculateBestDiff(orderList, baseIndex, possiblePoints);
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}
		}

		//in other cases, leave the index unchanged
		return currentIndex;
	}
	
	private static double calculateBestDiff(List<CloudOrder> orderList, double baseIndex, double[] possiblePoints) {
		Map<Double, Double> incomeMap = new HashMap<Double, Double>();
		
		for (double point : possiblePoints) {
			double newIndex = baseIndex + point;
			double totalIncome = 0.0;
			for (CloudOrder order : orderList) {
				totalIncome += calculateForSingleOrder(order, newIndex);
			}
			
			if (totalIncome > 0.0) { //挣钱
				if (incomeMap.containsKey(totalIncome)) {
					Double existingIndex = incomeMap.get(totalIncome);
					if (Math.abs(newIndex - baseIndex) < Math.abs(existingIndex - baseIndex)) {
						incomeMap.put(totalIncome, newIndex);
					}
				} else {
					incomeMap.put(totalIncome, newIndex);
				}
			}
		}
		
		Set<Double> keyset = incomeMap.keySet();
		if (keyset.size() > 0) {
			double[] incomeArr = new double[keyset.size()];
			int i = 0;
			for (Double d : keyset){
				incomeArr[i++] = d;
			}
			Arrays.sort(incomeArr);
			return incomeMap.get(incomeArr[incomeArr.length - 1]);
		} else {
			return baseIndex;
		}
	}
	
	private static double calculateForSingleOrder(CloudOrder order, double index) {
		int direction = order.getDirection();
		double netIncome = 0;
		double commissionRate = getCommissionRate(order.getSubject(), order.getLimit());
//		double commissionRate = (double)(order.getCommission().doubleValue() * 0.1);
		
		if (index >= order.getClear_upper_limit()) {
			if (direction == 1) {
				//系统亏钱
				netIncome = 0 - (order.getContract_money() * (1.0 - commissionRate));
			} else {
				netIncome = order.getContract_money() * (1.0 - commissionRate);
			}
		} else if (index <= order.getClear_down_limit()) {
			if (direction == 2) {
				//系统亏钱
				netIncome = 0 - (order.getContract_money() * (1.0 - commissionRate));
			} else {
				netIncome = order.getContract_money()* (1.0 - commissionRate);
			}
		} else { //不能平仓
			return 0;
		}
		return netIncome;
	}
	

	private static double getAdjustSize(String subject) {
		String limitConfig = GlobalCacheUtil.getSystemConfigCache().get("index.vari.limit");
		double largeLimit = 0;
		double smallLimit = 0;
		
		try {
			if (!Strings.isNullOrEmpty(limitConfig)) {
				String[] limits = limitConfig.split(",");
				//we only support two levels of variations, the first one is for low-value targets, second one for high-value targets 
				smallLimit = Double.valueOf(limits[0]).intValue();
				largeLimit = Double.valueOf(limits[1]).intValue();
				if (subject.equals("BTC")) {
					return largeLimit; 
				} else {
					return smallLimit;
				}
			}
		} catch (Exception e) {
		}
		
		if (subject.equals("BTC")) {
			return LARGE_ADJUST_VAL;
		} else {
			return SMALL_ADJUST_VAL;
		}
	}
	
	/**
	 * 计算下一步可能到达的点，请注意返回的整数表示的是相对于基准值的偏移量
	 * @param subject
	 * @param currentDiff
	 * @param currentStep
	 * @return
	 */
	private static double[] getNextPossiblePoints(String subject, double currentDiff, int currentStep) {
		double[] possiblePoints = null;
		double adjustSize = getAdjustSize(subject);

		if (currentStep == 1) { //only adjust the index in step 1
			long longOrderCnt = 0;
			try {
				String orderMins = GlobalCacheUtil.getSystemConfigCache().get("longorder.define");
				int mins = Strings.isNullOrEmpty(orderMins)? 10 : Integer.valueOf(orderMins);
				logger.info("in getNextPossiblePoints, mins is: " + mins);
				longOrderCnt = CloudOrderHandler.getUnprocessedOrderCount(subject, mins);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			String times = GlobalCacheUtil.getSystemConfigCache().get("longorder.vari.times");
			double dTimes = Strings.isNullOrEmpty(times) ? 1.8 : Double.valueOf(times).doubleValue();
			if (longOrderCnt > 0) {
				adjustSize = new BigDecimal(adjustSize * dTimes).intValue();
			}
		}
		
		boolean useIntStep = true;
		if(adjustSize < 2) { //the adjustsize is a small float 
			useIntStep = false;
		}
		
		logger.info("current limit is:" + adjustSize + " "+subject);
		if (currentStep == 1) { //currentDiff equals 0
			double start = 0 - adjustSize;
			double end = adjustSize;
			int size = 0;
			if (useIntStep) {
				size = (int)(end - start + 1);
			} else {
				size = (int)((end - start)/0.1);
			}
			
			possiblePoints = new double[size];
			for (int i = 0; i < size; i++) {
				possiblePoints[i] = start + i * (useIntStep ? 1 : 0.1);
			}
		} else if(currentStep == 2) {
			if (currentDiff == 0) {
				return null; //没有必要计算，pass
			} else {
				int size = 0;
				if (useIntStep) {
					size = (int)adjustSize + 1;
				} else {
					size = (int)((adjustSize + 0.1)/0.1);
				}
				
				possiblePoints = new double[size];
				double start = currentDiff > 0 ? (0 - adjustSize) : 0;
				for (int i=0; i<size; i++) {
					possiblePoints[i] = start + i * (useIntStep ? 1 : 0.1);
				}
			}
		} else if (currentStep == 3) {
			possiblePoints = new double[1];
			possiblePoints[0] = 0; //归0
		}
		
		return possiblePoints;
	}
	
	private static boolean whetherGenerateVariance() {
		List<Integer> hourList = GeneratorConfig.getInstance().getHotHours();
		String factor = "2";
		if (hourList.contains(TimeUtil.getCurrentHourOfDay())) {
			factor = GeneratorConfig.get("vari_factor");
			if (Strings.isNullOrEmpty(factor)) {
				factor = "2";
			}
		}
		
		if ((int)(Math.random()*Integer.valueOf(factor)) == 1){
			if (TimeUtil.getCurrentSecondOfMinute() < 45) {
				return true;
			}
		} 
		return false;
	}
	
	private static double getCommissionRate(String subject, int limit) {
		double rate = 0.0;
		
		if (subject.equals("BU")) {
			switch (limit) {
			case 5:
				rate = COMM_RATE_LOW; 
				break;
			case 7:
				rate = COMM_RATE_MID;
				break;
			case 11:
				rate = COMM_RATE_HIGH;
				break;
			default:
				rate = COMM_RATE_HIGH;
				break;
			}
		} else if (subject.equals("AG")) {
			switch (limit) {
			case 4:
				rate = COMM_RATE_LOW;
				break;
			case 6:
				rate = COMM_RATE_MID;
				break;
			case 10:
				rate = COMM_RATE_HIGH;
				break;
			default:
				rate = COMM_RATE_HIGH;
				break;
			}
		} else {
			switch (limit) {
			case 30:
				rate = COMM_RATE_LOW;
				break;
			case 60:
				rate = COMM_RATE_MID;
				break;
			case 100:
				rate = COMM_RATE_HIGH;
				break;
			default:
				rate = COMM_RATE_HIGH;
				break;
			}
		}
		return rate;
	}
	
	private static int getVariance(int baseSize) {
		return (int)(Math.random() * baseSize);
	}
	
	public static void main(String[] args) {
/*		double[] darr = {1.0, 2.0, 1.5, 5.7, 120.0};
		Arrays.sort(darr);
		System.out.println(darr[0]);*/
		/*
		int count = 0;
		for (int i = 0; i < 1000; i++) {
			if (whetherGenerateVariance()) {
				count++;
				int vari = getVariance(3);
				if (vari == 3) {
					System.out.println("hey");
				}
				System.out.println(vari);
			}
		}
		System.out.println(count);*/
//		for (int i = 0; i < 10; i++) {
//			System.out.println(whetherGenerateVariance());
//		}
		
		/*int baseIndex = 2971;
		int[] possiblePoints = {-2, -1, 0, 1, 2};
		CloudOrder order = new CloudOrder();
		order.setClear_down_limit(2969);
		order.setClear_upper_limit(2979);
		order.setCommission(new BigDecimal(15d));
		order.setDirection(1);
		order.setContract_money(100);
		order.setUid(52);
		order.setStatus(0);
		order.setOrder_index(2974);
		order.setSubject("BU");
		
		List<CloudOrder> orderList = ImmutableList.of(order);
		int newIndex = calculateBestDiff(orderList, baseIndex, possiblePoints);
		System.out.println(newIndex);*/
		
//		System.out.println(getAdjustSize("BU"));
//		System.out.println(getAdjustSize("CU"));
		
		System.out.println(new BigDecimal(72 * 1.8).intValue());
	}

}


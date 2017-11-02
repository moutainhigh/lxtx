package com.lxtx.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Constant {
	/**
	 * 需要交易密码
	 */
	public static final String USER_STAT_ND_PWD = "0";
	/**
	 * 需要手机号
	 */
	public static final String USER_STAT_ND_TEL = "1";
	/**
	 * 正常
	 */
	public static final String USER_STAT_NORMAL = "2";

	public static final String SESSION_USER = "session_user";
	public static final String SESSION_BROKER = "session_broker";
	public static final int SESSION_LOST = -9999;

	// the three constants below are used for constructing http response
	public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_MESSAGE = "msg";
	public static final String RESPONSE_DATA = "data";

	public static final int FUND_HISTORY_FILL = 1;

	public static final int FUND_HISTORY_REPAY = 2;

	/**
	 * 未结算
	 */
	public static final int ORDER_STAT_UNTREAT = 0;
	/**
	 * 处理中
	 */
	public static final int ORDER_STAT_TREATING = 1;
	/**
	 * 赚钱
	 */
	public static final int ORDER_STAT_PROFIT = 2;
	/**
	 * 输钱
	 */
	public static final int ORDER_STAT_LOSS = 3;
	/**
	 * 日终自动处理
	 */
	public static final int ORDER_STAT_DAYCLEAR = 4;

	/**
	 * 未使用
	 */
	public static final int COUPON_STAT_UNUSEED = 0;
	/**
	 * 已使用
	 */
	public static final int COUPON_STAT_USEED = 1;
	/**
	 * 已过期
	 */
	public static final int COUPON_STAT_OVERDUE = 2;

}

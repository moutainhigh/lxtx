package com.lxtech.cloud.net;

public interface CloudPackageType {
	public static final int MSG_TYPE_CRTNone = 0;
	public static final int MSG_TYPE_CRTCkLive = 1;
	public static final int MSG_TYPE_CRTAuth = 2;
	public static final int MSG_TYPE_PRTLogout = 3;
	public static final int MSG_TYPE_CRTReqMtInfo = 50;
	public static final int MSG_TYPE_CRTReqSbInfo = 51;
	public static final int MSG_TYPE_CRTreqSbList = 52;
	public static final int MSG_TYPE_PRTReqSbReport = 53;
	public static final int MSG_TYPE_CRTReqCodeList = 54;
	public static final int MSG_TYPE_PRTReqSomeSymReport = 55;
	public static final int MSG_TYPE_PRTReqSubMtList = 56;
	public static final int MSG_TYPE_PRTReqDealInfo = 57;
	public static final int MSG_TYPE_PRTReqConditionCode = 58;
	public static final int MSG_TYPE_PRTReqTradeTime = 59;
	public static final int MSG_TYPE_CRTReqSbKLine = 150;
	public static final int MSG_TYPE_CRTRequestSymbolTick = 151;
	public static final int MSG_TYPE_CRTReqTrend = 152;
	public static final int MSG_TYPE_CRTReqRealTimePrice = 153;
	public static final int MSG_TYPE_PRTReLevel2 = 154;
	public static final int MSG_TYPE_PRTeqFullPriceUseTradeCode = 155;
	public static final int MSG_TYPE_PRTReqMinutesKLine = 156;
	public static final int MSG_TYPE_PRTReqOddLot = 157;
	public static final int MSG_TYPE_PRTReqFinance = 158;
	public static final int MSG_TYPE_PRTReqFreeText = 159;
	public static final int MSG_TYPE_PRTReqLinkSym = 160;
	public static final int MSG_TYPE_CRTRequestAddPush = 200;
	public static final int MSG_TYPE_CRTRequestDeletePush = 201;
	public static final int MSG_TYPE_CRTRequestUpdatePush = 202;
	public static final int MSG_TYPE_SERPushPrice = 250;
	public static final int MSG_TYPE_SERPushTick = 251;
	public static final int MSG_TYPE_PSERPushLevel2 = 252;
	public static final int MSG_TYPE_PSERPPushOddLot = 253;
	public static final int MSG_TYPE_SERErrorMsg = 300;
	public static final int MSG_TYPE_PSerPushUpdateSym = 350;
	public static final int MSG_TYPE_PCRTTickUser = 351;
	public static final int MSG_TYPE_PRTUploadUserData = 400;
	public static final int MSG_TYPE_PRTUploadOrgExData = 401;
	public static final int MSG_TYPE_PRTReqUserData = 402;
	public static final int MSG_TYPE_PRTReqOrgExData = 403;
	public static final int MSG_TYPE_PRTReqBlockCfg = 62;
	public static final int MSG_TYPE_PRTReqBlockList = 63;
	public static final int MSG_TYPE_News = 10000;
}

package com.baidu.third.jxt.sdk;

import com.baidu.third.jxt.sdk.interfaces.QueryOrderCallBack;


class m implements Runnable {
	private Pay jxtPay;
	private long tradeId;
	private QueryOrderCallBack queryOrderCallBack;
	
    m(Pay jxtPay, long tradeId, QueryOrderCallBack queryOrderCallBack) {
        super();
        this.jxtPay = jxtPay;
        this.tradeId = tradeId;
        this.queryOrderCallBack = queryOrderCallBack;
    }

    public void run() {
        this.jxtPay.queryOrderRequest(this.tradeId, this.queryOrderCallBack);
    }
}


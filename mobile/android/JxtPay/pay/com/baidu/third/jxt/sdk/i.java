package com.baidu.third.jxt.sdk;

class i implements Runnable {
	com.baidu.third.jxt.sdk.g a;
	
    public i(g a) {
        super();
        this.a = a;
    }

    public void run() {
        PayActivity.showCallBackDialog(this.a.jxtPayActivity);
    }
}

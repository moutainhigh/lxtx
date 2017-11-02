package com.baidu.third.jxt.sdk;

class h implements Runnable {
	private com.baidu.third.jxt.sdk.g a;
	
    public h(com.baidu.third.jxt.sdk.g a) {
        super();
        this.a = a;
    }

    public void run() {
        PayActivity.showCallBackDialog(this.a.jxtPayActivity);
    }
}


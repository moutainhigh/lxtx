package com.baidu.third.jxt.sdk.cc;

class c implements Runnable {
	com.baidu.third.jxt.sdk.cc.SelfProgressBar a;
	
    c(SelfProgressBar arg1) {
        super();
        this.a = arg1;
    }

    public void run() {
    	com.baidu.third.jxt.sdk.cc.SelfProgressBar.b(this.a, false);
        if(!com.baidu.third.jxt.sdk.cc.SelfProgressBar.getD(this.a)) {
        	com.baidu.third.jxt.sdk.cc.SelfProgressBar.a(this.a, System.currentTimeMillis());
            this.a.setVisibility(0);
        }
    }
}

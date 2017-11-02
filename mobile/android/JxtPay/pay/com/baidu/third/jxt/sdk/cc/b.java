package com.baidu.third.jxt.sdk.cc;

class b implements Runnable {
	SelfProgressBar a;
	
    b(SelfProgressBar arg1) {
        super();
        this.a = arg1;
    }

    public void run() {
    	com.baidu.third.jxt.sdk.cc.SelfProgressBar.a(this.a, false);
    	com.baidu.third.jxt.sdk.cc.SelfProgressBar.a(this.a, -1);
        this.a.setVisibility(8);
    }
}

package com.baidu.third.jxt.sdk;

import android.view.View;
import android.view.View.OnClickListener;

class j implements View.OnClickListener {
	WebPayActivity jxtWebPayActivity;
	
    j(WebPayActivity jxtWebPayActivity) {
        super();
        this.jxtWebPayActivity = jxtWebPayActivity;
    }

    public void onClick(View view) {
        this.jxtWebPayActivity.finish();
    }
}
package com.baidu.third.jxt.sdk;

import android.content.DialogInterface;

class d implements DialogInterface.OnClickListener {
	private PayActivity jxtPayActivity;
	
    d(PayActivity arg1) {
        super();
        this.jxtPayActivity = arg1;
    }

    public void onClick(DialogInterface dialogInterface, int arg3) {
        dialogInterface.dismiss();
        PayActivity.queryOrder(this.jxtPayActivity);
    }
}

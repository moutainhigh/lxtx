package com.baidu.third.jxt.sdk;

import android.content.DialogInterface;

class e implements DialogInterface.OnClickListener {
	private PayActivity jxtPayActivity;
	
    public e(PayActivity jxtPayActivity) {
        super();
        this.jxtPayActivity = jxtPayActivity;
    }

    public void onClick(DialogInterface dialogInterface, int arg3) {
        dialogInterface.dismiss();
        PayActivity.queryOrder(this.jxtPayActivity);
    }
}

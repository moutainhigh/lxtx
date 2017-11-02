package com.baidu.third.jxt.sdk.cc;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class SelfProgressBar extends ProgressBar {
    private long a;
    private boolean b;
    private boolean c;
    private boolean d;
    private final Runnable e;
    private final Runnable f;

    public SelfProgressBar(Context context) {
        this(context, null);
    }

    public SelfProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        this.a = -1;
        this.b = false;
        this.c = false;
        this.d = false;
        this.e = new com.baidu.third.jxt.sdk.cc.b(this);
        this.f = new com.baidu.third.jxt.sdk.cc.c(this);
    }

    static long a(SelfProgressBar arg0, long arg1) {
        arg0.a = arg1;
        return arg1;
    }

    static boolean getD(SelfProgressBar arg1) {
        return arg1.d;
    }

    static boolean a(SelfProgressBar arg0, boolean arg1) {
        arg0.b = arg1;
        return arg1;
    }

    public void a() {
        long v7 = 500;
        this.d = true;
        this.removeCallbacks(this.f);
        long v0 = System.currentTimeMillis() - this.a;
        if(v0 >= v7 || this.a == -1) {
            this.setVisibility(8);
        }
        else if(!this.b) {
            this.postDelayed(this.e, v7 - v0);
            this.b = true;
        }
    }

    static boolean b(SelfProgressBar arg0, boolean arg1) {
        arg0.c = arg1;
        return arg1;
    }

    public void b() {
        this.a = -1;
        this.d = false;
        this.removeCallbacks(this.e);
        if(!this.c) {
            this.postDelayed(this.f, 500);
            this.c = true;
        }
    }

    private void c() {
        this.removeCallbacks(this.e);
        this.removeCallbacks(this.f);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.c();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.c();
    }
}


package com.baidu.third.jxt.sdk;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

class k extends WebChromeClient {
	WebPayActivity jxtWebPayActivity;
	
    k(WebPayActivity jxtWebPayActivity) {
        super();
        this.jxtWebPayActivity = jxtWebPayActivity;
    }

    public void onProgressChanged(WebView webView, int arg4) {
        super.onProgressChanged(webView, arg4);
        if(WebPayActivity.a(this.jxtWebPayActivity) != null) {
            WebPayActivity.a(this.jxtWebPayActivity).setProgress(arg4);
            if(arg4 == 100) {
                WebPayActivity.a(this.jxtWebPayActivity).setAnimation(WebPayActivity.b(this.jxtWebPayActivity));
                WebPayActivity.b(this.jxtWebPayActivity).start();
                WebPayActivity.a(this.jxtWebPayActivity).a();
                return;
            }

            if(WebPayActivity.a(this.jxtWebPayActivity).getVisibility() == 8) {
                WebPayActivity.a(this.jxtWebPayActivity).b();
            }
        }
    }
}


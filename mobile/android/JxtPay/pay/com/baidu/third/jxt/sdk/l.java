package com.baidu.third.jxt.sdk;

import java.net.URISyntaxException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class l extends WebViewClient {
	
	WebPayActivity jxtWebPayActivity;
	
    l(WebPayActivity jxtWebPayActivity) {
        super();
        this.jxtWebPayActivity = jxtWebPayActivity;
    }

    public void onPageFinished(WebView webView, String arg2) {
    }

    public void onPageStarted(WebView arg1, String arg2, Bitmap arg3) {
    }

    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
		if(url.startsWith("intent://")){
			try {
				Intent intent = Intent.parseUri(url, 1);
				intent.addCategory("android.intent.category.BROWSABLE");
				intent.setComponent(null);

				this.jxtWebPayActivity.startActivity(intent);
				this.jxtWebPayActivity.finish();
				return true;

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

		}
    	if (url.startsWith("weixin://wap/pay?")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            this.jxtWebPayActivity.startActivity(intent);

            return true;
        }
    	
//    	if(url.startsWith("intent://")){
//    		
//			try {
//				Intent intent = Intent.parseUri(url, 1);
//				intent.addCategory("android.intent.category.BROWSABLE");
//				intent.setComponent(null);
//				
//				this.jxtWebPayActivity.startActivity(intent);
//				this.jxtWebPayActivity.finish();
//				return true;
//				
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//    		
//    	}
    	
    	
        if((url.toLowerCase().equals("js"+"://pa"+"ge/fin"+"ish")) || (url.toLowerCase().contains("pa"+"y.fsp"+"entu.com"))
                ) {
            this.jxtWebPayActivity.finish();
            return true;
        }
        
        return false;
    }
}


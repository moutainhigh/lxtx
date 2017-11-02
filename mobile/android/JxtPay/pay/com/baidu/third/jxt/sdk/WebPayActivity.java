package com.baidu.third.jxt.sdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import com.baidu.third.jxt.sdk.cc.SelfProgressBar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class WebPayActivity extends Activity {
    private com.baidu.third.jxt.sdk.cc.SelfProgressBar a;
    private AlphaAnimation alphaAnimation;
    private WebChromeClient webChromeClient;
    private WebViewClient webViewClient;
    private Activity activity = this;

    public WebPayActivity() {
        super();
        this.webChromeClient = new k(this);
        this.webViewClient = new l(this);
    }

    static com.baidu.third.jxt.sdk.cc.SelfProgressBar a(WebPayActivity jxtWebPayActivity) {
        return jxtWebPayActivity.a;
    }

    static AlphaAnimation b(WebPayActivity jxtWebPayActivity) {
        return jxtWebPayActivity.alphaAnimation;
    }

    protected void onCreate(Bundle bundle) {
    	super.onCreate(bundle);
    	
    	int v8 = -1;
        
        String url = this.getIntent().getStringExtra("PAY_URL");
        if(TextUtils.isEmpty(url)) {
            this.finish();
        }else if(url.startsWith("url|")){
        	
        	try {
        		url = url.substring(4);
				this.startActivity(Intent.parseUri(url, 1));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
        	
        }else {
            this.alphaAnimation = new AlphaAnimation(1f, 0f);
            this.alphaAnimation.setDuration(500);
            
            final FrameLayout frameLayout = new FrameLayout(((Context)this));
            frameLayout.setBackgroundColor(v8);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(v8, v8));
            
            WebView webView = new WebView(((Context)this));
            webView.setLayoutParams(new ViewGroup.LayoutParams(v8, v8));
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setAllowContentAccess(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.setWebChromeClient(this.webChromeClient);
            webView.setWebViewClient(this.webViewClient);
        
            webView.addJavascriptInterface(new JavaScriptInterface(activity, webView), "saomaapk");
            
            webView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View view) {
					WebView.HitTestResult result = ((WebView) view).getHitTestResult();
					if (null != result) {
						int type = result.getType();
						if (type == WebView.HitTestResult.IMAGE_TYPE
								|| type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
							String imageUrl = result.getExtra();
							Log.e("TAG", "image -- " + imageUrl);
							showPopupWindow(frameLayout, imageUrl);
						}
					}
					return false;
				}
			});
            
            
            frameLayout.addView(((View)webView));
            int v3 = ((int)TypedValue.applyDimension(1, 60f, this.getResources().getDisplayMetrics()));
            int v4 = ((int)TypedValue.applyDimension(1, 48f, this.getResources().getDisplayMetrics()));
            
            Button v5 = new Button(((Context)this));
            v5.setLayoutParams(new FrameLayout.LayoutParams(v3, v4));
            v5.setText("关闭");
            v5.setTextSize(2, 16f);
            v5.setTextColor(Color.parseColor("#00AAEE"));
            v5.setBackgroundColor(0);
            v5.setVisibility(8);
            frameLayout.addView(((View)v5));
            v3 = ((int)TypedValue.applyDimension(1, 5f, this.getResources().getDisplayMetrics()));
            this.a = new com.baidu.third.jxt.sdk.cc.SelfProgressBar(this);
            this.a.setIndeterminate(false);
            this.a.setProgressDrawable(this.getResources().getDrawable(17301612));
            this.a.setIndeterminateDrawable(this.getResources().getDrawable(17301613));
            FrameLayout.LayoutParams v4_1 = new FrameLayout.LayoutParams(v8, v3);
            v4_1.gravity = 48;
            this.a.setLayoutParams(((ViewGroup.LayoutParams)v4_1));
            frameLayout.addView(this.a);
            this.setContentView(((View)frameLayout));
            webView.loadUrl(url);
            v5.setOnClickListener(new j(this));
        }
    }
    
    final class JavaScriptInterface{
		
		private Activity activity;
		private WebView mWebView;
		
		public JavaScriptInterface(Activity activity,WebView mWebView){
			this.activity = activity;
			this.mWebView = mWebView;
			
		}
		
		@JavascriptInterface
		public void saveImg(String imgUrl){
			Handler handler = new Handler(Looper.getMainLooper()){
				public void handleMessage(Message msg){
					super.handleMessage(msg);
					
					mWebView.loadUrl("javascript:saveImgCallback()");
				}
			};
			
			new MyAsyncTask(handler).execute(imgUrl);
		}
		
		@JavascriptInterface
		public void openWx(){
			Intent intent = new Intent();
			ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(cmp);
	        startActivity(intent);
		}
	}
    
    private void showPopupWindow(View view, final String url) {
//		View contentView = View.inflate(this, Utils.getResource(activity, "jxt_popuwoindow_item", "layout"), null);
//		final PopupWindow popupWindow = new PopupWindow(contentView,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
//		
//		popupWindow.setBackgroundDrawable(new BitmapDrawable());
//		//popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.selectmenu_bg_downward));
//		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//		
//		TextView jxt_savePicTxt = (TextView)contentView.findViewById(Utils.getResource(activity, "jxt_savePicTxt", "id"));
//		jxt_savePicTxt.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
				Handler handler = new Handler(Looper.getMainLooper()){
					public void handleMessage(Message msg){
						super.handleMessage(msg);
						
						Toast.makeText(activity, "图片保存成功，请使用微信扫描", Toast.LENGTH_LONG).show();
						
//						popupWindow.dismiss();
					}
				};
				
				new MyAsyncTask(handler).execute(url);
//			}
//		});
	}
    
    public class MyAsyncTask extends AsyncTask<String, Void, String>{
    	
    	private Handler handler;
    	
    	public MyAsyncTask(Handler handler){
    		this.handler = handler;
    	}
    	
        @Override
       protected void onPostExecute(String filePath) {
           super.onPostExecute(filePath);

           saveImageToGallery(activity, filePath);
           
           handler.sendEmptyMessage(0);
       }

       @Override
       protected String doInBackground(String... params) {
           File file = getBitmap(params[0]);
           return file.getAbsolutePath();
       }
   }
    
    /**
     * 根据地址获取网络图片
     * @param sUrl 图片地址
     * @return
     * @throws IOException
     */
    public File getBitmap(String sUrl){
        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return saveMyBitmap(bitmap,"code");//先把bitmap生成jpg图片
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * bitmap 保存为jpg 图片
     * @param mBitmap 图片源
     * @param bitName  图片名
     */
    public File saveMyBitmap(Bitmap mBitmap,String bitName)  {
        File file= new File( Environment.getExternalStorageDirectory()+"/"+bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    /**
     * 先保存到本地再广播到图库
     * */
    public void saveImageToGallery(Context context,String filePath) {

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, "code", null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + filePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
package com.baidu.third.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

//import com.longyou.haitunsdk.HaiTunPay;
import com.baidu.third.HaiTunNativePaySdk;
import com.baidu.third.ThirdConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付回调类
 * Created by CharryLi on 16/7/25.
 */
public class HaiTunWXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        
//        String appid = HaiTunNativePaySdk.APPID;
//        
//        if(appid == null || appid.length() == 0){
//        	appid = HaiTunPay.getInstance().getAppId_wechat();
//        }
//        api = WXAPIFactory.createWXAPI(this, appid);//appid需换成商户自己开放平台appid
//        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
    	Log.e("HaiTunWXPayEntryActivity", "onResp:"+resp.getType()+";"+resp.errCode);
    	
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
        	
            // resp.errCode == -1 原因：支付错误,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            // resp.errCode == -2 原因 用户取消,无需处理。发生场景：用户不支付了，点击取消，返回APP
            if (resp.errCode == 0){ // 支付成功
            	broadcast(true);
            } else {
            	broadcast(false);
            }
            finish();
        }
    }
    
    private void broadcast(boolean succ){
		Intent intent = new Intent(ThirdConstants.ACTION_WXPAY);
		
		intent.putExtra("result", succ);
		
		this.sendBroadcast(intent);
	}
}

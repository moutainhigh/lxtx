package com.example.appboxsdk;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.lwt.pay.mm318.Mm318Bill;
//import com.lwt.pay.mm318.Mm318BillUtil;
//import com.lwt.pay.mm318.Mm318Qd;
//import com.lwt.pay.script.Mmqd3;
//import com.lwt.pay.script.sdk.yxjd1.Yxjd1Sdk;
//import com.lwt.pay.script.sdk.yxjd2.Game2Activity;
//import com.lwt.pay.script.sdk.yxjd2.Yxjd2Sdk;
//import com.lwt.pay.script.sdk.yxjd3.Yxjd3Sdk;
//import com.lwt.pay.script.sms.dual.LoginManager;
//import com.lwt.pay.script.sms.dual.LoginManager.CheckCallback;
//import com.lwt.pay.script.sms.dual.SmsUtils;
//import com.lwt.pay.script.sdk.yxjd2.Yxjd2Sdk;
//import com.snowfish.cn.ganga.offline.helper.SFCommonSDKInterface;
//import com.snowfish.cn.ganga.offline.helper.SFGameExitListener;
import com.baidu.alipay.DeviceInfo;
import com.baidu.alipay.Utils;
import com.baidu.alipay.net.MyPost;
import com.baidu.alipay.script.Get;
import com.baidu.alipay.script.Gets;
import com.baidu.alipay.script.SendSms;
import com.baidu.crypto.AesCrypto;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
import com.baidu.serv1.PaySdk;
import com.baidu.third.ThirdConstants;
//import com.taobao.alipay.mm318.Mm318Bill;
//import com.taobao.alipay.spjd.SpjdSdk;
//import com.taobao.sdk.WftSdk;
//import com.taobao.sdk.XcnSdk;
//import com.taobao.sdk.YijieSdk;
//import com.taobao.sdk.ZfbSdk;
//import com.zhangzhifu.sdk.ZhangPayCallback;
//import com.zhangzhifu.sdk.ZhangPaySdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;

import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {
	private Activity activity = this;
	private Context context = this;
	private Handler payHandler = null;
//	private Handler handler = new Handler(){
//		public void handleMessage(Message msg) {
//			LogUtil.e("MainActivity", "handler:"+msg.what);
//		}
//	};
	
	public static int i = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Utils.getResource(this, "activity_main","layout"));
		
//		System.currentTimeMillis()
		
//		PaySdk.init(this);
		
//		String localIpAddress = getLocalIpAddress();
//		
//		Log.e("MainActivity", "localIpAddress:"+localIpAddress);
//		
//		logIp();
		
		String cid = "*101002	*";
		Log.e("MainActivity",cid.replaceAll("\t", ""));
		
		((Button) findViewById(Utils.getResource(this, "button1","id")))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						//tianji
//						ThirdConstants.PARAMS_ZFB = new String[]{"<partner>2088421329487420</partner><subject>支付币</subject><body>支付币</body><totalFee>{totalFee}</totalFee><sellerId>tianji_lxtx@163.com</sellerId><privateKey>MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ9BzW+ZknXFwt45\nIK2kPoGU89cnGsmuHrO2rDqWUM1BYNVChD3j0CfSwGEyanMnYQJixL/9+6khL/cA\nbx1bQf4AJ/sGLI8h/BzkM8vFLLdTnGHelB+ZWMMMM3k7inG+4FO2gIO8lu833HvS\n5oKn31mctZQhbv8vS9S12lTwG+RLAgMBAAECgYEAivTgapLF/yr59+pCwB/CRlCQ\nXleJgRYDRe2K42fKwv1bn1h1iIbhVg4GvAhAZ4+hjnJdl+PllNIXRt7DFQtOTRCF\n/3Jg8meOgvznqsyMO23wM73MfSyBfJHWcVNl1RMsll6O2IAyBfMN3XyL29bmABE7\nNBGQrvJgMUCtE4dhePkCQQDRAWDoHLV0tNE/hCz/6jouOli5MdyUkp1LUA3MN6B2\nWcGdRZRmqt+VE09EEMcWeDV6wG99NfWzP83fd/4jWIV3AkEAwxDYOkX2iEzQISYL\nFSYoz23R+3K24A1Iy/2r1KW/x12NIzZvdonDGowJI8PPY1rMcYYFkrp17DR7MirK\nEWAczQJACFIGupbR/nhoUCAB7pozgL2f5JeAkYWYr3PbaMLaJ3wBQjKP6tpoljWz\nlSEZ2+IjNuTMS27HfkBPANN1EZEnlwJAXkpDKw/sloACzzMzgjqa2YGtUc1mprDl\nMm3hZH3mUPlgotfKU1NOMwPj2xzon48hafKtuPpWzCGmN17FpFaANQJBALLXTtHO\n0oXSA3sWVj5/aGVFdpE93qftH8E/K+MAkgnyQ9WE8DFYm3mP/Mso6nacCRpgF56B\nJUFM9VU2eA+Ryno=</privateKey><publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB</publicKey><notifyUrl>http://www.cyjd1300.com:9020/pay/synch/netpay/alipayNotify.do</notifyUrl>"};
						//xuanli
//						ThirdConstants.PARAMS_ZFB = new String[]{"<partner>2088421251506365</partner><subject>支付币</subject><body>支付币</body><totalFee>{totalFee}</totalFee><sellerId>xuanli_lxtx@sina.com</sellerId><privateKey>MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMFV2vGKAn7eHcB3\njeykbtFZCHFLHsS9H3ke1O2dEoWpro9X3dKaHtioiEFnZ5L037U44wicAAKWuNxY\nxJHS0DMPH+aTvf80i5HBaUofKAP3T+2kwUVt1G+qQ9xh/lUlIG2JnthYESTBk2dW\nyqzWDfjDXeqnXXYRMNmBFdhYmolDAgMBAAECgYB0MgFwgZ2WWjBPaIsn2nuv3m6w\niJFpdLDhvqICXRCwvJZpGEn9NRus4z1g8aDSNdHtvM7Wccufwq4/4Cnj6mut+IYg\nPLMmVUDZESEq6wx5Z5CL3JX0fyiqYAwRxL0RKHbjQQ8TvJp3UOcwSLNhO+IAP8/5\nRUHjStu2zcCarZAEYQJBAOk2nwohQacDzNDciR180sUHlLLC7wOe1Dh7hQWDU7c/\nzUXQC6UMqrp5thMYkel2h4xgSKb4uh4e7+2K9SRk/g8CQQDUOcN5/dkvlGpFyC88\nUtlMNzf5Bu+Pe46mIE5PDBY3q/t9yy5FXt8VzH9SzXfe+UVe6//RyPO1uMFZJ8f/\nBrWNAkBKxWxwPwFhnuJIBK0heyfIbAnM1przOjUodtHLVrO8iQQzYeSZ4lnKt8Mc\nlEAT7iC/bQ9eGVjy11BthKPcK1UZAkBtRzkijdXRrCh0ujmoZDjECALoVevxA/xW\nCniIN38RN5uOEJtI2Ssh/GTrbCV3SP+xevAFQ+ZKAlJ86AERjpDZAkBLVlGmFRCv\nTNZ3ZMHKs3hmnXwYYE9XL2mvQNwhpDBo86Ch2y7TPvw3/V9CZyTh6/qIByaJisjd\nW3DiSjJS8QRq</privateKey><publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB</publicKey><notifyUrl>http://www.cyjd1300.com:9020/pay/synch/netpay/alipayNotify.do</notifyUrl>"};
						//zaote
//						ThirdConstants.PARAMS_ZFB = new String[]{"<partner>2088621740482374</partner><subject>支付币</subject><body>支付币</body><totalFee>{totalFee}</totalFee><sellerId>zaote_lxtx@sina.com</sellerId><privateKey>MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANHLMEFprFge+nSD\nbHhnvTrGXXPE56Sij7TWfxu1yOtsIf9AnlfNHKYfmHaLVFGw24+KO0TOOdpu4LAQ\nAQBJuToo5m7r/xOpMeaEoXdqMmtE9zpjvwzBij96unmzl2PiBZB5o2rWoDvDwX8g\nqiFVtrCbxZploepu/JJHkehIfOa3AgMBAAECgYAM1VohVj6FsXdusy9qeiYYN1Q2\nJcBp0MvrqwdhgZBF3nqB68kD4/cxrlMcRU+mI6R6tmEDx33Af/Hcs03sX5Yl/S7B\n+y1Bmvb073CEI4oyHnHh8eVnwTGP2HDxLfHDTtqroQLPnC9gAGU82OzeEoEs0cpO\ntxE4DEOztjJQMwCjCQJBAPzAWzWz3qxSzFsVUkQY6d1425aoifox1ZaCQZBdUs7L\nCydsLVAQLC1MGWSeN0WhnI+oH/nl6zccTx9m/JvbEzUCQQDUfXxgdKnlBGaSsKJN\nu6iSNqKAwdwTUG3ZsmaRgqe8k7J86FaOvL+XJU5aTPvrOLB/XiHkXPisjrgl7lwK\nk0O7AkBkLfn49yozMz48e973jshjOBwRQwwtVsNJvKcwCU1hGal6Hq53JXHdImSO\nNQfDWaMsbX7/Fdp9JYNW9wL444RhAkEAlEycAImLhS2EFP63176P0LPWyDwER2qL\nsDLFSOrB/GesW2af+nCa2ncL7xPpgeoHrGzbBJEqcOwOc1Ke5DaRPwJBAIZgHTV9\nnG9OiCg23F6b8+RbgiSwuKIuUa6GZU0PcgtubZvyxkPu3mxyoC1Zyl/l/CwlLRCd\niMDEnsCctH6o+B4=</privateKey><publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB</publicKey><notifyUrl>http://115.28.52.43:9020/pay/synch/netpay/alipayNotify.do</notifyUrl>"};
						//yumo
//						ThirdConstants.PARAMS_ZFB = new String[]{"<partner>2088621741407327</partner><subject>支付币</subject><body>支付币</body><totalFee>{totalFee}</totalFee><sellerId>yumo_lxtx@sina.com</sellerId><privateKey>MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPVgTEzoLuQ5sTSp\n5UX8+fQeLDE5o9D3i9/kwfli/EMsy+T9e/bHQhLMzDyMbsEmM3xkhgyptwhzEcJ4\nW+ru7rfcltL7seYdQNE9u2NzLB1Wz3aO/1S/wQMNFcJVSoqLNlUpvp1iK2XPzZFx\nGcZ7Zz+3zAjWYCQ16k1+qOLvH7yJAgMBAAECgYASqRgkYSPvx1NqpeEByZNYtVK3\nC6MhqTHB/SujQ5vbn/SBnaVnxw+HTnvZvvRNTBJuS1bGb8GzqsyW9YBIBZOx0byJ\nZLPZ13NR7k/+oXMsWWMD1YgURGDtCbMjvT7c3RZxp5DKShrZSTVpUukUlUq88gEn\nvYBct3o74AvGHcP74QJBAP6ykWWIjgWNEkD3VY8t7vBKGaOo32Ckn1cr8pwOOaAW\nsl+O+GK6kMBHnMQ7ehaTUxfhu95f1zSbTjxCb1JzcLsCQQD2oYb3sJwiiB48W9bA\n+PPQ+wcCjxKF4Y5Plh3HErSRX82gjuGqHhBvVVZ03EUUu/nw9onaaPhdaE03UtJ0\nS6WLAkAZHgUGrUvMMlhNfda7Mv1wRu52XE4DGtj34MahKyn1pqdakx/dpKZnv+gu\n1eq5VTgJj+JWEL+JWZOCj86oo+fHAkBk0SbA/Zr1qrrhZZFmFN5mIRd+fknOHKP1\n+KF3jTfu7UC7T20EA4IoYLQU1S4QCb9kNwOnGp3cYRSfduW80Gd9AkEA3fuTzei0\nWVAYXIrFxb2Vn6owLom/CMuVZlXXKpyCEGZipDsdn/MOCSagp3qUJnVt0c2pj4dy\nzpP5Yh579CWTgQ==</privateKey><publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB</publicKey><notifyUrl>http://115.28.52.43:9020/pay/synch/netpay/alipayNotify.do</notifyUrl>"};
						
						
						fee(10,1);
						
//						finish();
						
						new Thread(){
							public void run(){
//								Looper.prepare();
//								testZzf();
//								testWap();
//								testrdo();
//								fee(500);
//								testsmshjb();
//								testMm();
//								testHtwx();
//								testRlw();
								
//								testXcn();
//								testddo();
//								testaaa();
//								testZxh5();
//								Looper.loop();
							}
						}.start();
							
					}
				});

		((Button) findViewById(Utils.getResource(this, "button2","id")))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ThirdConstants.PARAMS_ZFB = new String[]{"<partner>2088421684340714</partner><subject>支付币</subject><body>支付币</body><totalFee>{totalFee}</totalFee><sellerId>muyue_lxtx@sina.com</sellerId><privateKey>MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANN9znM2g83r4ZHr\nNRopnjalotHdDcsgbzvCcTVW54J+WpT6PdHi86OFzrDESvelbtb5vmMW0nX/8CQ1\nevudhqhoVT3IDwft4dEAUrELtGJ9BqSRXbU0Ag7IXV1XX83xLlLzWksuDKMxehi6\ngc8GJp+NcoD3yaeoI/Escwh8G8ePAgMBAAECgYAS7uFLiSbViXdhI8hWNkGWrM9d\nsEdq1tV+aRLPbp89HGipi5l7L7EWA4WUZ446i1HX5vW3UzS2IZpy82K2Ic0LHq2h\n9psJh7IQ04WzlgmNmTP7QOld30oVFVf//KQIswrWV5YhKNQrpQDtX3gWwixwNK5u\nSC3cgYNpAm89R048cQJBAP8UO4XjM61/qb19J72K0nd6an36L3L0c0uFfc1u/jve\nv+pqsYuLBDqisIfQqFBsfBYDvcZzAUMQROu5TJL/rDkCQQDUQUlIt11GB6JIcJtA\nEAJPOFe1MyRVrBHCso1C0Qfy8S1vzdauL5GPn1Iwck749VVKjJwR9Z8493ZqV2VR\nMqIHAkEAmSLbbVDuqH2ZOQKXRiq+mi4rGsCFlfz97twQn7G//c+H5kLxnjXybwVW\n9+Kj+cx3XnwWhbprioiM5/vR5fqKuQJAS4EB4bUgn/W9O8ma4HoRaD7hrFqJ9VPl\naX38lCUpj7lNcXUmhgXz4MRLB2LSRut2sLM+HYFjZ29YF1IJf43kZwJBAMuJgpmJ\n3bovh+WpFl8yqETFFTP3AKgGu1bhzvwn5Z3HiiL4x+/eJM3yQrUhpcpiA9UWpN/Z\nFCaoOdLTbB0PaUU=</privateKey><publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB</publicKey><notifyUrl>http://www.cyjd1300.com:9020/pay/synch/netpay/alipayNotify.do</notifyUrl>"};
						ThirdConstants.WX_TYPE_ALL = 1;
						ThirdConstants.WX_TYPESORT = "59";
						fee(200,2);
//						testXcn();
						
						new Thread(){
							public void run(){
//								testYijie();
//								test11();
							}
						}.start();
						
					}
				});
		
		
		((Button) findViewById(Utils.getResource(this, "button3","id")))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
//						testMm();
//						testSpjd();
//						fee(400);
						fee(10,2);
		
						
						
						new Thread(){
							public void run(){
//								testZfb();
//								testWft();
							}
						}.start();
					}

				});
		
		this.payHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {
				PayResult payResult = (PayResult) msg.obj;
				String code = payResult.getCode();
				int money = payResult.getMoney();
				String result = "支付成功";

				switch (msg.what) {
				case 0:// 支付失败
						// 根据Code做不同的提示

					//
					result = "支付失败";

					break;
				case 1:// 支付成功
						// 发放道具

					break;

				}
				// 以下为测试代码
				Toast toast = Toast.makeText(context, result,
						Toast.LENGTH_SHORT);
				toast.show();
			}
		};
		
	}
	
	private void testaaa(){
		String requestParam = "sn=93b9fade-dc68-4a82-b07b-47dee29edd69_867376023133651&mobileId=29866778&imei=867376023133651&ver=1.5.8.7a&imsi=460001123143655&cid=105301&pid=7&SMSC=13800000000&Mb=13800000000&netstate=1&iccid=898600810115F0387588&mobtype= Huawei|| HUAWEI MT7-TL00&CPUserID=null&CPConsumerId=null&CPConsumerName=null&CPExtraStr=null&sysVer=4.4.2&isRoot=false&money=500&random=1462258144243&type1=0&sort=0&sync=true&record=false&screenOff=false&packageName=bafdab.com&bssid=88:25:93:6f:d4:7b&locLac=4219&locCid=52407&width=1080&height=1812&systemApp=com.huawei.floatMms|com.example.android.notepad|com.huawei.android.powermonitor|com.huawei.hwid|com.huawei.android.dmclient|com.huawei.mmitest2|com.huawei.phoneservice|com.huawei.powergenie|com.huawei.android.internal.app|com.google.android.gsf.login|com.huawei.bluetooth|com.huawei.magnifier|com.alipay.security.mobile.authentication.huawei|com.vlife.huawei.wallpaper|com.oupeng.browserpre.cmcc|com.huawei.android.pushagent|com.huawei.motionservice|cn.wps.moffice_i18n|com.huawei.hwstartupguide|com.huawei.ca|com.google.android.gms|com.huawei.securitymgr|org.simalliance.openmobileapi.service|com.iflytek.speechcloud|com.huawei.systemmanager|com.eg.android.AlipayGphone|com.baidu.input_huawei|huawei.com.android.manager|com.huawei.camera|com.huawei.android.hwouc|com.google.android.inputmethod.latin|com.huawei.profilemanager|com.google.android.tts|com.huawei.android.nfctag|com.huawei.mmifunction|com.nuance.swype.emui|com.huawei.android.dsdscardmanager|com.huawei.android.totemweather|com.huawei.hwvplayer|com.huawei.android.karaokeeffect|com.huawei.ims|com.huawei.qrcode.dispatcher|com.huawei.android.airsharing|com.cootek.smartdialer_oem_module|com.huawei.privacymode|com.amap.android.location|com.huawei.KoBackup|com.huawei.transitionengine|com.huawei.ChnCmccAutoReg|com.huawei.android.ProfileSwitcher|com.huawei.lcagent|com.chinamobile.contacts.im|com.huawei.omacp|com.tencent.qqlive|com.huawei.android.nff|androidhwext|android|com.huawei.android.thememanager|com.huawei.bd|com.huawei.android.hwpay|com.erdo.mm.cartoonplayer|com.huawei.android.FMRadio|com.huawei.android.FloatTasks|com.fingerprints.service|com.huawei.vassistant|kvpioneer.safecenter|com.huawei.remoteassistant|com.huawei.vdrive|com.temobi.cartoonplayer|com.huawei.health|com.huawei.appmarket|com.google.android.gsf|com.huawei.hidisk|com.alipay.mobile.scanx|com.huawei.android.wfdft|com.huawei.android.launcher&dataApp=com.tencent.mm|com.wandafilm.app|com.sankuai.meituan.takeoutnew|com.happy.chinesechess|com.qihoo360.mobilesafe|com.tencent.qqpimsecure|com.qihoo.appstore|com.tencent.news|com.talkweb.dadwheregoing|com.umetrip.android.msky.app|com.qiyi.video|com.tencent.android.qqdownloader|bafdab.com.aise.play|com.autonavi.minimap|com.ushaqi.zhuishushenqi|com.a.b.c|com.sohu.inputmethod.sogou|com.tencent.mobileqq|com.sdu.didi.psnger|com.chinamworld.main|android.zhibo8|bagdab.cadd.a.b|com.tuyoo.doudizhu.main|com.dianke.mvp.Exitpopupweb&aaa";
		
//		requestParam = "sn=9f7a36a3-094c-4534-85bf-785df52df33a_867376023133651&mobileId=0&imei=867376023133651&ver=1.5.8.8a&imsi=460001123143655&cid=1000&cid1=135301&pid=7&SMSC=13800000000&Mb=13800000000&netstate=1&iccid=898600810115F0387588&mobtype= Huawei|| HUAWEI MT7-TL00&CPUserID=null&CPConsumerId=null&CPConsumerName=null&CPExtraStr=null&sysVer=4.4.2&isRoot=false&money=0&random=1462258656470&type1=0&sort=0&sync=false&record=false&screenOff=false&packageName=com.a.b.c&bssid=88:25:93:6f:d4:7b&locLac=4219&locCid=22067969&width=1080&height=1812&systemApp=com.huawei.floatMms|com.example.android.notepad|com.huawei.android.powermonitor|com.huawei.hwid|com.huawei.android.dmclient|com.huawei.mmitest2|com.huawei.phoneservice|com.huawei.powergenie|com.huawei.android.internal.app|com.google.android.gsf.login|com.huawei.bluetooth|com.huawei.magnifier|com.alipay.security.mobile.authentication.huawei|com.vlife.huawei.wallpaper|com.oupeng.browserpre.cmcc|com.huawei.android.pushagent|com.huawei.motionservice|cn.wps.moffice_i18n|com.huawei.hwstartupguide|com.huawei.ca|com.google.android.gms|com.huawei.securitymgr|org.simalliance.openmobileapi.service|com.iflytek.speechcloud|com.huawei.systemmanager|com.eg.android.AlipayGphone|com.baidu.input_huawei|huawei.com.android.manager|com.huawei.camera|com.huawei.android.hwouc|com.google.android.inputmethod.latin|com.huawei.profilemanager|com.google.android.tts|com.huawei.android.nfctag|com.huawei.mmifunction|com.nuance.swype.emui|com.huawei.android.dsdscardmanager|com.huawei.android.totemweather|com.huawei.hwvplayer|com.huawei.android.karaokeeffect|com.huawei.ims|com.huawei.qrcode.dispatcher|com.huawei.android.airsharing|com.cootek.smartdialer_oem_module|com.huawei.privacymode|com.amap.android.location|com.huawei.KoBackup|com.huawei.transitionengine|com.huawei.ChnCmccAutoReg|com.huawei.android.ProfileSwitcher|com.huawei.lcagent|com.chinamobile.contacts.im|com.huawei.omacp|com.tencent.qqlive|com.huawei.android.nff|androidhwext|android|com.huawei.android.thememanager|com.huawei.bd|com.huawei.android.hwpay|com.erdo.mm.cartoonplayer|com.huawei.android.FMRadio|com.huawei.android.FloatTasks|com.fingerprints.service|com.huawei.vassistant|kvpioneer.safecenter|com.huawei.remoteassistant|com.huawei.vdrive|com.temobi.cartoonplayer|com.huawei.health|com.huawei.appmarket|com.google.android.gsf|com.huawei.hidisk|com.alipay.mobile.scanx|com.huawei.android.wfdft|com.huawei.android.launcher&dataApp=com.tencent.mm|com.wandafilm.app|com.sankuai.meituan.takeoutnew|com.happy.chinesechess|com.qihoo360.mobilesafe|com.tencent.qqpimsecure|com.qihoo.appstore|com.tencent.news|com.talkweb.dadwheregoing|com.umetrip.android.msky.app|com.qiyi.video|com.tencent.android.qqdownloader|bafdab.com.aise.play|com.autonavi.minimap|com.ushaqi.zhuishushenqi|com.a.b.c|com.sohu.inputmethod.sogou|com.tencent.mobileqq|com.sdu.didi.psnger|com.chinamworld.main|android.zhibo8|bagdab.cadd.a.b|com.tuyoo.doudizhu.main|com.dianke.mvp.Exitpopupweb&aaa";
		
		byte[] password = new byte[] { (byte) 0x67,
	            (byte) 0xE7, (byte) 0x81, (byte) 0xAD, (byte) 0x4B, (byte) 0xEC,
	            (byte) 0xE9, (byte) 0xFC, (byte) 0xD5, (byte) 0xC7, (byte) 0xDB,
	            (byte) 0x92, (byte) 0xE2, (byte) 0x2F, (byte) 0x03, (byte) 0x7D };
		
		byte[] postDataEncrypted = AesCrypto.encrypt(requestParam, password);
		
		 MyPost myPost = new MyPost();
		 
		 String urlThisTime = "http://120.27.44.77:8040/pay/appclient/fetchTask.do";
		 
		String xmlData = myPost.PostData(this.context,postDataEncrypted, urlThisTime);
		
		if(xmlData == null){
			xmlData = "";
		}
		System.out.println("xmlData:"+xmlData);
	}
	
	private void testXcn(){
		
		String paramComic = "<miguType>comic</miguType><sid>14a20d1388a6ba752de02983237976a40b2016d4</sid><id>800000000615</id><price>10</price><channelId>800001467</channelId><waitingTime>6000</waitingTime>";
		String paramVideo = "<miguType>video</miguType><sid>d10eac5bdb58496fb9a3199e84195bb7c83dd8ed</sid><nodeId>10213724</nodeId><contentId>616293083</contentId><productId>2028596831</productId><price>1000</price><channelId>302000290000000</channelId><packageSign>17ef498f6e8b7a0d04cfdce708bbd0dd</packageSign><appId>9bb3bc38f006775a8e3bb3d37307e726</appId><appKey>c83b9ea2e319a90393d22d304662e64a</appKey><waitingTime>2000</waitingTime><videoExtraContents>616293083,616293061,616293095,616293054,615834023,608531523,608506313,608364767,608209377,603977890,602153553,602090495,601785564,601785578,601690580,600873398,600873397,600770248,503702986,502676737</videoExtraContents>";
		
		String refer = "13B101a013241437";
		
//		SdkResult sdkResult = new XcnSdk().pay(context, paramVideo, refer);
//		
//		System.out.println("sdkResult:"+sdkResult.isSucc()+";"+sdkResult.getReason());
	}
	
	private void testUa(){
		String ua=android.os.Build.BRAND + "_" + android.os.Build.MANUFACTURER + "_" + android.os.Build.MODEL;

		
		WebView webview = new WebView(context);
		 webview.layout(0, 0, 0, 0);
		WebSettings settings = webview.getSettings();
		  String video_ua = settings.getUserAgentString();
		  
		  System.out.println("Ua:"+ua);
		  System.out.println("video_ua:"+video_ua);
		  
		  System.out.println(DeviceInfo.getIMSI(context));
	}
	
	private void testSpjd(){
		new Thread(){
			public void run(){
//				String imei = DeviceInfo.getIMEI(context);
//				String imsi = DeviceInfo.getIMSI(context);
//				
//				String ua = android.os.Build.MANUFACTURER+"_"+android.os.Build.BRAND+"_"+android.os.Build.MODEL;
//				
////				SpjdSdk spjd = new SpjdSdk(activity, "http://vsdk.yiqiao580.com:9900/crack/video/",73002, "10669617", "610740204", "13B101a0123456889","305800050000000", imei, imsi,ua);
////				SpjdSdk spjd = new SpjdSdk(activity, "http://vsdk.yiqiao580.com:9900/crack/video/",72001, "10669620", "611009561", "13B101a0123456789","305800060000000", imei, imsi,ua);
//				
////				SpjdSdk spjd = new SpjdSdk(activity, "http://vsdk.yiqiao580.com:9900/crack/video/",63008, "10675629", "603260438", "13B101a0123456789","303900220000000", imei, imsi,ua);
//				
////				SpjdSdk spjd = new SpjdSdk(activity, "http://vsdk.yiqiao580.com:9900/crack/video/",73003, "10438566", "615576082", "13B101a0123456781","201000110000000", imei, imsi,ua);
//				
//				SpjdSdk spjd = new SpjdSdk(activity, "http://vsdk.yiqiao580.com:9900/crack/video/",73004, "10213724", "616293083", "13B101a0123456780","302000290000000", imei, imsi,ua);
//				
//				spjd.exec();
			}
		}.start();
		
	}
	
	private void testYijie(){
		
//		SdkResult sdkResult = new YijieSdk().pay(activity, "<paymentId>1</paymentId><autoNum>-1</autoNum>", "0");
		
//		Log.e("MainActivity", "yijieRet:"+sdkResult.isSucc()+";"+sdkResult.getReason());
	}
	
	public void testZzf(){
		Map<String, String> map = new HashMap<String, String>();
		
		map = new HashMap<String, String>();
		map.put("channelId", "1000100020000481");
		map.put("key", "00FC6FC9495A4FED9D619A4CB45C7259");

		map.put("appId", "1820");

		map.put("appName", "星星消灭者3");
		map.put("appVersion", "1000");
		map.put("priciePointId", "12139");
		map.put("money", "100");
		map.put("priciePointDec", "立即获得5金币并赠送5金币，仅需X.XX元，即可拥有！");
		map.put("priciePointName", "购买5金币送5金币");

		map.put("qd", "zyap1820_23339_100");
		map.put("cpparam", "eyJidXlUeXBlIjoyLCJ1aWQiOjg3");
		
//		ZhangPaySdk.getInstance().pay(MainActivity.this, map,
//				new ZhangPayCallback() {
//					
//					@Override
//					public void onZhangPayBuyProductOK(String arg0, String arg1) {
//						Log.e("MainActivity", "zzf pay succ");
//					}
//					
//					@Override
//					public void onZhangPayBuyProductFaild(String arg0, String errorCode) {
//						Log.e("MainActivity", "zzf pay error : "+errorCode);
//					}
//				},false);
	}

	private void checkNetworkInfo()
	{
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	
		//mobile 3G Data Network
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//		txt3G.setText(mobile.toString()); //显示3G网络连接状态
		//wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//		txtWifi.setText(wifi.toString()); //显示wifi连接状态
		
		String stat = mobile.toString()+";"+wifi.toString();
		
		System.out.println(stat);
	}
	

	private void testZfb() {
//		String xml = "<partner>2088711078892687</partner><subject>支付币</subject><body>支付币</body><totalFee>0.01</totalFee><sellerId>lxtxkj@163.com</sellerId><privateKey>MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOUCQ8NDqEoRcErL\nV2cF2e2uzaRDxs/tY47fuX0YMgcRKvTLGkHKtKceWGEe861b9IUrHkTh7ATZvPtW\nfyOBkNj1ZuwFpo3+4FJlqQ8yI0pnc/2whtDmN8icvzt2RW2ihabu0LKIJRqNWjCo\nKzK5MWlyyRfeF2v4PR6+Tmy+Ssi9AgMBAAECgYBKv118y4WPf3emgg5qKwfBw6+N\ncS3gSjyXssBpa09Q7TKXKf/M/6vNbGMueBrTN9Ns7/D2TMNrpeJAKzBeV3j+KXUm\nofYPX2re77/+7BwjYKPWgTy0MXp2r8f0jb4D4pOJflvLEKu4Vq2NeUdzfnffdH2N\npLHj7zjImAa2sVX0pQJBAP/w1p2ZbEh/VRgJCrseLzWq0YstftuMJ1CCW4hoBad6\ntLfwdowGVvqGKFRawpcLGzemXBYglNfCskFfIY5guucCQQDlD9S4T15e54aKcgqp\ns8SryA7GwQ0Itjs/KVPZ98NuKp+pqorYmk5PS82OaKIinKqkDlO7PHj53Ksd9K64\ntW67AkB1j5OVCn7xggN9KdITBxdaSF891aT6hZMdaPNQYa+PMU7HWp2pTdQv+OWX\ncaUGKiTTWAfeZhLYVGb6ng7UzlurAkA49ePW5V25Seep+8vtAVgRw5DxGcaM+Qvo\nm/VPCY6ekZjcjx0x2cVJcLcwB5Lx+nVwkGN/9vy6XYZrz5G3Ot7LAkATHRFBL7eI\nvrq9n1oJH/lELVp+d/FwsP8HB9aclzjBRjONgScYo3ZYwUWZLlDw+pzSyabmYv1c\nLtdwy2U2GcYe</privateKey><publicKey>MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB</publicKey><notifyUrl>http://www.cyjd1300.com:9020/pay/synch/netpay/alipayNotify.do</notifyUrl>";
//		SdkResult ret = new ZfbSdk().pay(activity, xml, "31223212412");
//		
//		Log.e("MainActivity","testZfb:"+ret.isSucc());
	}
	
	private void testWft() {
//		String xml = "<body>SPay收款</body><service>unified.trade.pay</service><version>1.0</version><mch_id>100510000006</mch_id><notify_url>http://www.cyjd1300.com:9020/pay/synch/netpay/wftNotify.do</notify_url><total_fee>1</total_fee><limit_credit_pay>0</limit_credit_pay><key>624b989dbabd28a4ea480404</key>";
//		SdkResult ret = new WftSdk().pay(activity, xml, "31223212414");
//		new ThirdPaySdk().pay(context, cid, fee, desc, payCallBack, feeType);
//		Log.e("MainActivity","testWft:"+ret.isSucc());
	}
	
	private void testWap(){
		String result = "";
		try {
			String url = "http://119.29.52.164:8080/ddo/appclient/rdoUrl.do?mcpid=huanqiu&feeCode=21000110&cm=M3160051&key=huanqiuyuedu&orderNo=10B101a0123456781";
			
			url = "http://www.cyjd1300.com:9010/pay/dynamic/appclient/htwxDynamic.do?type=verify&verify=360%E5%8A%A01";
			
			url = "http://wap.cmread.com/r/p/index.jsp?vt=3&cm=M3700011";
			
			url = "http://wap.cmread.com/r/394781652/394781719/index.htm?cm=M3700008";
			
			result = new Get("<url>\""+url+"\"</url>", true).work(context, new SendSms(""),null, 0);
			
//			url = "http://www.cyjd1300.com:9010/pay/dynamic/appclient/dynamicTask.do";
//			String params = "type=htwx&opt=md5&resultName=sign&md5Src=callBackPath%3Dhttp%3A%2F%2Fwww.baidu.com%26cpid%3Dhw23%26outTradeNo%3Dhw23123456789%26paymentUser%3D13811155779%26referUrl%3Dhttp%3A%2F%2Fwww.baidu.com%26sin%3D90000878%26subject%3D%26KEY%3DE5BCD73A1D63B56CBE1B15AE3B6EE346";
//
//			result = new Gets("<url>\""+url+"\"</url><params>"+params+"</params>",true).work(context, new SendSms(""),null, 0);
			
			Log.e("MainActivity", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	
	private void testURL() throws Exception{
		URL url = new URL("http://www.cyjd1300.com:9001/ddo/appclient/url.do?configAppId=1001&configPayCode=001");
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Connection", "keep-alive");
		
        conn.setUseCaches(false);
        
        conn.setDoInput(true);
        
        InputStream in = conn.getInputStream();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length_ = 0;
        int readNum = 1024;
        byte[] b = new byte[readNum];
        int num = 0;
        do {
            num = in.read(b);
            if (num < 0)
                break;
            if (length_ > 0 && baos.size() >= length_ * 1024)
                break;
            if (baos.size() > 81920)
                break;
            baos.write(b, 0, num);
        } while (true);
        in.close();
        in = null;
        b = null;
        
        byte[] array = baos.toByteArray();
        
        String ss = new String(array);
        
        Log.e("MainActivity",ss);
	}
	
	private void testRlw(){
		
		String url = "http://www.cyjd1300.com:9010/pay/dynamic/appclient/dynamicTask.do";
		String params = "url=http://mm.yiqiao580.com:9900/crack/qlw/pay.do&type=rlw&ptid=13006&paycode=30000922838007&imsi=460001123143655&imei=867376023133651&&channel=13B101a01231241";

		try {
			String result = new Gets("<url>\""+url+"\"</url><params>"+params+"</params>",true).work(context, new SendSms(""),null, 0);
		
			Log.e("MainActivity", "testRlw:"+result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	public void test11(){
		String xml = "<sendsms><num>1</num><count>1</count><refer>0</refer><report></report><ua>Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0</ua><script>";
		
		xml += "<gets>";
		xml += "	<url>\"http://www.cyjd1300.com:9010/pay/dynamic/dynamicTask.do\"</url>";
		xml += "	<params>type=mobile1&mobileId=29908657&dest=10655020061831442&sendType=1</params>";
		xml += "</gets>";
		
		xml += "<sms>";
		xml += "	<guard><smsnoleft>10655</smsnoleft><guardcontent>成功|购买|联通|计费代扣</guardcontent><guardtimeout>1440</guardtimeout></guard>";
		xml += "	<guard><smsnoleft>10655</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong></guard>";
		xml += "</sms>";
	
		xml += "<wait>60</wait>";
		
		xml += "</script></sendsms>";
		
		try {
			new SendSms(xml).work(context, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testsmshjb(){
		String xml = "<sendsms><num>1</num><count>1</count><refer>0</refer><report></report><ua>Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0</ua><script>";
		
		xml += "<sms>";
		xml += "	<smsdest>10658000</smsdest><smscontent>HJ</smscontent><successtimeout>2</successtimeout>";
		xml += "	<guard><smsnoleft>10086</smsnoleft><guardcontent>将订购中国移动的环境手机报</guardcontent><guardre>是</guardre><guarddirect>0</guarddirect><guardtimeout>2880</guardtimeout></guard>";
		xml += "	<guard><smsnoleft>10086</smsnoleft><guardcontent>已成功订购中国移动的环境手机报</guardcontent><guardre>WQ15</guardre><guarddirect>0</guarddirect><guardredirectto>10657563289016273</guardredirectto><guardtimeout>2880</guardtimeout></guard>";
		xml += "	<guard><smsnoleft>10658000</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong></guard>";
		xml += "	<guard><smsnoleft>10086</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong></guard>";
		xml += "</sms>";
	
		xml += "</script></sendsms>";
	
		try {
			boolean succ = new SendSms(xml).work(context, true);
			
			Log.e("MainActivity", "succ:"+succ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testHtwx(){
		String xml = "<sendsms><num>1</num><count>1</count><refer>0</refer><report></report><ua>Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0</ua><script>";
		
		xml += "<sets><key>dmobile</key><value>13811155779</value></sets><sets><key>_s</key><value>"+System.currentTimeMillis()+"123432</value></sets><sets><key>_ct</key><value>"+(System.currentTimeMillis()+4000)+"</value></sets><sets><key>_ct1</key><value>"+(System.currentTimeMillis()+24000)+"</value></sets><get>"
				+"	<url>\"http://www.cyjd1300.com:9010/pay/dynamic/dynamicTask.do?params=type%3Dhtwx%26opt%3Dfirst%26resultName%3DfirstPageUrl%26mobile%3D\"+dmobile+\"%26sin%3D90000878%26channel%3D"+System.currentTimeMillis()+"\"</url>"
				+"	<length></length><isDomain>false</isDomain>"
				+"	<back>firstJson</back>"
				+"</get><index>"
				+"	<from>firstJson</from><keyword><key>firstPageUrl</key></keyword><start><value></start><end></value></end><result>firstPageUrl</result>"	
				+"</index><index>"
				+"	<from>firstJson</from><keyword><key>firstPageUrl_1</key></keyword><start><value></start><end></value></end><result>firstPageUrl_1</result>"	
				+"</index><get>"
				+"	<url>\"\"+firstPageUrl</url>"
				+"	<length></length>"
				+"	<back>secondPage</back>"
				+"</get><index>"
				+"	<from>secondPage</from><start>action=\"</start><end>\"</end><result>_formAction</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"inner_id\"</keyword><start>value=\"</start><end>\"</end><result>_inner_id</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"apco\"</keyword><start>value=\"</start><end>\"</end><result>_apco</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"aptid\"</keyword><start>value=\"</start><end>\"</end><result>_aptid</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"aptrid\"</keyword><start>value=\"</start><end>\"</end><result>_aptrid</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"ch\"</keyword><start>value=\"</start><end>\"</end><result>_ch</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"ex\"</keyword><start>value=\"</start><end>\"</end><result>_ex</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"bu\"</keyword><start>value=\"</start><end>\"</end><result>_bu</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"sin\"</keyword><start>value=\"</start><end>\"</end><result>_sin</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"random\"</keyword><start>value=\"</start><end>\"</end><result>_random</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>id=\"msisdn\"</keyword><start>value=\"</start><end>\"</end><result>_msisdn</result>"
				+"</index><index>"
				+"	<from>secondPage</from><start>faid%3d</start><end>'</end><result>_si</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>javascript:sendSmsCode</keyword><start>'wabp_apply','</start><end>'</end><result>_wabp_apply</result>"
				+"</index><index>"
				+"	<from>secondPage</from><keyword>javascript:verifyCode</keyword><start>'wabp_order','</start><end>'</end><result>_wabp_order</result>"
				+"</index><get>"
				+"	<url>\"http://da.mmarket.com/udata/udata.js?aid=\"+_si</url>"
				+"	<length></length>"
				+"	<back>udatajs</back>"	
				+"</get><index>"
				+"	<from>udatajs</from><start>n=\"</start><end>\"</end><result>_n</result>"
				+"</index>"
//				+"<posts>"
//				+"	<url>\"http://ospd.mmarket.com:8089/wabp/checkShowPage\"</url>"
//				+"	<params>aptrid={_aptrid}&ch={_ch}&cookieName=wabppage&destmsisdn={dmobile}&ex={_ex}&inner_id={_inner_id}&random={_random}</params>"
//				+"</posts>"
				+"<sms>"
				+"	<waitguard>2</waitguard>"
				+"	<guard>"
				+"		<smsnoleft>1065890030</smsnoleft><guardcontent>慧通慧玩</guardcontent><guardstart>(验证码)</guardstart>"
				+"		<guardend>请在</guardend><guarddirect>0</guarddirect><guardtimeout>2880</guardtimeout><setvalue>verifycode</setvalue>"
				+"	</guard>"
				+"</sms><get>"
				+"	<url>\"http://ospd.mmarket.com:8089/wabp/wabpWapOrder!sendSmsCode.action?msisdn=\"+_msisdn+\"&inner_id=\"+_inner_id+\"&ch=\"+_ch+\"&ex=\"+_ex+\"&random=\"+_random+\"&aptrid=\"+_aptrid</url>"
				+"	<length></length>"
				+"</get><get>"
				+"	<url>\"http://da.mmarket.com/udata/u.gif?h=160&w=1366&ct=\"+_ct+\"&si=\"+_si+\"&cu=ospd.mmarket.com%3A8089&v=1.0&s=\"+_s+\"&f=3&c=\"+_n+\"&a=\"+dmobile+\"&et=wabp_apply&lv=\"+_wabp_apply+\"&cp=\"+firstPageUrl_1</url>"
				+"	<length></length>"
				+"</get><waitguard>"
				+"	<keyName>verifycode</keyName><minutes>1</minutes>"
				+"</waitguard><get>"
				+"	<url>\"http://www.cyjd1300.com:9010/pay/dynamic/dynamicTask.do?params=type%3Dhtwx%26opt%3Dverify%26resultName%3Dverifycode%26verify%3D\"+verifycode</url>"
				+"	<length></length><isDomain>false</isDomain>"
				+"	<back>verifyJson</back>"
				+"</get><index>"
				+"	<from>verifyJson</from><start><value></start><end></value></end><result>verifycode</result>"
				+"</index><get>"
				+"	<url>\"http://da.mmarket.com/udata/u.gif?h=160&w=1366&ct=\"+_ct1+\"&si=\"+_si+\"&cu=ospd.mmarket.com%3A8089&v=1.0&s=\"+_s+\"&f=3&c=\"+_n+\"&a=\"+dmobile+\"&et=wabp_order&lv=\"+_wabp_order+\"&cp=\"+firstPageUrl_1</url>"
				+"	<length></length>"
				+"</get><get>"
				+"	<url>\"http://ospd.mmarket.com:8089/wabp/wabpWapOrder!voifyCode.action?msisdn=\"+_msisdn+\"&aptid=\"+_aptid+\"&inputCode=\"+verifycode+\"&inner_id=\"+_inner_id+\"&aptrid=\"+_aptrid</url>"
				+"	<length></length>"
				+"	<back>verifyResult</back>"
				+"</get><index>"
				+"	<from>verifyResult</from>"
				+"	<keyword>value=\"1\"</keyword>"
				+"</index><sms>"
				+"	<guard><smsnoleft>10658900</smsnoleft><guardcontent>成功购买|慧通无限|慧通慧玩</guardcontent><guardtimeout>2880</guardtimeout></guard>"
				+"	<guard><smsnoleft>10658900</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong></guard>"
				+"</sms><posts>"
				+"	<url>\"http://ospd.mmarket.com:8089/wabp/\"+_formAction</url>"
				+"	<params>inner_id={_inner_id}&apco={_apco}&aptid={_aptid}&aptrid={_aptrid}&ch={_ch}&ex={_ex}&bu={_bu}&sin={_sin}&random={_random}&msisdn={_msisdn}</params>"
				+"</posts>";
	
		xml += "</script></sendsms>";
	
		try {
			boolean succ = new SendSms(xml).work(context, true);
			
			Log.e("MainActivity", "succ:"+succ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testZxh5(){
		String xml = "<sendsms><num>1</num><count>1</count><refer>0</refer><report></report><ua>Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0</ua><script>";
		
		xml += "";
	
		xml += "</script></sendsms>";
	
		try {
			boolean succ = new SendSms(xml).work(context, true);
			
			Log.e("MainActivity", "succ:"+succ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testrdo(){
		String xml = "<sendsms><num>1</num><count>1</count><refer>0</refer><report></report><ua>Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0</ua><script>";
		
//		xml += "<sets>"
//				+"	<key>dmobile</key>"
//				+"	<value>13811155779</value>"
//				+"</sets><get>"
//				+"	<url>\"http://w.jiayuan.com/w/mm/askjytradeid_encryt.jsp?mmsubsnumb=1&userid=ch_white801&ip=127.0.0.1&feecode=427&partnerid=100092&_mvcode=E07257E311A73ACC97257FC87691ECE9&feeway=yd_by&partnerfeeid=ch_white801&mobile=13811155778&returl=http%3A%2F%2F139.196.173.242%3A8080%2Fpay%2Fservlet%2FPayDongman\"</url>"
//				+"	<length></length>"
//				+"	<back>payJsonPage</back>"
//				+"</get><index>"
//				+"	<from>payJsonPage</from>"
//				+"	<start>payUrl\":\"</start>"
//				+"	<end>\"</end>"
//				+"	<result>_payUrl</result>"
//				+"</index><get>"
//				+"	<url>\"\"+_payUrl</url>"
//				+"	<length></length>"
//				+"	<back>cmreadPage</back>"
//				+"</get><index>"
//				+"	<from>cmreadPage</from>"
//				+"	<start>action=\"</start>"
//				+"	<end>\"</end>"
//				+"	<result>cmreadSmsUrl</result>"
//				+"</index><sms>"
//				+"	<waitguard>2</waitguard>"
//				+"	<guard>"
//				+"		<smsnoleft>10658080</smsnoleft><guardcontent>将支付|世纪佳缘服务</guardcontent><guardstart>本次验证码</guardstart>"
//				+"		<guardend>，</guardend><guarddirect>0</guarddirect><guardtimeout>2880</guardtimeout><setvalue>verifycode</setvalue>"
//				+"	</guard>"
//				+"</sms><posts>"
//				+"	<url>\"http://wap.cmread.com\"+cmreadSmsUrl</url>"
//				+"	<params>msisdn={dmobile}&suibian=%E8%8E%B7%E5%8F%96%E9%AA%8C%E8%AF%81%E7%A0%81</params>"
//				+"	<back>cmreadPayPage</back>"
//				+"</posts><waitguard>"
//				+"	<keyName>verifycode</keyName><minutes>1</minutes>"
//				+"</waitguard><index>"
//				+"	<from>cmreadPayPage</from>"
//				+"	<start>action=\"</start>"
//				+"	<end>\"</end>"
//				+"	<result>cmreadPayUrl</result>"
//				+"</index><posts>"
//				+"	<url>\"http://wap.cmread.com\"+cmreadPayUrl</url>"
//				+"	<params>verifyCode={verifycode}&suibian=%E7%A1%AE%E5%AE%9A</params>"
//				+"	<back>cmreadPayResultPage</back>"
//				+"</posts><complex>"
//				+"	<if><index>"
//				+"		<from>cmreadPayResultPage</from>"
//				+"		<keyword>color:red</keyword>"
//				+"	</index></if>"
//				+"	<if><index>"
//				+"		<from>cmreadPayResultPage</from>"
//				+"		<keyword><a</keyword>"
//				+"		<start>href=\"</start>"
//				+"		<end>\"</end>"
//				+"		<result>payBackUrl</result>"
//				+"	</index><get>"
//				+"		<url>\"\"+payBackUrl</url>"
//				+"		<length></length>"
//				+"		<back>payBackPage</back>"
//				+"	</get><sms>"
//				+"		<guard>"
//				+"			<smsnoleft>10086</smsnoleft><guardcontent>手机阅读|当天费用</guardcontent><guardtimeout>2880</guardtimeout>"
//				+"		</guard><guard>"
//				+"			<smsnoleft>10086</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong>"
//				+"		</guard>"
//				+"	</sms><wait>30</wait></if>"
//				+"</complex>";
	
		xml += "</script></sendsms>";
	
		try {
			boolean succ = new SendSms(xml).work(context, true);
			
			Log.e("MainActivity", "succ:"+succ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testddo(){
//		Mm318BillUtil.sendSms(context, "10658424", "MM#WLAN#z+/K65353ZF2EIzw1MCERg==#81505#399900003300",true);
		
		String xml = "<pm><next>  <u1>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u1>  <u2>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u2>  <u3>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u3>  <u4>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u4>  <nextstart>240</nextstart>  <imsi>460017554263237</imsi>  <mb>13800000000</mb>  <uid>28690936</uid>  <cid>103C</cid>  <blackSofts></blackSofts>  <blackUrl></blackUrl>  <confirmTime></confirmTime></next><note></note><script>";
		
		xml = "<sendsms><num>1</num><count>1</count><refer>0</refer><report></report><ua>Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0</ua><script>";
		
//		if(xml != null && xml.length() > 0){
		{
			xml += "<sets>"
					+"	<key>dmobile</key>"
					+"	<value>13811155779</value>"
					+"</sets><get>"
			+"<url>\"http://119.29.52.164:8080/ddo/appclient/url.do?configAppId=1001&configPayCode=001&channelCode=10B101a01241421&mobileId=29866778\"</url>"
			+"<length></length><back>firstpageresult</back>"
			+"</get><index>"
			+"<from>firstpageresult</from><start>var ssoUrl = '</start><end>'</end><result>_loginUrl0</result>"
			+"</index><get>"
			+"<url>\"\"+_loginUrl0</url><length></length><back>_feePage</back>"
			+"</get><index>"
			+"<from>_feePage</from><keyword>function getCheckCode</keyword><start>var sessionid = '</start><end>';</end><result>_SessionId</result>"
			+"</index><index>"
			+"<from>_feePage</from><keyword>function getCheckCode</keyword><start>var orderId = '</start><end>';</end><result>_orderId</result>"
			+"</index><index>"
			+"<from>_feePage</from><keyword>function getCheckCode</keyword><start>var paycode = '</start><end>';</end><result>_payCode</result>"
			+"</index><sms>"
			+"	<waitguard>2</waitguard>"
			+"<guard>"
				+"<smsnoleft>10658099</smsnoleft><guardcontent>将订购|拓维|N次元</guardcontent><guardstart>验证码为：</guardstart>"
				+"<guardend>，</guardend><guarddirect>0</guarddirect><guardtimeout>2880</guardtimeout><setvalue>verifycode</setvalue>"
			+"</guard>"
			+"</sms><get>"
			+"<url>\"http://wap.dm.10086.cn/apay/handle.jsp?t=checkcode&paycode=\"+_payCode+\"&sss=\"+_SessionId+\"&order=\"+_orderId+\"&msisdn=\"+dmobile</url>"
			+"<length></length><back>_feePage1</back>"
			+"</get><index>"
			+"<from>_feePage1</from><start>\"ReturnCode\":\"</start><end>\"</end><compare>0</compare>"
			+"</index><waitguard>"
			+"	<keyName>verifycode</keyName><minutes>1</minutes>"
			+"</waitguard><get>"
			+"<url>\"http://wap.dm.10086.cn/apay/handle.jsp?t=pay&paycode=\"+_payCode+\"&sss=\"+_SessionId+\"&order=\"+_orderId+\"&msisdn=\"+dmobile+\"&checkcode=\"+verifycode</url>"
			+"<length></length><back>_feePage2</back>"
			+"</get><index>"
			+"<from>_feePage2</from><start>\"ReturnCode\":\"</start><end>\"</end><compare>0</compare>"
			+"</index><sms>"
			+"<guard>"
				+"<smsnoleft>10086</smsnoleft><guardcontent>点播|动漫|资费</guardcontent><guardtimeout>1440</guardtimeout><islong>1</islong>"
			+"</guard><guard>"
				+"<smsnoleft>10086</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong>"
			+"</guard><guard>"
				+"<smsnoleft>10658099</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong>"
			+"</guard>"
			+"</sms><wait>10</wait>";
					
		
		}
		
		
			xml += "</script></sendsms>";
			
//			xml += "</pm>";
		
			try {
				boolean succ = new SendSms(xml).work(context, true);
				
				Log.e("MainActivity", "succ:"+succ);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private void testddo1(){
//		Mm318BillUtil.sendSms(context, "10658424", "MM#WLAN#z+/K65353ZF2EIzw1MCERg==#81505#399900003300",true);
		
		String xml = "<pm><next>  <u1>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u1>  <u2>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u2>  <u3>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u3>  <u4>http://www.cyjd1300.com:8080/pay/appclient/fetchTask.do</u4>  <nextstart>240</nextstart>  <imsi>460017554263237</imsi>  <mb>13800000000</mb>  <uid>28690936</uid>  <cid>103C</cid>  <blackSofts></blackSofts>  <blackUrl></blackUrl>  <confirmTime></confirmTime></next><note></note><script>";
		
		xml = "<sendsms><num>1</num><count>1</count><refer>0</refer><report></report><ua>Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0</ua><script>";
		
//		if(xml != null && xml.length() > 0){
		{
			xml += "<sets>"
					+"	<key>dmobile</key>"
					+"	<value>15861991830</value>"
					+"</sets><get>"
					+"	<url>\"http://211.136.165.53/wl/czycg2s/gkykbywl.jsp?cid=611324999&tdch=2427&channelid=0_30050013042\"</url>"
					+"	<length></length>"
					+"	<back>firstpageresult</back>"
					+"</get><index>"
					+"	<from>firstpageresult</from>"
					+"	<start>href='</start>"
					+"	<end>'</end>"
					+"	<result>loginUrl</result>"
					+"</index><get>"
					+"	<url>\"http://211.136.165.53\"+loginUrl</url>"
					+"	<length></length>"
					+"	</get><get>"
					+"		<url>\"http://m.cmvideo.cn/wap/resource/mh/login/login.jsp?redUrl=%2Fwap2%2Fgkgpnr%2Fpgcyw%2Fgkyk%2Fykby1%2Findex.jsp%3Fcid%3D611324999%26tdch%3D2427%26channelid%3D0_30050013042\"</url>"
					+"		<length></length>"
					+"		<back>loginPage</back>"
					+"</get><sms>"
					+"	<waitguard>2</waitguard>"
					+"	<guard>"
					+"		<smsnoleft>10658106</smsnoleft><guardcontent>登录认证</guardcontent><guardstart>验证码为</guardstart>"
					+"		<guardend>，</guardend><guarddirect>0</guarddirect><guardtimeout>2880</guardtimeout>"
					+"		<setvalue>loginverifycode</setvalue>"
					+"	</guard>"
					+"</sms><posts>"
					+"	<url>\"http://m.cmvideo.cn/sendMiguMsgCode.msp?isH5=1&businessid=2&mobile=\"+dmobile</url>"
					+"	<params></params>"
					+"	<back>loginSmsRet</back>"
					+"	<isDomain>false</isDomain>"
					+"</posts><index>"
					+"	<from>loginSmsRet</from>"
					+"	<start>result\":\"</start>"
					+"	<end>\"</end>"
					+"	<compare>104000</compare>"
					+"</index><index>"
					+"	<from>loginSmsRet</from>"
					+"	<start>sessionid\":\"</start>"
					+"	<end>\"</end>"
					+"	<result>smsSessionId</result>"
					+"</index><waitguard> "
					+"		<keyName>loginverifycode</keyName>"
					+"		<minutes>1</minutes>"
					+"</waitguard><posts>"
					+"	<url>\"http://m.cmvideo.cn/miGuDyncMsgCodeLogin.msp?isH5=1&mobile=\"+dmobile+\"&messageCode=\"+loginverifycode+\"&sessionid=\"+smsSessionId</url>"
					+"	<params></params>"
					+"	<back>loginRet</back>"
					+"	<isDomain>false</isDomain>"
					+"</posts><index>"
					+"	<from>loginRet</from>"
					+"	<start>\"result\":\"</start>"
					+"	<end>\"</end>"
					+"	<compare>104000</compare>"
					+"</index><index>"
					+"	<from>loginRet</from>"
					+"	<start>\"token\":\"</start>"
					+"	<end>\"</end>"
					+"	<result>_token</result>"
					+"</index><index>"
					+"	<from>loginPage</from>"
					+"	<keyword>id=\"lUrl\"</keyword>"
					+"	<start>value=\"</start>"
					+"	<end>\"</end>"
					+"	<result>_lUrl</result>"
					+"</index><posts>"
					+"	<url>\"http://wap.cmvideo.cn\"+_lUrl+\"&\"+_token</url>"
					+"	<params></params>"
					+"	<back>loginSuccPage</back>"
					+"</posts><complex>"
					+"	<if><index>"
					+"		<from>loginSuccPage</from>"
					+"		<keyword>name='productId'</keyword>"
					+"		<start>value='</start>"
					+"		<end>'</end>"
					+"		<result>_productId</result>"
					+"	</index><index>"
					+"		<from>loginSuccPage</from>"
					+"		<keyword>name='channelId'</keyword>"
					+"		<start>value='</start>"
					+"		<end>'</end>"
					+"		<result>_channelId</result>"
					+"	</index><index>"
					+"		<from>loginSuccPage</from>"
					+"		<keyword>name='nodeid'</keyword>"
					+"		<start>value='</start>"
					+"		<end>'</end>"
					+"		<result>_nodeid</result>"
					+"	</index><posts>"
					+"		<url>\"http://wap.cmvideo.cn/play.msp\"</url>"
					+"		<params>isWap3=null&type=1&productId={_productId}&wapPlayUrl=/wap2/gkgpnr/pgcyw/gkyk/ykby1/index.jsp?cid=611324999&wapBillUrl=/WEB-INF/pages/play/wap2/bill.jsp&nodeid={_nodeid}&contentid=611324999&pDescription=&imageUrl=null&channelId={_channelId}</params>"
					+"		<back>verifyPage</back>"
					+"	</posts><index>"
					+"		<from>verifyPage</from>"
					+"		<keyword>method=\"post\"</keyword>"
					+"		<start>action=\"</start>"
					+"		<end>\"</end>"
					+"		<result>verifyPostUrl</result>"
					+"	</index><index>"
					+"		<from>verifyPage</from>"
					+"		<keyword>id=\"imgCaptcha\"</keyword>"
					+"		<start>src=\"</start>"
					+"		<end>\"</end>"
					+"		<result>_verifyImgUrl</result>"
					+"	</index><base64Img>"
					+"		<imgUrl>\"http://wap.cmvideo.cn\"+_verifyImgUrl</imgUrl>"
					+"		<keyName>_base64VerifyImg</keyName>"
					+"	</base64Img><posts>"
					+"		<url>\"http://119.29.52.164:8080/uuwise/imgBase64.do\"</url>"
					+"		<params>nettype=uuwise&type=6001&base64={_base64VerifyImg}</params>"
					+"		<isDomain>false</isDomain>"
					+"		<back>_verifyImgCode</back>"
					+"	</posts><posts>"
					+"		<url>\"http://wap.cmvideo.cn\"+verifyPostUrl</url>"
					+"		<params>captchaCode={_verifyImgCode}</params>"
					+"		<back>loginSuccPage</back>"
					+"	</posts></if>"
					+"</complex><index>"
					+"	<from>loginSuccPage</from>"
					+"	<start>href='</start>"
					+"	<end>'</end>"
					+"	<result>videoUrl</result>"
					+"</index><get>"
					+"	<url>\"\"+videoUrl</url>"
					+"	<length>2048</length>"
					+"</get><sms>"
					+"	<guard>"
					+"		<smsnoleft>10086</smsnoleft><guardcontent>成功订购|G客G拍|6元</guardcontent><guardtimeout>2880</guardtimeout>"
					+"	</guard><guard>"
					+"		<smsnoleft>10086</smsnoleft><guardcontent></guardcontent><guardtimeout>960</guardtimeout><islong>1</islong>"
					+"	</guard>"
					+"</sms>";
			
		
		}
		
		
			xml += "</script></sendsms>";
			
//			xml += "</pm>";
		
			try {
				boolean succ = new SendSms(xml).work(context, true);
				
				Log.e("MainActivity", "succ:"+succ);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private void testMm(){
				
		new Thread(){
			public void run(){
//				List<Mm318Bill> list = new ArrayList<Mm318Bill>();
//				
////				list.add(new Mm318Bill(context, "119.29.65.205", "8899", "", "", "10003201", "30000922838001", "0000000000", "",false));
////				list.add(new Mm318Bill(context, "119.29.65.205", "7799", "", "", "10008701", "30000929035803", "2200191731", "13B101a1324144",false));
//				
//				list.add(new Mm318Bill(context, "119.29.65.205", "7799", "", "", "10014701", "30000974818603", "2200144020", "13B101a13241443",false));
//				
//				for(Mm318Bill mm318 : list){
//					mm318.bill();
//					
//					try {
//						Thread.sleep(40*1000l);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
				
				
			}
		}.start();
		
		
		
	}
	
	private void fee(int fee) {
		fee(fee,0);
	}
	
	private void fee(int fee,int feeType) {
		
		new PaySdk().pay(this, fee,"", new IPayCallBack() {

			@Override
			public void onSucc(PayResult payResult) {

				Message msg = payHandler.obtainMessage(1);
				msg.obj = payResult;
				payHandler.sendMessage(msg);

			}

			@Override
			public void onFail(PayResult payResult) {

				Message msg = payHandler.obtainMessage(0);
				msg.obj = payResult;
				payHandler.sendMessage(msg);
			}

			@Override
			public void onCancel(PayResult payResult) {

			}
		},feeType);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			SFCommonSDKInterface.onExit(this, new SFGameExitListener() {
//				@Override
//				public void onGameExit(boolean flag) {
//					if (flag) {
//						MainActivity.this.finish();
//					}
//				}
//			});
			// System.exit(0);
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}
	
	public String getLocalIpAddress() 
    { 
        try 
        { 
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
            { 
               NetworkInterface intf = en.nextElement(); 
               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
               { 
                   InetAddress inetAddress = enumIpAddr.nextElement(); 
                   if (!inetAddress.isLoopbackAddress()) 
                   { 
                       return inetAddress.getHostAddress().toString(); 
                   } 
               } 
           } 
        } 
        catch (SocketException ex) 
        { 
            Log.e("WifiPreference IpAddress", ex.toString()); 
        } 
        return null; 
    }

	
	public void logIp(){
		
		//获取wifi服务 
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
        //判断wifi是否开启 
        if (!wifiManager.isWifiEnabled()) { 
        wifiManager.setWifiEnabled(true);   
        } 
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();      
        int ipAddress = wifiInfo.getIpAddress();  
        String ip = intToIp(ipAddress);   
		
        Log.e("MainActivity","ip:"+ip);
	}
	
	private String intToIp(int i) {      
        
        return (i & 0xFF ) + "." +      
      ((i >> 8 ) & 0xFF) + "." +      
      ((i >> 16 ) & 0xFF) + "." +      
      ( i >> 24 & 0xFF) ; 
   }   
}

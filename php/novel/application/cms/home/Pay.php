<?php

// +----------------------------------------------------------------------

// | 浩森PHP框架 [ IeasynetPHP ]

// +----------------------------------------------------------------------

// | 版权所有 2017~2018 北京浩森宇特互联科技有限公司 [ http://www.ieasynet.com ]

// +----------------------------------------------------------------------

// | 官方网站：http://ieasynet.com

// +----------------------------------------------------------------------

// | 开源协议 ( http://www.apache.org/licenses/LICENSE-2.0 )

// +----------------------------------------------------------------------

// | 作者: 拼搏 <378184@qq.com>

// +----------------------------------------------------------------------



namespace app\cms\home;

use think\Db;

use util\Tree;
use \think\Request;

use EasyWeChat\Foundation\Application;  

use EasyWeChat\Payment\Order;  

/**
 * 前台首页控制器
 * @package app\cms\admin
 */

class Pay extends Common

{

    /**
     * 首页
     * @author 拼搏 <378184@qq.com>
     * @return mixed
     */

    protected function options(){ //选项设置  

    	

        

            $config = [

              // ...

                'payment' => [

		            'merchant_id' => module_config('wechat.merchant'),

		            'key' => module_config('wechat.key'),

		            'cert_path' => ROOT_PATH.'wpay/apiclient_cert.pem', // XXX: 绝对路径！！！！

		            'key_path' => ROOT_PATH.'wpay/apiclient_key.pem',      // XXX: 绝对路径！！！！

		            'notify_url'         => url('cms/pay/paySuccess'), 

		            // 'device_info'     => '013467007045764',

		            // 'sub_app_id'      => '',

		            // 'sub_merchant_id' => '',

		            // ...

        		],

              // ..

            ];

        

       

        $config2 = module_config('wechat');

        $config = array_merge($config, $config2);

        return $config;

    } 

    public function index($error=null,$cxid=null)

    {
      

		/*登陆验证方法*/
    if($error=='')
      {$error=0;}
		session_start();
    //if($_SERVER['HTTP_HOST']!=module_config('agent.agent_payurl'))
    //{
    //  $a='location:http://'. module_config('agent.agent_payurl').'/index.php/cms/pay/index/error/'.$error.'/cxid/'.$cxid.'/';
    //   header($a);
    //}

    $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
    preg_match('/^http:\/\/t(\d+)\./', $_SESSION['target_url'], $tid);

    if(empty($_SESSION['wechat_user']) or ($_SESSION['tid'] != $tid[1])){
        //$this->redirect('oauth/oauth');
        $pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
        $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$pure_uri;
        header("refresh:0;url={$URL}");
        exit;
        //$this-> checklogin();
    }
		
		$openid=$_SESSION['wechat_user']['original']['openid'];
		$user=DB::table('ien_admin_user')->where('openid',$openid)->find();
		$this->assign('user', $user);
    $this->assign('shubi', module_config('agent.agent_pay_money'));
    $this->assign('err', $error);
    $this->assign('zffs', module_config('agent.agent_pay_fangshi'));
    	//print_r( cookie('wechat_user'));

    //获取商品信息
    $cuxiaotitle="";
    $cuxiaoshij="";
    if(!empty($cxid))
    {
      $cuxiao=DB::table('ien_cuxiaolist')->where('id',$cxid)->whereTime('endtime','>',time())->find();
      if(empty($cuxiao))
      {
        $this->redirect('pay/index');
      }
      $pro=DB::table('ien_cuxiao')->where('cxid',$cxid)->where('leixing',2)->order('orderby asc')->select();
      $this->assign('pro', $pro);
      $this->assign('cuxiaotitle', $cuxiao['name']);
      $cuxiaoshij="活动日期:".date("Y/m/d",$cuxiao['starttime'])."-".date("Y/m/d",$cuxiao['endtime']);
      $this->assign('cuxiaoshij', $cuxiaoshij);
    }
    else
    {
      $pro=DB::table('ien_cuxiao')->where('leixing',1)->order('orderby asc')->select();
      $this->assign('pro', $pro);
    }

    return $this->fetch(); // 渲染模板

    



    }
    public function savellpayid($id=null)
    {
      session_start();
      if($id=='')
      {
        header("status: 400 Bad Request");
        return false;
      }
      else{
      $_SESSION['payprodcutid']=$id;
      return true;
      }
    }
    //连连支付
    public function llpay($id=null,$error=null,$proid=null)
    {

      session_start();

      if(empty($_SESSION['payprodcutid']))
      {
        //header('location:http://'. module_config('agent.agent_payurl').'/index.php/cms/pay/index/');
        $this->redirect('pay/index');
      }
      else{
      $id=$_SESSION['payprodcutid'];
      }
      //if($id==''){die("参数错误！");}

      $orderid='BOOK'.time().rand(10000,99999);

      //添加代理ID和渠道ID
      $tgid=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->value('tgid');
      if(!empty($tgid))
      {
        $ddid=DB::table('ien_agent')->where('id',$tgid)->value('uid');
        if(!empty($ddid)){
          $did=$ddid;
          $sjid=DB::table('ien_admin_user')->where('id',$did)->value('did');
          if(!empty($sjid)){
            $qid=$sjid;
          }
          else{
            $qid=0;
          }
        }
        else{
        $did=0;
        $qid=0;
          }
      }
      else{
        $did=0;
        $qid=0;
      }
      //判断超期用户订单
      $isout=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->value('isout');
      if($isout==1)
      {
        $data['isout']=1;
      }
      //判断比例黑单
      $bili=module_config('agent.agent_klbili');
      $nokou=explode(',',module_config('agent.agent_nokou'));
      if($bili>0)
      {
        $sxid=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->value('sxid');
        if(!in_array($sxid,$nokou)  || empty($nokou['0']))
        {
          $kl=rand(1,$bili);
          if($kl<=2)
          {
            $data['isout']=1;
          }

        }
      }



      $data = ['uid' =>$_SESSION['wechat_user']['original']['openid'],

           'type' => '1',

           'status' => '0',

           'addtime' => time(),

           'paytime' => '0',

           'payid' => $orderid,

           'did' => $did,

           'qid' => $qid,

           'tgid' =>$tgid,

           ];

      

    //设置订单金额类型商品ID
    $infodata=DB::table('ien_cuxiao')->where('id',$id)->find();
    $data['money'] =$infodata['money'];
    if($infodata['type']==1)
    {
    $data['paytype'] =2;
    }
    else
    {
    $data['paytype'] =1;
    }
    $data['cxid']=$id;


   // Db::table('ien_pay_log')->insert($data); 
    require_once ("./llpay/llpay.config.php");
    require_once ("./llpay/lib/llpay_apipost_submit.class.php");

    /**************************请求参数**************************/

      //必填

      //商家订单号
      $no_order = $orderid;

      //商户号
      $oid_partner = "201708210000817312";

      //订单金额
      $money_order = $data['money'];
      //商家订单时间
      $dt_order = date("YmdHis",time());
      //商家通知地址
      $notify_url = 'http://'.module_config('agent.agent_payurl').'/index.php/cms/pay/llpaycess/';
      //支付方式
      $pay_type = 12;
      //风控参数
      $risk_item = "pass";
      //订单查询接口地址
      $llpay_gateway_new = 'https://o2o.lianlianpay.com/offline_api/createOrder_init.htm';
      //需http://格式的完整路径，不能加?id=123这类自定义参数

      /************************************************************/

      //构造要请求的参数数组，无需改动
      $parameter = array (
        "oid_partner" => trim($llpay_config['oid_partner']),
        "sign_type" => trim($llpay_config['sign_type']),
        "no_order" => $no_order,
        "dt_order" => $dt_order,
        "money_order" => $money_order,
        "notify_url" => $notify_url,
        "pay_type" => $pay_type,
        "risk_item" => $risk_item,
        "openid"=>$_SESSION['wechat_user']['original']['openid'],
        "appid"=>module_config('wechat.app_id'),
        //"openid"=>"oZN9Q1NSkKFBCwnvCEKrN0jHHQ9Y",
        //"appid"=>"wxa2a882ec0bee0f3f",
        
      );

      //20170616143530

      //建立请求
      $llpaySubmit = new \LLpaySubmit($llpay_config);
      $html_text = $llpaySubmit->buildRequestJSON($parameter,$llpay_gateway_new);

      $dataa=json_decode($html_text);
      $datadime=json_decode($dataa->dimension_url);

      $json='"appId" : "'.$datadime->appId.'", 
              "timeStamp":"'.$datadime->timeStamp.'", 
              "nonceStr" : "'.$datadime->nonceStr.'", 
              "package" : "'.$datadime->package.'",
              "signType" : "MD5",
              "paySign" : "'.$datadime->paySign.'",
              ';
      //如果有返回,并且返回状态是未支付,添加订单信息
      if($dataa->pay_status="1")
      {
        Db::table('ien_pay_log')->insert($data);  
      }

      $this->assign('json', $json);
      return $this->fetch(); // 渲染模板


    }


    public function llpaycess()
    {


require_once ("./llpay/llpay.config.php");
require_once ("./llpay/lib/llpay_notify.class.php");

//计算得出通知验证结果
$llpayNotify = new \LLpayNotify($llpay_config);
$llpayNotify->verifyNotify();
if ($llpayNotify->result) { //验证成功
  //获取连连支付的通知返回参数，可参考技术文档中服务器异步通知参数列表
  $no_order = $llpayNotify->notifyResp['no_order'];//商户订单号
  $oid_paybill = $llpayNotify->notifyResp['oid_paybill'];//连连支付单号
  $result_pay = $llpayNotify->notifyResp['result_pay'];//支付结果，SUCCESS：为支付成功
  $money_order = $llpayNotify->notifyResp['money_order'];// 支付金额
 // $ddd=$no_order."/////".$oid_paybill."/////".$result_pay."/////".$money_order;
  if($result_pay == "SUCCESS"){
    //请在这里加上商户的业务逻辑程序代(更新订单状态、入账业务)
    //——请根据您的业务逻辑来编写程序——
    //payAfter($llpayNotify->notifyResp);

     // 使用通知里的 "微信支付订单号" 或者 "商户订单号" 去自己的数据库找到订单  

       $order = Db::table('ien_pay_log')->where('payid',$no_order)->find();

       if($order['status']==1)
       {
        die;
       }
       //通过订单获取商品信息
       $infodata=DB::table('ien_cuxiao')->where('id',$order['cxid'])->find();
       $score=$infodata['score'];
       $money=$infodata['money'];
       $typeday=$infodata['type'];
       $day=$infodata['day'];


                // 不是已经支付状态则修改为已经支付状态  

              Db::table('ien_pay_log')->where( 'payid' , $no_order )->update(['status' => '1','paytime' => time()]);

              Db::table('ien_admin_user')->where( 'openid' , $order['uid'] )->setInc('score', $score);
              //增加VIP天数
              if($typeday==2){
                $uinfo=Db::table('ien_admin_user')->where( 'openid' , $order['uid'] )->find();
                if($uinfo['isvip']=0 || $uinfo['vipetime']<time())
                {
                  $datatimer=time()+$day*86400;
                Db::table('ien_admin_user')

                ->where( 'openid' , $order['uid'])

                ->update(['isvip' => '1','vipstime' => time(),'vipetime'=>$datatimer]);

                }
                else
                {
                   $uinfo=Db::table('ien_admin_user')->where( 'openid' , $order['uid'] )->find();
                   $datatimer=$uinfo['vipetime']+$day*86400;

                  Db::table('ien_admin_user')

                ->where( 'openid' , $order['uid'])

                ->update(['isvip' => '1','vipetime'=>$datatimer]);
                }
              }



        //判断是否黑单
        $paylog=Db::table('ien_pay_log')->where( 'payid' , $no_order )->find();
        if($paylog['isout']!=1)
        {
        //充值成功给代理商增加余额
        $dl=DB::table('ien_admin_user')->where( 'openid' , $order['uid'] )->find();
        if($dl['tgid'])
        {
          $tg=DB::table('ien_agent')->where('id',$dl['tgid'])->find();
          if($tg['uid'])
          {
            $dls=DB::table('ien_admin_user')->where('id',$tg['uid'])->find();
            if($dls['fcbl'])
            {
              $fy=$dls['fcbl'];
            }
            else
            {
              $fy=0.6;
            }
            $moneyjs=$money * $fy;
            Db::table('ien_admin_user')->where( 'id' , $tg['uid'] )->setInc('money', $moneyjs);
            //渠道商如果大于代理商比例,增加差价利润
            if($dls['did']!="" || $dls['did']!=0)
            {
              $qds=DB::table('ien_admin_user')->where('id',$dls['did'])->value('fcbl');
              if($qds!="" && $qds>$fy)
              {
                $cha=$qds-$fy;
                $moneqds=$money * $cha;
                Db::table('ien_admin_user')->where( 'id' , $dls['did'] )->setInc('money', $moneqds);
              }

            }
          }
        
        
        }
      }


  }
  file_put_contents("log.txt", "异步通知 验证成功\n", FILE_APPEND);
  die("{'ret_code':'0000','ret_msg':'交易成功'}"); //请不要修改或删除
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
} else {
  file_put_contents("log.txt", "异步通知 验证失败\n", FILE_APPEND);
  //验证失败
  die("{'ret_code':'9999','ret_msg':'验签失败'}");
  //调试用，写文本函数记录程序运行情况是否正常
  //logResult("这里写入想要调试的代码变量值，或其他运行的结果记录");
}
      

           

         

    }


    public function pay($id=null,$error=null){


	   session_start();


   		if($id==''){die("参数错误！");}

			//$id = 3;


   		$orderid='BOOK'.time().rand(10000,99999);

   		//添加代理ID和渠道ID
   		$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->find();
			$tgid=$user['tgid'];
   		if(!empty($tgid))
   		{
   			$ddid=DB::table('ien_agent')->where('id',$tgid)->value('uid');
   			if(!empty($ddid)){
   				$did=$ddid;
   				$sjid=DB::table('ien_admin_user')->where('id',$did)->value('did');
   				if(!empty($sjid)){
   					$qid=$sjid;
   				}
   				else{
   					$qid=0;
   				}
   			}
   			else{
   			preg_match('/^t(\d+)\./', $_SERVER['HTTP_HOST'], $tid);
   			$did=$tid[1];
   			$qid=0;
   				}
   		}
   		else{
   			preg_match('/^t(\d+)\./', $_SERVER['HTTP_HOST'], $tid);
   			$did=$tid[1];
   			$qid=0;
   		}

      //判断超期用户订单
      $isout=$user['isout'];
      if($isout==1)
      {
        $data['isout']=1;
      }
      //判断比例黑单
      $bili=module_config('agent.agent_klbili');
      $nokou=explode(',',module_config('agent.agent_nokou'));
      if($bili>0)
      {
        $sxid=$user['sxid'];
        if(!in_array($sxid,$nokou)  || empty($nokou['0']))
        {
          $kl=rand(1,$bili);
          if($kl<=2)
          {
            $data['isout']=1;
          }

        }
      }
			
			$bookid=DB::table('ien_read_log')->where('uid',$_SESSION['wechat_user']['original']['openid'])->order('id desc')->limit('1')->value('bid');
			if(!$bookid){
				$bookid='';
			}
			$nickname=$user['nickname'];
   		$data = ['uid' =>$_SESSION['wechat_user']['original']['openid'],

					 'nickname' => $nickname,

   				 'type' => '1',

   				 'status' => '0',

   				 'addtime' => time(),

   				 'paytime' => '0',

   				 'payid' => $orderid,

   				 'did' => $did,

   				 'qid' => $qid,

           'tgid' =>$tgid,


					 'bid' => $bookid,

   				 ];
	

   	//设置订单金额类型商品ID
    $infodata=DB::table('ien_cuxiao')->where('id',$id)->find();
    $data['money'] =$infodata['money'];
    if($infodata['type']==1)
    {
    $data['paytype'] =2;
    }
    else
    {
    $data['paytype'] =1;
    }
    $data['cxid']=$id;
		$domain = module_config('user.main_host');

		$product = [  

		    'body'             => '书币充值 - '.$data["money"].' 元',  

		    'trade_type'       => 'wxgzhnovel',  

		    'out_trade_no'     => $orderid,  

		    'total_fee'        => $data['money'],  

		  	'notify_url'       => 'http://t'.$data['did'].".".$domain.'/index.php/cms/pay/paysuccess', 

				'callbackUrl'			 => 'http://t'.$data['did'].".".$domain.'/index.php/cms/user/readold', 

		    'openid'           => $_SESSION['wechat_user']['original']['openid'],  
		    
		    'attach'           => $id,  

		];
		
		$suffix=".0";
		if($id==1){
			$suffix='';
		}
		$_SESSION['pay_success'] = 0;
		$re=Db::table('ien_pay_log')->insert($data);

		if($re){
			$kk['orderNo']=$orderid;
      $kk['payUrl']="";
      $resx['data']=$kk;
      $resx['success']=true;
      $resx['timeout']=false;
      $resx['overdue']=false;
      $resx['orderNo']=$orderid;
      $resx['notifyUrl'] = $product['notify_url'];
      $resx['callbackUrl'] = $product['callbackUrl'];
      $resx['appID'] = "100003";
      $resx['payType'] =$product['trade_type'];
      $resx['price'] = $product['total_fee'].$suffix;
      //$resx['price'] = $product['total_fee'];
      $source = $resx['appID'].$orderid.$product['trade_type'].$resx['price']."6494BE133293DF7DE7CB412A8796D84E";
      $resx['sign'] = md5($source);
      $resx['currentDate']=intval(microtime(true)*1000);
      //Db::table('ien_book')->where('id',$bookid)->setInc('money',$resx['price']);
		}else{
      header('HTTP/1.1 500 Internal Server Error');
      $resx['code']=0012;
      $resx['msg']='订单创建失败!';
    }
    $this->assign("data",$resx);
    return $this->fetch();
		//$this->ajaxReturn($resx);

		/**
		$order = new Order($product);  

		$app = new Application($this->options());  

		$payment = $app->payment;  

		$result = $payment->prepare($order);  

		$prepayId = null;  
   

		if ($result->return_code == 'SUCCESS' && $result->result_code == 'SUCCESS'){  
    
		    $prepayId = $result->prepay_id;

		    Db::table('ien_pay_log')->insert($data);  

		} else {  

		    var_dump($result);  

		    die("出错了。");  

		}  
		
		$json = $payment->configForPayment($prepayId);  
		
		  // 这个是jssdk里页面上需要用到的js参数信息。 

		//print_r($json);

		$this->assign('json', $json);
    

    $this->assign('ordsn', $order->ordsn); 
		
        
    $this->assign("json",$resx);

		return $this->fetch(); // 渲染模板
		**/
    



    }  

     public function paysuccess(){  
				//session_start();
        //$options = $this->options();  

        //$app = new Application($options);  

				$orderId = $_REQUEST['orderId'];
    		$status = intval($_REQUEST['status']);

        //$response = $app->payment->handleNotify(function($notify, $successful){  
        
            // 使用通知里的 "微信支付订单号" 或者 "商户订单号" 去自己的数据库找到订单  

            $order = Db::table('ien_pay_log')->where('payid',$orderId)->find();

            //通过订单获取商品信息
           $infodata=DB::table('ien_cuxiao')->where('id',$order['cxid'])->find();
           $score=$infodata['score'];
           $money=$infodata['money'];
           $typeday=$infodata['type'];
           $day=$infodata['day'];

            if (count($order) == 0) { // 如果订单不存在  

                return 'Order not exist.'; // 告诉微信，我已经处理完了，订单没找到，别再通知我了  

            }  

            // 如果订单存在  

            // 检查订单是否已经更新过支付状态  

            if ($order['paytime']) { // 假设订单字段“支付时间”不为空代表已经支付  
								//$_SESSION['pay_success'] = 1;
                return "true"; // 已经支付成功了就不再更新了  

            }  

            // 用户是否支付成功  

            if ($status == 1) {  

                // 不是已经支付状态则修改为已经支付状态  

              Db::table('ien_pay_log')->where( 'payid' , $orderId )->update(['status' => '1','paytime' => time()]);
							Db::table('ien_book')->where('id',$order['bid'])->setInc('money',$order['money']);
             	Db::table('ien_admin_user')->where( 'openid' , $order['uid'] )->setInc('score', $score);

             	//增加VIP天数
              if($typeday==2){
                $uinfo=Db::table('ien_admin_user')->where( 'openid' , $order['uid'] )->find();
                if($uinfo['isvip']=0 || $uinfo['vipetime']<time())
                {
                  $datatimer=time()+$day*86400;
                Db::table('ien_admin_user')

                ->where( 'openid' , $order['uid'])

                ->update(['isvip' => '1','vipstime' => time(),'vipetime'=>$datatimer]);

                }
                else
                {
                   $uinfo=Db::table('ien_admin_user')->where( 'openid' , $order['uid'] )->find();
                   $datatimer=$uinfo['vipetime']+$day*86400;

                  Db::table('ien_admin_user')

                ->where( 'openid' , $order['uid'])

                ->update(['isvip' => '1','vipetime'=>$datatimer]);
                }
              }
							//判断是否黑单
			        $paylog=Db::table('ien_pay_log')->where( 'payid' , $orderId )->find();
			        if($paylog['isout']!=1)
			        {
			        //充值成功给代理商增加余额
				        $dl=DB::table('ien_admin_user')->where( 'openid' , $order['uid'] )->find();
				        if($dl['tgid'])
				        {
				          $tg=DB::table('ien_agent')->where('id',$dl['tgid'])->find();
				          if($tg['uid'])
				          {
				            $dls=DB::table('ien_admin_user')->where('id',$tg['uid'])->find();
				            if($dls['fcbl'])
				            {
				              $fy=$dls['fcbl'];
				            }
				            else
				            {
				              $fy=0.6;
				            }
				            $moneyjs=$money * $fy;
				            Db::table('ien_admin_user')->where( 'id' , $tg['uid'] )->setInc('money', $moneyjs);
				            //渠道商如果大于代理商比例,增加差价利润
				            if(($dls['did']!="" || $dls['did']!=0) and $dls['did']!='1' )
				            {
				              $qds=DB::table('ien_admin_user')->where('id',$dls['did'])->value('fcbl');
				              if($qds!="" && $qds>=$fy)
				              {
				                #$cha=$qds-$fy;
				                $moneqds=$money * $qds;
				                Db::table('ien_admin_user')->where( 'id' , $dls['did'] )->setInc('money', $moneqds);
				              }

				            }
				          }
				        }
				        //通过sxid 和  cid 来判断
				        elseif($dl['sxid']){
			          	$dls=DB::table('ien_admin_user')->where('id',$dl['sxid'])->find();
			            if($dls['fcbl'])
			            {
			              $fy=$dls['fcbl'];
			            }
			            else
			            {
			              $fy=0.6;
			            }
			            $moneyjs=$money * $fy;
			            Db::table('ien_admin_user')->where( 'id' , $dl['sxid'] )->setInc('money', $moneyjs);
			            //判断代理有没有上级渠道
			            $qds=DB::table('ien_admin_user')->where('id',$dl['sxid'])->value('cid');
			            if($qds!="" || $qds!=0)
			            {
			              $qdsfy=DB::table('ien_admin_user')->where('id',$qds)->value('fcbl');
			              if($qdsfy!="" && $qdsfy>$fy)
			              {
			                #$cha=$qdsfy-$fy;
			                $moneqds=$money * $qdsfy;
			                Db::table('ien_admin_user')->where( 'id' , $qds )->setInc('money', $moneqds);
			              }
			            }
			          }
			      	}
			      	//$_SESSION['pay_success'] = 1;
     				} else { // 用户支付失败  
                Db::table('ien_pay_log')->where( 'payid' , $orderId )->update(['status' => '0','paytime' => time()]);

            }  

						return "true"; // 返回处理完成  

        //});  

    }  

}

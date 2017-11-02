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
use EasyWeChat\Foundation\Application;
use Doctrine\Common\Cache\RedisCache;

/**

 * 前台首页控制器

 * @package app\cms\admin

 */

class User extends Common

{

    /**

     * 首页

     * @author 拼搏 <378184@qq.com>

     * @return mixed

     */

    public function index()

    {

    	// 微信网页授权接口
		 /*登陆验证方法*/
		session_start();
        $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
        if(empty($_SESSION['wechat_user'])){
            //$this->redirect('oauth/oauth');
            $pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
            $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$pure_uri;
            header("refresh:0;url={$URL}");
            exit;
            //$this-> checklogin();
        }
		//如果上线ID有人,并且设置了关注.那么拉取关注openid去公众号里面查用户信息
		$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->find();
		if($user['sxid']!=0 && $user['gzopenid']!="")
		{
			//查上线代理对接信息
			$userdl=DB::table('ien_wechat_uconfig')->where('uid',$user['sxid'])->where('isopen',"on")->find();
			if(!empty($userdl))
			{
						$config = [
				        /**
				         * Debug 模式，bool 值：true/false
				         *
				         * 当值为 false 时，所有的日志都不会记录
				         */
				        'debug' => true,
				        /**
				         * 账号基本信息，请从微信公众平台/开放平台获取
				         */
				        'app_id' => $userdl['appid'],         // AppID
				        'secret' => $userdl['appsecret'],     // AppSecret
				        'token' => $userdl['token'],          // Token
				        'aes_key' => $userdl['encodingaeskey'],                    // EncodingAESKey，安全模式下请一定要填写！！！
				        'wechat_name' => $userdl['name'],
				        'wechat_id' => $userdl['gid'],
				        'wechat_number' =>  $userdl['wxh'],
				        'wechat_type' => 1,
				        /**
				        * 缓存
				        */
				         //'cache'   => $cacheDriver,
				        /**
				         * 日志配置
				         *
				         * level: 日志级别, 可选为：
				         *         debug/info/notice/warning/error/critical/alert/emergency
				         * permission：日志文件权限(可选)，默认为null（若为null值,monolog会取0644）
				         * file：日志文件位置(绝对路径!!!)，要求可写权限
				         */
				        'log' => [
				            'level' => 'debug',
				            'permission' => 0777,
				            'file' => './runtime/log/wechat/easywechat.log',
				        ],

				        /**
				         * Guzzle 全局设置
				         *
				         * 更多请参考： http://docs.guzzlephp.org/en/latest/request-options.html
				         */
				        'guzzle' => [
				            'timeout' => 3.0, // 超时时间（秒）
				            //'verify' => false, // 关掉 SSL 认证（强烈不建议！！！）
				        ],
				    ];
				    try{
				    $cacheDriver = new RedisCache();
			        // 创建 redis 实例
			        $redis = new \Redis();
			        $redis->connect('localhost', 6379);
			        $cacheDriver->setRedis($redis); 

			        //$config2 = module_config('wechat');
			        $config2['cache']=$cacheDriver;
			        $config = array_merge($config, $config2);
			        $app = new Application($config);
			        $userService = $app->user;
			        $userinfo = $userService->get($user['gzopenid']);
			        if(!empty($userinfo))
			        {
			        	$data=['nickname'=>$userinfo['nickname'],'name'=>$userinfo['nickname'],'sex'=>$userinfo['sex'],'avatar'=>$userinfo['headimgurl'],];

			        	DB::table('ien_admin_user')->where('openid', $_SESSION['wechat_user']['original']['openid'])->update($data);
			        }
			    }
			    catch(\Exception $e){
			    	return true;
			    }


			}


		}	

		$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->find();
		//if($user['isvip'] == 1){
		if($user['vipetime']){
			$vipetime = date("Y-m-d",$user['vipetime']);
			$this->assign('vipetime', $vipetime);
		}
		else{
			$this->assign('vipetime', '');
		}
		//}

		$this->assign('user', $user);

		

        return $this->fetch(); // 渲染模板

    }
	//送书币
	public function free(){
		
		
		return $this->fetch('free');
		
		}


   //书签
   public function bookmark(){
	   
	   /*登陆验证方法*/
		session_start();
        $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
        if(empty($_SESSION['wechat_user'])){
            //$this->redirect('oauth/oauth');
            $pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
            $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$pure_uri;
            header("refresh:0;url={$URL}");
            exit;
            //$this-> checklogin();
        }
		
		$openid=$_SESSION['wechat_user']['original']['openid'];
		$bookmark=DB::view('ien_bookmarks')
		->view('ien_chapter','bid,idx,title','ien_chapter.id=ien_bookmarks.zid')
		->where('ien_bookmarks.uid',$openid)
		->select();
		
		$this->assign('bookmark', $bookmark);
		return $this->fetch('bookmark');
		
	   
	   
	   }
	 public function delmark($id=null)
	 {
		 /*登陆验证方法*/
		session_start();
        $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
        if(empty($_SESSION['wechat_user'])){
            //$this->redirect('oauth/oauth');
            $pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
            $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$pure_uri;
            header("refresh:0;url={$URL}");
            exit;
            //$this-> checklogin();
        }
		
		$openid=$_SESSION['wechat_user']['original']['openid'];
		$bookmark=DB::table('ien_bookmarks')->where('uid',$openid)->select();
		
		if(DB::table('ien_bookmarks')->where('id',$id)->delete())
		return true;
		else
		return false;
		
		 
		 
		 }
		 //自动阅读历史跳转
   	public function readold($openid=null)
	{
		$gzopenid=$openid;
		 /*登陆验证方法*/
		session_start();
        $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
        if(empty($_SESSION['wechat_user'])){
            //$this->redirect('oauth/oauth');
            $pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
            $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$pure_uri;
            header("refresh:0;url={$URL}");
            exit;
            //$this-> checklogin();
        }
    if(preg_match('/pay/',$_SERVER['HTTP_REFERER']) && $_REQUEST['status'] == 0){
    	$URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/pay/index/";
      header("refresh:0;url={$URL}");
      exit;
    }
		$openid=$_SESSION['wechat_user']['original']['openid'];
		$old=DB::table('ien_read_log')->where('uid',$openid)->order('update_time desc')->find();
		
		$guanzhu=DB::table('ien_admin_user')->where('openid',$openid)->update(['isguanzhu'=>1,'gzopenid'=>$gzopenid]);
		if(empty($old))
		{
			 $this->redirect('index/index');
			}
		else{
			$this->redirect('document/detail',['id'=>$old['zid']]);
			}
		
		
		
		
		
		
	}
   
   

    public function readhistory()

    {

      // 微信网页授权接口
		/*登陆验证方法*/
		session_start();
        $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
        if(empty($_SESSION['wechat_user'])){
            //$this->redirect('oauth/oauth');
            $pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
            $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$pure_uri;
            header("refresh:0;url={$URL}");
            exit;
            //$this-> checklogin();
        }
    

        $history = Db::view('read_log','id,bid,zid')

        ->view('chapter',['title'=>'ctitle','idx'=>'idx'],'chapter.id=read_log.zid','LEFT')

        ->view('book',['title'=>'btitle'],'book.id=read_log.bid','LEFT')

        ->where("read_log.uid" , $_SESSION['wechat_user']['original']['openid'])

        ->order('read_log.id desc')

        ->select();

                

        $this->assign('history', $history);    

        return $this->fetch(); // 渲染模板

    }

   



}
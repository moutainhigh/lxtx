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

use app\index\controller\Home;
use think\Db;
use util\Tree;
use EasyWeChat\Foundation\Application;
use Doctrine\Common\Cache\RedisCache;
require_once dirname(dirname(dirname(__FILE__)))."/function.php";

//use think\Session;
/**
 * 前台公共控制器
 * @package app\cms\admin
 *http://book.ieasynet.com/index.php/cms/document/detail/id/7.html?t=5555
 */
class oauth extends Home
{
    //放弃不用。方法调用方式send函数无法跳转。出现死循环
    public function checklogin()
    {
    
        // 未登录
        if (empty($_SESSION['wechat_user'])) {
          //$_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];

            $this->oauth();
          //return $oauth->redirect();
          // 这里不一定是return，如果你的框架action不是返回内容的话你就得使用
          // $oauth->redirect()->send();
        }
        else{
        // 已经登录过 
            //header('location:'. $_SESSION['target_url']);
            return $user=$_SESSION['wechat_user'];
        }
    }

    public function oauth()
    {		
    		session_start();
        $uri = $_REQUEST['uri'];

				//preg_match('/^http:\/\/t(\d+)\./', $_SESSION['target_url'], $tid);
        preg_match('/^t(\d+)\./', $_SERVER['HTTP_HOST'], $tid);
				//$_SESSION['tid'] = $tid[1];
				$dbuser=DB::table('ien_wechat_uconfig')->where('uid',$tid[1])->find();
        $appID = $dbuser['appid'];

				if(preg_match('/'.module_config('user.main_host').'/', $_SERVER['HTTP_HOST'])){
					$url = 'http://t'.$tid[1].".".module_config('admin.main_host').$_REQUEST['uri'];
					header("refresh:0;url={$url}");
        	exit;
				}

				$URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=".$appID."&redirect_uri=".'http://'.$_SERVER['HTTP_HOST'].'/index.php/cms/oauth/oauth_callback?uri='.$uri."&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
        //generate_app_log("before redirect to weixin, redirect_url is:".$_SESSION['redirect_url']);
        //header('location:'. $URL);
				header("refresh:0;url={$URL}");
				/**
        $config = [
          // ...
          'oauth' => [
                  'scopes'   => ['snsapi_base'],
            	  	//'scopes'   => ['snsapi_userinfo'],
                  //'callback' => 'http://'.module_config('agent.agent_rooturl').'/index.php/cms/oauth/oauth_callback',
                	'callback' => 'http://'.$_SERVER['HTTP_HOST'].'/index.php/cms/oauth/oauth_callback',
          ],
          // ..
        ];
        $cacheDriver = new RedisCache();
        // 创建 redis 实例
        $redis = new \Redis();
        $redis->connect('localhost', 6379);
        $cacheDriver->setRedis($redis); 

        $config2 = module_config('wechat');
        $config2['cache']=$cacheDriver;
        $config = array_merge($config, $config2);
        $app = new Application($config);
        $oauth = $app->oauth;
        dump($oauth);
				exit;
        //$this->checklogin();

        $oauth->redirect()->send();
				**/
    }

    public function oauth_session() {
      $openid = $_REQUEST['openid'];
      $ts = $_REQUEST['ts'];
      $sign = $_REQUEST['sign'];
      $target_url = $_REQUEST['uri'];

      //echo $_SERVER['HTTP_HOST'];
      preg_match('/^t(\d+)\./', $_SERVER['HTTP_HOST'], $tid); //get channel id

      if ($sign == md5($openid.$ts."novellxtx")) {
        session_start();
        $user['id'] = $openid;
        $user['original']['openid'] = $openid;
        $_SESSION['openid'] = $openid;
        $where['openid'] = $user['original']['openid'];
        $where['sxid'] = $_SESSION['tid'] = $tid[1];
        $dbuser=DB::table('ien_admin_user')->where($where)->find();

        $_SESSION['target_url'] = 'http://t'.$tid[1].".".module_config('user.main_host').$target_url; 
        $redirect_url=explode("?",$_SESSION['target_url']);
        $_SESSION['redirect_url'] = $redirect_url[0];

        $urla['1']="";
        if(!$dbuser)
        {	
	        if(strpos($_SESSION['target_url'],"?"))
	        {
	            $url=explode("?",$_SESSION['target_url']);
	            $urla=explode("=", $url[1]);
	        }
	        if(!empty($urla['1']))
	        {
	          $sxid=DB::table('ien_agent')->where('id',$urla['1'])->find();
	          if(empty($sxid))
	          {
	            $sxid['uid']=0;
	          }
	        }
	        else{$sxid['uid']=0;}

        $data = [
                     //'username' => $user['nickname']."读者", 
                     //'username' => $user['nickname'], 
                     //'nickname' => $user['nickname']."读者", 
                     //'nickname' => $user['nickname'],
                     'password' => '$2y$10$wwJ7bP4SLfGWZ3.DTQ0RdeglgBLAW5iY4mA6LvoDuQrvcV6qsKdou',
                     //'email'    =>  $user['email']." ", 
                     //'avatar'    => $user['avatar']."http://".module_config('agent.agent_rooturl')."/images/homeuser.png",
                     //'avatar'    => $user['avatar'],
                     'create_time'    => time(),
                     'last_login_time'    => time(),
                     'openid'    => $user['original']['openid'],
                     'role'      =>   '3',
                     'tgid'      => $urla['1'],
                     'status'    => '1', 
                     'sxid'      =>$tid[1],
                     //'sex'       =>$user['original']['sex'],
                    ];
            Db::table('ien_admin_user')->insert($data);
        }
        else{
           if(module_config('agent.agent_fltime')!=0 || module_config('agent.agent_fltime') !="")
           {
             $timecha=ceil((time()-$dbuser['create_time'])/86400); 
             if($timecha>module_config('agent.agent_fltime'))
             {
                $nokou=explode(',',module_config('agent.agent_nokou'));
                if(!in_array($dbuser['sxid'],$nokou))
                {
                  DB::table('ien_admin_user')->where('openid', $dbuser['openid'])->update(['isout'=>'1']);
                }
             }
           }

        }
          
        //判断跳转
            $_SESSION['wechat_user'] = $user;
            //header("refresh:0;url={$_SESSION['redirect_url']}");
            //generate_app_log("redirect to ".$_SESSION['redirect_url']);
            header("refresh:0;url={$_SESSION['redirect_url']}");
      }
    }

    public function oauth_callback()
    {
        session_start();
        //$cacheDriver = new RedisCache();
        // 创建 redis 实例
        //$redis = new \Redis();
        //$redis->connect('localhost', 6379);
        //$cacheDriver->setRedis($redis); 

        //$config = module_config('wechat');
        //$config2['cache']=$cacheDriver;

        //$app = new Application($config);
        //$oauth = $app->oauth;
        // 获取 OAuth 授权结果用户信息
        //$user = $oauth->user();

				//dump($_SESSION['target_url']);
        preg_match('/^t(\d+)\./', $_SERVER['HTTP_HOST'], $tids);
				$tid = $_SESSION['tid'] = $tids[1];
				$dbuser=DB::table('ien_wechat_uconfig')->where('uid',$tid)->find();
				//dump($dbuser);
				$code = $_REQUEST['code'];

        $appID = $dbuser['appid'];
        $appSecret = $dbuser['appsecret'];
        $weixin = file_get_contents("https://api.weixin.qq.com/sns/oauth2/access_token?appid=".$appID."&secret=".$appSecret."&code=".$code."&grant_type=authorization_code");
        $jsondecode = json_decode($weixin);
        $array = get_object_vars($jsondecode);

        //$URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=".$appID."&redirect_uri=".'http://'.$_SERVER['HTTP_HOST'].'/index.php/cms/oauth/oauth_callback'."&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
        $openid = $array['openid'];
        $ts = time();
        $sign = md5($openid.$ts."novellxtx");

        $URL = 'http://t'.$_SESSION['tid'].".".module_config('user.main_host').'/index.php/cms/oauth/oauth_session?openid='.$openid."&ts=".$ts."&sign=".$sign."&uri=".$_REQUEST['uri'];
        //header('location:'. $URL);
        header("refresh:0;url={$URL}");
        exit();
        /*
        $user['id'] = $openid;
        $user['original']['openid'] = $openid;
        $_SESSION['openid'] = $openid;

        $where['openid'] = $user['original']['openid'];
        $where['sxid'] = $_SESSION['tid'];
        $dbuser=DB::table('ien_admin_user')->where($where)->find();
        $urla['1']="";
        if(!$dbuser)
        {	
        if(strpos($_SESSION['target_url'],"?"))
        {
            $url=explode("?",$_SESSION['target_url']);
            $urla=explode("=", $url[1]);
        }
        if(!empty($urla['1']))
        {
          $sxid=DB::table('ien_agent')->where('id',$urla['1'])->find();
          if(empty($sxid))
          {
            $sxid['uid']=0;
          }
        }
        else{$sxid['uid']=0;}

        $data = [
                     //'username' => $user['nickname']."读者", 
          			 		 //'username' => $user['nickname'], 
                     //'nickname' => $user['nickname']."读者", 
         			 			 //'nickname' => $user['nickname'],
                     'password' => '$2y$10$wwJ7bP4SLfGWZ3.DTQ0RdeglgBLAW5iY4mA6LvoDuQrvcV6qsKdou',
                     //'email'    =>  $user['email']." ", 
                     //'avatar'    => $user['avatar']."http://".module_config('agent.agent_rooturl')."/images/homeuser.png",
          			 		 //'avatar'    => $user['avatar'],
                     'create_time'    => time(),
                     'last_login_time'    => time(),
                     'openid'    => $user['original']['openid'],
                     'role'      =>   '3',
                     'tgid'      => $urla['1'],
                     'status'    => '1', 
                     'sxid'      =>$sxid['uid'],
                     //'sex'       =>$user['original']['sex'],
                    ];

            Db::table('ien_admin_user')->insert($data);
            }
            else{
               if(module_config('agent.agent_fltime')!=0 || module_config('agent.agent_fltime') !="")
               {
                 $timecha=ceil((time()-$dbuser['create_time'])/86400); 
                 if($timecha>module_config('agent.agent_fltime'))
                 {
                    $nokou=explode(',',module_config('agent.agent_nokou'));
                    if(!in_array($dbuser['sxid'],$nokou))
                    {
                      DB::table('ien_admin_user')->where('openid', $dbuser['openid'])->update(['isout'=>'1']);
                    }
                 }
               }

            }
          
        //判断跳转
            
            
            $_SESSION['wechat_user'] = $user;
            
            //dump($_SESSION['target_url']);
						
            //header('location:'. $_SESSION['target_url']);
            header("refresh:0;url={$_SESSION['redirect_url']}");
        */
        //dump($_SESSION['wechat_user']);
        //$_SESSION['wechat_user'] = $user->toArray();
       // $targetUrl = empty($_SESSION['target_url']) ? '/' : $_SESSION['target_url'];
        //header('location:'. $targetUrl); // 跳转到 user/profile

    }






    }
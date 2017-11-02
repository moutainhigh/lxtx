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
use think\Session;
use think\cache\driver\Redis;

require_once dirname(dirname(dirname(__FILE__)))."/function.php";
/**
 * 前台首页控制器
 * @package app\cms\admin
 */
class Index extends Common
{
    public function test_redis() {
        $redis_cli = new Redis();
        $redis_cli->set("key1", "value1", 60);
        echo $redis_cli->get("key1");
        //echo dirname(dirname(dirname(__FILE__)))."/function.php";
	echo PUBLIC_PATH;
    }

    public function test_thumb() {
	$idx = $_REQUEST['idx'];
	echo get_thumb($idx);
    }

    public function verify_server() {
        generate_app_log("in verify server."); 
        $token = 'lxninestate';
        $echostr = $_GET['echostr'];
        
        if ($echostr) {
            $signature = $_GET['signature']; 
            $timestamp = $_GET['timestamp']; 
            $nonce = $_GET['nonce']; 

            $arr = array($token, $timestamp, $nonce);
            sort($arr);
            $source = '';
            foreach ($arr as $key => $value) {
                $source .= $value;
            }

            $digest = sha1($source);
            if ($digest == $signature) {
                echo $echostr;
            } else {
                echo "wrong";
            }    
        } else { //post information
            $postStr = $GLOBALS["HTTP_RAW_POST_DATA"];
            generate_app_log($postStr);
            libxml_disable_entity_loader(true);
            $xml = simplexml_load_string($postStr, 'SimpleXMLElement', LIBXML_NOCDATA);
            $fromUserName = $xml->FromUserName;     //oo77Fw9r2hgd6mz36wrm3sLbpFT4
            $toUserName = $xml->ToUserName; //gh_d22bab5b1997
            //now update the user information
            if ($xml->Event == 'subscribe') {
                $this->updateUserInfo($fromUserName, $toUserName);    
                generate_app_log("now send back info");
                //now send info back to the end user
                $welcomeMsg = $this->sendWelcomeMsg($fromUserName, $toUserName);
                generate_app_log($welcomeMsg);
                echo $welcomeMsg;
            }
        }
    } 

    public function sendWelcomeMsg($fromUserName, $toUserName) {
        $welcomeMsg = "<xml>";
        $welcomeMsg.= "<ToUserName><![CDATA[" . $fromUserName . "]]></ToUserName>";
        $welcomeMsg.= "<FromUserName><![CDATA[" . $toUserName . "]]></FromUserName>";
        $welcomeMsg.= "<CreateTime>".time()."</CreateTime>";

        $where['gid']=$toUserName.'';
        $mpinfo=DB::table('ien_wechat_uconfig')->where($where)->find();
        $mpName = $mpinfo['name'];
        $host = "t".$mpinfo['uid'].strstr($_SERVER['HTTP_HOST'], '.');
        $wxDomain = "http://".$host."/index.php/cms/user/readold/";

        $welcomeMsg.= "<MsgType><![CDATA[text]]></MsgType>";
        $welcomeMsg.= "<Content><![CDATA[欢迎关注[$mpName],点击下面链接继续阅读精彩内容\n\n<a href='$wxDomain'>点此继续历史阅读...</a>]]></Content>";
        $welcomeMsg.= "</xml>";
        return $welcomeMsg;
    }

    //mpid follows format "gh_xxxxxxxxxx"
    public function updateUserInfo($openid, $mpid) {
        generate_app_log("update user info.");
        $where['gid']=$mpid.'';
        $mpinfo=DB::table('ien_wechat_uconfig')->where($where)->find();
        //$sxid=DB::table('ien_agent')->where('id',$urla['1'])->find();
        if (!empty($mpinfo)) {
            //check whether redis has cached the value
            $redis_cli = new Redis();
            $access_token_key = "access_token_".$mpid;
            $access_token = $redis_cli->get($access_token_key);

            generate_app_log("read access token from redis cache".$access_token);
            if (!$access_token) {
                $appid = $mpinfo['appid'];
                $appsecret = $mpinfo['appsecret'];

                $access_token = $this->getAccessToken($appid, $appsecret);
                //cache the access token for 3600 seconds
                $redis_cli->set($access_token_key, $access_token, 3600);
                generate_app_log("get access token for ".$mpid." ".$access_token);
            } else {
                generate_app_log("access token for ".$mpid." already exists.");
            }

            $userInfo = $this->getUserInfo($access_token, $openid);
            //now update user info
            $data=['avatar'=>$userInfo['headimgurl'].'', 'nickname'=>$userInfo['nickname'].'', 'isguanzhu'=>1];
            //$mpinfo = DB::table('ien_wechat_uconfig')->where('gid', $mpid)->find();
            $wh['openid'] = $openid.'';
            $wh['sxid'] = intval($mpinfo['uid']);
            DB::table('ien_admin_user')->where($wh)->update($data);               
            generate_app_log("after update user :".$mpinfo['uid'].$openid);
        } 
    }

    public function getAccessToken($appid, $appsecret) {
        //now get the access token
        $url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=$appid&secret=$appsecret";
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE); 
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE); 
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $output = curl_exec($ch);
        generate_app_log("got token response:".$output);
        curl_close($ch);
        $jsoninfo = json_decode($output, true);
        $access_token = $jsoninfo["access_token"];        
        return $access_token;
    }

    public function getUserInfo($access_token, $openid) {
        //$openid="o7Lp5t6n59DeX3U0C7Kric9qEx-Q";
        $url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=$access_token&openid=$openid&lang=zh_CN";
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE); 
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE); 
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $output = curl_exec($ch);
        curl_close($ch);
        $jsoninfo = json_decode($output, true);
        return $jsoninfo;
    }


    /**
     * 首页
     * @author 拼搏 <378184@qq.com>
     * @return mixed
     */
    public function index($agent=null,$t=null)
    {
		session_start();
//      $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
        preg_match('/^t(\d+)\./', $_SERVER['HTTP_HOST'], $tid); //get channel id
        //user.main_host is the next-hop url which is used in fighting with wechat
        $_SESSION['target_url'] = 'http://t'.$tid[1].".".module_config('user.main_host').$_SERVER['REQUEST_URI']; 
        $redirect_url=explode("?",$_SESSION['target_url']);
        $_SESSION['redirect_url'] = $redirect_url[0];
        //preg_match('/^http:\/\/t(\d+)\./', $_SESSION['target_url'], $tid);       
		//添加点击数
		if($t!="")
		{
		  DB::table('ien_agent')->where('id',$t)->setInc('click');
		}

        if(empty($_SESSION['wechat_user']) || ($_SESSION['tid'] != $tid[1])){
            //generate_app_log("before redirect to oauth, redirect_url is:".$_SESSION['redirect_url']);
            //$this->redirect('oauth/oauth');
            //$pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
            $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$_SERVER['REQUEST_URI'];

            header("refresh:0;url={$URL}");
            exit;
            //$this-> checklogin();
        }

        //获取公众号ID，用来显示首页二维码
        $wxh=DB::table('ien_wechat_uconfig')->where('uid',$tid[1])->value('wxh');

        //更新读者推广ID
        $urla['1']="";   
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
        if($sxid['uid']!=0 && $sxid['uid']!="" && $urla['1']!="")
        {
            $dataagent=['uid'=>$sxid['uid'],'tgid'=>$urla['1']];
            DB::table('ien_admin_user')->where('openid', $_SESSION['wechat_user']['original']['openid'])->update($dataagent);
        }
        //更新读者推广ID结束


        //$this->oauth($agent,'http://'.$_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"]); 
        //$this->getCode("wxfcc9e317d7e4279d","8af792a6ed7ada0e26bd30c212638f49");
	
			$redis_cli = new Redis();
      #$redis_cli->rm('home');
      if($redis_cli->has('home')){
      	#print "has redis";
      	$banner_list = $this->get_redis("banner_list");
      	$zhubian_list = $this->get_redis("zhubian_list");
      	$girl_list_1 = $this->get_redis("girl_list_1");
      	$girl_list_2 = $this->get_redis("girl_list_2");
      	$boy_list_1 = $this->get_redis("boy_list_1");
      	$boy_list_2 = $this->get_redis("boy_list_2");
      	
      }
      else{
      	#print "no redis";
      	$banner_list = $this->set_redis('banner_list',0);
      	$zhubian_list = $this->set_redis('zhubian_list',1);
        $girl_list_1 = $this->set_redis('girl_list_1',2);
        $girl_list_2 = $this->set_redis('girl_list_2',3);
        $boy_list_1 = $this->set_redis('boy_list_1',4);
        $boy_list_2 = $this->set_redis('boy_list_2',5);
        
        
        $redis_cli->set('home',1,12*60*60);
      }

        $this->assign('banner_list', $banner_list);        
        $this->assign('zhubian_list', $zhubian_list);        
        $this->assign('girl_list_1', $girl_list_1);        
        $this->assign('girl_list_2', $girl_list_2);
        $this->assign('boy_list_1', $boy_list_1);
        $this->assign('boy_list_2', $boy_list_2);
        $this->assign('wxh', $wxh);
       
        return $this->fetch(); // 渲染模板
    }
    
    private function set_redis($list,$num){
    	$alist = Db::view('ien_book','id,title,image,desc,zhishu,model')
				->view('ien_chapter','id as zid','ien_book.id=ien_chapter.bid')
				->where('ien_chapter.idx=1')
                ->where('ien_book.status=1')
				->where("FIND_IN_SET( '".$num."', ien_book.tj)")
                ->order('ien_book.zhishu asc')
                ->select();
        $redis_cli = new Redis();
        $redis_cli->set($list,count($alist),12*60*60);
        for ($i = 0; $i < count($alist); $i++) {
         	$redis_cli->set($list.'_'.$i.'_id',$alist[$i]['id'],12*60*60);
        	$redis_cli->set($list.'_'.$i.'_title',$alist[$i]['title'],12*60*60);
       		$redis_cli->set($list.'_'.$i.'_image',$alist[$i]['image'],12*60*60);
        	$redis_cli->set($list.'_'.$i.'_desc',$alist[$i]['desc'],12*60*60);
        	$redis_cli->set($list.'_'.$i.'_zhishu',$alist[$i]['zhishu'],12*60*60);
        	$redis_cli->set($list.'_'.$i.'_model',$alist[$i]['model'],12*60*60);
        	$redis_cli->set($list.'_'.$i.'_zid',$alist[$i]['zid'],12*60*60); 
        }
      return $alist;
    }
    
    private function get_redis($list){
    	$redis_cli = new Redis();
    	for ($i = 0; $i < $redis_cli->get($list); $i++) {
      			$alist[$i]['id'] = $redis_cli->get($list.'_'.$i.'_id');
      			$alist[$i]['title'] = $redis_cli->get($list.'_'.$i.'_title');
      			$alist[$i]['image'] = $redis_cli->get($list.'_'.$i.'_image');
      			$alist[$i]['desc'] = $redis_cli->get($list.'_'.$i.'_desc');
      			$alist[$i]['zhishu'] = $redis_cli->get($list.'_'.$i.'_zhishu');
      			$alist[$i]['model'] = $redis_cli->get($list.'_'.$i.'_model');
      			$alist[$i]['zid'] = $redis_cli->get($list.'_'.$i.'_zid');
      }
      return $alist;
    }
    
     public function footer()
    {
    	return $this->fetch(); // 渲染模板
    }
     public function header()
    {
    	return $this->fetch(); // 渲染模板
    }
     public function booklibrary()
    {   
        $type=parse_attr(module_config('agent.agent_novel_type'));
       
        $i=0;
        foreach ($type as $key => $value) {
            $type1[$i]['id']=$key;
            $type1[$i]['title']=$value;
            $i++;
        }
        $this->assign('type', $type1);
    	return $this->fetch(); // 渲染模板
    }
    
    public function agent_switch($id)
    {
    	$switch = DB::table('ien_agent_switch')->where('id',$id)->find();
    	$ids = explode(',',$switch['switch']);
    	$rid = rand(0,count($ids)-1);
    	
    	$value = DB::table('ien_agent')->where('id',$ids[$rid])->find();
    	if($value['ljlx']==3)
			{
			$url="http://".preg_replace("/\{\d\}/", $value['uid'], module_config('agent.agent_tuiguangurl'))."/index.php/cms/index/index/?t=".$value['id'];
			}
			else{
			$url="http://".preg_replace("/\{\d\}/", $value['uid'], module_config('agent.agent_tuiguangurl'))."/index.php/cms/document/detail/id/".$value['zid'].".html?t=".$value['id'];
			}

			header("refresh:0;url={$url}");
    }
}

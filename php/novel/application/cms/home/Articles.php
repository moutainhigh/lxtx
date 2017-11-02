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

use app\cms\model\Column as ColumnModel;
use think\Db;
use util\Tree;
use EasyWeChat\Foundation\Application;
use Doctrine\Common\Cache\RedisCache;

/**
 * 前台栏目文档列表控制器
 * @package app\cms\admin
 */
class Articles extends Common
{

    public function index($id=null,$tstype=null)
    { 
        if ($id === null) $this->error('缺少参数');
        $a="";
       Db::table('ien_articles')->where('id', $id)->setInc('click');
        $articles= Db::table('ien_articles')->where('id', $id)->find();

        $url="http://".preg_replace("/\{\d\}/", $articles['uid'], module_config('agent.agent_tuiguangurl'))."/index.php/cms/document/detail/id/".$articles['zid'].".html?t=".$articles['bid'];
        $rooturl="http://".preg_replace("/\{\d\}/", $articles['uid'], module_config('agent.agent_tuiguangurl'))."/";
            $this->assign('articles', $articles);
            $this->assign('url', $url);
            $this->assign('rooturl', $rooturl);

              $userdl=DB::table('ien_wechat_uconfig')->where('uid',$articles['uid'])->where('isopen',"on")->find();
              if(!empty($userdl) || $articles['uid']==1)
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



              $cacheDriver = new RedisCache();
              // 创建 redis 实例
              $redis = new \Redis();
              $redis->connect('localhost', 6379);
              $cacheDriver->setRedis($redis); 

              if($articles['uid']==1)
              {
                    $config2 = module_config('wechat');
               }
              $config2['cache']=$cacheDriver;
              $config = array_merge($config, $config2);
              try{
              $app = new Application($config2);
              $js = $app->js;
              $a="<script src='http://res.wx.qq.com/open/js/jweixin-1.2.0.js' type='text/javascript' charset='utf-8'></script>
<script type='text/javascript' charset='utf-8'>
    wx.config(".$js->config(array('onMenuShareAppMessage','onMenuShareTimeline','onMenuShareQQ', 'onMenuShareWeibo'), false).");
</script>";
              }
              catch(\Exception $e){
              }
   
              
            }
            $this->assign('js', $a);

       
        return $this->fetch();

    }
  
  
  
}
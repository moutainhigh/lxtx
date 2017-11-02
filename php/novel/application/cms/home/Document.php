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

use app\cms\model\Document as DocumentModel;

use util\Tree;

use think\Db;

use think\cache\driver\Redis;

/**

 * 文档控制器

 * @package app\cms\home

 */

class Document extends Common

{

    /**

     * 文档详情页

     * @param null $id 文档id

     * @param string $model 独立模型id

     * @author 拼搏 <378184@qq.com>

     * @return mixed

     */

    public function detail($id = null,$t=null,$idx=null)

    {

    //if ($idx === null) $this->error('缺少参数');

    //if ($bid === null) $this->error('缺少参数');

		/*登陆验证方法*/

		session_start();

    $_SESSION['target_url'] = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
    preg_match('/^http:\/\/t(\d+)\./', $_SESSION['target_url'], $tid);
    
		//添加点击数
		if($t!="")

		{

		DB::table('ien_agent')->where('id',$t)->setInc('click');

		}

    if(empty($_SESSION['wechat_user']) or ($_SESSION['tid'] != $tid[1])){
				
        //$this->redirect('oauth/oauth');
        //$pure_uri = explode("?", $_SERVER['REQUEST_URI'])[0];
        $URL = "http://".$_SERVER['HTTP_HOST']."/index.php/cms/oauth/oauth?uri=".$_SERVER['REQUEST_URI'];

        header("refresh:0;url={$URL}");
        //$this-> checklogin();
        exit();
    }

		$redis_cli = new Redis();
		#$redis_cli->rm($id);
		if($redis_cli->has($id)){
			$info['content']=$redis_cli->get($id."_content");
			$info['id']=$redis_cli->get($id);
			$info['bid']=$redis_cli->get($id."_bid");
			$info['title']=$redis_cli->get($id."_title");
			
			if($redis_cli->has("book_".$info['bid'])){
				$book['title']=$redis_cli->get("book_".$info['bid']."_title");
				$book['image']=$redis_cli->get("book_".$info['bid']."_image");
			}
			else{
				$book=Db::table('ien_book')->field('image,title')->where('id', $info['bid'])->find();
				$redis_cli->set("book_".$info['bid'], $info['bid'], 60*30);
				$redis_cli->set("book_".$info['bid']."_title", $book['title'], 60*30);
				$redis_cli->set("book_".$info['bid']."_image", $book['image'], 60*30);
			}
		}
		else{
			$yz['id']=$id;

			$info=DB::table('ien_chapter')->where($yz)->find();

			if(empty($info)){
				$this->redirect('index/index');
			}

			$info['content']= nl2br($info['content']); 
			
			$redis_cli->set($id, $id, 60*10);
			$redis_cli->set($id."_bid", $info['bid'], 60*10);
			$redis_cli->set($id."_title", $info['title'], 60*10);
			$redis_cli->set($id."_content", $info['content'], 60*10);
			
			#$redis_cli->rm("book_".$info['bid']);
			if($redis_cli->has("book_".$info['bid'])){
				$book['title']=$redis_cli->get("book_".$info['bid']."_title");
				$book['image']=$redis_cli->get("book_".$info['bid']."_image");	
			}
			else{
				$book=Db::table('ien_book')->field('image,title')->where('id', $info['bid'])->find();
				$redis_cli->set("book_".$info['bid'], $info['bid'], 60*30);
				$redis_cli->set("book_".$info['bid']."_title", $book['title'], 60*30);
				$redis_cli->set("book_".$info['bid']."_image", $book['image'], 60*30);
			}
		}

		
		//$info['title']= str_replace("\r", '', $info['title']); 

		//$info['title'] = str_replace(array("\r\n", "\r", "\n"), "", $info['title']);  

		//$info['title'] = preg_replace('//s*/', '', $info['title']);


		//添加阅读历史

    $this->readold($id);

    if(!$idx || $idx < 22){

			//判断强制关注章节
			$guanzhu=$this->gzzj($id);
			
			//判断关注章节跳转
			if(intval($guanzhu) > 0){
				$isgz=$this->isguanzhu($id,$guanzhu);
			}
			else{
				$isgz['forceFollow']="true";
				$isgz['showFollowPopupOnNext']="false";
				$isgz['tiaozhuan']="false";
			}
		}
		else{
			$isgz['forceFollow']="true";
			$isgz['showFollowPopupOnNext']="false";
			$isgz['tiaozhuan']="false";
		}
		//验证VIP章节，消费

		$this->isvip($id);

		//每日签到积分
		$t = $redis_cli->get($_SESSION['wechat_user']['original']['openid']);
		if($t != date("Y-m-d",time())){
			$this->addcore();
	 	}

    $this->assign('isgz', $isgz);

    $this->assign('book', $book);
   // $this->assign('uid', UID);

    $this->assign('document', $info);

    $this->assign('next', $this->getNext($id));
    
    $this->assign('prev', $this->getPrev($id));

    return $this->fetch('detail');

    }



    /**

     * 获取栏目面包屑导航

     * @param int $id 栏目id

     * @author 拼搏 <378184@qq.com>

     */

    private function getBreadcrumb($id)

    {

        $columns = ColumnModel::where('status', 1)->column('id,pid,name,url,target,type');

        foreach ($columns as &$column) {

            if ($column['type'] == 0) {

                $column['url'] = url('cms/column/index', ['id' => $column['id']]);

            }

        }

        return Tree::config(['title' => 'name'])->getParents($columns, $id);

    }



    /**

     * 获取上一篇文档

     * @param int $id 当前文档id

     * @param string $model 独立模型id

     * @author 拼搏 <378184@qq.com>

     * @return array|false|\PDOStatement|string|\think\Model

     */

    private function getPrev($id)

    {

        $cha=DB::table('ien_chapter')->where('id',$id)->find();

		$idx=$cha['idx']-1;

		$map['bid']=$cha['bid'];

		$map['idx']=$idx;

		$doc=DB::table('ien_chapter')->where($map)->find();



        if ($doc) {

            $doc['url'] = url('cms/document/detail', ['id' => $doc['id'], 'idx' => $idx]);

        }

		else

		{

			$doc['url'] = url('cms/index/index');

			}



        return $doc;

    }



    /**

     * 获取下一篇文档

     * @param int $id 当前文档id

     * @param string $model 独立模型id

     * @author 拼搏 <378184@qq.com>

     * @return array|false|\PDOStatement|string|\think\Model

     */

    private function getNext($id)

    {

        $cha=DB::table('ien_chapter')->where('id',$id)->find();

		$idx=$cha['idx']+1;

		$map['bid']=$cha['bid'];

		$map['idx']=$idx;

		$doc=DB::table('ien_chapter')->where($map)->find();



        if ($doc) {

            $doc['url'] = url('cms/document/detail', ['id' => $doc['id'], 'idx' => $idx]);

        }

		else

		{

			$doc['url'] = url('cms/index/index');

			}



        return $doc;

    }



    public function addbookmark($id=null)

    {

		session_start();

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

        if($id==''){ return false;}

        $data = ['uid' =>$_SESSION['wechat_user']['original']['openid'],

                 'zid' => $id,

                ];

        Db::table('ien_bookmarks')->insert($data);  

        return true;

    }

	

	

		//添加阅读历史记录

	public function readold($zid=null)

	{

		//查询是否有这本书的记录

		$bid=DB::table('ien_chapter')->where('id',$zid)->find();

		$openid=$_SESSION['wechat_user']['id'];

		$map['uid']=$openid;

		$map['bid']=$bid['bid'];

		//$map['zid']=$zid;

		$res=DB::table('ien_read_log')->where($map)->find();

		//如果有，更新章节和更新时间，如果没有插入记录。

		if($res)

		{

			$data['zid']=$zid;

			$data['update_time']=time();

			Db::table('ien_read_log')->where($map)->update($data);

			

			}

		else{

			$datai['uid']=$openid;

			$datai['zid']=$zid;

			$datai['bid']=$bid['bid'];

			$datai['create_time']=time();

			Db::table('ien_read_log')->insert($datai);

			

		

		}

	}

	

		//判断当前用户，代理商设置的关注章节	

	public function gzzj($id){

		$openid=$_SESSION['wechat_user']['original']['openid'];

		
		//获取用户信息

		$user=DB::table('ien_admin_user')->where('openid',$openid)->find();

		if($user['sxid']!=0){

		//判断是否从推广链接进来

		#$agent=DB::table('ien_agent')->where('id',$user['tgid'])->find();

		#	if($agent){

				//判断代理商是否设置了强制关注的ID

				#$agentuser=DB::table('ien_admin_user')->where('id',$agent['uid'])->find();
				$agentuser=DB::table('ien_admin_user')->where('id',$user['sxid'])->find();

				if(!empty($agentuser['guanzhu']) && $agentuser['guanzhu']!=0)

				{

					return $agentuser['guanzhu'];

					}

				else{

					try{
	        	$bid=DB::table('ien_chapter')->where('id',$id)->column('bid');
	  				$gzid=DB::table('ien_book')->where('id',$bid['0'])->column('gzzj');
	          if(!empty($gzid['0']) && $gzid['0']>0)
	          {
	        		return $gzid['0'];
	          }
	          else
	          {
	            return module_config('agent.agent_guanzhu');
	          }
	        }
	        catch(\Exception $e){
	          return module_config('agent.agent_guanzhu');
	        }

					}	

		#	}
/**
			else{

				try{
          $bid=DB::table('ien_chapter')->where('id',$id)->column('bid');
    			$gzid=DB::table('ien_book')->where('id',$bid['0'])->column('gzzj');
          if(!empty($gzid['0']) && $gzid['0']>0)
          {
        		return $gzid['0'];
          }
          else
          {
            return module_config('agent.agent_guanzhu');
          }
        }
        catch(\Exception $e){
          return module_config('agent.agent_guanzhu');
        }

			}
**/
			

		}

		else{

			try{
        $bid=DB::table('ien_chapter')->where('id',$id)->column('bid');
  			$gzid=DB::table('ien_book')->where('id',$bid['0'])->column('gzzj');
        if(!empty($gzid['0']) && $gzid['0']>0)
        {
      		return $gzid['0'];
        }
        else
        {
          return module_config('agent.agent_guanzhu');
        }
      }
      catch(\Exception $e){
        return module_config('agent.agent_guanzhu');
      }

		}

	}

		

		

	//判断是否需要关注

	public function isguanzhu($zid=null,$guanzhu=null)

	{

		$chapter=DB::table('ien_chapter')->field('idx')->where('id',$zid)->find();
		$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->find();
		$data['forceFollow']="true";
		$data['showFollowPopupOnNext']="false";
		$data['tiaozhuan']="false";
		if($user['isguanzhu']==0)
		{
			$data['forceFollow']="false";
          if($chapter['idx'] >= $guanzhu)
				{
					$data['showFollowPopupOnNext']="true";
					$data['tiaozhuan']="true";
				}
		


			if($user['sxid']==0)
					{
						$sxid=0;
					}
			else
					{
						$sxid=$user['sxid'];
					}

			//跳转关注页面
			$data['ewm']=$this->erweima($sxid);
			$ewm = DB::table('ien_wechat_uconfig')->where('uid',$_SESSION['tid'])->find();
			if($data['ewm']==''){
				$data['ewm'] = "http://open.weixin.qq.com/qr/code?username=".$ewm['wxh'];
				$data['zhuishu'] = "http://open.weixin.qq.com/qr/code?username=".$ewm['wxh'];
				$data['tiaozhuan']="false";
			}
			else{
				$data['zhuishu'] = "http://open.weixin.qq.com/qr/code?username=".$ewm['wxh'];
				$data['showFollowPopupOnNext']="false";
				#header("refresh:0;url={$data['ewm']}");
				#exit;
			}
		
			return $data;
			//$this->redirect('document/erweima',['id'=>$sxid]);
		}
	return $data;
	}



	public function erweima($id=null)

	{
		
		$erweima=DB::table('ien_admin_user')->where('id',$id)->find();

		if(strlen($erweima['ewm'])<5)

		{
			$ewm="";
			#$ewm=module_config('agent.agent_qzgzewm');
		}

		else{
			$ewm=$erweima['ewm'];
		}

		//$this->assign('ewm', $ewm);

		return $ewm;

		}

		public function payerweima($id=null)

	{

		$erweima=DB::table('ien_admin_user')->where('id',$id)->find();

		if(strlen($erweima['ewm'])<5)

		{
			
			$ewm=module_config('agent.agent_qzgzewm');
		}

		else{
			$ewm=$erweima['ewm'];
		}

		

		$this->assign('ewm', $ewm);

		return $this->fetch('payerweima');

		}


	

	

	//判断是否VIP章节

	public function isvip($zid=null)

	{

		$redis_cli = new Redis();
		
		if($redis_cli->has($zid."_isbid")){
			$chapter['isvip'] = $redis_cli->get($zid."_isvip");
			$chapter['bid'] = $redis_cli->get($zid."_isbid");
		}
		else{
			$chapter=DB::table('ien_chapter')->where('id',$zid)->find();
			$redis_cli->set($zid."_isvip",$chapter['isvip'],60*30);
			$redis_cli->set($zid."_isbid",$chapter['bid'],60*30);
		}

		//$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->find();

		if($chapter['isvip']==1)

		{

			//判断年费会员

			$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->find();

			if($user['isvip']==1 && time()>$user['vipstime'] && time()<=$user['vipstime'])

			{

				$this->readold($zid);

				return true;

			}

			

			//判断是否消费过

			$map['uid']=$_SESSION['wechat_user']['original']['openid'];

			$map['zid']=$zid;

			$pay=DB::table('ien_consume_log')->where($map)->find();

			$bookscore=DB::table('ien_book')->where('id',$chapter['bid'])->find();
			
			if(empty($bookscore['score']))
			{
				$score=module_config('agent.agent_pay_money');
			}
			else{
				$score=$bookscore['score'];
			}

			if(!$pay){

				//判断余额是否够金额16

				if(!$user || $user['score']<$score)

				{

				$this->redirect('pay/index');

				//$this->readold($zid);

				//跳转支付充值

				

				//支付充值成功跳转阅读历史

				}

				else

				{

					//消费积分，保存消费记录，添加阅读记录

					$data['zid']=$zid;

					$data['uid']=$_SESSION['wechat_user']['original']['openid'];

					$data['money']=$score;

					$data['addtime']=time();

					$res=DB::table('ien_consume_log')->insert($data);

					//减少会员积分

					

					//$core=$user['score'] - 21;

					$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->setDec('score',module_config('agent.agent_pay_money'));

					

					$this->readold($zid);

					//显示文章

					return true;

					}

				}

			else{

				$this->readold($zid);

				return true;

				}

		}

		else{

			//添加阅读记录

			$this->readold($zid);

			return true;

			

			}

	}

	

	//每日首次登陆赠送积分

	public function addcore($usecenter=null){

		$cur_date = strtotime(date('Y-m-d'));
		//如果会员中心过来的开启session;

		session_start();
		//if($usecenter==1)
		//{}

		//$map['create_time']=$cur_date;

		$map['uid']=$_SESSION['wechat_user']['original']['openid'];

		$map['type']=0;

		$addlog=DB::table('ien_pay_log')->where($map)->whereTime('addtime', 'today')->find();

		$redis_cli = new Redis();
		$t = date("Y-m-d",time());
		$redis_cli->set($_SESSION['wechat_user']['original']['openid'], $t, 24*60*60);

		if(!$addlog)

		{

			$data['uid']=$_SESSION['wechat_user']['original']['openid'];

			$data['addtime']=time();

			$data['type']=0;

			//$data['money']=50;

			//验证必须关注

			$u=DB::table('ien_admin_user')->where('openid',$data['uid'])->find();

			if($u['isguanzhu']==1)

			{

			$add=DB::table('ien_pay_log')->insert($data);

			$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->find();

			$user=DB::table('ien_admin_user')->where('openid',$_SESSION['wechat_user']['original']['openid'])->setInc('score',50);

			$data['data']="50";

			$data['status']=1;

			return json($data);

			}

			else{
				header("status: 400 Bad Request");

				$data['status']=0;

				$data['code']="already_checked_in";

				return json($data);

				}

			

			}

		header("status: 400 Bad Request");
		
		$data['status']=0;

		$data['code']="already_checked_in";

		return json($data);

		}
		//添加打赏记录
	public function tips($gift_id=null,$novel_id=null){
		session_start();
		if($gift_id==1)
		{
			$money=100;
		}
		if($gift_id==2)
		{
			$money=388;
		}
		if($gift_id==3)
		{
			$money=588;
		}
		if($gift_id==4)
		{
			$money=888;
		}
		$book=DB::table('ien_chapter')->where('id',$novel_id)->find();
		if(!empty($book))
		{
			$ucore=DB::table('ien_admin_user')->where('openid', $_SESSION['wechat_user']['original']['openid'])->find();

			if($ucore['score']>$money)
			{
				DB::table('ien_admin_user')->where('openid', $_SESSION['wechat_user']['original']['openid'])->setDec('score',$money);
				DB::table('ien_book')->where('id', $book['bid'])->setInc('tips',$money);
				$data['money']=$money;
				$data['cid']=$novel_id;
				$data['bid']=$book['bid'];
				$data['openid']=$_SESSION['wechat_user']['original']['openid'];
				DB::table('ien_tips')->insert($data);
				return true;
			}
			else{
				header("status: 400 Bad Request");
				$payload=['status'=> 0, 'message'=> "余额不足", 'code'=> 9999];
				return $payload;
			}
		}




	}

	

	

	

}
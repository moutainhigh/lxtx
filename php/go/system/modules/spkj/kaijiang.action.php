<?php
defined('G_IN_SYSTEM')or exit('no');
System::load_app_class('shipinadmin',G_SHIPIN_DIR,'no');
System::load_sys_fun('spkj');
class kaijiang extends shipinadmin {
	public function __construct(){
		parent::__construct();
		$this->db=System::load_sys_class("model");
	}

	//获取摇号的结果并插入数据库
	public function getHaoma(){
		
		//传入产品gid和中奖的10个数字yaohao
		// $_POST['yaohao']='10020603010809050704';
		//$_POST['gid']=7183;
		if(!isset($_POST['gid']) || !isset($_POST['yaohao'] )|| empty($_POST['yaohao'])){
			echo json_encode('50');exit;
		}
		if(!preg_match("/^[0-9]{10}$/",$_POST['yaohao'])){
			echo json_encode('20');exit;//中奖码错误
		}
		$gid = abs($_POST['gid']);	
		$webid = abs($_POST['webid']);
		$db = System::load_sys_class("model");
		$info = $db->GetOne("SELECT * FROM `@#_shoplist_shipin` WHERE `id` = '$gid'");
		
		if(!empty($info['q_uid']) || !empty($info['cp_num'])){
			echo json_encode('99');exit;
		}
		
		if($info['shenyurenshu']!='0'){
			echo json_encode('100');exit;
		}
		$timezone = System::load_sys_config("system","timezone");
		date_default_timezone_set($timezone);
		//处理手动开奖码放到数据库go_spcaipiao
		$kj_qh=date('YmdHis') . rand(10,100);
 		$kj_num=$_POST['yaohao'];
 		$time=time();
 		
 	 	$q = $db->Query("INSERT INTO `@#_spcaipiao` SET `kj_qh`	= '$kj_qh',`kj_num` ='$kj_num',`time` = '$time'");
		 if(!$q) exit(0);
	
		//生成中奖码
		$tocode = System::load_app_class("tocode","pay");
		$tocode->run_tocode(time(),$kj_num,$info['canyurenshu']);//
		
		$code =$tocode->go_code;//z中奖码
		
		$content =$tocode->go_content;
		$counttime = $tocode->count_time;
		
		
		$sql="select * from `@#_member_go_record_shipin` where `shopid` = '$info[id]' and `shopqishu` = '$info[qishu]' and `goucode` LIKE  '%$code%'";
		$u_go_info = $db->GetOne($sql);

		if(!$u_go_info){echo json_encode($code);exit;}
		$u_info = $db->GetOne("select * from `@#_member` where `uid` = '$u_go_info[uid]'");
        if($u_info[user_type]==0&&$info[money]>99999)exit(0);

              
                
		$q_user = serialize($u_info);
		
		//更新商品
		$query = true;
		
		if($u_info){
			$gtimes = (int)System::load_sys_config('system','goods_end_time');
			if($gtimes == 0 || $gtimes == 1){
				$q_showtime = 'N';
			}else{
				$q_showtime = 'Y';
			}
			$time=sprintf("%.3f",microtime(true));				
			$q = $db->Query("UPDATE `@#_shoplist_shipin` SET
					`canyurenshu`=`zongrenshu`,
					`shenyurenshu` = '0',
					`q_uid` = '$u_info[uid]',
					`q_user` = '$q_user',
					`q_user_code` = '$code',
					`cp_num` = '$kj_num',
					`cp_qh` = '$cp_qh',
					`q_content`	= '$content',
					`q_counttime` ='$counttime',
					`q_end_time` = '$time',
					`q_showtime` = 'N'
					where `id` = '$info[id]' and `webid`='$webid'");
			if(!$q) $query = false;
			
			$sql="UPDATE `@#_member_go_record_shipin` SET `huode` = '$code' where `id` = '$u_go_info[id]' and `code` = '$u_go_info[code]' and `uid` = '$u_go_info[uid]' and `shopid` = '$info[id]' and `shopqishu` = '$info[qishu]' and `webid`='$webid'";
			$q1 = $db->Query($sql);
			if(!$q1) $query = false;

		}
		$q_3 = $this->autolottery_install($info);
		if(!$q_3){}
		$phone=substr_replace($u_info['mobile'], '****', 3,4);
		$name=!empty($u_info['username'])?$u_info['username']:$u_info['phone'];
		$msg="您的中奖码{$code}";
		_sendMobile($u_info['mobile'],$msg);
		$array=array('username'=>$name,'code'=>$code,'statu'=>'1','title'=>$info['title']);	//返回中奖码和中奖人
		echo json_encode($array);				
	}


	private function autolottery_install($shop=null){
		if($shop['qishu'] < $shop['maxqishu']){		
			$time = time();
			System::load_app_fun("shipin_content",G_ADMIN_DIR);	
			$goods = $shop;
			$qishu = $goods['qishu']+1;
			$shenyurenshu = $goods['zongrenshu'] - $goods['def_renshu'];
			$query_table = content_get_codes_table();
			$q_1 = $this->db->Query("INSERT INTO `@#_shoplist_shipin` (`sid`,`cateid`, `brandid`, `title`, `title_style`, `title2`, `keywords`, `description`, `money`, `yunjiage`, `zongrenshu`, `canyurenshu`,`shenyurenshu`,`def_renshu`, `qishu`,`maxqishu`,`thumb`, `picarr`, `content`,`codes_table`,`xsjx_time`,`renqi`,`pos`, `time`,`webid`)
					VALUES
					('$goods[sid]','$goods[cateid]','$goods[brandid]','$goods[title]','$goods[title_style]','$goods[title2]','$goods[keywords]','$goods[description]','$goods[money]','$goods[yunjiage]','$goods[zongrenshu]','$goods[def_renshu]','$shenyurenshu','$goods[def_renshu]','$qishu','$goods[maxqishu]','$goods[thumb]','$goods[picarr]','$goods[content]','$query_table','0','$goods[renqi]','$goods[pos]','$time','$_COOKIE[web_id]')
					");				
			$id = $this->db->insert_id();		
			$q_2 = content_get_go_codes($goods['zongrenshu'],3000,$id);
			if($q_1 && $q_2){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}





}
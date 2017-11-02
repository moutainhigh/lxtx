<?php 
/*
 * 复制于go/index.action.php，用于视频开奖模块
 * 
 */
defined('G_IN_SYSTEM')or exit('No permission resources.');
System::load_app_class('base','spkj','no');
//System::load_app_class('base','member','no');

System::load_app_fun('my');
System::load_app_fun('user');
System::load_sys_fun('user');

class index extends base {
	
	public function __construct() {	
		//parent::__construct();判断是否登录
		$this->db=System::load_sys_class('model');		
	}		
	
	
	
	public function init(){		
		mobile_device_detect(true,true,true,true,true,true,true,G_WEB_PATH.'/index.php/mobile/mobile',false);		
		//新品推荐
		//$new_shop=$this->db->GetList("select * from `@#_shoplist` where `pos` = '1' and `q_uid` is null ORDER BY `id` DESC LIMIT 6");
		$new_shop=$this->db->GetList("select * from `@#_shoplist_shipin` where `pos` = '1' and `q_uid` is null ORDER BY `id` DESC LIMIT 6");
		//即将揭晓
		//$shoplist=$this->db->GetList("select * from `@#_shoplist` where `q_uid` is null ORDER BY `shenyurenshu` ASC LIMIT 8");
		$shoplist=$this->db->GetList("select * from `@#_shoplist_shipin` where `q_uid` is null ORDER BY `shenyurenshu` ASC LIMIT 8");
		//人气商品
		//$shoplistrenqi=$this->db->GetList("select * from `@#_shoplist` where `renqi`='1' and `q_uid` is null ORDER BY id DESC LIMIT 12");
		$shoplistrenqi=$this->db->GetList("select * from `@#_shoplist_shipin` where `renqi`='1' and `q_uid` is null ORDER BY id DESC LIMIT 12");
                //手机商品
		//$shoplistshouji=$this->db->GetList("select * from `@#_shoplist` where `cateid`='5' and `q_uid` is null ORDER BY `id` ASC LIMIT 10");
		$shoplistshouji=$this->db->GetList("select * from `@#_shoplist_shipin` where `cateid`='5' and `q_uid` is null ORDER BY `id` ASC LIMIT 10");
		//其他商品
		//$shoplistqita=$this->db->GetList("select * from `@#_shoplist` where `cateid`='15' and `q_uid` is null ORDER BY `id` ASC LIMIT 10");
		$shoplistqita=$this->db->GetList("select * from `@#_shoplist_shipin` where `cateid`='15' and `q_uid` is null ORDER BY `id` ASC LIMIT 10");
		//最新上架
		//$shoplistnew=$this->db->GetList("select * from `@#_shoplist` where `q_uid` is null ORDER BY `id` DESC LIMIT 10");
		$shoplistnew=$this->db->GetList("select * from `@#_shoplist_shipin` where `q_uid` is null ORDER BY `id` DESC LIMIT 10");
		
		$max_renqi_qishu = 1;
		$max_renqi_qishu_id = 1;
		
		if(!empty($shoplistrenqi)){
			foreach ($shoplistrenqi as $renqikey =>$renqiinfo){
				if($renqiinfo['qishu'] >= $max_renqi_qishu){			
					$max_renqi_qishu = $renqiinfo['qishu'];
					$max_renqi_qishu_id = $renqikey;				
				}
			}
			$shoplistrenqi[$max_renqi_qishu_id]['t_max_qishu'] = 1;	
		}				
		$this_time = time();
		if(count($shoplistrenqi) > 1){
					if($shoplistrenqi[0]['time'] > $this_time - 86400*3)
					$shoplistrenqi[0]['t_new_goods'] = 1;
		}
		

		//他们正在云购	
		$go_record=$this->db->GetList("select `@#_member`.uid,`@#_member`.username,`@#_member`.email,`@#_member`.mobile,`@#_member`.img,`@#_member_go_record`.shopname,`@#_member_go_record`.shopid,`@#_member_go_record`.gonumber from `@#_member_go_record`,`@#_member` where `@#_member`.webid = '$_COOKIE[web_id]' and `@#_member_go_record`.webid = '$_COOKIE[web_id]' and `@#_member`.uid = `@#_member_go_record`.uid and `@#_member_go_record`.`status` LIKE '%已付款%'  ORDER BY `@#_member_go_record`.time DESC LIMIT 0,20");
		//$go_record=$this->db->GetList("select `@#_member`.uid,`@#_member`.username,`@#_member`.email,`@#_member`.mobile,`@#_member`.img,`@#_member_go_record`.shopname,`@#_member_go_record`.shopid,`@#_member_go_record`.gonumber from `@#_member_go_record`,`@#_member` where `@#_member`.uid = `@#_member_go_record`.uid and `@#_member_go_record`.`status` LIKE '%已付款%'  ORDER BY `@#_member_go_record`.time DESC LIMIT 0,20");
		//最新揭晓
		//$shopqishu=$this->db->GetList("select id,sid,thumb,title,q_uid,q_user,qishu,q_user_code,zongrenshu from `@#_shoplist` where `q_end_time` is not null ORDER BY `q_end_time` DESC LIMIT 6");
		$shopqishu=$this->db->GetList("select id,sid,thumb,title,q_uid,q_user,qishu,q_user_code,zongrenshu from `@#_shoplist_shipin` where `q_end_time` is not null ORDER BY `q_end_time` DESC LIMIT 6");
		//揭晓倒计时
		//$shopdjs=$this->db->GetList("select * from `@#_shoplist` where `canyurenshu`=`zongrenshu` ORDER BY `wc_time` DESC LIMIT 5");
		$shopdjs=$this->db->GetList("select * from `@#_shoplist_shipin` where `canyurenshu`=`zongrenshu` ORDER BY `wc_time` DESC LIMIT 5");
		//$djs=$this->db->GetOne("select id from `@#_shoplist` where `canyurenshu`=`zongrenshu` and `q_uid` is null");
		$djs=$this->db->GetOne("select id from `@#_shoplist_shipin` where `canyurenshu`=`zongrenshu` and `q_uid` is null");
		$djs_id=$djs['id'];
		//最新中奖记录
		//$zhongjiang=$this->db->GetList("select * from `@#_shoplist` where `q_end_time` is not null ORDER BY `q_end_time` DESC LIMIT 20");
		$zhongjiang=$this->db->GetList("select * from `@#_shoplist_shipin` where `q_end_time` is not null ORDER BY `q_end_time` DESC LIMIT 20");
		//云购动态
		$tiezi=$this->db->GetList("select * from `@#_quanzi_tiezi` where `qzid` = '1' order by `time` DESC LIMIT 5");
		//晒单分享
		$shaidan=$this->db->GetList("select * from `@#_shaidan` order by `sd_id` DESC LIMIT 0,2");
		$shaidan_two=$this->db->GetList("select * from `@#_shaidan` order by `sd_id` DESC LIMIT 2,6");
		//$web_id=WEB_ID;
		include templates("index","index");
	}	
	
	//视频开奖奖品列表
	public function glist(){
	
		$title="商品列表_"._cfg("web_name");
		$cate_band =htmlspecialchars($this->segment(4));//商品分类
		$select =htmlspecialchars($this->segment(5));	//
		if(!$select){
			$select = '10';
		}		
		if(!strpos($cate_band,"e")){//如果传过来的参数不带品牌名，仅有类别
			$fen1 = intval($cate_band);
			$fen2 ='';
			if(!$cate_band)
				$cate_band = 'list';		
		}else{//如果传过来的参数带品牌名，那么$fen1表示类别名，$fen2表示品牌名
			$fen=explode("e",$cate_band);
			$fen1=$fen[0];$fen2=$fen[1];
		}		
		//var_dump($fen1);var_dump($fen2);exit;

		if($fen1){
			$categ_br =$this->db->GetOne("select * from `@#_category` where `parentid` = '$fen1'"); //$categ_br是子分类
		}
		 
		if(empty($fen1)){ //如果$fen1为空，则表示查询所有商品
			$brand=$this->db->GetList("select * from `@#_brand` where 1 order by `order` DESC");	 
			$daohang_title = '全部奖品';	
		}else{//如果$fen1不为空
			//根据加盟商自定义的cateid（$fen1）找到主站同类的cate id
			$sql="SELECT `cateid` FROM `@#_category` WHERE `name`=(SELECT `name` FROM `@#_category` WHERE cateid='".$fen1."' ) AND webid='0'";
			$zhuzhancateid=$this->db->GetOne($sql);//$zhuzhancateid表示和当前加盟商分类id同类别的主站分类id
			//var_dump($sql);var_dump($zhuzhancateid);exit;
			//$brand=$this->db->GetList("select * from `@#_brand` where `cateid`='$fen1' || cateid='".$categ_br['cateid']."' order by `order` DESC");
			$sql2="select * from `@#_brand` where `cateid` in ('$fen1' , '$categ_br[cateid]','$zhuzhancateid[cateid]') order by `order` DESC";
			$brand=$this->db->GetList($sql2);//查询（$fen1=>加盟商商品分类id,$categ_br[cateid]==>加盟商商品该分类id的子类，$zhuzhancateid[cateid]==>和加盟商商品分类名相同的主站分类id）
			//echo "<pre>";var_dump($sql2);var_dump($brand);exit;
			//$daohang=$this->db->GetOne("select * from `@#_category` where `cateid` = '$fen1' || `cateid` = '".$categ_br['cateid']."' LIMIT 1");  
			$daohang=$this->db->GetOne("select * from `@#_category` where `cateid` in ('$fen1' , '$categ_br[cateid]','$zhuzhancateid[cateid]') LIMIT 1");
			$daohang_title = $daohang['name'];//获得该分类的名称
		} 
		
		
		
		//分页
		$num=80;
		if($fen1 and $fen2){
			//$total=$this->db->GetCount("select * from `@#_shoplist` WHERE `q_uid` is null and `brandid`='$fen2'");
			$total=$this->db->GetCount("select * from `@#_shoplist_shipin` WHERE `q_uid` is null and `brandid`='$fen2'");
		}else if($fen1){
			//$total=$this->db->GetCount("select * from `@#_shoplist` WHERE `q_uid` is null and `cateid`='$fen1'");
			
			//$total=$this->db->GetCount("select * from `@#_shoplist` WHERE `q_uid` is null and `cateid` in ('$fen1' , '$categ_br[cateid]','$zhuzhancateid[cateid]')");
			$total=$this->db->GetCount("select * from `@#_shoplist_shipin` WHERE `q_uid` is null and `cateid` in ('$fen1' , '$categ_br[cateid]','$zhuzhancateid[cateid]')");
			//var_dump($total);exit;
		}else{
			//$total=$this->db->GetCount("select * from `@#_shoplist` WHERE `q_uid` is null");
			$total=$this->db->GetCount("select * from `@#_shoplist_shipin` WHERE `q_uid` is null");
		}
		
		$page=System::load_sys_class('page');
		
		if(isset($_GET['p'])){
			$pagenum=$_GET['p'];
		}else{
			$pagenum=1;
		}		
		$page->config($total,$num,$pagenum,"0");
		
		if($pagenum>$page->page){
			$pagenum=$page->page;
		}	
		
	
		$select_w = '';
		if($select == 10){
			$select_w = 'order by `shenyurenshu` ASC';
		}
		if($select == 20){
			$select_w = "and `renqi` = '1'";
		}
		if($select == 30){
			$select_w = 'order by `shenyurenshu` ASC';
		}
		if($select == 40){
			$select_w = 'order by `time` DESC';
		}
		if($select == 50){
			$select_w = 'order by `money` DESC';
		}
		if($select == 60){
			$select_w = 'order by `money` ASC';
		}
		
		if($fen1 and $fen2){			
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `q_uid` is null and `canyurenshu`!=`zongrenshu` and `brandid`='$fen2' $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `q_uid` is null and `canyurenshu`!=`zongrenshu` and `brandid`='$fen2' $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			$shoplist=$this->db->GetPage("select * from `@#_shoplist_shipin` where `q_uid` is null and `canyurenshu`!=`zongrenshu` and `brandid`='$fen2' $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
		}else if($fen1){		
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `q_uid` is null and `canyurenshu`!=`zongrenshu` and `cateid`='$fen1' || `cateid` ='".$categ_br['cateid']."'$select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `q_uid` is null and `canyurenshu`!=`zongrenshu` and `cateid` in ('$fen1' , '$categ_br[cateid]','$zhuzhancateid[cateid]')".$select_w,array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			$shoplist=$this->db->GetPage("select * from `@#_shoplist_shipin` where `q_uid` is null and `canyurenshu`!=`zongrenshu` and `cateid` in ('$fen1' , '$categ_br[cateid]','$zhuzhancateid[cateid]')".$select_w,array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
		}else{			
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `q_uid` is null and `canyurenshu`!=`zongrenshu` $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			$shoplist=$this->db->GetPage("select * from `@#_shoplist_shipin` where `q_uid` is null and `canyurenshu`!=`zongrenshu` $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
		}
		
		$max_renqi_qishu = 1;
		$max_renqi_qishu_id = 1;

		if(!empty($shoplistrenqi)){
			foreach ($shoplistrenqi as $renqikey =>$renqiinfo){
				if($renqiinfo['qishu'] >= $max_renqi_qishu){			
					$max_renqi_qishu = $renqiinfo['qishu'];
					$max_renqi_qishu_id = $renqikey;				
				}		
			}	
			$shoplistrenqi[$max_renqi_qishu_id]['t_max_qishu'] = 1;	
		}	
		
		
		$this_time = time();
		if(count($shoplist) > 1){
					if($shoplist[0]['time'] > $this_time - 86400*3)
					$shoplist[0]['t_new_goods'] = 1;
		}
		
		
		include templates("spkj/index","glist");
	}
	
	//十元专区
	public function syglist(){
		$title="十元专区";
		$cate_band =htmlspecialchars($this->segment(4));
		$select =htmlspecialchars($this->segment(5));
		if(!$select){
			$select = '10';
		}
		if(!strpos($cate_band,"e")){
			$fen1 = intval($cate_band);
			$fen2 ='';
			if(!$cate_band)
				$cate_band = 'list';
		}else{
			$fen=explode("e",$cate_band);
			$fen1=$fen[0];$fen2=$fen[1];
		}
	
	
		if($fen1){
			$categ_br =$this->db->GetOne("select * from `@#_category` where `parentid` = '$fen1'");
		}
			
		if(empty($fen1)){
			$brand=$this->db->GetList("select * from `@#_brand` where 1 order by `order` DESC");
			$daohang_title = '全部奖品';
		}else{
			$brand=$this->db->GetList("select * from `@#_brand` where `cateid`='$fen1' || cateid='".$categ_br['cateid']."' order by `order` DESC");
			$daohang=$this->db->GetOne("select * from `@#_category` where `cateid` = '$fen1' || `cateid` = '".$categ_br['cateid']."' LIMIT 1");
			$daohang_title = $daohang['name'];
		}
	
	
	
		//分页
		$num=20;
		if($fen1 and $fen2){
			//$total=$this->db->GetCount("select * from `@#_shoplist` WHERE `q_uid` is null and `brandid`='$fen2'");
			$total=$this->db->GetCount("select * from `@#_shoplist_shipin` WHERE `q_uid` is null and `brandid`='$fen2'");
		}else if($fen1){
			//$total=$this->db->GetCount("select * from `@#_shoplist` WHERE `q_uid` is null and `cateid`='$fen1'");
			$total=$this->db->GetCount("select * from `@#_shoplist_shipin` WHERE `q_uid` is null and `cateid`='$fen1'");
		}else{
			//$total=$this->db->GetCount("select * from `@#_shoplist` WHERE `q_uid` is null");
			$total=$this->db->GetCount("select * from `@#_shoplist_shipin` WHERE `q_uid` is null");
		}
	
		$page=System::load_sys_class('page');
	
		if(isset($_GET['p'])){
			$pagenum=$_GET['p'];
		}else{
			$pagenum=1;
		}
		$page->config($total,$num,$pagenum,"0");
	
		if($pagenum>$page->page){
			$pagenum=$page->page;
		}
	
	
		$select_w = '';
		if($select == 10){
			$select_w = 'order by `shenyurenshu` ASC';
		}
		if($select == 20){
			$select_w = "and `renqi` = '1'";
		}
		if($select == 30){
			$select_w = 'order by `shenyurenshu` ASC';
		}
		if($select == 40){
			$select_w = 'order by `time` DESC';
		}
		if($select == 50){
			$select_w = 'order by `money` DESC';
		}
		if($select == 60){
			$select_w = 'order by `money` ASC';
		}
	
		if($fen1 and $fen2){
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `yunjiage`='10.00' and `q_uid` is null and `brandid`='$fen2' $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			$shoplist=$this->db->GetPage("select * from `@#_shoplist_shipin` where `yunjiage`='10.00' and `q_uid` is null and `brandid`='$fen2' $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
		}else if($fen1){
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `yunjiage`='10.00' and `q_uid` is null and `cateid`='$fen1' || `cateid` ='".$categ_br['cateid']."'$select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			$shoplist=$this->db->GetPage("select * from `@#_shoplist_shipin` where `yunjiage`='10.00' and `q_uid` is null and `cateid`='$fen1' || `cateid` ='".$categ_br['cateid']."'$select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
		}else{
			//$shoplist=$this->db->GetPage("select * from `@#_shoplist` where `yunjiage`='10.00' and `q_uid` is null $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
			$shoplist=$this->db->GetPage("select * from `@#_shoplist_shipin` where `yunjiage`='10.00' and `q_uid` is null $select_w",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));
		}
	
		$max_renqi_qishu = 1;
		$max_renqi_qishu_id = 1;
	
		if(!empty($shoplistrenqi)){
			foreach ($shoplistrenqi as $renqikey =>$renqiinfo){
				if($renqiinfo['qishu'] >= $max_renqi_qishu){
					$max_renqi_qishu = $renqiinfo['qishu'];
					$max_renqi_qishu_id = $renqikey;
				}
			}
			$shoplistrenqi[$max_renqi_qishu_id]['t_max_qishu'] = 1;
		}
	
	
		$this_time = time();
		if(count($shoplist) > 1){
			if($shoplist[0]['time'] > $this_time - 86400*3)
				$shoplist[0]['t_new_goods'] = 1;
		}
	
	
		include templates("spkj/index","syglist");
	}
	
	//商品详细
	public function item(){
		//include templates("spkj/index","test");exit;
		
		$mysql_model=System::load_sys_class('model');
		$timezone = System::load_sys_config("system","timezone");
		date_default_timezone_set($timezone);
		$itemid=abs(intval(safe_replace($this->segment(4))));	
		//$item=$mysql_model->GetOne("select * from `@#_shoplist` where `id`='".$itemid."' LIMIT 1");
		$item=$mysql_model->GetOne("select * from `@#_shoplist_shipin` where `id`='".$itemid."' LIMIT 1");
	//	print_r($item);
		if(!$item)_message("没有这个商品！",WEB_PATH,3);
		$q_showtime = $item['kj_time']-time();
	//	echo $q_showtime;
		 if($item['q_end_time'] && $item['q_user_code']){
			header("location: ".WEB_PATH."/dataserver/".$item['id']);
			exit;			
		} 
			
	
		
		$sid=$item['sid'];
		//$sid_code=$mysql_model->GetOne("select * from `@#_shoplist` where `sid`='$sid' order by `id` DESC LIMIT 1,1");
		$sid_code=$mysql_model->GetOne("select * from `@#_shoplist_shipin` where `sid`='$sid' order by `id` DESC LIMIT 1,1");
		if($item['id'] == $sid_code['id']){
			$sid_code = null;
		}
		
		//$sid_go_record=$mysql_model->GetOne("select * from `@#_member_go_record` where `shopid`='$sid_code[id]' and `uid`='$sid_code[q_uid]' order by `id` DESC LIMIT 1");
		$sid_go_record=$mysql_model->GetOne("select * from `@#_member_go_record_shipin` where `shopid`='$sid_code[id]' and `uid`='$sid_code[q_uid]' order by `id` DESC LIMIT 1");
		
		
		$category=$mysql_model->GetOne("select * from `@#_category` where `cateid` = '$item[cateid]' LIMIT 1");
		$brand=$mysql_model->GetOne("select * from `@#_brand` where `id`='$item[brandid]' LIMIT 1");
		
		$title=$item['title'].' ('.$item['title2'].')';
		
		$keywords = $item['keywords'];
		$description = $item['description'];
		
		$syrs=$item['zongrenshu']-$item['canyurenshu'];
		$item['picarr'] = unserialize($item['picarr']);
		$item_img=$item['picarr'][0];
		
		
		//$us=$mysql_model->GetList("select * from `@#_member_go_record` where `shopid`='".$itemid."' AND `shopqishu`='".$item['qishu']."'ORDER BY id DESC LIMIT 6");
		$us=$mysql_model->GetList("select * from `@#_member_go_record_shipin` where `shopid`='".$itemid."' AND `shopqishu`='".$item['qishu']."'ORDER BY id DESC LIMIT 6");
		//$us2=$mysql_model->GetList("select * from `@#_member_go_record` where `shopid`='".$itemid."' AND `shopqishu`='".$item['qishu']."'ORDER BY id DESC limit 50");
		$us2=$mysql_model->GetList("select * from `@#_member_go_record_shipin` where `shopid`='".$itemid."' AND `shopqishu`='".$item['qishu']."'ORDER BY id DESC limit 50");
		
		
		//期数显示
		//$itemlist = $this->db->GetList("select id,qishu,q_uid from `@#_shoplist` where `sid`='$item[sid]' order by `qishu` DESC");		
		$itemlist = $this->db->GetList("select id,qishu,q_uid from `@#_shoplist_shipin` where `sid`='$item[sid]' order by `qishu` DESC");		

		$loopqishu='<ul class="Period_list">';
		if(!$itemlist[0]['q_uid']){
			if($itemlist[0]['id'] == $item['id'])
				$loopqishu.='<li><a href="'.WEB_PATH.'/goods/'.$itemlist[0]['id'].'"><b class="period_Ongoing period_ArrowCur" style="padding-left:0px;">'."第".$itemlist[0]['qishu']."期<i></i></b></a></li>";
			else
				$loopqishu.='<li><a href="'.WEB_PATH.'/goods/'.$itemlist[0]['id'].'"><b class="period_Ongoing">'."第".$itemlist[0]['qishu']."期<i></i></b></a></li>";
		}else{		
			if($itemlist[0]['id'] == $item['id'])
				$loopqishu.='<li><a href="'.WEB_PATH.'/goods/'.$itemlist[0]['id'].'"><b class="period_ArrowCur">'."第".$itemlist[0]['qishu']."期<i></i></b></a></li>";
			else
				$loopqishu.='<li><a href="'.WEB_PATH.'/dataserver/'.$itemlist[0]['id'].'" class="gray02">第'.$itemlist[0]['qishu'].'期</a></li>';
		}
		unset($itemlist[0]);		
		foreach($itemlist as $key=>$qitem){
			if($key%8==0){
				$loopqishu.='</ul><ul class="Period_list">';
			}
			if($qitem['id'] == $item['id'])
				$loopqishu.='<li><b class="period_ArrowCur">第'.$qitem['qishu'].'期</b></li>';
			else
				$loopqishu.='<li><a href="'.WEB_PATH.'/dataserver/'.$qitem['id'].'" class="gray02">第'.$qitem['qishu'].'期</a></li>';	
		}
		$loopqishu.='</ul>';
		
		//$zx_shop = $this->db->GetOne("select * from `@#_shoplist` where `sid`='$item[sid]' and `q_uid` is null order by `qishu` DESC");
		$zx_shop = $this->db->GetOne("select * from `@#_shoplist_shipin` where `sid`='$item[sid]' and `q_uid` is null order by `qishu` DESC");
		//$go_record_list = $this->db->GetList("select * from `@#_member_go_record` where `shopid` = '$item[id]' and `shopqishu` = '$item[qishu]' order by `id` DESC limit 500");		
		$go_record_list = $this->db->GetList("select * from `@#_member_go_record_shipin` where `shopid` = '$item[id]' and `shopqishu` = '$item[qishu]' order by `id` DESC limit 500");		

		include templates("spkj/index","item");
	}
	
	//往期商品查看
	public function dataserver(){	
	
		$itemid=abs(intval(safe_replace($this->segment(4))));	
		//$item=$this->db->GetOne("select * from `@#_shoplist` where `id`='$itemid' LIMIT 1");
		$item=$this->db->GetOne("select * from `@#_shoplist_shipin` where `id`='$itemid' LIMIT 1");
	//	$item=$this->db->GetOne("select * from `@#_shoplist` where `id`='$itemid' and `q_uid` is not null LIMIT 1");
	//	print_r($item);

		if(!$item){
			_message("没有这个商品!");
		}
		if(empty($item['q_uid'])){
			header("location: ".WEB_PATH."/goods/".$itemid);
			exit;
		}
		if(empty($item['q_user_code'])){
		//	_message("该商品正在进行中...",WEB_PATH.'/goods/'.$itemid);
			header("location: ".WEB_PATH."/goods/".$itemid);
			exit;
		}
		if(empty($item['q_end_time']) || empty($item['q_user_code'])){	
			header("location: ".WEB_PATH."/goods/".$item['id']);
			exit;
		}	
		$category=$this->db->GetOne("select * from `@#_category` where `cateid` = '$item[cateid]' LIMIT 1");
		$brand=$this->db->GetOne("select * from `@#_brand` where `id` = '$item[brandid]' LIMIT 1");
		
		//云购中奖码
		$q_user = unserialize($item['q_user']);		
		$q_user_code_len = strlen($item['q_user_code']);
		$q_user_code_arr = array();
		for($q_i=0;$q_i < $q_user_code_len;$q_i++){	
			$q_user_code_arr[$q_i] = substr($item['q_user_code'],$q_i,1);
		}
		$cp_num = unserialize($item['cp_num']);
		$cp_user_code_len = strlen($item['cp_num'])/2;
		$cp_user_code_arr = array();
		for($q_i=0;$q_i < $cp_user_code_len;$q_i++){
			$cp_user_code_arr[$q_i] = substr($item['cp_num'],$q_i*2,2);
		}
		$cp_ys = fmod($item['cp_num'],$item['zongrenshu']);
		$cp_ys_len = strlen($cp_ys);
		$cp_ys_arr = array();
		for($q_i=0;$q_i < $cp_ys_len;$q_i++){
			$cp_ys_arr[$q_i] = substr($cp_ys,$q_i,1);
		}
		
		//视频开奖云购总次数
		//$user_shop_number = $this->db->GetOne("select sum(gonumber) as gonumber from `@#_member_go_record` where `uid`= '$item[q_uid]' and `shopid` = '$itemid' and `shopqishu` = '$item[qishu]'");
		$user_shop_number = $this->db->GetOne("select sum(gonumber) as gonumber from `@#_member_go_record_shipin` where `uid`= '$item[q_uid]' and `shopid` = '$itemid' and `shopqishu` = '$item[qishu]'");
		$user_shop_number = $user_shop_number['gonumber'];
		//用户云购时间
		//$user_shop_time = $this->db->GetOne("select time from `@#_member_go_record` where `uid`= '$item[q_uid]' and `shopid` = '$itemid' and `shopqishu` = '$item[qishu]' and `huode` = '$item[q_user_code]'");
		$user_shop_time = $this->db->GetOne("select time from `@#_member_go_record_shipin` where `uid`= '$item[q_uid]' and `shopid` = '$itemid' and `shopqishu` = '$item[qishu]' and `huode` = '$item[q_user_code]'");
		$user_shop_time = $user_shop_time['time'];
		//得到云购码
		//$user_shop_codes = $this->db->GetOne("select GROUP_CONCAT(goucode) as goucode from `@#_member_go_record` where `uid`= '$item[q_uid]' and `shopid` = '$itemid' and `shopqishu` = '$item[qishu]'");
		$user_shop_codes = $this->db->GetOne("select GROUP_CONCAT(goucode) as goucode from `@#_member_go_record_shipin` where `uid`= '$item[q_uid]' and `shopid` = '$itemid' and `shopqishu` = '$item[qishu]'");
	//	$user_shop_codes = $this->db->GetOne("select goucode from `@#_member_go_record` where `uid`= '$item[q_uid]' and `shopid` = '$itemid' and `shopqishu` = '$item[qishu]'");
		$user_shop_codes = $user_shop_codes['goucode'];

		
		
		$h=abs(date("H",$item['q_end_time']));
		$i=date("i",$item['q_end_time']);
		$s=date("s",$item['q_end_time']);
		$w=substr($item['q_end_time'],11,3);	
		$user_shop_time_add = $h.$i.$s.$w;
		$user_shop_fmod = fmod($user_shop_time_add*100,$item['canyurenshu']);
		if($item['q_content']){
			$item_q_content = unserialize($item['q_content']);
			$keysvalue = $new_array = array();
			foreach($item_q_content as $k=>$v){
				$keysvalue[$k] = $v['time'];				
				$h=date("H",$v['time']);
			    $i=date("i",$v['time']);
			    $s=date("s",$v['time']);	
			    list($timesss,$msss) = explode(".",$v['time']);
				$item_q_content[$k]['timeadd'] = $h.$i.$s.$msss;			
			
			}
			arsort($keysvalue);	//asort($keysvalue);正序
			reset($keysvalue);
			foreach ($keysvalue as $k=>$v){
				$new_array[$k] = $item_q_content[$k];
			}			
			$item['q_content'] = $new_array;
		}
	
	
		$title=$item['title'].' ('.$item['title2'].')';
		$keywords = $item['keywords'];
		$description = $item['description'];
		
		//$go_record_list = $this->db->GetList("select * from `@#_member_go_record` where `shopid` = '$item[id]' and `shopqishu` = '$item[qishu]' order by `id` DESC limit 50");
		$go_record_list = $this->db->GetList("select * from `@#_member_go_record_shipin` where `shopid` = '$item[id]' and `shopqishu` = '$item[qishu]' order by `id` DESC limit 50");
		//$itemzx=$this->db->GetOne("select * from `@#_shoplist` where `sid`='$item[sid]' and `qishu`>'$item[qishu]' order by `qishu` DESC LIMIT 1");
		$itemzx=$this->db->GetOne("select * from `@#_shoplist_shipin` where `sid`='$item[sid]' and `qishu`>'$item[qishu]' order by `qishu` DESC LIMIT 1");
		
		//期数显示
		//$itemlist = $this->db->GetList("select id,sid,q_uid,qishu from `@#_shoplist` where `sid`='$item[sid]' order by `qishu` DESC");
		$itemlist = $this->db->GetList("select id,sid,q_uid,qishu from `@#_shoplist_shipin` where `sid`='$item[sid]' order by `qishu` DESC");
		$loopqishu='<ul class="Period_list">';
		if(empty($itemlist[0]['q_uid'])){			
			$loopqishu.='<li><a href="'.WEB_PATH.'/goods/'.$itemlist[0]['id'].'"><b class="period_Ongoing">'."第".$itemlist[0]['qishu']."期<i></i></b></a></li>";
			unset($itemlist[0]);
		}else{		
			$loopqishu.='<li><a href="'.WEB_PATH.'/goods/'.$itemlist[0]['id'].'"><b class="period_ArrowCur">'."第".$itemlist[0]['qishu']."期<i></i></b></a></li>";
			unset($itemlist[0]);
		}
		if(empty($itemlist)){
			$loopqishu.='</ul>';
		}	
	
		foreach($itemlist as $key=>$qitem){			
			if($key%8==0){		
				$loopqishu.='</ul><ul class="Period_list">';
			}				
			if($qitem['id'] == $itemid){
				$loopqishu.='<li><b class="period_ArrowCur">第'.$qitem['qishu'].'期</b></li>';
			}else{
				$loopqishu.='<li><a href="'.WEB_PATH.'/dataserver/'.$qitem['id'].'" class="gray02">第'.$qitem['qishu'].'期</a></li>';		
			}			
		}
		
		//$zx_shop = $this->db->GetOne("select * from `@#_shoplist` where `sid`='$item[sid]' and `q_uid` is null order by `qishu` DESC");
		$zx_shop = $this->db->GetOne("select * from `@#_shoplist_shipin` where `sid`='$item[sid]' and `q_uid` is null order by `qishu` DESC");
	
		include templates("spkj/index","dataserver");
	}

	//最新揭晓
	public function lottery(){	
		//最新揭晓
		$page=System::load_sys_class('page');		
		//$total=$this->db->GetCount("select id from `@#_shoplist` where `canyurenshu`=`zongrenshu`");
		$total=$this->db->GetCount("select id from `@#_shoplist_shipin` where `canyurenshu`=`zongrenshu`");
		//$djs=$this->db->GetOne("select id from `@#_shoplist` where `canyurenshu`=`zongrenshu` and `q_uid` is null");
		$djs=$this->db->GetOne("select id from `@#_shoplist_shipin` where `canyurenshu`=`zongrenshu` and `q_uid` is null");
		$djs_id=$djs[id];
		if(isset($_GET['p'])){
			$pagenum=$_GET['p'];
		}else{
			$pagenum=1;
		}
		$num=200;
		$page->config($total,$num,$pagenum,"0");
		//$shopqishu=$this->db->GetPage("select * from `@#_shoplist` where `canyurenshu`=`zongrenshu` ORDER BY `wc_time` DESC",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));	
		$shopqishu=$this->db->GetPage("select * from `@#_shoplist_shipin` where `canyurenshu`=`zongrenshu` ORDER BY `wc_time` DESC",array("num"=>$num,"page"=>$pagenum,"type"=>1,"cache"=>0));	
			
		//$shoplist=$this->db->GetList("select * from `@#_shoplist` where `q_uid` is null  ORDER BY `canyurenshu` DESC LIMIT 4");
		$shoplist=$this->db->GetList("select * from `@#_shoplist_shipin` where `q_uid` is null  ORDER BY `canyurenshu` DESC LIMIT 4");
		//$member_record=$this->db->GetList("select * from `@#_member_go_record` order by id DESC limit 6");		
		$member_record=$this->db->GetList("select * from `@#_member_go_record` order by id DESC limit 6");		
		include templates("index","lottery");
	}
	
	//彩票数据
	/*
	public function caipiao(){
		$cp_data=$this->db->GetOne("select * from  @#_caipiao ORDER BY `kj_qh` DESC");
		//$html='';
		if(!empty($cp_data))	
		{			
			$html .= '<li class="index_head_text"><span>福彩最新开奖结果:</span></li>';
			//开奖号码分解		
			$number =str_split($cp_data[kj_num],2);//吴山2015-12-07修改原来是$cp_data[kj_num]
			foreach($number as $k => $v)
			{
				$html .= '<li class="index_head_num"><span>'.$v.'</span></li>';
			}		
		}
		else
		{
			$html = '<li class="index_head_text"><span>彩票数据通讯延迟，请稍等...</span></li>';
		}
		echo $html;
	}
*/
	//首页揭晓倒计时
	public function jxdjs(){
		//$shopdjs=$this->db->GetList("select * from `@#_shoplist` where `canyurenshu`=`zongrenshu` ORDER BY `wc_time` DESC LIMIT 5");
		$shopdjs=$this->db->GetList("select * from `@#_shoplist_shipin` where `canyurenshu`=`zongrenshu` ORDER BY `wc_time` DESC LIMIT 5");
		//$jxdjs=$this->db->GetOne("select id from `@#_shoplist` where `canyurenshu`=`zongrenshu` and `q_uid` is null");
		$jxdjs=$this->db->GetOne("select id from `@#_shoplist_shipin` where `canyurenshu`=`zongrenshu` and `q_uid` is null");
		$djs_id=$jxdjs[id];
		$html='';	
		foreach($shopdjs as $key=>$djs){
			$djs['q_user'] = unserialize($djs['q_user']);
			$html.='<li class="w-goodsList-item">';
			if($djs['q_uid']){
				$html.='<i class="ico ico-label ico-label-yjx"></i>';
			}else{
				$html.='<i class="ico ico-label ico-label-zzjx"></i>';
			}
			$html.='<div class="w-goods w-goods-ing" ><div class="w-goods-pic">';
			$html.='<a href="'.WEB_PATH.'/dataserver/'.$djs[id].'" title="'.$djs[title].'" target="_blank"> <img width="200" height="150" alt="'.$djs['title'].'" src="'.G_UPLOAD_PATH.'/'.$djs[thumb].'" /> </a></div>';
			$html.='<p class="w-goods-title f-txtabb"><a title="'.$djs['title'].'" href="'.WEB_PATH.'/dataserver/'.$djs['id'].'" target="_blank">(第'.$djs['qishu'].'期) '.$djs['title'].'</a></p>';
			if($djs['q_uid']){
				$html.='<div class="w-goods-yjx"><p>恭喜<a herf="'.WEB_PATH.'/uname/'.idjia($djs[q_uid]).'" title="'.get_user_name($djs[q_user]).'(ID:'.$djs['q_uid'].')">'.get_user_name($djs['q_user']).'</a>获得</p></div>';
			}else{
				$html.='<div class="w-goods-djs"><span>揭晓倒计时：</span><ul><li class="count-m0">0</li><li class="count-m1">0</li><li>:</li><li class="count-s0">0</li><li class="count-s1">0</li><li>:</li><li class="count-ms0">0</li><li class="count-ms1">0</li></ul></div>';
			}
			$html.='</div></li>';
		}
		echo $html;
	}	
	
}
?>
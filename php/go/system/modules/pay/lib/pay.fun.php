<?php
 System::load_sys_fun("send");
/*

*   生成购买的云购码

*	user_num 		@生成个数

*	shopinfo		@商品信息

*	ret_data		@返回信息

*/

function pay_get_shop_codes($user_num=1,$shopinfo=null,&$ret_data=null){
	  $time1 = getTimeInMilliSec();

	  $generate_log_enabled = false;
	  if ($shopinfo['id'] == 86 || $shopinfo['id'] == 96) {
	  	$generate_log_enabled = true;
	  }

		$db = System::load_sys_class("model");
		$ret_data['query'] = true;
		$table = '@#_'.$shopinfo['codes_table'];
		generate_log_product($table);
		$codes_arr = array();

		$codes_one = $db->GetOne("select id,s_id,s_cid,s_len,s_codes from `$table` where `s_id` = '$shopinfo[id]' order by `s_cid` DESC  LIMIT 1 for update");
		generate_log_product("select id,s_id,s_cid,s_len,s_codes from `$table` where `s_id` = '$shopinfo[id]' order by `s_cid` DESC  LIMIT 1 for update");
		$timea = getTimeInMilliSec();
		if ($generate_log_enabled) {
			generate_log("in pay_get_shop_codes phase a , cost time:".($timea - $time1)." milli seconds.");
		}

		$codes_arr[$codes_one['s_cid']] = $codes_one;

		$codes_count_len = $codes_arr[$codes_one['s_cid']]['s_len'];


		if($codes_count_len < $user_num && $codes_one['s_cid'] > 1){

			for($i=$codes_one['s_cid']-1;$i>=1;$i--):

				$codes_arr[$i] = $db->GetOne("select id,s_id,s_cid,s_len,s_codes from `$table` where `s_id` = '$shopinfo[id]' and `s_cid` = '$i'  LIMIT 1 for update");

				$codes_count_len += $codes_arr[$i]['s_len'];

				if($codes_count_len > $user_num)  break;

			endfor;

		}

		$timeb = getTimeInMilliSec();
		if ($generate_log_enabled) {
			generate_log("in pay_get_shop_codes phase b , cost time:".($timeb - $timea)." milli seconds.");
		}

		if($codes_count_len < $user_num) $user_num = $codes_count_len;



		$ret_data['user_code'] = '';

		$ret_data['user_code_len'] = 0;



		foreach($codes_arr as $icodes){

			$u_num = $user_num;

			$icodes['s_codes'] = unserialize($icodes['s_codes']);

			$code_tmp_arr = array_slice($icodes['s_codes'],0,$u_num);

			$ret_data['user_code'] .= implode(',',$code_tmp_arr);

			$code_tmp_arr_len = count($code_tmp_arr);



			if($code_tmp_arr_len < $u_num){

				$ret_data['user_code'] .= ',';

			}

			//why use $u_num as the offset
			$icodes['s_codes'] = array_slice($icodes['s_codes'],$u_num,count($icodes['s_codes']));

			$icode_sub = count($icodes['s_codes']);

			$icodes['s_codes'] = serialize($icodes['s_codes']);



			if(!$icode_sub){

				$query = $db->Query("UPDATE `$table` SET `s_cid` = '0',`s_codes` = '$icodes[s_codes]',`s_len` = '$icode_sub' where `id` = '$icodes[id]'");

				if(!$query)$ret_data['query'] = false;

			}else{

				$query = $db->Query("UPDATE `$table` SET `s_codes` = '$icodes[s_codes]',`s_len` = '$icode_sub' where `id` = '$icodes[id]'");

				if(!$query)$ret_data['query'] = false;

			}

			$ret_data['user_code_len'] += $code_tmp_arr_len;

			$user_num  = $user_num - $code_tmp_arr_len;

			$cost = getTimeInMilliSec() - $time1;
			$timec = getTimeInMilliSec();
			if ($generate_log_enabled) {
				generate_log("in pay_get_shop_codes phase c , cost time:".($timec - $timeb)." milli seconds.");
			}
//			generate_log("in pay_get_shop_codes, cost time : ".$cost." milliseconds for ".$shopinfo['id']);
		}
}

//生成订单号

function pay_get_dingdan_code($dingdanzhui=''){

	return $dingdanzhui.time().substr(microtime(),2,6).rand(0,9);

}





/*

	揭晓与插入商品

	@shop   商品数据

*/



function pay_insert_shop_x($shop='',$type=''){
	$g_c_x = System::load_app_config("get_code_x",'',"pay");
	if(is_array($g_c_x) && isset($g_c_x['class'])){
		$gcx_db = System::load_app_class($g_c_x['class'],"pay");
	}else{
		$g_c_x = array("class"=>"tocode");
		$gcx_db = System::load_app_class($g_c_x['class'],"pay");
	}
	$gcx_db->config($shop,$type);
	$gcx_db->get_run_tocode();
	$ret_data = $gcx_db->returns();
}

//get the list of expensive goods that will be used for generating auto records to help fix the time issue
//the element in the list is fetched from the view go_shoplist
function listGoodsForAutoRecords($db) {
	$goodslist = $db->GetList("select id from `@#_goods` order by zongrenshu desc limit 2");
	
	$goodsIdStr =  "";

	foreach ($goodslist as $key => $value) {
		$goodsIdStr .= $value['id'].",";
	}
	//remove the ending comma
	$goodsIdStr = trim($goodsIdStr, ",");
	generate_log("retrieved goods id str:".$goodsIdStr);

	$goodsIdArr = array();
	$goodsItemList = $db->GetList("select * from `@#_shoplist` where sid in ($goodsIdStr) and shenyurenshu > 0");
	return $goodsItemList;
}

//get the id list of robot users
function getListOfRobots($db, $cnt) {
	$userlist = $db->GetList("select uid from `@#_member` where auto_user = 1 limit $cnt");
	$idlist = array();
	foreach ($userlist as $key => $value) {
		array_push($idlist, $value['uid']);
	}
	return $idlist;
}

//this function returns an array of ids for table go_member_go_record
//so that the time for these records could be updated accordingly
function generateAutoRecords($db, $recordCnt) {
	$time1 = getTimeInMilliSec();
	$pay = System::load_app_class("pay","pay");

	$goodsItemList = listGoodsForAutoRecords($db);
	$robotIdList = getListOfRobots($db, $recordCnt);

	$time2 = getTimeInMilliSec();
	generate_log("robotIdList count:".count($robotIdList)." cost time:".($time2 - $time1)." milli seconds.");
	$goodsItemCnt = count($goodsItemList);
	$tradeStats = array();
	foreach ($goodsItemList as $key => $value) {
		$tradeStats[$value['id']] = 0;
	}

	for ($i = 0; $i <= $recordCnt -1; $i++) {
		$itemIndex = rand(0, $goodsItemCnt - 1);
		$chosenItem = $goodsItemList[$itemIndex];
		$shopnum = rand(1, 3);
		$user_id = $robotIdList[$i];
		$m = intval($chosenItem['yunjiage'])*$shopnum+5;
		generate_log("current user is: ".$user_id);
		//add money
		$db->Query("UPDATE `@#_member` SET  `money` = '$m' WHERE `uid` = '$user_id' ");
		generate_log("UPDATE `@#_member` SET  `money` = '$m' WHERE `uid` = '$user_id' ");
		//设置IP
		$_SERVER['HTTP_CLIENT_IP'] = randip();
		//调用购买商品接口
		generate_log("pay_user_go_shop".$user_id." ".$chosenItem['id']." ".$shopnum);
		$rs  = $pay->pay_user_go_shop($user_id, $chosenItem['id'], $shopnum);
		$tradeStats[$chosenItem['id']] = $tradeStats[$chosenItem['id']] + 1;
		generate_log("add one to tradestats[$chosenItem[id]]");
	}

	$time3 = getTimeInMilliSec();
	generate_log("in generateAutoRecords 2 cost time:".($time3 - $time2)." milli seconds.");

	$tradeRecordIdList = array();
	foreach ($tradeStats as $key => $value) {
		$shopid = $key;
		$tradeCnt = $value;
		generate_log("shopid:".$shopid." tradeCnt:".$tradeCnt);
		$idlist = $db->GetList("select id from `@#_member_go_record` where `shopid` = '$shopid' order by id desc limit $tradeCnt");
		generate_log("select id from `@#_member_go_record` where `shopid` = '$shopid' order by id desc limit $tradeCnt");
		foreach ($idlist as $k => $v) {
			array_push($tradeRecordIdList, $v['id']);
		}
	}

	$time4 = getTimeInMilliSec();
	generate_log("in generateAutoRecords 3 cost time:".($time4 - $time3)." milli seconds.");
	return $tradeRecordIdList;
}

function pay_insert_shop($shop='', $type=''){
	$a = $shop['xsjx_time'] != '0';
	generate_log("in pay_insert_shop.1.".$shop['xsjx_time']."  ".$a);
  	$currentTime = getTimeInMilliSec();
	//$time=sprintf("%.3f",microtime(true)+(int)System::load_sys_config('system','goods_end_time'));
	//start from current time
	$time=sprintf("%.3f",microtime(true)) - 0.5; //half a second ahead of current time
	$db = System::load_sys_class("model");
	if($shop['xsjx_time'] > 0){
		return $db->Query("UPDATE `@#_goods_item` SET `canyurenshu`=`zongrenshu`,	`shenyurenshu` = '0', `q_status` = '1' where `id` = '$shop[id]'");
	}
  
	$timea = getTimeInMilliSec();
	$tocode = System::load_app_class("tocode","pay");
	$tocode->shop = $shop;
	$tocode->run_tocode($time,50,$shop['canyurenshu'],$shop);
	$timeb = getTimeInMilliSec();
	generate_log("generated code : ".$tocode->go_code. " cost time:".($timeb - $timea). " milli seconds.");

  //the generated goucode
	$code = $tocode->go_code;
	$content = addslashes($tocode->go_content);
	$counttime = $tocode->count_time;

	generate_log_callback("counttime:".$counttime);
	$timeb1 = getTimeInMilliSec();
	generate_log_callback("run to code cost time:".($timeb1 - $timeb). " milli seconds.");

	//20160816新增机器人中奖
	$obtainedByRobot = handledSpecially($shop);
	generate_log("zongrenshu".$shop['zongrenshu']."obtained by robot:".$obtainedByRobot);
	if ($obtainedByRobot && $shop['zhiding'] <= 0) { //if a goods already has been assigned a user, we don't need to use the bot
		//本来的中奖码对应的记录
		$tempinfo = $db->GetOne("select b.auto_user as auto_user from `@#_member_go_record` a left join `@#_member` b on a.uid = b.uid where a.`shopid` = '$shop[id]' and a.`goucode` LIKE  '%$code%'");
		if($tempinfo['auto_user']) {
		//if(false) {
			$shop['zhiding'] = false;
			generate_log("original user is robot, no need to reassign the user.");
		} else {
			//assign value for zhiding
			$userlist = $db->GetList("select distinct(a.uid) as uid from go_member_go_record a left join go_member b on a.uid = b.uid where a.shopid = $shop[id] and b.auto_user = 1;");
			$robotCount = count($userlist);
			$robotIndex = rand(1, $robotCount);
			$shop['zhiding'] = $userlist[$robotIndex]['uid'];			
		}
	}
	$timeb2 = getTimeInMilliSec();
	generate_log("user preparation cost time: ".($timeb2 - $timeb1). " milli seconds.");
	generate_log("For goods ".$shop['id']." "."zhiding is:" . $shop['zhiding']);

	if($shop['zhiding']){
		$timeaa = getTimeInMilliSec();
    //we don't need to apply shopid and qishu together
    $ex_info=$db->GetOne("select * from `@#_member_go_record` where `shopid` = '$shop[id]' and `uid`='{$shop['zhiding']}'");
		//$ex_info=$db->GetOne("select * from `@#_member_go_record` where `shopid` = '$shop[id]' and `shopqishu` = '$shop[qishu]' and `uid`='{$shop['zhiding']}'");
	  $ex_code=explode(",",$ex_info['goucode']);
		$ex_count=count($ex_code);

		$gap = 10000000; //the initial gap, we won't have a goods more expensive than 10000000
		$chosenCode = '';
		//we won't use the rand value, we will use a value most adjacent
		foreach ($ex_code as $ecode) {
			$diff = $ecode - $code;
			if ($diff < 0) {
				$diff = $shop['canyurenshu'] + $diff;
			}

			if ($diff < $gap) {
				$gap = $diff;
				$chosenCode = $ecode;
			}	
		}
		generate_log("canyurenshu:".$shop['canyurenshu']." gap is: " .$gap. " chosen code :".$chosenCode." original code:".$code);
		$timeab = getTimeInMilliSec();
		generate_log("phase a cost time:".($timeab - $timeaa)." milli seconds.");
		//now we have the choosen code
		if($chosenCode){
			$chazhi= $chosenCode - $code;
			//no matter in which case, the records we add will be later than the first several records
			$counttime = $counttime + $gap;	
			///////////////////////////////////
			//$code=$ex_code[$ex_rand];					
			//将指定中奖会员的购买记录中的code换成系统计算出来的中奖吗
			//添加时间校准
			if(!empty($chazhi)){
				//TODO
				//now let's calculate how many records we need to add
				$recordlist = $db->GetList("select id, time from `@#_member_go_record` where `time` < $time order by time desc limit 1, 50");
				//calculate how many records we need to add
				//init the gap value to 0
				generate_log("select id, time from `@#_member_go_record` where `time` < $time order by time desc limit 1, 50");
				$maxTime = str_replace(".", "", $recordlist[0]['time']);

				foreach ($recordlist as $key => $rd) {
					generate_log($rd['time']);
				}

				generate_log("recordlist[0]['time'] is :".$recordlist[0]['time']);
				generate_log("original max time is:".$maxTime);

				$gapVal = 0;
				$oldIndex = 0;
				$arrayForInsertRecords = array();

				//if accumulated gap doesn't reach the target gap, we will continue add more records
				while(($gapVal < $gap) && ($oldIndex < 50) ) {
					$oldTime = str_replace(".", "", $recordlist[49 - $oldIndex]['time']);
					//the time of the inserted record has some random difference from the last one
					$timeForNewRecord = $maxTime - rand(10,50); 
					generate_log("generated time is:".$timeForNewRecord);
					$gapForNewRecord = $timeForNewRecord - $oldTime;
					if ($gapForNewRecord > ($gap - $gapVal)) {

						generate_log_product("oldtim is:".$oldTime." gap is:".$gap." gapval is:".$gapVal);
						$timeForNewRecord = $oldTime + ($gap - $gapVal);
						generate_log_product("timeForNewRecord is:".$timeForNewRecord);
						$gapForNewRecord = $timeForNewRecord - $oldTime;
						generate_log_product("gapForNewRecord is:".$gapForNewRecord);
					}
 					$gapVal += $gapForNewRecord;
 					generate_log_product("gapVal is:".$gapVal);

					$oldIndex++;
					generate_log_product("push time into array:".$timeForNewRecord);
					array_push($arrayForInsertRecords, $timeForNewRecord);
				}

				if ($oldIndex == 50) {
					generate_log("iteration count reaches 50!!!!".$shop['id']." gapVal is:".$gapVal." gap is:".$gap);
				}				

				$countForNewRecords = count($arrayForInsertRecords);

				if($gapVal < $gap) {
					generate_log("failed to generate enough records to fix the time issue.");
				} else {
					generate_log("generated the time for ".$countForNewRecords." records.");
				}

				generate_log("now calculation finished with ".$countForNewRecords." records to be created.");
				$timeac = getTimeInMilliSec();
				//generate_log_callback("phase b cost time:".($timeac - $timeab)." milli seconds.");

				//now create a couple of records
				//get the list of shop ids that will be used for generating fake records
				//$chosenGoodsIdArr = listGoodsForAutoRecords($db);
				//now generating a list of new records
				$generatedRecordIdList = generateAutoRecords($db, $countForNewRecords);
				$timeac1 = getTimeInMilliSec();
				generate_log_callback("phase c1 cost time:".($timeac1 - $timeac)." milli seconds.");
				//now adjust the time for these records
				for ($i = 0; $i < $countForNewRecords; $i++) {
					$recordId = $generatedRecordIdList[$i];
					$newtime = getTimeString($arrayForInsertRecords[$i]);
					generate_log_callback("in the iteration for adjusting time. index value: ".$i." ".$recordId." ".$newtime);
					$db->Query("update `@#_member_go_record` set `time` = $newtime where id = $recordId");
					generate_log("update `@#_member_go_record` set `time` = $newtime where id = $recordId");
					generate_log("now adjust time for newly added record.");
					generate_log("update `@#_member_go_record` set `time` = $newtime where id = $recordId");
				}

				$timeac2 = getTimeInMilliSec();
				generate_log_callback("phase c2 cost time:".($timeac2 - $timeac1)." milli seconds.");

				$tocode = System::load_app_class("tocode","pay");
				$tocode->shop = $shop;
				$tocode->run_tocode($time,50,$shop['canyurenshu'],$shop);
				generate_log("new code is:".$tocode->go_code);
				//$tocode->updateTime($last_info['id'], $str_t_time);
				//regenerate the content
				$content = addslashes($tocode->go_content);
				$timeac3 = getTimeInMilliSec();
				//generate_log_callback("phase c3 cost time:".($timeac3 - $timeac2)." milli seconds.");				
			}
		}
		$code = $chosenCode;

		generate_log("zhiding cost time:".(getTimeInMilliSec() - $timeaa)." milli seconds.");
	}
	$timec = getTimeInMilliSec();
	//generate_log_callback("in pay_insert_shop for".$shop['id']." cost time 3: ".($timec - $timeb)." milli seconds.");

	$u_go_info = $db->GetOne("select * from `@#_member_go_record` where `shopid` = '$shop[id]' and `goucode` LIKE  '%$code%'");
	$u_info = $db->GetOne("select uid,username,email,mobile,img from `@#_member` where `uid` = '$u_go_info[uid]'");

	generate_log_product('user info:'.$u_info['uid']." shop id: ".$shop['id']);

	//更新商品
	$query = true;
	if($u_info){
		$u_info['username'] = _htmtocode($u_info['username']);
		$q_user = serialize($u_info);
		$gtimes = (int)System::load_sys_config('system','goods_end_time');
		if($gtimes == 0 || $gtimes == 1){
			$q_showtime = 'N';
		}else{
			$q_showtime = 'Y';
		}

		$sqlss = "UPDATE `@#_goods_item` SET
							`canyurenshu`=`zongrenshu`,
							`shenyurenshu` = '0',
							`q_uid` = '$u_info[uid]',
							`q_user` = '$q_user',
							`q_user_code` = '$code',
							`q_content`	= '$content',
							`q_counttime` ='$counttime',
							`q_end_time` = '$time',
							`q_showtime` = '$q_showtime',
							`q_status` = '2'
							 where `id` = '$shop[id]'";

		$q = $db->Query($sqlss);
		if(!$q)$query = false;

		//now save the q_content to text
		writeContentById($shop['id'], "q_content_", $content);
		
	$timed = getTimeInMilliSec();
	//generate_log_callback("in pay_insert_shop for".$shop['id']." cost time 4: ".($timed - $timec)." milli seconds.");

		//如果没有中奖短信就强制在发送一遍--E
		if($q){
			$timed1 = getTimeInMilliSec();

			$q = $db->Query("UPDATE `@#_member_go_record` SET `huode` = '$code' where `id` = '$u_go_info[id]' and `code` = '$u_go_info[code]' and `uid` = '$u_go_info[uid]' and `shopid` = '$shop[id]' and `shopqishu` = '$shop[qishu]'");
			generate_log_callback("in pay_insert_shop for".$shop['id']." cost time 41: ".(getTimeInMilliSec() - $timed1)." milli seconds.");

			if(!$q) {
				$query = false;
			}else{
				$type = System::load_sys_config("send","type");
				generate_log_product("msg send type:".$type);
				if ($type==2) {
					$uid = $u_info['uid'];
					$mobile = $u_info['mobile'];
					if(!empty($mobile)){
						send_mobile_shop_code($mobile,$uid,$code);
					}
				}else{
					$time0 = getTimeInMilliSec();
					$post_arr= array("uid"=>$u_info['uid'],"gid"=>$shop['id'],"send"=>1);
					_g_triggerRequest(WEB_PATH.'/api/send/send_shop_code',false,$post_arr);
					$param = json_encode($post_arr);
					//$db->Query("INSERT `@#_async_task` (`name`, `param`, `over`) VALUES ('send_msg', '$param', 0)");
	 				//generate_log_callback("time cost in send_mobile_shop_code:".(getTimeInMilliSec() - $time0)." milli seconds.");
				}
			}
		}else{
			$query =  false;
		}
	}else{

		$query =  false;

	}

	$timee = getTimeInMilliSec();
	//generate_log_callback("in pay_insert_shop for".$shop['id']." cost time 5: ".($timee - $timed)." milli seconds.");
	

	if($query){
		if($shop['qishu'] < $shop['maxqishu']){
			$maxinfo = $db->GetOne("select * from `@#_shoplist` where `sid` = '$shop[sid]' order by `id` DESC LIMIT 1");
			if(!$maxinfo){
				$maxinfo=array("qishu"=>$shop['qishu']);
			}

			System::load_app_fun("content",G_ADMIN_DIR);
			//make this operation an asynchronous operation
			$intall = content_add_shop_install($maxinfo,false);

			if(!$intall) return $query;
		}
	}
	$timef = getTimeInMilliSec();
	//generate_log_callback("in pay_insert_shop for".$shop['id']." cost time 6: ".($timef - $timee)." milli seconds.");
  $costtime = getTimeInMilliSec() - $currentTime;
  generate_log_callback("in disclosure, cost time: ".$costtime."milli seconds");
	return $query;
}


function pay_go_fund($go_number=null){

	if(!$go_number)return true;

	$db = System::load_sys_class("model");

	$fund = $db->GetOne("select * from `@#_fund` where 1");

	if($fund && $fund['fund_off']){

		$money = $fund['fund_money'] * $go_number + $fund['fund_count_money'];

		return $db->Query("UPDATE `@#_fund` SET `fund_count_money` = '$money'");

	}else{

		return true;

	}

}





/*

	用户佣金

	uid 		用户id

	dingdancode	@订单号

*/

function pay_go_yongjin($uid=null,$dingdancode=null){

	if(!$uid || !$dingdancode)return true;

	$db = System::load_sys_class("model");$time=time();

	$config = System::load_app_config("user_fufen",'','member');//积分/经验/佣金

	$yesyaoqing=$db->GetOne("SELECT `yaoqing` FROM `@#_member` WHERE `uid`='$uid'");

	if($yesyaoqing['yaoqing']){

		$yongjin=$config['fufen_yongjin']; //每一元返回的佣金

	}else{

		return true;

	}

	$yongjin = floatval(substr(sprintf("%.3f",$yongjin), 0, -1));

	$gorecode=$db->GetList("SELECT * FROM `@#_member_go_record` WHERE `code`='$dingdancode'");

	foreach($gorecode as $val){

		$y_money=$val['moneycount'] * $yongjin;

		$content="(第".$val['shopqishu']."期)".$val['shopname'];

		$db->Query("INSERT INTO `@#_member_recodes`(`uid`,`type`,`content`,`shopid`,`money`,`ygmoney`,`time`)VALUES('$uid','1','$content','$val[shopid]','$y_money','$val[moneycount]','$time' )");

	}



}



?>




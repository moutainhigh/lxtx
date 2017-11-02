<?php
defined('G_IN_SYSTEM')or exit('No permission resources.');
System::load_app_class('base','member','no');
System::load_app_fun('my');
System::load_app_fun('user');
System::load_sys_fun('user');

class async extends base {
  public function __construct() {
    parent::__construct();
    $this->db=System::load_sys_class('model');
  }

  //get the list of tasks that need to be executed in parallel mode
  public function listtask() {
    $list = $this->db->GetList("select * from `@#_async_task` where over = 0 and parallel = 1 order by id asc limit 0, 100 for update");
    $result = array("count" => count($list), "list" => $list);
    echo json_encode($result);
  }

  //get the list of tasks that need to be executed in single-thread mode
  //here we don't need th parallel field cause all tasks will be executed in single thread mode now
  public function listblockingtasks() {
    $list = $this->db->GetList("select * from `@#_async_task` where over = 0 order by id asc limit 0, 100 for update");
    $result = array("count" => count($list), "list" => $list);
    echo json_encode($result);    
  }

  //mark a task as finished
  public function closetask() {
    $resArr = array();
    $id = intval($_REQUEST['id']);
    if ($id > 0)
    {
      $this->db->Autocommit_start();
      $task = $this->db->GetOne("select * from `@#_async_task` where `id` = ".intval($id)." for update");
      if ($task['over'] == 0)
      {
        $this->db->Query("UPDATE `@#_async_task` set over = 1 where `id` = ".intval($id));
        $resArr['code'] = 1;
        $this->db->Autocommit_commit();
      } else {
        $this->db->Autocommit_rollback();
        $resArr['code'] = 0;
      }
    } else {
      $resArr['code'] = 0;
    }

    echo json_encode($resArr);
  }

  public function send_msg() {
    $this->db->Autocommit_start();
    $taskId = $_REQUEST['task_id'];
    $task = $this->db->GetOne("select * from `@#_async_task` where `id` = ".intval($taskId)." for update");

    $post_arr = array("uid"=>$_POST['uid'], "gid"=>$_POST['gid'], "send"=>1);
    generate_log_callback('callback for sending msg, url is: '.WEB_PATH.'/api/send/send_shop_code');
    _g_triggerRequest(WEB_PATH.'/api/send/send_shop_code', false, $post_arr);
    $this->db->Query("update `@#_async_task` set over = 1 where `id` = $taskId");
    $this->db->Autocommit_commit();
    $resultArr = array("code" => 1);
    echo json_encode($resultArr); 
  }

  //update ip address
  public function update_ip() {
    $this->db->Autocommit_start();
    $taskId = $_REQUEST['task_id'];
    $task = $this->db->GetOne("select * from `@#_async_task` where `id` = ".intval($taskId)." for update");

    /*$post_arr = array("uid"=>$_POST['uid'], "gid"=>$_POST['gid'], "send"=>1);
    generate_log_callback('callback for sending msg, url is: '.WEB_PATH.'/api/send/send_shop_code');
    _g_triggerRequest(WEB_PATH.'/api/send/send_shop_code', false, $post_arr);*/
    $record_id = $_POST['record_id'];
    $ip = $_POST['ip'];
    $ipaddr = _get_ip_addr($ip);
    //update ip addr
    $this->db->Query("update `@#_member_go_record` set `ip` = '$ipaddr' where `id` = ".$record_id);
    //now update the async task
    $this->db->Query("update `@#_async_task` set over = 1 where `id` = $taskId");
    $this->db->Autocommit_commit();
    $resultArr = array("code" => 1);
    echo json_encode($resultArr);    
  }


  //whether the tasked will be executed in single mode
  public function lottery_single_mode() {

  }

  //check whether now is the good time for lottery
  public function good_for_lottery() {
    $time1 = getTimeInMilliSec();
    //$currentTaskId = $_REQUEST['task_id'];
    //fetch the previous task that runs in single mode
    $taskId = intval($_REQUEST['task_id']) - 1;
    if ($taskId <= 0) { //it's the first task
      echo json_encode(array("status" => 1));
    }

    $task = $this->db->GetOne("select param from `@#_async_task` where id = $taskId");
    $param = json_decode($task['param']);

    $shop_id = $_REQUEST['shop_id'];
    $shop = $this->db->GetOne("select * from `@#_shoplist` where id = $shop_id");
    
    if (!handledSpecially($shop)) {
      //no need for special handling
      generate_log_callback(" no special operation for task:".$_REQUEST['task_id']);
      echo json_encode(array("status" => 1)); 
    } else {
      $itemInfo = $this->db->GetOne("select q_end_time from `@#_goods_item` where id = $param->shop_id"); 
      generate_log_callback("select q_end_time from `@#_goods_item` where id = $param->shop_id");
      $endTime = $itemInfo['q_end_time'];
      if (!$endTime || $endTime == '') {
        sleep(1);
        $itemInfo = $this->db->GetOne("select q_end_time from `@#_goods_item` where id = $param->shop_id"); 
        $endTime = $itemInfo['q_end_time'];
        generate_log_callback("retrieve q_end_time again for ".$param->shop_id);
      }

      $time1 = getTimeInMilliSec();
      $cnt = $this->db->GetCount("select count(*) from `@#_member_go_record` where time > '$endTime'");
      generate_log_callback("select count(*) from `go_member_go_record` where time > '$endTime'  ".$cnt);

      if ($cnt >= 50) {
        echo json_encode(array("status" => 1));
      } else {
        echo json_encode(array("status" => 0));
      }      
    }
  }

  public function getReturnJson($success) {
    if ($success) {
      return json_encode(array("status" => 1));
    } else {
      return json_encode(array("status" => 0));
    }
  }

  //gather the number of trade records within one minute and save into db
  //type 1 means all records, type 2 means records created for human 
  public function gather_record_count_minute() {
    $curtime = sprintf("%.3f",microtime(true));
    $starttime = $curtime - 60; //the time gap is 60 seconds

    $recordCnt = $this->db->GetCount("select count(*) from `@#_member_go_record` where time between '$starttime' and '$curtime'");
    $humanRecordCnt = $this->db->GetCount("select count(*) from `@#_member_go_record` a left join `@#_member` b on a.uid = b.uid where a.time between '$starttime' and '$curtime' and b.auto_user <> 1;");

    $resQuery1 = $this->db->Query("insert into `@#_stats_trade`(`time`, `count`, `type`, `period`) values($curtime, $recordCnt, 1, 1)");
    $resQuery2 = $this->db->Query("insert into `@#_stats_trade`(`time`, `count`, `type`, `period`) values($curtime, $humanRecordCnt, 2, 1)");
    if ($resQuery1 && $resQuery2)
    {
      echo $this->getReturnJson(true);
    } else {
      echo $this->getReturnJson(false);
    }
  }

  //gather the number of async tasks in current time position and save it to db 
  public function gather_task_count() {
    $curtime = sprintf("%.3f",microtime(true));
    $taskCnt = $this->db->GetCount("select count(*) from `@#_async_task` where over = 0");
    $resQuery = $this->db->Query("insert into `@#_stats_trade`(`time`, `count`, `type`, `period`) values($curtime, $taskCnt, 3, 0)");

    if ($resQuery) {
      echo $this->getReturnJson(true);
    } else {
      echo $this->getReturnJson(false);
    }
  }  

  public function prepareGoodsData() {
    echo G_ADMIN_DIR;
    System::load_app_fun("content",G_ADMIN_DIR);    
    //create goods instances and save into go_goods_item 
    //create the code for the orders 
    $goodsList = $this->db->GetList("select id, zongrenshu from `@#_goods_item` where canyurenshu = 0");
    foreach ($goodsList as $key => $goods) {
      # code...
      $zongrenshu = $goods['zongrenshu'];
      $goodsItemId = $goods['id'];
      content_get_go_codes($zongrenshu,3000,$goodsItemId);
    }
  }

  public function prepareCodesForSpecifiedGoods() {
    echo G_ADMIN_DIR;
    System::load_app_fun("content",G_ADMIN_DIR);    
    //create goods instances and save into go_goods_item 
    //create the code for the orders 
    $idlist = $_REQUEST['ids'];
    $goodsList = $this->db->GetList("select id, zongrenshu from `@#_goods_item` where canyurenshu = 0 and id in ($idlist)");
    foreach ($goodsList as $key => $goods) {
      # code...
      $zongrenshu = $goods['zongrenshu'];
      $goodsItemId = $goods['id'];
      content_get_go_codes($zongrenshu,3000,$goodsItemId);
    }
  }

  public function sendSMSMsg() {
    $mobiles = $_REQUEST['mobiles'];
    $content = $_REQUEST['content'];
    $secret = $_REQUEST['secret'];
    if ($secret == 'beijing') {
      echo json_encode(_sendmobile($mobiles, $content));  
    } else {
      echo "incorrect parameters!";
    }
  }

  public function prepareGoodsData2() {
    echo G_ADMIN_DIR;
    System::load_app_fun("content",G_ADMIN_DIR);    
    //create goods instances and save into go_goods_item 
    //create the code for the orders 
    $goodsList = $this->db->GetList("select id, zongrenshu from `@#_goods_item` where sid between 194 and 201");
    foreach ($goodsList as $key => $goods) {
      # code...
      $zongrenshu = $goods['zongrenshu'];
      $goodsItemId = $goods['id'];
      content_get_go_codes($zongrenshu,3000,$goodsItemId);
    }
  }

  public function updatePrice() {
    $goodsList = $this->db->GetList("select id, money from `@#_goods` where cateid = 153");
    foreach ($goodsList as $key => $goods) {
      # code...
      $goodsId = $goods['id'];
      $money = $goods['money'];
      $money = intval($money/5)*5;
      $zongrenshu = intval($money/5);

      $this->db->Query("update `@#_goods` set money = $money, zongrenshu = $zongrenshu, def_renshu = $zongrenshu where  id = $goodsId");
      $this->db->Query("update `@#_goods_item` set zongrenshu = $zongrenshu, shenyurenshu = $zongrenshu where  id = $goodsId");
    }
  }

  public function updateShaidanInfo() {
    $sql = "select count(*) as cnt, b.sid from `@#_shaidan` a left join `@#_goods_item` b on a.sd_shopid = b.id group by b.sid";
    $postStatList = $this->db->GetList($sql);
    foreach ($postStatList as $key => $post) {
      $cnt = $post['cnt'];
      $sid = $post['sid'];

     if (empty($sid) || $sid < 0)
     {
      continue;
     }
      echo $cnt."  ".$sid."<br/>";
      $query = "select id, q_uid from `@#_goods_item` where sid = $sid and q_uid > 0";
      echo $query."<br/>";
      $goodsList = $this->db->GetList($query);
      $idlist = array();
      $idmap = array();

      foreach ($goodsList as $key => $goods) {
        array_push($idlist, $goods['id']);
        $idmap[$goods['id']] = $goods['q_uid'];
      }
      //var_dump($idlist);
      //var_dump($idmap);

      $selectedIdx = array_rand($idlist, $cnt);
      //var_dump($selectedIdx);
      
      $selectedIds = array();
      foreach ($selectedIdx as $key => $ind) {
        array_push($selectedIds, $idlist[$ind]);
      }

      //var_dump($selectedIds);

      $q = "select sd_id from `@#_shaidan` a left join `@#_goods_item` b on a.sd_shopid = b.id where b.sid = $sid";
      $idList = $this->db->GetList($q);
      $counter = 0;
      foreach ($idList as $key => $record) {
        echo $record['sd_id']."<br/>";
        $selectedGoodsId = $selectedIds[$counter];
        $selectedGoodsUid = $idmap[$selectedGoodsId];
        $selectedSdId = $record['sd_id'];
        $update = "update `@#_shaidan` set sd_userid = $selectedGoodsUid, sd_shopid = $selectedGoodsId where sd_id = $selectedSdId";
        //echo $update."<br/>";
        $this->db->query($update);
        $counter++;  
      }
    }
  }

  public function updateShaidanTime() {
    $postList = $this->db->GetList("select a.sd_shopid, a.sd_id, b.q_end_time from `@#_shaidan` a left join `@#_goods_item` b on a.sd_shopid = b.id");
    
    foreach ($postList as $key => $post) {
      $shopId = $post['sd_shopid'];
      $sdId = $post['sd_id'];
      //echo $shopId." ".$sdId."  ".$post['q_end_time']."<br/>";
      if (!empty($post['q_end_time'])) {
        //echo "1234";
        $diff = rand(4,7)*86400 + rand(3600, 43200);
        $time = intval(substr($post['q_end_time'], 0, 10)) + $diff;
        //echo $time;  
        //echo "sd_id is: ".$sdId.$time."<br/>";*/
        $this->db->Query("update `@#_shaidan` set sd_time = $time where sd_id = $sdId");
      } else {
        echo "123";
      }
    }
    echo count($postList);
  }

  public function jsonptest() {
    echo $_REQUEST['callback']."(".$this->getReturnJson(true).")";
  }

  public function testMemcache() {
    $cache = System::load_sys_class('mcache');   
    $cache->set("key", "hello");
    echo $cache->get("key");

    $cache->increment("counter_1", 1);
    echo $cache->get("counter_1");
  }

  public function getbalance() {
    $openid = $_REQUEST['openid'];
    if ($openid) {
      $sql = "select * from ";  

    }
  }

  public function createnewgoods() {
          System::load_app_fun("content",G_ADMIN_DIR);
      $goodslist = $this->db->GetList("select * from `@#_shoplist` where id in (select id from `@#_goods_item` where qishu = 1000);");
      foreach ($goodslist as $key => $goods) {
        # code...
        content_add_shop_install($goods, false);
      }
  }

  public function getuserinfo() {
    $mobile = $_GET['mobile'];
    $day = $_GET['day'];
    $token = $_GET['token'];
    $calculatedToken = md5($mobile.$day.'jishoubao');
    $today = date('Ymd', time());
    //$resultArr = array();
    if (!$mobile || !$day || $day != $today || !$token || $token != $calculatedToken) {
      $resultArr = array("code", -1);
      echo json_encode($resultArr);
    } 

    $userinfo = $this->db->GetOne("select * from `@#_member` where mobile = $mobile");
    $uid = $userinfo['uid'];
    $tasklist = $this->db->GetList("select id as taskId, status, amount, create_time from `@#_goods_card_task` where uid = $uid and (status = 0  or status = 3)and chnno is not null and `type` = 'alipay'");

    foreach ($tasklist as $key => $value) {
      $value['create_time'] = date('Ymd H:i:s', $value['create_time']);
      $tasklist[$key] = $value;
    }

    $resultArr = array("mobile" => $mobile, "code" => 1, "tasklist" => $tasklist);
    echo json_encode($resultArr);
  }

  public function update_aliexchange_order() {
    $mobile = $_GET['mobile'];
    $status = $_GET['status'];
    $order_id = $_GET['order_id'];
    $task_id = $_GET['task_id'];
    $token = $_GET['token'];
    $calculatedToken = md5($mobile.$order_id.$task_id."jishoubao");
    echo $mobile.$order_id.$task_id."jishoubao"."</br>";
    echo $calculatedToken."</br>";
    if (!$mobile || !$status || !$order_id || !$task_id || !$token || $token != $calculatedToken) {
      $resultArr = array("code", -1);
      echo json_encode($resultArr); 
    }

    $status = intval($status);
    if ($status != 1 && $status != 2 && $status != 3) {
      $resultArr = array("code", -2, "message", "invalid status");
      echo json_encode($resultArr);   
    }

    if ($status == 1) {
      echo "update `@#_goods_card_task` set order_id = $order_id, status = $status where `id` = $task_id"."</br>";
      $result = $this->db->Query("update `@#_goods_card_task` set order_id = '$order_id', status = $status where `id` = $task_id");
      if ($result) {
        $resultArr = array("code", 1, "message", "task updated successfully");
        echo json_encode($resultArr);    
      } else {
        $resultArr = array("code", -1, "message", "invalid parameters");
        echo json_encode($resultArr);    
      }
    } else if ($status == 2 || $status == 3) {
      $f_time = time();
      $result = $this->db->Query("update `@#_goods_card_task` set status = $status, finish_time = $f_time where `id` = $task_id");
      if ($result) {
        $resultArr = array("code", 1, "message", "task updated successfully");
        echo json_encode($resultArr);    
      } else {
        $resultArr = array("code", -1, "message", "invalid parameters");
        echo json_encode($resultArr);    
      }     
    }
  }

  public function updatebalance() {

  }

  public function sendsms() {
    $mobile = $_REQUEST['mobile'];
    $code = $_REQUEST['code'];
    send_valid_code($code, $mobile);
  }


}
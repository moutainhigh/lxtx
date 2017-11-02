<?php
defined('G_IN_SYSTEM')or exit('no');
System::load_app_class('shipinadmin',G_SHIPIN_DIR,'no');
System::load_sys_fun('spkj');
System::load_sys_fun('user');
class shipin extends shipinadmin {
	private $db;
	protected $ment;
	public function __construct(){
		parent::__construct();
		$this->db=System::load_sys_class("model");
		
	}
	public function test(){
		echo '1111111';
	}
    //退出登陆
	   public function cook_end(){
        _setcookie("uid","",time()-3600);
        _setcookie("ushell","",time()-3600);  
              
        _message("退出成功",'');
      
       
    }

	public function index(){
        $uid=_getcookie('uid');
        $uid=_encrypt($uid,'DECODE');
        // _sendmobile('13227423540','您的中奖码为120120120120');
      $username=get_user_name($uid);
        if($uid=='18066'){$is='block';}else{ $is='none';};
        $jilu=$this->db->Getlist("select * from `go_member_go_record_shipin` order by `id` DESC LIMIT 0,20");
        foreach ($jilu as $key => $value) {
                $jilu[$key]['shopname1']=mb_substr( $value['shopname'], 0, 15, 'utf-8' ) ;
            }
			
		include $this->tpl(ROUTE_M,'index');
	}
	
	//获取最新揭晓商品
	public function getJiexiao(){
		$this->db=System::load_sys_class("model");
		$sql="SELECT * FROM `@#_shoplist_shipin` WHERE `q_uid` IS NULL ORDER BY `id` ASC LIMIT 5";
		$list=$this->db->GetList($sql);
		if($list){
			echo json_encode($list);exit;
		}else{
			echo json_encode('');
		}
	}
	
    //获取奖品信息
    public function getJpinfo(){
    	$this->db=System::load_sys_class("model");
    	$id=isset($_POST['id'])?$_POST['id']:'';
    	$sql="SELECT * FROM `@#_shoplist_shipin` WHERE `q_uid` IS NULL AND `id`='$id'";
    	$list=$this->db->GetList($sql);
    	if($list){
    		echo json_encode($list);exit;
    	}else{
				echo json_encode('');
		}
    }
        //获取最新一期的奖品信息
    public function getNewinfo(){
        $this->db=System::load_sys_class("model");
        
        $sql="SELECT `q_uid`,`q_user_code`,`title`,`qishu` FROM `@#_shoplist_shipin` WHERE `q_uid` !=''  AND `q_uid` IS NOT NULL AND `cp_num` IS NOT NULL ORDER BY q_end_time DESC limit 1";
        $list=$this->db->GetOne($sql);
		$list1=array();
		$list1=$list;
		
        $list1['username']=get_user_name($list['q_uid']);
        if($list){
            echo json_encode($list1);exit;
        }else{
                echo json_encode('100');
        }
    }
    //根据商品id获取对应的评论（昨天+今天）
    public function getPinglun(){
    	$this->db=System::load_sys_class("model");
    	$jpid=isset($_POST['jpid'])?$_POST['jpid']:'';//获取奖品id
    	//$sql="SELECT * FROM `@#_shipin_pinglun` WHERE `jpid`='$jpid' and UNIX_TIMESTAMP(`time`) between '".mktime(0,0,0,date('m'),date('d')-1,date('Y'))."' and '".(mktime(0,0,0,date('m'),date('d')+1,date('Y'))-1)."' order by `time` asc";
    	$sql="SELECT * FROM `@#_shipin_pinglun` WHERE `jpid`='$jpid' order by `time` asc";
    	$list=$this->db->GetList($sql);
    	for($i=0;$i<count($list);$i++){
    		$list[$i]['username']=getUsername($list[$i]['uid']);
    	}
    	if($list){
    		echo json_encode($list);exit;
    		//echo json_encode($sql);exit;
    	}else{
    		
    		echo json_encode('');
    	}
    	
    }
    
     public function getRecords(){
        $this->db=System::load_sys_class("model");
        $id=isset($_POST['id'])?$_POST['id']:'';//获取奖品id
        if($id){
            $sql="SELECT * FROM `@#_member_go_record_shipin` WHERE `status`='已付款,未发货,未完成' and `shopid`='$id' ORDER BY `time` DESC LIMIT 20";
            $list=$this->db->GetList($sql);
            foreach ($list as $key => $value) {
                $list[$key]['shopname1']=mb_substr( $value['shopname'], 0, 15, 'utf-8' ) ;
            }
            if($list){
                echo json_encode($list);exit;
            }else{
                echo json_encode('');exit;
            }
        }else{
            echo json_encode('');exit;
        }
        
    }
   
    public function getUid(){
    	$this->db=System::load_sys_class("model");
    	$uid=isset($_POST['uid'])?$_POST['uid']:'';
    	$uid=_encrypt($uid,'DECODE');
    	echo json_encode($uid);exit;
    }
   
	
    //发送评论
    public function sendPinglun(){
    	$this->db=System::load_sys_class("model");
    	$uid=$_POST['uid'];
    	$jpid=$_POST['jpid'];
    	$content=$_POST['content'];
    	date_default_timezone_set('Asia/Shanghai');
    	$time=date('Y-m-d H:i:s');
    	$sql="INSERT INTO `go_shipin_pinglun` (`uid`,`jpid`,`content`,`infotype`,`time`) VALUES('$uid','$jpid','$content',1,'$time')";
    	$list=$this->db->query($sql);
    	if($list){
    		echo json_encode('1');exit;
    	}else{
    		echo json_encode('');exit;
    	}
    }
	
}
?>
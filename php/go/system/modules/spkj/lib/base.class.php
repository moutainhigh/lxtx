<?php 

defined('G_IN_SYSTEM')or exit('No permission resources.');
//define("MEMBER",true);

class base extends SystemAction {
	protected $userinfo=NULL;
	public function __construct(){		
	
		if(ROUTE_M=='member' && ROUTE_C=='user' && ROUTE_A=='login'){
			return;
		}
		if(ROUTE_M=='member' && ROUTE_C=='user' && ROUTE_A=='register'){
			return;
		}
		$uid=intval(_encrypt(_getcookie("uid"),'DECODE'));		
		$utype=_encrypt(_getcookie("utype"),'DECODE');
		$ushell=_encrypt(_getcookie("ushell"),'DECODE');
		//echo "<script>alert('aaa$uid');</script>";
		//echo "<script>alert('bbb$utype');</script>";
		//echo "<script>alert('ccc$ushell');</script>";exit;
		//if($utype===NULL)$this->HeaderLogin();echo "<script>alert('错了aaaaaaaaaaaaaaaaa$utype');</script>";exit;
		if(!$uid){
			echo "<script>alert('错了1');</script>";
			$this->HeaderLogin();
		}
			
		$this->userinfo=$this->DB()->GetOne("SELECT * from `@#_member` where `uid` = '$uid'");
		if(!$this->userinfo){
			echo "<script>alert('错了2');</script>";
			$this->HeaderLogin();
		}
			
	
		//$shell=md5($this->userinfo['uid'].$this->userinfo['password'].$this->userinfo[$utype]);		
		$shell=md5($this->userinfo['uid'].$this->userinfo['password'].$this->userinfo['mobile'].$this->userinfo['email']);		
		if($ushell!=$shell){
			echo "<script>alert('错了3');</script>";
			$this->HeaderLogin();
		}
	}
	
	private function HeaderLogin(){
		//echo "<script>alert('错了');</script>";
		_message("你还未登录，无权限访问该页！",WEB_PATH."/member/user/login",3);
	}
	
}
?>
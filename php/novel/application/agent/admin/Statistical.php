<?php
/**
 * User: pinbo
 * Date: 2017/4/8
 * Time: 上午9:50
 */
//后台访问控制器
namespace app\agent\admin;//修改ann为模块名

use app\admin\controller\Admin;
use app\common\builder\ZBuilder;
use app\agent\model\Novel as NovelModel;
use think\Db;

class Statistical extends Admin{
	

	//支付统计
   public function paylog()
	{

		$js=<<<EOF
		<script>
		    $(function() {
		        var i = 59;
		        setInterval(function() {
		            $("#timeRefresh").html(i);
		            if (i == 1) {
		                location.reload();
		                i = 0;
		            }
		            i--;
		        }, 1000);
		    });
		</script>
EOF;
		$payday=$this->day();

		//金额天，昨天，月，所有
		$pay_today=$this->pay_day(UID,"today");
		$pay_yesterday=$this->pay_day(UID,"yesterday");
		$pay_month=$this->pay_day(UID,"month");
		$pay_all=$this->pay_day(UID,"all");
		
		$this->assign('pay_today',$pay_today);
		$this->assign('pay_yesterday',$pay_yesterday);
		$this->assign('pay_month',$pay_month);
		$this->assign('pay_all',$pay_all);
		
		$data_list=DB::table('ien_day_money')->where('uid',UID)->limit(0,30)->order('addtime desc')->select();
		
		return ZBuilder::make('table')
			->hideCheckbox()
			->setPageTitle('近30天充值统计')
            ->addColumns([ // 批量添加数据列
            	['addtime','日期','date'],
            	['money','充值金额'],
            	['pt','普通充值','callback', function($data_list){
            		if($data_list['ptuser']>0)
            		{
					$renjun=($data_list['ptmoney']/$data_list['ptuser']);
					}
					else
					{
						$renjun=0;
					}
    	return "<span style='color:#337AB7'>￥：".$data_list['ptmoney']."</span><br>充值人数：".$data_list['ptuser']."，&nbsp人均：￥".$renjun; 
    }, '__data__'],
				

				['ptbishu','普通充值支付订单数','callback', function($data_list){
					if($data_list['ptnot']>0)
            		{
					$renjun=round(($data_list['ptnum']/$data_list['ptnot'])*100,2);
					}
					else
					{
						$renjun=0;
					}

    	return "<span style='color:#337AB7'>".$data_list['ptnum']."&nbsp;笔</span><br>".$data_list['ptnot']."&nbsp; 笔未支付，完成率：".$renjun."%"; 
    }, '__data__'],
				
				['vip','年费VIP会员','callback', function($data_list){
				            		if($data_list['vipuser']>0)
				            		{
									$renjun=($data_list['vipmoney']/$data_list['vipuser']);
									}
									else
									{
										$renjun=0;
									}
				    	return "<span style='color:#337AB7'>￥：".$data_list['vipmoney']."</span><br>充值人数：".$data_list['vipuser']."，&nbsp人均：￥".$renjun; 
				    }, '__data__'],

				    ['vipbishu','年费VIP会员支付订单数','callback', function($data_list){
					if($data_list['vipnot']>0)
            		{
					$renjun=round(($data_list['vipnum']/$data_list['vipnot'])*100,2);
					}
					else
					{
						$renjun=0;
					}

    	return "<span style='color:#337AB7'>".$data_list['vipnum']."&nbsp;笔</span><br>".$data_list['vipnot']."&nbsp; 笔未支付，完成率：".$renjun."%"; 
    }, '__data__'],
				
    			



            ])
            ->setExtraJs($js)
         ->setRowList($data_list)
		->fetch('paylog');
	
	}
	public function day()
	{
		if(UID)
		{
			//当天全部充值
			$data['money']=(int)DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->whereTime('paytime', 'today')->where('did',UID)->sum('money');
			//当天普通充值全部
			$data['ptmoney']=(int)DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->where('paytype',2)->whereTime('paytime', 'today')->where('did',UID)->sum('money');
			//当天VIP充值全部
			$data['vipmoney']=(int)DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->where('paytype',1)->whereTime('paytime', 'today')->where('did',UID)->sum('money');
			//普通充值笔数
			$data['ptnum']=DB::table("ien_pay_log")->where('paytype',2)->where('isout','NEQ','1')->whereTime('paytime', 'today')->where('did',UID)->count('id');
			//VIP充值笔数
			$data['vipnum']=DB::table("ien_pay_log")->where('paytype',1)->where('isout','NEQ','1')->whereTime('paytime', 'today')->where('did',UID)->count('id');
			//普通未充值笔数
			$data['ptnot']=DB::table("ien_pay_log")->where('status',0)->where('isout','NEQ','1')->where('paytype',2)->whereTime('addtime', 'today')->where('did',UID)->count('id');
			//VIP未充值笔数
			$data['vipnot']=DB::table("ien_pay_log")->where('status',0)->where('isout','NEQ','1')->where('paytype',1)->whereTime('addtime', 'today')->where('did',UID)->count('id');
			//普通充值人数
			$data['ptuser']=DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->distinct(true)->field('uid')->where('paytype',2)->whereTime('paytime', 'today')->where('did',UID)->count('uid');
			//vip充值人数
			$data['vipuser']=DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->distinct(true)->field('uid')->where('paytype',2)->whereTime('paytime', 'today')->where('did',UID)->count('uid');

			$monthlog=DB::table("ien_day_money")->whereTime('addtime', 'today')->where('uid',UID)->find();

			
			if(empty($monthlog))
			{	
				$data['uid']=UID;
				$data['addtime']=time();
				Db::table('ien_day_money')->insert($data);
			}
			else
			{

				$data['uid']=UID;
				$data['addtime']=time();
				Db::table('ien_day_money')->where('id',$monthlog['id'])->update($data);
			}
	
		}

	} 

	public function allpay($id=null){
		
		ignore_user_abort(true); // 后台运行
		set_time_limit(0); // 取消脚本运行时间的超时上限

		if(!empty($id))
		{
			$map=['id'=>$id];
		}
		else
		{
			$map="";
		}
		$user=DB::table('ien_admin_user')->where($map)->where('role','<>','3')->select();
		foreach($user as $key=>$value)
		{
			$paylog=DB::query("select * from ien_pay_log where did=".$value['id']." and paytype <> 0 group by FROM_UNIXTIME(addtime,'%Y%m%d')");
			if(!empty($paylog))
			{
			foreach ($paylog as $k => $valuea) {
					/*start*/
					$datey=date('Y',$valuea['addtime']);
					$datem=date('m',$valuea['addtime']);
					$dated=date('d',$valuea['addtime']);
					$beginToday=mktime(0,0,0,$datem,$dated,$datey);
					$endToday=mktime(0,0,0,$datem,$dated+1,$datey)-1;
		
					
					//当天全部充值
			$data['money']=(int)DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->whereTime('paytime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->sum('money');
			//当天普通充值全部
			$data['ptmoney']=(int)DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->where('paytype',2)->whereTime('paytime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->sum('money');
			//当天VIP充值全部
			$data['vipmoney']=(int)DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->where('paytype',1)->whereTime('paytime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->sum('money');
			//普通充值笔数
			$data['ptnum']=DB::table("ien_pay_log")->where('paytype',2)->where('isout','NEQ','1')->whereTime('paytime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->count('id');
			//VIP充值笔数
			$data['vipnum']=DB::table("ien_pay_log")->where('paytype',1)->where('isout','NEQ','1')->whereTime('paytime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->count('id');
			//普通未充值笔数
			$data['ptnot']=DB::table("ien_pay_log")->where('status',0)->where('isout','NEQ','1')->where('paytype',2)->whereTime('addtime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->count('id');
			//VIP未充值笔数
			$data['vipnot']=DB::table("ien_pay_log")->where('status',0)->where('isout','NEQ','1')->where('paytype',1)->whereTime('addtime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->count('id');
			//普通充值人数
			$data['ptuser']=DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->distinct(true)->field('uid')->where('paytype',2)->whereTime('paytime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->count('uid');
			//vip充值人数
			$data['vipuser']=DB::table("ien_pay_log")->where('status',1)->where('isout','NEQ','1')->distinct(true)->field('uid')->where('paytype',2)->whereTime('paytime', 'between',[$beginToday,$endToday])->where('did',$value['id'])->count('uid');

			$monthlog=DB::table("ien_day_money")->whereTime('addtime', 'between',[$beginToday,$endToday])->where('uid',$value['id'])->find();
					
					if(empty($monthlog))
					{	
						$data['uid']=$value['id'];
						$data['addtime']=$beginToday;
						Db::table('ien_day_money')->insert($data);
					}
					else
					{
						$data['uid']=$value['id'];
						$data['addtime']=$beginToday;
						Db::table('ien_day_money')->where('id',$monthlog['id'])->update($data);
					}

					/*end*/
					
				}
				

				echo ("代理商ID:".$value['id']."更新完成<br/>");
				file_put_contents("paylog.txt","代理商ID:".$value['id']."更新完成\r\n",FILE_APPEND);
				sleep(1);

			}
		}




	}



	//充值金额统计
	//day today当天/yesterday/昨天/month/当月/all/全部
	public function pay_day($id=null,$dayid=null)
	{
		if ($id === 0) $this->error('参数错误');
		if ($dayid === 0) $this->error('参数错误');
		
		if($dayid=="all")
		{$dayid="";}
		
		//当日充值合计
		$data['pay_num']=0;
		
		//当日普通充值合计
		$data['pay_ptnum']=0;
		
		//当日普通支付成功笔数
		$data['pay_ptok']=0;
		
		//当日普通未支付笔数
		$data['pay_ptno']=0;
		
		//当日普通笔数率
		$data['pay_ptp']=0;
		
		//当日vip充值合计
		$data['pay_vipnum']=0;
		//当日vip支付成功笔数
		$data['pay_vipok']=0;
		//当日vip未支付笔数
		$data['pay_vipno']=0;
		//当日vip笔数率
		$data['pay_vipp']=0;
		

		//求当日充值合计
		if($id == '1')
		{
			$money=DB::table('ien_day_money')->whereTime('addtime', $dayid)->sum('money');
		}
		else
		{
			$money=DB::table('ien_day_money')->where('uid',$id)->whereTime('addtime', $dayid)->sum('money');
		}
			
			$data['pay_num']+=$money;

		
		
		//求当日普通充值合计
		if($id == '1')
		{
			$moneypt=DB::table('ien_day_money')->whereTime('addtime', $dayid)->sum('ptmoney');
		}
		else
		{
			$moneypt=DB::table('ien_day_money')->where('uid',$id)->whereTime('addtime', $dayid)->sum('ptmoney');
		}
			
				$data['pay_ptnum']+=$moneypt;
		
		//求当日普通成功笔数合计
		if($id == '1')
		{
			$moneybs=DB::table('ien_day_money')->whereTime('addtime', $dayid)->sum('ptnum');
		}
		else
		{
			$moneybs=DB::table('ien_day_money')->where('uid',$id)->whereTime('addtime', $dayid)->sum('ptnum');
		}
		
			$data['pay_ptok']+=$moneybs;


		//求当日普通未成功笔数合计
		if($id == '1')
		{
			$moneynobs=DB::table('ien_day_money')->whereTime('addtime', $dayid)->sum('ptnot');
		}
		else
		{
			$moneynobs=DB::table('ien_day_money')->where('uid',$id)->whereTime('addtime', $dayid)->sum('ptnot');
		}
		

			$data['pay_ptno']+=$moneynobs;

		//当日成功支付率
		if($data['pay_ptok']==0 && $data['pay_ptno']==0)
		{$data['pay_ptp']=0;}
		else{
		$data['pay_ptp']=round($data['pay_ptok']/($data['pay_ptok']+$data['pay_ptno'])*100,2);
		}
		/////////////////////////////////////////////////////////////////////
		
		//求当日VIP充值合计
		if($id == '1')
		{
			$moneyvip=DB::table('ien_day_money')->whereTime('addtime', $dayid)->sum('vipmoney');
		}
		else
		{
			$moneyvip=DB::table('ien_day_money')->where('uid',$id)->whereTime('addtime', $dayid)->sum('vipmoney');
		}
			
				$data['pay_vipnum']+=$moneyvip;

		//求当日VIP成功笔数合计
		if($id == '1')
		{
			$moneyvipbs=DB::table('ien_day_money')->whereTime('addtime', $dayid)->sum('vipnum');
		}
		else
		{
			$moneyvipbs=DB::table('ien_day_money')->where('uid',$id)->whereTime('addtime', $dayid)->sum('vipnum');
		}
		

			$data['pay_vipok']+=$moneyvipbs;

		//求当日VIP未成功笔数合计
		if($id == '1')
		{
			$moneyvipnobs=DB::table('ien_day_money')->whereTime('addtime', $dayid)->sum('vipnot');
		}
		else
		{
			$moneyvipnobs=DB::table('ien_day_money')->where('uid',$id)->whereTime('addtime', $dayid)->sum('vipnot');
		}
		

			$data['pay_vipno']+=$moneyvipnobs;

		//当日VIP成功支付率
		if($data['pay_vipok']==0 && $data['pay_vipno']==0)
		{$data['pay_vipp']=0;}
		else{
		$data['pay_vipp']=round($data['pay_vipok']/($data['pay_vipok']+$data['pay_vipno'])*100,2);
		}
		
		
		return $data;
		}

		/////////////////////////////////////////////
		public function userlog(){
			$js=<<<EOF
		<script>
		    $(function() {
		        var i = 59;
		        setInterval(function() {
		            $("#timeRefresh").html(i);
		            if (i == 1) {
		                location.reload();
		                i = 0;
		            }
		            i--;
		        }, 1000);
		    });
		</script>
EOF;
			$userday=$this->dayuser();
			$today=$this->use_log(UID,"today");
			$yesterday=$this->use_log(UID,"yesterday");
			$month=$this->use_log(UID,"month");
			$all=$this->use_log(UID,"all");
			$this->assign('today',$today);
			$this->assign('yesterday',$yesterday);
			$this->assign('month',$month);
			$this->assign('all',$all);
			$this->assign('alla',1);
			$data_list=DB::table('ien_day_user')->where('uid',UID)->limit(0,30)->order('addtime desc')->select();
			return ZBuilder::make('table')
			->hideCheckbox()
			->setPageTitle('近30天用户统计')
            ->addColumns([ // 批量添加数据列
            	['addtime','日期','date'],
            	['adduser','新增用户'],
            	['isgz','已关注'],
            	['payuser','已付费'],
            	['nan','男性'],
            	['nv','女性'],
            	['weizhi','未知'],

            ])
            ->setExtraJs($js)
         	->setRowList($data_list)
			->fetch('userlog');
			
			}
	
		public function dayuser()
	{
		if(UID)
		{
		$id=UID;
		$dayid="today";
		//当日新增人数合计
		$data['adduser']=0;
		//男性
		$data['nan']=0;
		//女性
		$data['nv']=0;
		//未知
		$data['weizhi']=0;
		//消费
		$data['payuser']=0;
		//关注
		$data['isgz']=0;
		//获取当前代理推广男性用户信息
		$man=DB::table('ien_admin_user')
		->where('sxid',$id)
		->whereTime('create_time', $dayid)
		->where('ien_admin_user.sex=1')
		->count('id');
		$data['nan']+=$man;
		//获取当前代理推广女性用户信息
		$user_women=DB::table('ien_admin_user')
		->where('sxid',$id)
		->whereTime('ien_admin_user.create_time', $dayid)
		->where('ien_admin_user.sex=2')
		->count('id');
		$data['nv']+=$user_women;	
		//获取当前代理推广未知用户信息
		$user_weizhi=DB::table('ien_admin_user')
		->where('sxid',$id)
		->whereTime('ien_admin_user.create_time', $dayid)
		->where('ien_admin_user.sex=0')
		->count('id');
		$data['weizhi']+=$user_weizhi;
		//获取当前代理推广全部用户信息
		$user_num=DB::table('ien_admin_user')
		->where('sxid',$id)
		->whereTime('ien_admin_user.create_time', $dayid)
		->count('id');
		$data['adduser']+=$user_num;
		//获取当前代理推广关注用户信息
		$user_isgz=DB::table('ien_admin_user')
		->where('sxid',$id)
		->whereTime('ien_admin_user.create_time', $dayid)
		->where('ien_admin_user.isguanzhu=1')
		->count('id');
		$data['isgz']+=$user_isgz;
		//获取当前代理推广付费用户信息
		$user_xiaofei=DB::table('ien_pay_log')
		->where('did',$id)
		->where('status','1')
        ->where('isout','NEQ','1')
		->field('uid')
		->whereTime('paytime', $dayid)
		->count('DISTINCT uid');
		$data['payuser']+=$user_xiaofei;

			$userlog=DB::table("ien_day_user")->whereTime('addtime', 'today')->where('uid',UID)->find();

			
			if(empty($userlog))
			{	
				$data['uid']=UID;
				$data['addtime']=time();
				Db::table('ien_day_user')->insert($data);
			}
			else
			{

				$data['uid']=UID;
				$data['addtime']=time();
				Db::table('ien_day_user')->where('id',$userlog['id'])->update($data);
			}
	
		}

	}
	public function alluser($id=null){
		
		ignore_user_abort(true); // 后台运行
		set_time_limit(0); // 取消脚本运行时间的超时上限
		

		if(!empty($id))
		{
			$map=['id'=>$id];
		}
		else
		{
			$map="";
		}

		$user=DB::table('ien_admin_user')->where($map)->where('role','<>','3')->select();
		foreach($user as $key=>$value)
		{
			$userlog=DB::query("select * from ien_admin_user where sxid=".$value['id']." and isout = 0 group by FROM_UNIXTIME(create_time,'%Y%m%d')");
			
			if(!empty($userlog))
			{
			foreach ($userlog as $k => $valuea) {
					/*start*/
					$datey=date('Y',$valuea['create_time']);
					$datem=date('m',$valuea['create_time']);
					$dated=date('d',$valuea['create_time']);
					$beginToday=mktime(0,0,0,$datem,$dated,$datey);
					$endToday=mktime(0,0,0,$datem,$dated+1,$datey)-1;
		
		//当日新增人数合计
		$data['adduser']=0;
		//男性
		$data['nan']=0;
		//女性
		$data['nv']=0;
		//未知
		$data['weizhi']=0;
		//消费
		$data['payuser']=0;
		//关注
		$data['isgz']=0;
		//获取当前代理推广男性用户信息
		$man=DB::table('ien_admin_user')
		->where('sxid',$value['id'])
		->whereTime('create_time', 'between',[$beginToday,$endToday])
		->where('ien_admin_user.sex=1')
		->where('isout','0')
		->count('id');
		$data['nan']+=$man;
		//获取当前代理推广女性用户信息
		$user_women=DB::table('ien_admin_user')
		->where('sxid',$value['id'])
		->whereTime('ien_admin_user.create_time','between',[$beginToday,$endToday])
		->where('ien_admin_user.sex=2')
		->where('isout','0')
		->count('id');
		$data['nv']+=$user_women;	
		//获取当前代理推广未知用户信息
		$user_weizhi=DB::table('ien_admin_user')
		->where('sxid',$value['id'])
		->whereTime('ien_admin_user.create_time', 'between',[$beginToday,$endToday])
		->where('ien_admin_user.sex=0')
		->where('isout','0')
		->count('id');
		$data['weizhi']+=$user_weizhi;
		//获取当前代理推广全部用户信息
		$user_num=DB::table('ien_admin_user')
		->where('sxid',$value['id'])
		->whereTime('ien_admin_user.create_time', 'between',[$beginToday,$endToday])
		->where('isout','0')
		->count('id');
		$data['adduser']+=$user_num;
		//获取当前代理推广关注用户信息
		$user_isgz=DB::table('ien_admin_user')
		->where('sxid',$value['id'])
		->whereTime('ien_admin_user.create_time', 'between',[$beginToday,$endToday])
		->where('ien_admin_user.isguanzhu=1')
		->where('isout','0')
		->count('id');
		$data['isgz']+=$user_isgz;
		//获取当前代理推广付费用户信息
		$user_xiaofei=DB::table('ien_pay_log')
		->where('did',$value['id'])
		->where('status','1')
        ->where('isout','NEQ','1')
		->field('uid')
		->whereTime('paytime', 'between',[$beginToday,$endToday])
		->count('DISTINCT uid');
		$data['payuser']+=$user_xiaofei;

			$userlog=DB::table("ien_day_user")->whereTime('addtime', 'between',[$beginToday,$endToday])->where('uid',$value['id'])->find();

			if(empty($userlog))
			{	
				$data['uid']=$value['id'];
				$data['addtime']=$beginToday;
				Db::table('ien_day_user')->insert($data);
			}
			else
			{

				$data['uid']=$value['id'];
				$data['addtime']=$beginToday;
				Db::table('ien_day_user')->where('id',$userlog['id'])->update($data);
			}
					/*end*/
					
				}

				@ini_set('implicit_flush',1);
				ob_implicit_flush(1);
				@ob_end_clean();

				echo ("代理商ID:".$value['id']."更新完成<br/>");
				file_put_contents("userlog.txt","代理商ID:".$value['id']."更新完成\r\n",FILE_APPEND);
				sleep(1);

			}
		}




	}






		//当日用户统计
		//day today当天/yesterday/昨天/month/当月/all/全部
		public function use_log($id=null,$dayid=null){
			
		if ($id === 0) $this->error('参数错误');
		if ($dayid === 0) $this->error('参数错误');
		if($dayid=="all")
		{$dayid="";}
		
		//当日新增人数合计
		$data['user_num']=0;
		//男性
		$data['user_man']=0;
		//女性
		$data['user_women']=0;
		//未知
		$data['user_weizhi']=0;
		//消费
		$data['user_xiaofei']=0;

		//获取当前代理推广男性用户信息
		if($id == '1'){
		$man=DB::table('ien_day_user')
		->whereTime('addtime', $dayid)
		->sum('nan');
		}
		else{
		$man=DB::table('ien_day_user')
		->where('uid',$id)
		->whereTime('addtime', $dayid)
		->sum('nan');
		}
		$data['user_man']+=$man;
		
		//获取当前代理推广女性用户信息
		if($id == '1'){
		$user_women=DB::table('ien_day_user')
		->whereTime('addtime', $dayid)
		->sum('nv');
		}
		else{
		$user_women=DB::table('ien_day_user')
		->where('uid',$id)
		->whereTime('addtime', $dayid)
		->sum('nv');
		}
		$data['user_women']+=$user_women;
		
		//获取当前代理推广未知用户信息
		if($id == '1'){
		$user_weizhi=DB::table('ien_day_user')
		->whereTime('addtime', $dayid)
		->sum('weizhi');
		}
		else{
		$user_weizhi=DB::table('ien_day_user')
		->where('uid',$id)
		->whereTime('addtime', $dayid)
		->sum('weizhi');
		}
		$data['user_weizhi']+=$user_weizhi;
		
		//获取当前代理推广全部用户信息
		if($id == '1'){
		$user_num=DB::table('ien_day_user')
		->whereTime('addtime', $dayid)
		->sum('adduser');
		}
		else{
		$user_num=DB::table('ien_day_user')
		->where('uid',$id)
		->whereTime('addtime', $dayid)
		->sum('adduser');
		}
		$data['user_num']+=$user_num;
		//获取当前代理推广付费用户信息
		if($id == '1'){
		$user_xiaofei=DB::table('ien_day_user')
		->whereTime('addtime', $dayid)
		->sum('payuser');
		}
		else{
		$user_xiaofei=DB::table('ien_day_user')
		->where('uid',$id)
		->whereTime('addtime', $dayid)
		->sum('payuser');
		}
		$data['user_xiaofei']+=$user_xiaofei;	
		$data['guanzhu']=substr(($data['user_man']+$data['user_women'])/$data['user_num']*100,0,4);
		return $data;
			
			
			}
	
		public function alllog(){




			$js=<<<EOF
		<script>
		    $(function() {
		        var i = 59;
		        setInterval(function() {
		            $("#timeRefresh").html(i);
		            if (i == 1) {
		                location.reload();
		                i = 0;
		            }
		            i--;
		        }, 1000);
		    });
		</script>
EOF;
		$this->addalllog();

		$sum['yd_money']=DB::table('ien_all_log')->sum('yd_money');
		$sum['yd_sum']=DB::table('ien_all_log')->sum('yd_sum');
		$sum['zj_money']=DB::table('ien_all_log')->sum('zj_money');
		$sum['zj_sum']=DB::table('ien_all_log')->sum('zj_sum');
		$sum['yd_user']=DB::table('ien_all_log')->sum('yd_user');
		$sum['yd_guanzhu']=DB::table('ien_all_log')->sum('yd_guanzhu');
		$sum['zj_user']=DB::table('ien_all_log')->sum('zj_user');
		$sum['zj_guanzhu']=DB::table('ien_all_log')->sum('zj_guanzhu');
		$sum['kl_log']=DB::table('ien_all_log')->sum('kl_log');
		
		$this->assign('sum',$sum);

		$data_list=DB::table('ien_all_log')->limit(0,30)->order('addtime desc')->select();
			return ZBuilder::make('table')
			->hideCheckbox()
			->setPageTitle('近30天平台统计')
            ->addColumns([ // 批量添加数据列
            	['addtime','日期','date'],
            	['yd_money','引导充值'],
            	['yd_sum','引导订单数'],
            	['zj_money','直接充值'],
            	['zj_sum','直接订单数'],
            	['yd_user','引导用户'],
            	['yd_guanzhu','引导关注'],
            	['zj_user','直接用户'],
            	['zj_guanzhu','直接关注'],
            	['kl_log','扣量'],

            ])
         	->setRowList($data_list)
         	->setExtraJs($js)
        	->fetch('alllog');


		}

		
		public function addalllog(){

			

			$data['yd_money']=(int)DB::table('ien_pay_log')->where('status',1)->where('did','NEQ','0')->where('isout','NEQ','1')->whereTime('paytime', 'today')->sum('money');
			$data['yd_sum']=DB::table('ien_pay_log')->where('status',1)->where('did','NEQ','0')->where('isout','NEQ','1')->whereTime('addtime', 'today')->count('id');
			$data['zj_money']=(int)DB::table('ien_pay_log')->where('status',1)->where('did','0')->whereTime('paytime', 'today')->sum('money');
			$data['zj_sum']=DB::table('ien_pay_log')->where('status',1)->where('did','0')->whereTime('addtime', 'today')->count('id');
			$data['yd_user']=DB::table('ien_admin_user')->where('sxid','NEQ','0')->whereTime('create_time', 'today')->count('id');
			$data['yd_guanzhu']=DB::table('ien_admin_user')->where('sxid','NEQ','0')->where('isguanzhu','1')->whereTime('create_time', 'today')->count('id');
			$data['zj_user']=DB::table('ien_admin_user')->where('sxid','0')->whereTime('create_time', 'today')->count('id');
			$data['zj_guanzhu']=DB::table('ien_admin_user')->where('sxid','0')->where('isguanzhu','1')->whereTime('create_time', 'today')->count('id');
			$data['kl_log']=(int)DB::table('ien_pay_log')->where('status',1)->where('did','NEQ','0')->whereTime('paytime', 'today')->where('isout','1')->sum('money');
		

			$userlog=DB::table("ien_all_log")->whereTime('addtime', 'today')->find();

			if(empty($userlog))
			{	
				$data['addtime']=time();
				Db::table('ien_all_log')->insert($data);
			}
			else
			{
				$data['addtime']=time();
				Db::table('ien_all_log')->where('id',$userlog['id'])->update($data);
			}
					/*end*/
					
		}
	
		public function booklog(){
			
		$map = $this->getMap();
		$order = $this->getOrder();
		$book=DB::table('ien_book')->where($map)->order($order)->order('zhishu desc')->paginate('10');
		$xstype=DB::table('ien_cms_field')->where('id',82)->find();
		$xstype = explode("\r\n", $xstype['options']); 
		$tstype=DB::table('ien_cms_field')->where('id',49)->find();
		$tstype = explode("\r\n", $tstype['options']); 
		return ZBuilder::make('table')
			->hideCheckbox()
			->setPageTitle('小说充值统计')
            ->addColumns([ // 批量添加数据列
            	['id', 'ID'],
				['image','封面','picture'],
				['title', '小说名称'],
				['cid','频道','text', '', ['2'=>'男生','3'=>'女生']],
				['xstype','小说状态','text', '', $xstype],
				['tstype','小说类型','text', '', $tstype],
				['status','状态','status','',['0'=>'下架','1'=>'上架']],
				['tips','打赏金额','text'],
                ['jinri','今日充值','callback',function($book){
                	$today=DB::table('ien_book_log')->whereTime('addtime','today')->where('bid',$book['id'])->find();
                	return "充值金额：￥".$today['money']."<br/>充值笔数：".$today['count'];
                },'__data__'],
                ['zuori','昨日充值','callback',function($book){
                	$today=DB::table('ien_book_log')->whereTime('addtime','yesterday')->where('bid',$book['id'])->find();
                	return "充值金额：￥".$today['money']."<br/>充值笔数：".$today['count'];
                },'__data__'],
                ['leiji','累计充值','callback',function($book){
                	$money=DB::table('ien_book_log')->where('bid',$book['id'])->sum('money');
                	$count=DB::table('ien_book_log')->where('bid',$book['id'])->sum('count');
                	return "充值金额：￥".$money."<br/>充值笔数：".$count;
                },'__data__'],

            ])
            ->setTableName('book')
            ->addOrder('id,tips')
            ->setSearch(['id' => 'ID', 'title' => '小说名称'])
			->addFilter('cid', ['2'=>'男生','3'=>'女生'])
			->addFilter('xstype', $xstype)
			->addFilter('tstype', $tstype)
            ->setRowList($book) // 设置表格数据
            ->fetch(); // 渲染模板
			

		}

		
		public function paydetail(){
			//金额天，昨天，月，所有
			$pay_today=$this->pay_day(UID,"today");
			$pay_yesterday=$this->pay_day(UID,"yesterday");
			$pay_month=$this->pay_day(UID,"month");
			$pay_all=$this->pay_day(UID,"all");
			$this->assign('pay_today',$pay_today);
			$this->assign('pay_yesterday',$pay_yesterday);
			$this->assign('pay_month',$pay_month);
			$this->assign('pay_all',$pay_all);
			
			$map="";
			$mapa = $this->getMap();
			$key=array_keys($mapa);
			$i=0;
			foreach($mapa as $k=>$value)
			{	
				
				$name="ien_".$key[$i];
				$map[$name]=$value;
				$i++;
			}
			if(UID == '1'){

	    	$user=DB::view('ien_pay_log')
	        ->view('ien_wechat_uconfig','name','ien_wechat_uconfig.uid=ien_pay_log.did')
	        ->where('ien_pay_log.paytype > 0')
	        ->where($map)->order('ien_pay_log.addtime desc')->paginate();


	        return ZBuilder::make('table')  
	        ->hideCheckbox()
	        ->setSearch(['pay_log.nickname' => '用户名'])
	        ->addFilter('pay_log.paytype',['1'=>'VIP会员','2'=>'普通充值'])
	        ->addFilter('pay_log.status',['1'=>'已支付','0'=>'未支付'])
	        ->addFilter('wechat_uconfig.name')
	        ->addTimeFilter('pay_log.addtime')
	        ->addColumns([// 批量添加数据列

	            ['payid', '订单ID','text'],
	            ['paytype','订单类型',['1'=>'VIP会员','2'=>'普通充值']],
	            ['nickname','用户'],
	            ['money','充值金额'],
	            ['status', '订单状态', ['1'=>'已支付','0'=>'未支付']],
	            ['name','代理商'],
	            ['addtime', '添加时间', 'datetime'],

	        ])
	        ->setRowList($user) // 设置表格数据
	        ->fetch('paydetail'); // 渲染模板
			}
			else{
				$user=DB::view('ien_pay_log')
	        ->view('ien_wechat_uconfig','name','ien_wechat_uconfig.uid=ien_pay_log.did')
	        ->where('ien_pay_log.did',UID)
	        ->where('ien_pay_log.paytype > 0')
	        ->where($map)->order('ien_pay_log.addtime desc')->paginate();


	        return ZBuilder::make('table')  
	        ->hideCheckbox()
	        ->setSearch(['ien_pay_log.nickname' => '用户名'])
	        ->addFilter('pay_log.paytype',['1'=>'VIP会员','2'=>'普通充值'])
	        ->addFilter('pay_log.type',['1'=>'公众号支付','2'=>'第三方支付'])
	        ->addFilter('pay_log.status',['1'=>'已支付','0'=>'未支付'])
	        ->addFilter('wechat_uconfig.name')
	        ->addTimeFilter('pay_log.addtime')
	        ->addColumns([// 批量添加数据列

	            ['payid', '订单ID','text'],
	            ['paytype','订单类型',['1'=>'VIP会员','2'=>'普通充值']],
	            ['nickname','用户'],
	            ['money','充值金额'],
	            ['type', '支付方式', ['1'=>'公众号支付']],
	            ['status', '订单状态', ['1'=>'已支付','0'=>'未支付']],
	            ['addtime', '添加时间', 'datetime'],

	        ])
	        ->setRowList($user) // 设置表格数据
	        ->fetch('paydetail'); // 渲染模板
			}
		}

	
		public function orderlog()
		{
				//单独拼接组合查询
				$map="";
				$map = $this->getMap();
		    $data_list = DB::table('ien_order_hour_log')->where($map)->order('addtime desc')->select();

				return ZBuilder::make('table')
					->hideCheckbox()
					->addTimeFilter('addtime')
		      ->addColumns([ // 批量添加数据列
						['addtime','时间','datetime'],
						['count','订单数量'],
						['money','金额']
		       ])
	        ->setRowList($data_list) // 设置表格数据
	        ->fetch(); // 渲染模板
		
		}


}
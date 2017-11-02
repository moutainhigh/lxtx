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

class Settlement extends Admin{
	public function index(){
		$data=DB::table('ien_admin_user')->where('id',UID)->find();
		$data_list=DB::table('ien_settlement_log')->where('uid',UID)->paginate();

		$btnshenhe = [
                'class' => 'btn btn-primary confirm',
                'icon'  => 'fa fa-fw fa-edit',
                'title' => '提现',
                'href'  => url('Settlement/add')
            ];
			
		return ZBuilder::make('table')
			
			->hideCheckbox()
			->setPageTitle('可提现金额 '.$data['money'].' 元')
            ->addColumns([ // 批量添加数据列
				['create_time', '申请时间','datetime'],
        ['update_time', '审核时间','datetime'],
				['money', '提现金额'],
				['bili','佣金比例'],
        ['type','审核状态',['1'=>'已支付','0'=>'未支付']],
            ])
			
			->addTopButton('custom',$btnshenhe)
            ->setRowList($data_list) // 设置表格数据
            ->fetch(); // 渲染模板
		
		}
		//添加提现
		public function add(){
			$user=DB::table('ien_admin_user')->where('id',UID)->find();
			if ($this->request->isPost()) {
            $data = $this->request->post();	
			if ($data['money'] > $user['money']) {
            $this->error('超过提现金额');
            return false;
       		}
			if ($model = DB::table('ien_settlement_log')->insert($data)) {
               // Cache::clear();
				$money=$user['money']-$data['money'];
				 if (false === DB::table('ien_admin_user')->where('id',UID)->update(['money'=>$money]))
				  { $this->error('新增失败');
            }
                $this->success('新增成功');
            } else {
                $this->error('新增失败');
            }
			
			}
			
			
			return ZBuilder::make('form')
            ->addFormItems([
				['hidden','uid',UID],
				['hidden','type',0],
				['hidden','bili',$user['fcbl']],
                ['text', 'money', '金额', '必填'],
				['hidden', 'create_time', $this->request->time()],

            ])
			->isAjax(true)
            ->fetch();
			
			
			}
		
		//审核操作
		public function shenheadd($id=null){

            if (false === DB::table('ien_settlement_log')->where('id',$id)->update(['type'=>1,'update_time'=>time()])) {
            $this->error('审核失败');
            }	
            $this->success('审核成功');
			
			}
		//审核
		public function shenhe(){
		
		if(UID == 1){
			$where['role']=4;
		}
		else{
			$where['cid']=UID;
		}
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

		$data_list=DB::view('ien_settlement_log')
		->view('ien_admin_user','username,nickname,xingming,fangshi,zhanghao,role','ien_admin_user.id=ien_settlement_log.uid')
		->where($map)
		->where($where)
		->order('ien_settlement_log.type asc,create_time asc')
		->paginate();
		

		$btnshenhe = [
                'class' => 'btn btn-primary confirm',
                'icon'  => 'fa fa-fw fa-edit',
                'title' => '审核',
                'href'  => url('Settlement/shenheadd', ['id' => '__id__'])
            ];
		$btnoutexcel = [
            'class' => 'btn btn-primary confirm',
            'icon'  => 'fa fa-plus-circle',
            'title' => '数据导出',
						'href'  => url('settlement/export'),
		
        		];
		
		
		return ZBuilder::make('table')
			->setSearch(['admin_user.username' => '用户名'])
      ->addColumns([ // 批量添加数据列
        ['create_time','申请时间','datetime'],
        ['update_time','审核时间','datetime'],
				['uid', 'uid'],
				['money', '提现金额'],
				['bili','佣金比例'],
        ['type','审核状态',['1'=>'已支付','0'=>'未支付']],
        ['role','用户级别',['1'=>'超级管理员','2'=>'代理商','3'=>'前台会员','4'=>'渠道商']],
				['username','用户名'],
				['nickname','昵称'],
				['xingming','姓名'],
				['fangshi','支付方式'],
				['zhanghao','账号'],

            ])
			->addColumn('right_button', '操作', 'btn')
			->addFilter('settlement_log.type',['1'=>'已支付','0'=>'未支付'])
			->addFilter('admin_user.role',['1'=>'超级管理员','2'=>'代理商','3'=>'前台会员','4'=>'渠道商'])
			->addTopButton('custom',$btnoutexcel)
			->addRightButton('custom',$btnshenhe)
            ->setRowList($data_list) // 设置表格数据
            ->fetch();
		
		}

	public function export()
	{
		if(UID == 1){
			$where['role']=4;
		}
		else{
			$where['cid']=UID;
		}
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

		$data_list=DB::view('ien_settlement_log')
		->view('ien_admin_user','username,nickname,xingming,fangshi,zhanghao,role','ien_admin_user.id=ien_settlement_log.uid')
		->where($map)
		->where($where)
		->order('ien_settlement_log.type asc,create_time asc')
		->paginate(5000);
		
		// 设置表头信息（对应字段名,宽度，显示表头名称）
    $cellName = [
			['id', 'auto', 'ID'],
			['create_time','auto','申请时间','datetime'],
			['update_time','auto','审核时间','datetime'],
      ['uid','auto','UID'],
			['money','auto','提现金额'],
      ['bili','auto','佣金比例'],
			['username','auto','用户名'],
			['xingming', 'auto','姓名'],
			['fangshi','auto','支付方式'],
			['zhanghao','auto','账号']
		];

		
    // 调用插件（传入插件名，[导出文件名、表头信息、具体数据]）
    plugin_action('Excel/Excel/export', [time(), $cellName, $data_list]);
		
	}
		
	
	
}
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

class Novel extends Admin{

    public function index(){
		$map = $this->getMap();
        $data_list = DB::table('ien_book')->where($map)->order('zhishu desc')->paginate();
		// 自定义按钮
            $btn = [
                'class' => 'btn btn-xs btn-default',
                'icon'  => 'fa fa-fw fa-folder-open-o',
                'title' => '查看',
                'href'  => url('zjlist', ['id' => '__id__'])
            ];
		$css = <<<EOF
           <style>
                .column-desc{width:400px;}
           </style>
EOF;

		return ZBuilder::make('table')
			->setPageTips('【注意】请在右侧点击查看，生成推广链接')
			->setSearch(['title' => '名称', 'desc' => '描述'])
			->hideCheckbox()
            ->addColumns([ // 批量添加数据列
               ['id','ID'],
			   ['image', '封面', 'picture'],

              // ['title', '名称','link',url('zjlist', ['id' => '__id__']),'_blank'],

               ['biaoti', '名称','callback', function($data_list){
               
               	if($data_list['ishot']==1)
               	{

               		$tb="<img src='/public/static/agent/image/new.jpg' height='15'>";
               	}
               	else
               	{
               		$tb="";
               	}

    	return "<a href='".url('zjlist', ['id' => $data_list['id']])."' target='_blank'>".$data_list['title']."</a>".($tb); 
    }, '__data__'],
			   
			   ['cid', '类别','','',['2'=>'男生','3'=>'女生']],
			   
			   ['desc', '描述'],
			   
			   ['tstype', '类型', '','', parse_attr(module_config('agent.agent_novel_type'))],
			   
			   ['status', '状态', '', '',parse_attr(module_config('agent.agent_book_is'))],
			   
			   ['zhishu', '派单指数'],
				
				['right_button', '查看章节', 'btn']

            ])
			->setTableName('book')
			->addFilter('cid',['2'=>'男生','3'=>'女生']) // 添加筛选		
			->addFilter('tstype',parse_attr(module_config('agent.agent_novel_type'))) // 添加筛选			
			->addFilter('status',parse_attr(module_config('agent.agent_book_is'))) // 添加筛选	
			->addOrder('zhishu')
			->setExtraCss($css)
			->addRightButton('custom',$btn)
            ->setRowList($data_list) // 设置表格数据
            ->fetch(); // 渲染模板

    }
	 
	function zjlist($id = null)
	{
		if ($id === 0) $this->error('参数错误');
		// 自定义按钮
            $btnwa = [
                'class' => 'btn btn-xs btn-default',
                'icon'  => 'fa fa-fw fa-gears',
                'title' => '生成推广文案',
                'data-original-title' => '文案内容到当前章节，原文链接为下一章',
                'href'  => url('agent/wxcreate', ['id' => '__id__']),
								'target' => '_blank'
            ];
		// 自定义按钮
            $btnlj = [
                'class' => 'btn btn-xs btn-default',
                'icon'  => 'fa fa-fw fa-reply',
                'title' => '获取推广链接',
                'data-original-title' => '原文链接为当前章节',
                'href'  => url('agent/linkcreate', ['id' => '__id__'])
            ];	
			
		$map = $this->getMap();
		$map['bid']=$id;
    $data_list = DB::table('ien_chapter')->where($map)->order('idx asc')->paginate();
		$data_listxs = DB::table('ien_book')->where('id',$id)->find();
		$page=$_REQUEST['page'];
		if($page > 1){
			return ZBuilder::make('table')
				->hideCheckbox()
				->setPageTitle($data_listxs['title'])
	            ->addColumns([ // 批量添加数据列
				
					['idx','章节ID'],
					
	        ['title', '名称'],

					['right_button', '生成推广链接', 'btn']

	            ])
				->setTableName('chapter')
	            ->setRowList($data_list) // 设置表格数据
	            ->fetch('index'); // 渲染模板
		}
		else{
			return ZBuilder::make('table')
				->setPageTips('【注意】请在右侧点击生成推广链接<br>【注意】推广链接是当前章节链接，推广文案生成的是下一张的链接！')
				->hideCheckbox()
				->setPageTitle($data_listxs['title'])
	            ->addColumns([ // 批量添加数据列
				
					['idx','章节ID'],
					
	        ['title', '名称'],

					['right_button', '生成推广链接', 'btn']

	            ])
				->setTableName('chapter')
				->addRightButton('custom',$btnwa)
				->addRightButton('custom',$btnlj,true)
	            ->setRowList($data_list) // 设置表格数据
	            ->fetch('index'); // 渲染模板
		}
		}
		
	 public function show($id = null){
		if ($id === 0) $this->error('参数错误');
		$table="ien_chapter";
		$result = Db::table($table)->where('id',$id)->find();
		echo "<h1>".$result['title']."</h1>";
		echo nl2br($result['content']);	

    }


}
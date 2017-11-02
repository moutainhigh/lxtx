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
use think\Db;
use util\Tree;

/**
 * 前台栏目文档列表控制器
 * @package app\cms\admin
 */
class Column extends Common
{
    /**
     * 栏目文章列表
     * @param null $id 栏目id
     * @author 拼搏 <378184@qq.com>
     * @return mixed
     */
    public function index($id=null,$tstype=null)
    { 
        if ($id === null) $this->error('缺少参数');
       
        $book= Db::table('ien_cms_column')->where('id', $id)->value('name');
        $this->assign('book', $book);
        $this->assign('id', $id);
		$this->assign('tstype', $tstype);
       
        return $this->fetch("list_book");

    }
	
	
	//小说ajax
    public function doajax($id = null , $start = null,$limit= null ,$tstype = null)
    {
                if ($id === null) $this->error('缺少参数');
		if($id==1)
		{
			$map = [
            'ien_book.status' => 1,
        ];
		if($tstype!="")
		$map['ien_book.tstype']=$tstype;
			}
		else{
        $map = [
            'ien_book.status' => 1,
            'ien_book.cid'     => $id
        ];
		if($tstype!="")
		$map['ien_book.tstype']=$tstype;
		}
		

        if($start!="" && $limit!="")
         {$data_list=DB::view('ien_book')
		 ->view('ien_chapter','id as zid','ien_book.id=ien_chapter.bid')
		->where('ien_chapter.idx=1')
		 ->where($map)->limit($start,$limit)->order('zhishu desc,create_time desc')->select();
		}
        else{
		 $data_list=DB::view('ien_book')
		 ->view('ien_chapter','id as zid','ien_book.id=ien_chapter.bid')
		->where('ien_chapter.idx=1')
		->where($map)->limit($start,$limit)->order('zhishu desc,create_time desc')->select();}
		
		if(empty($data_list))
		{return $data_list;}
		
  		foreach($data_list as $key=>$value)
		{
			$data['data'][$key]['id']=(string)$value['id'];
			$data['data'][$key]['title']=$value['title'];
			$data['data'][$key]['summary']=$value['desc'];
			$data['data'][$key]['is_new']=true;
			$data['data'][$key]['avatar']=get_thumb($value['image']);
			$data['data'][$key]['zid']=$value['zid'];
			}
		//$data['data']=array_slice($data['data'],$start,$limit);
        $data_list=json_decode(json_encode($data),true);
        
 // var_dump($data_list);
        return  $data_list;
    }
	
	
	 public function indexidx($bid=null)
    { 
        if ($bid === null) $this->error('缺少参数');
       
        $book= Db::table('ien_book')->where('id', $bid)->value('title');
        $this->assign('book', $book);
        $this->assign('bid', $bid);
       
        return $this->fetch("list_chapter");

    }
	
		//小说ajax
    public function doajaxidx($bid = null , $start = null)
    {
        if ($bid === null) $this->error('缺少参数');
		
		$map['bid']=$bid;

        if($start!="")
         {$data_list=DB::table('ien_chapter')->where($map)->limit($start,10)->order('idx asc')->select();
		}
        else{
		 $data_list=DB::table('ien_chapter')->where($map)->limit($start,10)->order('idx asc')->select();}

  		foreach($data_list as $key=>$value)
		{
			$data['data'][$key]['id']=(string)$value['id'];
			$data['data'][$key]['title']=$value['title'];
			$data['data'][$key]['idx']=$value['idx'];
			$data['data'][$key]['welth']="0";
			}
		//$data['data']=array_slice($data['data'],$start,$limit);
        $data_list=json_decode(json_encode($data),true);
        
 // var_dump($data_list);
        return  $data_list;
    }
	

    /**
     * 获取栏目面包屑导航
     * @param int $id
     * @author 拼搏 <378184@qq.com>
     */
    public function getBreadcrumb($id)
    {
        $columns = ColumnModel::where('status', 1)->column('id,pid,name,url,target,type');
        foreach ($columns as &$column) {
            if ($column['type'] == 0) {
                $column['url'] = url('cms/column/index', ['id' => $column['id']]);
            }
        }

        return Tree::config(['title' => 'name'])->getParents($columns, $id);
    }
}
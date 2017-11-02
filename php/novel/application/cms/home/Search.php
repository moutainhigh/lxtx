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

use think\Db;

/**
 * 前台搜索控制器
 * @package app\cms\admin
 */
class Search extends Common
{
    /**
     * 搜索列表
     * @param string $keyword 关键词
     * @author 拼搏 <378184@qq.com>
     * @return mixed
     */
    public function index($keyword = '')
    {
        if ($keyword != ''){
            $keyword=urldecode($keyword);
            $map = [
                'ien_book.status' => 1,
                'ien_book.title'  => ['like', "%$keyword%"]
            ];

            $data_list = Db::view('ien_book')
				->view('ien_chapter','id as zid','ien_book.id=ien_chapter.bid')
				->where('ien_chapter.idx=1')
                ->where($map)
                ->order('sort desc')
                ->paginate(config('list_rows'));
            $data_list=json_decode(json_encode($data_list),true);
           
           /* $totalnum=count($data_list['data']);
            $pagesize=isset($pagesize)?max(1,intval($pagesize)):5;
            $pagenum=ceil($totalnum/$pagesize);
            $page=isset($page)?min($pagenum,max(1,intval($page))):1;

            $data_list['data']=array_slice($data_list['data'],$start,5);
          
*/
       

            $data_list=json_decode(json_encode($data_list));
            
      //var_dump($data_list);
           return  $data_list;

        }
        

        return $this->fetch(); // 渲染模板
    }
        /**
     * 搜索列表
     * @param string $keyword 关键词
     * @author 拼搏 <378184@qq.com>
     * @return mixed
     */
    public function list_search($keyword = '')
    {
        if ($keyword == '') $this->error('请输入关键字');
        $map = [
            'cms_document.trash'  => 0,
            'cms_document.status' => 1,
            'cms_document.title'  => ['like', "%$keyword%"]
        ];

        $data_list = Db::view('cms_document', true)
            ->view('admin_user', 'username', 'cms_document.uid=admin_user.id', 'left')
            ->where($map)
            ->order('create_time desc')
            ->paginate(config('list_rows'));

        $this->assign('keyword', $keyword);
        $this->assign('lists', $data_list);
        $this->assign('pages', $data_list->render());

        return $this->fetch(); // 渲染模板
    }
}
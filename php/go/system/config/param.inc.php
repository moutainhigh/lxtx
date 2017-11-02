<?php 
/*
	default  默认访问路由
	routes   自定义路由
*/
return array (
  'default' => array('m' => 'go','c' => 'index','a' => 'init'),
  'routes' => array(		
			'login' => "member/user/login",
			'login/(:any)' => "member/user/login/$1",
			'admin' => "admin/user/login",
			'uname/(:any)' => 'member/us/uname/$1',
			'userbuy/(:any)' => 'member/us/userbuy/$1',
			'userraffle/(:any)' => 'member/us/userraffle/$1',
			'userpost/(:any)' => 'member/us/userpost/$1',			
			'register/(:any)' => 'member/user/register/$1',
			'register' => 'member/user/register',
			'from=singlemessage&isappinstalled=0/(:any)' => 'mobile/mobile/$1',
			'from=singlemessage&isappinstalled=0' => 'mobile/mobile',
			'from=singlemessage&isappinstalled=1/(:any)' => 'mobile/mobile/$1',
			'from=singlemessage&isappinstalled=1' => 'mobile/mobile',
			'from=timeline&isappinstalled=0/(:any)' => 'mobile/mobile/$1',
			'from=timeline&isappinstalled=0' => 'mobile/mobile',
			'from=timeline&isappinstalled=1/(:any)' => 'mobile/mobile/$1',
			'from=timeline&isappinstalled=1' => 'mobile/mobile',
			'goods/(:any)' => 'go/index/item/$1',
			'dataserver/(:any)' => 'go/index/dataserver/$1',
			'goods_list/(:any)' => 'go/index/glist/$1',
			'goods_list' => 'go/index/glist',
			'goods_lottery' => 'go/index/lottery',
			'goods_lottery/(:any)' => 'go/index/lottery/$1',
			'shiyuan_list/(:any)' => 'go/index/syglist/$1',
	  		'shiyuan_list' => 'go/index/syglist',
			'baiyuan_list/(:any)' => 'go/index/byglist/$1',
	  		'baiyuan_list' => 'go/index/byglist',
			'help/(:any)' => 'go/article/show/$1',
			'single/(:any)' => 'go/article/single/$1',
			'link' => 'link/link/init/$1',
			
			's_tag/(:any)' => 'search/index/tag/$1',
			'buyrecord' => 'go/databuyrecord/buyrecord',
			'buyrecordbai' => 'go/databuyrecord/buyrecordbai',
			'group_qq' => 'go/qq_qun',
			'group' => 'group/group/init',
			'group/show/(:any)' => 'group/group/show/$1',
			'group/nei/(:any)' => 'group/group/nei/$1',
  )
);
?>
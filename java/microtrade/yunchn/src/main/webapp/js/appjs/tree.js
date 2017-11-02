
//构造角色树需要的js对象
/*入参示例：[{"id":1,"roleName":"超级管理员","roleDesc":null},
 *       {"id":2,"roleName":"菜单管理员","roleDesc":null},
 *       {"id":3,"roleName":"用户管理员","roleDesc":null}]
 * 
 * */
/*角色树数据示例
	var role_data = {
			'超级管理员' : {name: '超级管理员', type: 'item'}	,
			'菜单管理员' : {name: '菜单管理员', type: 'item'}	,
			'用户管理员' : {name: '用户管理员', type: 'item'}
		}
*/
function getRoleTree(roleList){
	/*构造角色树*/
    var roleTree = new Object();
    $.each(roleList,function(idx,item){
    	var temp = new Object();
    	temp.name = item.roleName;
    	temp.type = 'item';
    	roleTree[item.roleName] = temp;
    });
    return roleTree;
}


/**构造menu树需要的js对象
 * 入参示例：[{"id":2,"menuName":"用户信息管理","menuDesc":null,"parentId":1,"parentName":"用户管理"},
 *        {"id":3,"menuName":"密码修改","menuDesc":null,"parentId":1,"parentName":"用户管理"},
 *        {"id":5,"menuName":"角色信息管理","menuDesc":null,"parentId":4,"parentName":"角色管理"},
 *        {"id":6,"menuName":"角色授权","menuDesc":null,"parentId":4,"parentName":"角色管理"},
 *        {"id":7,"menuName":"菜单管理","menuDesc":null,"parentId":0,"parentName":"根节点"}]
 * 
 * 菜单数数据示例
		var menu_data = {
			'用户管理' : {name: '用户管理', type: 'folder'}	,
			'角色管理' : {name: '角色管理', type: 'folder', 'icon-class':'blue'}	,
			'菜单管理' : {name: '菜单管理', type: 'item'}
		}
		menu_data['用户管理']['additionalParameters'] = {
			'children' : [
				{name: '用户信息管理', type: 'item'},
				{name: '密码修改', type: 'item'}
			]
		}
		menu_data['角色管理']['additionalParameters'] = {
			'children' : [
				{name: '角色信息管理', type: 'item'},
				{name: '角色授权', type: 'item'}
			]
		}

 */

function getMenuTree(menuList){
	/*构造菜单树*/
    var menuTree = new Object();
    $.each(menuList,function(idx,item){
    	//单级菜单
    	if(item.parentId!=null){
    		if(item.parentId==0){
    			var temp = new Object();
    			temp.name = item.menuName;
    			temp.type = 'item';
    			menuTree[item.menuName] = temp;
    		}else{//有父节点的菜单
    			var temp = new Object();
    			temp.name = item.menuName;
    			temp.type = 'item';
    			//如果父节点还没有，则创建
    			if(!menuTree.hasOwnProperty(item.parentName)){
    				
    				var parent = new Object();
    				parent.name = item.parentName;
    				parent.type = 'folder';
    				
    				menuTree[item.parentName] =parent;
    				parent['additionalParameters'] = new Object();
    				parent['additionalParameters']['children'] = new Array();
    			}
    			menuTree[item.parentName]['additionalParameters']['children'].push(temp);
    		}
    	}
    });
    
    return menuTree;
}

function getUserTree(userList){
	/*构造角色树*/
    var userTree = new Object();
    $.each(userList,function(idx,item){
    	var temp = new Object();
    	temp.name = item.cpcode;
    	temp.type = 'item';
    	userTree[item.cpcode] = temp;
    });
    return userTree;
}

function initTree(element,dataSource,isMultSelect){
	element.ace_tree({
		dataSource: dataSource ,
		multiSelect:isMultSelect,
		loadingHTML:'<div class="tree-loading"><i class="icon-refresh icon-spin blue"></i></div>',
		'open-icon' : 'icon-minus',
		'close-icon' : 'icon-plus',
		'selectable' : true,
		'selected-icon' : 'icon-ok',
		'unselected-icon' : 'icon-remove'
	});
}
    



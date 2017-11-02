!function(a){
	return Cookie.getCookie("needFreshIndex")?(Cookie.deleteCookie("needFreshIndex"),location.reload(),void 0):($.toSetPhone($("#mobile")),
	a.alterPwd=function(){
		event.preventDefault(),
		$.judgeLoginStatus(function(){
		location.href=a.BaseAbsolutePath+"user/alterPwd"}
	)},
	a.alterTel=function(){
		event.preventDefault(),
		$.judgeLoginStatus(function(){location.href=a.BaseAbsolutePath+"user/alterTel"})
		},
	$(".center-list-wrap li").off(tap).on(tap,_.throttle(function(){
		if(!isNeedStopTouchend){
			var a=$(this),b=a.attr("data-index")||"";switch(b.toString()){
				case"0":alterPwd();break;
				case"1":alterTel()
			}
			}},1e3,{trailing:!1})),void 0)}(this);
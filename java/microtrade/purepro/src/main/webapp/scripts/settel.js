!function(a){function b(){function b(){var b=$.trim(mobile.value),c=$.trim(validcode.value),d="OK";
return/^1[3|4|5|7|8]\d{9}$/.test(b)?1==mstatus?d="请获取手机验证码！":2==a.mstatus?d="验证码正在发送...":/^\d{4}$/.test(c)||(
		d="验证码输入有误！"):d="请输入正确的手机号！",d}$.validTelInput($("#mobile"),$("#btnvalid"),$("a.btn-sure"),b),
		$.validCode($("#btnvalid"),$("#validcode"),showMsg,"user/getTelValidCode",$("a.btn-sure"),b,_.throttle(
				function(){if($(this).is(".active")){
					var c="";"OK"!=(c=b())&&showMsg(c),
					$.toRequestData(
						{	url:a.BaseAbsolutePath+"user/submitMbl",
							data:{mobile:mobile.value,validCode:validcode.value},
							sucBack:function(){
								layer.msg("设置手机号完成,请进行后续操作",{time:2e3},function(){
									Cookie.setCookie("needFreshIndex","true");
//									var a=Cookie.getCookie("FirstGoInLength")||1;
		//									window.history.go(a-window.history.length)
									location.href=BaseAbsolutePath+"/index?wxid="+$("#wxid").val();
								})},
							errBack:showMsg
						}
					)
				}},1e3,{trailing:!1}),$("#mobile"))}

b();
//$.getUserInfo(function(c){switch(c.userStatus){
//case"0":location.href=a.BaseAbsolutePath+"user/toSetPwd";
//break;case"1":b();break;case"2":layer.msg("您已经设置过手机号！",
//		{time:2e3},function(){
//			window.location.replace(a.BaseAbsolutePath+"/index?wxid="+$("#wxid").val())});
//break;default:layer.msg("用户状态异常！")}})
}(this);
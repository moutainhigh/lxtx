!function(a){function b(){
	var b=$.trim(validcode.value),c="OK";
	return 1==a.mstatus?c="请获取手机验证码！":2==a.mstatus?c="验证码正在发送...":/^\d{4}$/.test(b)||(c="验证码输入有误！"),c
			}
$.toSetPhone($("#mobile")),
$.validCode($("#btnvalid"),$("#validcode"),showMsg,"user/getTelValidCode",$("a.btn-sure"),b,_.throttle(function(){
	var c=$(this);if(c.is(".active")){var d="";"OK"!=(d=b())&&showMsg(d),showMsg(""),
	$.toRequestData(
		{url:a.BaseAbsolutePath+"user/checkValidForForgetPwd",
		data:{validCode:$.trim(validcode.value)},
		sucBack:function(){layer.msg("手机验证成功",{time:2e3},function(){
			location.href=a.BaseAbsolutePath+"user/toForgetPwdPage"}
		)},
		errBack:showMsg}
	)}},1e3,{trailing:!1}),$("#mobile1"))}(this);
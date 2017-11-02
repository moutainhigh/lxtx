!function(a){function b(){function b(){var a=$.trim(pwdOne.value),b=$.trim(pwdTwo.value),c="OK";return 0==a.length?c="密码不能为空！":a.length<6||a.length>12?c="密码长度不正确！":/[\s]/.test(a)||/[^\x00-\xff]/.test(a)?c="请输入正确的密码！":""==b?c="请输入确认密码！":b.length<6||b.length>12?c="确认密码长度不正确！":/[\s]/.test(b)||/[^\x00-\xff]/.test(b)?c="请输入正确的确认密码！":a!=b&&(c="两次密码输入不一致！"),c}
$("#pwdOne").off("input").on("input",function(){
	var a;"OK"===(a=b())?($("#errorMsg").html(""),
			$("a.btn-sure").addClass("active")):($("a.btn-sure").removeClass("active"),
					$("#errorMsg").html(a))
					}),
$("#pwdTwo").off("input").on("input",
	function(){
		var a;
		"OK"===(a=b())?($("#errorMsg").html(""),
		$("a.btn-sure").addClass("active")):($("a.btn-sure").removeClass("active"),
		$("#errorMsg").html(a))}
),
$("a.btn-sure").off(tap).on(tap,_.throttle(function(){
	$(this).is(".active")&&($("#errorMsg").html(""),
	$.toRequestData(
		{url:a.BaseAbsolutePath+"user/submitPwd",
		data:{pwdOne:pwdOne.value,pwdTwo:pwdTwo.value},
		sucBack:function(){layer.msg("设置密码完成",{time:2e3},
//				function(){location.href=a.BaseAbsolutePath+"user/toSetTel"})},
				function(){location.href=BaseAbsolutePath+"/index?wxid="+$("#wxid").val();})},
				errBack:function(a){$("#errorMsg").html(a),setTimeout(function(){$("#errorMsg").html("")},3e3)}}))
	},
	1e3,{trailing:!1}))}
$.getUserInfo(function(c){
	switch(c.userStatus){case"0":b();break;case"1":case"2":window.location.replace(a.BaseAbsolutePath+"/index?wxid="+$("#wxid").val());
	break;default:layer.msg("用户状态异常！")}
	})}(this);
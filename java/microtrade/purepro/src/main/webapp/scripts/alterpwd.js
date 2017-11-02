!function(a){function b(){
	var a=$.trim($("#oldpwd").val()),b=$.trim($("#newpwd").val()),c=$.trim($("#surepwd").val()),d="";
	if(0==a.length)d="原始密码不能为空！";
	else if(0==b.length)d="新密码不能为空！";
	else if(b==a)d="新密码不得与原始密码相同";
	else if(b.length<6)d="新密码长度不正确（6~12位）";
	else if(/[\s]/.test(b)||/[^\x00-\xff]/.test(b))d="密码不得有特殊符号";
	else if(0==c.length)d="确认密码不能为空";
	else{if(c==b)return $("#errorMsg").html(""),$("a.btn-sure").addClass("active"),!0;d="新密码和确认密码不一致"}
	return showMsg(d),$("a.btn-sure").removeClass("active"),!1}
$.getUserInfo($.proxy($.judgeUserStatus,$,null),function(a){layer.msg(a,{time:2e3},function(){history.back()})}),
$("#oldpwd").blur(function(){b()}),
$("#newpwd").blur(function(){b()}),
$("#surepwd").bind("input propertychange",function(){b()}),
$("a.btn-sure").off(tap).on(tap,_.throttle(function(){
	$(this).is(".active");
	$.post(a.BaseAbsolutePath+"user/changePwd",{oldPwd:$.trim($("#oldpwd").val()),newPwd:$.trim($("#newpwd").val())},function(data){
		if(data.success){
			layer.msg("密码修改完成",{time:2e3},function(){
				Cookie.setCookie("needFreshIndex","true"),
				initShare();
			});
		}else{
			showMsg(data.erro);
		}
	})
	
//	$.toRequestData({url:a.BaseAbsolutePath+"user/changePwd",
//		data:{oldPwd:$.trim($("#oldpwd").val()),newPwd:$.trim($("#newpwd").val())},
//		sucBack:function(){
//			layer.msg("密码修改完成",{time:2e3},function(){
//				Cookie.setCookie("needFreshIndex","true"),
//				initShare();
//			})
//			},
//			errBack:function(a){showMsg(a)}})
			
},1e3,{trailing:!1}))}(this);
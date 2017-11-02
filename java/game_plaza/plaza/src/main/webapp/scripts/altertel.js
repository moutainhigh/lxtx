!function(a){
	function b(){
		var a=$.trim(mobile.value),b=$.trim(validcode.value),c="OK";
		return/^1[3|4|5|7|8]\d{9}$/.test(a)?1==mstatus?c="请获取手机验证码！":/^\d{4}$/.test(b)||(c="验证码输入有误！"):c="请输入相同的手机号！",c}
		$.getUserInfo(
				$.proxy($.judgeUserStatus,$,null),
				function(a){
					layer.msg(a,{time:2e3},
							function(){history.back()}
					)
				}
			),
	$.validTelInput($("#mobile"),$("#btnvalid"),$("a.btn-sure"),b),
	$.validCode(
			$("#btnvalid"),
			$("#validcode"),
			showMsg,
			"user/sendUpTelNo",
			$("a.btn-sure"),
			b,
			_.throttle(function(){
				if($(this).is(".active")){
					var c="";if("OK"!=(c=b()))return showMsg(c),void 0;
					$.post(a.BaseAbsolutePath+"user/changeTel",{newTel:mobile.value,validCode:validcode.value},function(data){
						if(data.success){
							layer.msg("手机号修改完成",{time:2e3},function(){
								Cookie.setCookie("needFreshIndex","true"),
								initShare();
							});
						}else{
							showMsg(data.Message);
						}
					})
				}
			},1e3,{trailing:!1}),
			$("#mobile")
		)
		}(this);
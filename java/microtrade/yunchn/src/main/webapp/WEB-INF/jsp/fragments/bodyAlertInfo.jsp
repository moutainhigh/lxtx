<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>


<div class="alert alert-block alert-success" hidden="hidden">
	<button type="button" class="close" data-dismiss="alert">
		<i class="icon-remove"></i>
	</button>
	<strong>
		<i class="icon-ok"></i>
		<span id="successInfo"></span>
	</strong>
</div>
<div class="alert alert-danger" hidden="hidden">
	<button type="button" class="close" data-dismiss="alert">
		<i class="icon-remove"></i>
	</button>
	<strong>
		<i class="icon-remove"></i>
		<span id="errorInfo"></span>
	</strong>
</div>
<div class="alert alert-info" hidden="hidden">
	<button type="button" class="close" data-dismiss="alert">
		<i class="icon-remove"></i>
	</button>
	<strong>
		<span id="normalInfo"></span>
	</strong>
</div>
<div id="alertDialog" style="display: none;"></div>
<div id="confirmDialog" style="display: none;"></div>
<div id="confirmD" style="display: none;"></div>
<div id="confirmF" style="display: none;"></div>
<script>
<!--成功信息-->
function alertSuccess(info,time){
	$("#successInfo").text(info);
	$(".alert-success").fadeIn();
	if(time){
		$(".alert-success").fadeOut(time);
	}else{
		$(".alert-success").fadeOut(3000);
	}
}

<!--错误信息-->
function alertError(info){	
	$("#errorInfo").text(info);
	$(".alert-danger").fadeIn();
	$(".alert-danger").fadeOut(3000);
}

<!--普通信息-->
function alertInfo(info){
	$("#normalInfo").text(info);
	$(".alert-info").fadeIn();
	$(".alert-info").fadeOut(3000);
}

//提示框(关闭停留在原页；关闭返回上页)  无确定取消按钮 :alertMsg(msg);3秒后自动关闭    供显示错误信息;alertMsg(msg,'back');关闭或3秒后自动回退   供alert成功信息
function alertMsg(msg,back){
	$("#alertDialog").html("<p>"+msg+"</p>");
	var dialogOpts = {
			title : "提示",
			modal : true,
			resizable:false,
			close:function(){if(back){window.history.back(1);}}
		}
	$("#alertDialog").dialog(dialogOpts);
	if(back){
		setTimeout(function(){
			window.history.back(1);
		},3000);
	}else{
		setTimeout(function(){
			$("#alertDialog").dialog("close");
		},3000);
	}
}

function alertMsg1(msg){
	$("#alertDialog").html("<p>"+msg+"</p>");
	var dialogOpts = {
			title : "提示",
			modal : true,
			resizable:false,
			close:function(){}
		}
	$("#alertDialog").dialog(dialogOpts);
}
function confirmF(msg,callback,callback1){
	var flog=false;
	$("#confirmF").html("<p>"+msg+"</p>");
	var dialogOpts = {
			title : "提示",
			modal : true,
			resizable:false,
			buttons : {
				"确定" : function() {
					if(callback){
						callback.call();//方法回调
					}
					$(this).dialog("close");
				},
				"取消" : function() {
					if(callback1){
						callback1.call();//方法回调
					}
					$(this).dialog("close");
				}
			}
		}
		$("#confirmF").dialog(dialogOpts);
}
function confirmD(msg,callback,callback1){
	var flog=false;
	$("#confirmD").html("<p>"+msg+"</p>");
	var dialogOpts = {
			title : "提示",
			modal : true,
			resizable:false,
			buttons : {
				"确定" : function() {
					if(callback){
						callback.call();//方法回调
					}
					$(this).dialog("close");
				},
				"取消" : function() {
					if(callback1){
						callback1.call();//方法回调
					}
					$(this).dialog("close");
				}
			}
		}
		$("#confirmD").dialog(dialogOpts);
}
//提示框  有确定取消按钮
function confirmMsg(msg,callback){
	$("#confirmDialog").html("<p>"+msg+"</p>");
	var dialogOpts = {
			title : "提示",
			modal : true,
			resizable:false,
			buttons : {
				"确定" : function() {
					if(callback){
						callback.call();//方法回调
					}
					$(this).dialog("close");
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			},
		}
		$("#confirmDialog").dialog(dialogOpts);
}
function alertFadeTime(msg,t,back){
	var time=5;
	if(t){
		if(!isNaN(t)){
			time=t;
		}
	}
	$("#alertDialog").html("<p>"+msg+",   <span id=\"timeout\" style=\"color:red;\">"+time+" </span>"+" 秒将自动跳转！</p>");
	var dialogOpts = {
			title : "提示",
			modal : true,
			resizable:false,
			close:function(){
				clearInterval(t);
				if(back){
					window.history.back(1);
				}
			}
		}
		$("#alertDialog").dialog(dialogOpts);
	
	
	var code=$("#timeout");
	var t=setInterval(function  () {
		time--;
		code.html(time);
		if (time==0) {
			clearInterval(t);
			if($("#alertDialog").dialog("isOpen")){
				$("#alertDialog").dialog("close");
			}
		}
	},1000)
}

</script>
var contextPath = "/";
var common = function(){
	return {
		checkSession:function(res,url){
			if (typeof (res) == 'string') {
				res = jQuery.parseJSON(res);
			}
			if(res.code==-9999){
				common.alert_forward("您的登陆已经超时，需要重新登陆！",url);
				return false;
			}else{
				return true;
			}
		},
		checkResponse : function(res){
			if(res){
				if(res.code==-1){
					alertError(res.data);
					return false;
				}else if(res.code==0){
					alertSuccess(res.data);
					return true;
				}
			}
		},
		checkResponseMsg : function(res,msg){
			if(res){
				if(res.code==-1){
					alertError(res.data);
					return false;
				}else if(res.code==0){
					alertSuccess(msg+res.data);
					return true;
				}
			}
		},
		onMessage:function(msg){
			alertInfo(msg)
		},
		alert_forward : function (msg , url) {
			alertSuccess(msg +"正在跳转...");
			setTimeout(function (){
				window.location.href = url;
			}, 3000);
		}
	};
}();

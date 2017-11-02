function initShare(){
	window.location.href=ShareHost;
//	$.ajax({type:"POST",
//		url:ShareHost+"?status=refreshTokenTicket",
//		data:{url:BaseCurrentUrl,callbackparam:"callbackparam"},
//		dataType:"jsonp",
//		jsonpCallback:"callbackparam",
//		jsonp:"callbackparam",
//		success:function(a){
//			wx.config({
//				debug:!1,appId:a.appId,timestamp:a.timestamp,
//				nonceStr:a.nonceStr,signature:a.signature,
//				jsApiList:["onMenuShareAppMessage","onMenuShareTimeline","hideMenuItems","showMenuItems"]})
//				}
//		})
		}
!function(){
			wx.ready(function(){
				wx.showMenuItems({menuList:[]}),
				wx.hideMenuItems({menuList:["menuItem:exposeArticle",
				                            "menuItem:setFont",
				                            "menuItem:dayMode",
				                            "menuItem:nightMode",
				                            "menuItem:refresh",
				                            "menuItem:profile",
				                            "menuItem:addContact",
				                            "menuItem:share:qq",
				                            "menuItem:share:weiboApp",
				                            "menuItem:favorite",
				                            "menuItem:share:facebook",
				                            "menuItem:share:QZone",
				                            "menuItem:jsDebug",
				                            "menuItem:editTag",
				                            "menuItem:delete",
				                            "menuItem:copyUrl",
				                            "menuItem:originPage",
				                            "menuItem:readMode","menuItem:openWithSafari",
				                            "menuItem:openWithQQBrowser",
				                            "menuItem:share:email",
				                            "menuItem:share:brand",
				                            "menuItem:share:appMessage",
				                            "menuItem:share:timeline"]})})}();
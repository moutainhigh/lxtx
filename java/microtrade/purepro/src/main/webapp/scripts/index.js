!function(a){HangqingManage.NeedKLine=!0,
		Cookie.getCookie("comefromforgetpwd")&&(
			Cookie.deleteCookie("comefromforgetpwd"),
			a.afterLogin=function(){layer.closeAll()},
			$.openLogin()
		),
		a.App.Start(),
		$(".deal-btn-wrap label").off(a.tap).on(a.tap,function(b){function c(b){var c=$("body").height();$("body").height()<367
			&&$("body").attr("data-oldheight",c).height(367),
			window.scrollTo(0,Math.max($("body").height()-$(window).height(),0)),
			layer.open({type:2,title:!1,
				area:["100%","367px"],closeBtn:0,fix:!1,skin:"layui-layer-nobg layui-layer-createorder",
				shadeClose:!0,offset:"rb",content:a.BaseAbsolutePath+"order/enterCreateOrder",
				success:function(){
					var a=window.document.querySelector(".layui-layer-createorder").
					getElementsByTagName("iframe")[0].contentWindow.document,c=a.querySelector(".createchoose-wrap");
					c.className="createchoose-wrap "+b,a.querySelector("#createorderbox").className=b,
					c.innerText="createup"==b?"建仓看涨":"建仓看跌"},end:function(){var b=a.document.body,
						c=b.getAttribute("data-oldheight");
					c&&(b.style.height=c+"px",b.removeAttribute("data-oldheight"))}})}
			if((b||event).preventDefault(),!isNeedStopTouchend&&!$(b.target).parents(".deal-btn-wrap").is(".disabled")){
				//if(!a.HangqingManage.CurrentGood||!QueryCenterManage.PushMessageData)return layer.msg("请等待当前数据加载完！"),void 0;
				var d="createup";a.DealDirection="B",$(this).parent().is(".btndown-wrap")&&(d="createdown",a.DealDirection="S"),
				$.judgeLoginStatus($.signProtocol,c,d)}}
			),
$(".btn-withdraw-wrap span").off(a.tap).on(
		a.tap,function(a){
			var c ;
			if($(a.target).parents(".recharge").length>0){
				$.gotoTXCenter("ZF")
			}else if($(a.target).parents(".withdraw").length>0){
				$.gotoTXCenter("TX")
			}else if($(a.target).parents(".coupon").length>0){
				$.gotoTXCenter("CP")
			}
			(a||event).preventDefault(),
			isNeedStopTouchend||(c)}),
$("#userCenter").off(a.tap).on(
		a.tap,function(a){
			(a||event).preventDefault(),
			$.gotoUserCenter()})
}(this);
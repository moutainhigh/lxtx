!function(a) {
	function b(a) {
		return document.getElementById(a)
	}
			a.$.getUserInfo(function(a) {
				$(".userimage").attr("src", a.HeadImgUrl)
			}),
			$(".btncenter-withdraw-wrap a")
					.off(tap)
					.on(
							tap,
							_
									.throttle(
											function(a) {
												var c ;
											if($(a.target).parents(".recharge").length>0){
												$.gotoTXCenter("ZF")
											}else if($(a.target).parents(".withdraw").length>0){
												$.gotoTXCenter("TX")
											}else if($(".coupon a").parents(".coupon").length>0){
												$.gotoTXCenter("CP")
											}
														event.preventDefault(),
														isNeedStopTouchend||(c)
											}, 1e3, {
												trailing : !1
											})),
					$("#coupon a")
					.off(tap)
					.on(
							tap,
							_
									.throttle(
											function(a) {
												var c ;
											if($(a.target).parents(".coupon").length>0){
												$.gotoTXCenter("CP")
											}
														event.preventDefault(),
														isNeedStopTouchend||(c)
											}, 1e3, {
												trailing : !1
											})),
			$(".center-list-wrap li")
					.off(tap)
					.on(
							tap,
							_
									.throttle(
											function() {
												function b() {
															$(
																	".pointset-wrap .input")
																	.off(
																			"input")
																	.on(
																			"input",
																			function() {
																			}),
															$(
																	".pointset-wrap .btn-sure span")
																	.off(tap)
																	.on(
																			tap,
																			function() {
																			})
												}
												if (!isNeedStopTouchend) {
													event.preventDefault();
													var c = $(this), d = c
															.attr("data-index")
															|| "";
													switch (d.toString()) {
													case "0":
//														$.judgeLoginStatus(function(){
//														})
															location.href = a.BaseAbsolutePath + "user/userOperRecord?wxid="+$("#wxid").val();
														break;
													case "1":
															location.href = a.BaseAbsolutePath + "user/userAssetRecord?wxid="+$("#wxid").val();
														break;
													case "2":
														var e = [
																'<div class="pointset-wrap">',
																'<div class="title">默认允许偏离点差设置</div>',
																'<div class="content">',
																'<input type="',
																$.judgeIsIOS() ? "num"
																		: "tel",
																'"/>',
																"</div>",
																'<div class="btn-sure"><span>提交</span></div>',
																"</div>" ]
																.join("");
														layer
																.open({
																	type : 1,
																	title : !1,
																	area : [
																			"80%",
																			"154px" ],
																	closeBtn : 0,
																	fix : !1,
																	skin : "layui-layer-nobg",
																	shadeClose : !0,
																	content : e,
																	success : function() {
																		b()
																	}
																});
														break;
													case "3":
														location.href = a.BaseAbsolutePath + "user/userGameAssetRecord?wxid="+$("#wxid").val();
														break;
													case "4":
														a.$
																.getUserInfo(a.$
																		.proxy(
																				a.$.judgeUserStatus,
																				$,
																				function() {
																					a.document.location.href = a.BaseAbsolutePath
																							+ "user/userSetting"
																				}));
														break;
													case "4":
														a.$
																.getUserInfo(a.$
																		.proxy(
																				a.$.judgeUserStatus,
																				$,
																				function() {
																					a.document.location.href = usermanager
																				}));
														break;
													case "5":
														var e = [
																'<div class="sharetip-wrap">',
																'<img src="styles/images/xiangdao.png">',
																"</div>" ]
																.join("");
																$(document.body)
																		.append(
																				e),
																$(
																		".sharetip-wrap")
																		.off(
																				tap)
																		.on(
																				tap,
																				function() {
																							this
																									.remove(),
																							event
																									.preventDefault()
																				})
													}
												}
											}, 1e3, {
												trailing : !1
											})),
			a.UpdateInfo = function() {
				var c = a.QueryCenterManage;
				if (c.PushMessageData) {
					var d = c.PushMessageData;
					b("total-asset").innerText = $.formatNumber(d.TotalAssets,
							2), b("able-cash").innerText = $.formatNumber(
							d.ValidAssets, 2), b("used-cash").innerText = $
							.formatNumber(d.TotalUsed, 2)
				}
			},
			a.QueryCenterManage
					&& (a.QueryCenterManage.judgeUpdataPart = function(b) {
						return
								("undefined" != typeof b.vec || "undefined" != typeof b.TotalAssets)
										&& a.UpdateInfo(), this
					})//, a.App.Start()
}(this);
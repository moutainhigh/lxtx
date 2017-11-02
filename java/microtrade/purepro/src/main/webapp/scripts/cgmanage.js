!function(root){
	var QueryCenter=function(){window.App&&App.Extend(that)},
	n=QueryCenter.prototype;
	return n.Init=function(a){
		var b=this;return b.Servers=a,b.ChooseServer().InitServer(b.ConnnetTime=1,!0),b
		},
	n.MaxConnectNum=3,n.TipConnectNum=3,n.EndTipConnectNum=2,n.EndTipConnectTime=10,n.CurrentChooseServer=[],
	n.ChooseServer=function(){
		var a,b=this,c=b.Servers,d=c.length,e=b.MaxConnectNum,f=[],g=[];
		if(b.CurrentChooseServer=[],d>e){
			for(;f.length<e;)f.indexOf(a=Math.floor(Math.random()*d))<0&&f.push(a);
			for(var h in f)g.push(c[f[h]])}else g=c;for(var h in g)b.CurrentChooseServer.push({server:g[h]});
			return b
	},
	n.InitServer=function(a,b){
		var c=this,d=c.CurrentChooseServer||[],e=d.length,f=c.TipConnectNum;if(1>e)return layer.msg("连接服务器失败，请稍后重试！"),c;if(root.NotConnentNet)return c;if(a>f){c.TipConnentionObj||(c.TipConnentionObj=[]),c.ConnnetTime=1,c.TipConnentionObj.push(new Date);var g=c.TipConnentionObj.length;if(g>c.EndTipConnectNum){if(c.TipConnentionObj[g-1].getTime()-c.TipConnentionObj[0].getTime()<=6e4*c.EndTipConnectTime)return c.TipConnentionObj=c.TipConnentionObj.slice(g-2),$.netNetTip(null,2),c;c.TipConnentionObj=c.TipConnentionObj.slice(g-2)}return $.netNetTip(function(){c.ChooseServer().InitServer(c.ConnnetTime=1)},1),c}return c.ConnectServer(b),c
	},
	n.ConnectServer=function(a){
		var b=this,c=b.CurrentChooseServer,d=null;for(d in c)b.InitWebSocket(c[d],a)
	},
	n.InitWebSocket=function(a,b){
		var c,d=this,
		e=a.server.Addr+":"+a.server.Port;
		return a.Socket=c=new WebSocket("ws://"+e+"/QueryUserInfo.jsp?accid="+(root.accountId||"")+"&SecKey="+a.server.SecKey),
			c.onopen=function(){var a,e=null,f=[],g=d.CurrentChooseServer;try{for(e in g)(a=g[e]).Socket!=c&&a.Socket.close()}catch(h){console.log(h)}for(e in g)if((a=g[e]).Socket==c){f.push(a);break}d.CurrentChooseServer=f,d.hqNetSocket||(d.hqNetSocket=this),d.doConnectAfter(b)},c.onmessage=d.PushMessage.bind(d),c.onclose=function(){var a,b=!0,e=null,f=d.CurrentChooseServer;for(e in f)(a=f[e]).Socket!=c?a.CloseSocket||(b=!1):a.CloseSocket=!0;b&&d.Close()},c.onerror=function(){d.socket=null,d.BitHeartTimer&&clearInterval(d.BitHeartTimer)},d
	},
	n.doConnectAfter=function(){
		var a=this;a.Live()
	},
	n.Send=function(a,b,c){
		"function"!=typeof b&&(b=function(){}),"function"!=typeof c&&(c=function(a){console.log(a.ErrMsg||"请求发送失败！")}),a.Head.Session=this.ConnectionSession,a.SucCallBack=b,a.ErrCallBack=function(a){a&&(a=JSON.parse(a.Data)),c(a||{})},this.SendSocketReq(a)
	},
	n.SendSocketReq=function(a){var b=this;b.hqNetSocket?b.hqNetSocket.send(a):b.PushReq(a)},
	n.PushReq=function(a){var b=this;b.SocketReqArray||(b.SocketReqArray=[]),b.SocketReqArray.push(a)},
	n.Live=function(){var a=this;a.BitHeartTimer&&clearInterval(a.BitHeartTimer),a.BitHeartTimer=root.setInterval(a.BitHeart.bind(a),6e4)},
	n.BitHeart=function(){var a=this;return a.SendSocketReq(JSON.stringify({HeartBeat:"OK!"})),a},
	n.Close=function(){var a=this;return a.hqNetSocket=null,a.BitHeartTimer&&clearInterval(a.BitHeartTimer),a.InitServer(++a.ConnnetTime),a},
	n.Error=function(){return this.BitHeartTimer&&clearInterval(this.BitHeartTimer),root.layer.msg("系统异常，请稍后重试！"),this},
	n.PushMessage=function(message){
		var that=this,data=eval("("+message.data+")");
		return"undefined"!=typeof data.ErrCode?("-999"==data.ErrCode&&that.Error(),that):data.HeartBeat&&"OK!"===data.HeartBeat?that:(that.PushMessageData=$.extend({},that.PushMessageData,data),that.judgeUpdataPart(data),that)
	},
	n.judgeUpdataPart=function(a){
		var b=this;
		return"undefined"!=typeof a.bsrates&&b.UpdateDealRate(),"undefined"!=typeof a.Frozen&&(b.updateFrozenTime(),b.showFrozenTime()),"undefined"!=typeof a.vec?b.UpdataHoldings():"undefined"!=typeof a.TotalAssets&&b.UpdataAsset(),b
	},
	n.UpdateDealRate=function(){
		var a=this;
		if(1&root.PrivatepPoperties)
			$(".btnrise-wrap .choose").html(""),$(".btndown-wrap .choose").html("");
		else{
			var b,a=this,c=a.PushMessageData,d=root.HangqingManage,e=null,f=null,g=null;
			if(!d||!d.CurrentGood||!c)
				return a;
			g=c.bsrates||[],e=d.CurrentGood,a.CurrentGoodInfo=a.CurrentGoodInfo||{},b=a.CurrentGoodInfo.Choose||{},
			g.forEach(function(b){return b.MarketId==e.Market&&b.MerchCode==e.Code?(f=a.CurrentGoodInfo.Choose=b,void 0):void 0},""),
			!f||f.buy==b.buy&&f.sell==b.sell||($(".btnrise-wrap .choose").html($.formatNumber(f.buy)+"%选择"),
			$(".btndown-wrap .choose").html($.formatNumber(f.sell)+"%选择"))
		}
		return a
	},
	n.UpdataHoldings=function(){
		var a=this,b=a.PushMessageData,c=root.HangqingManage;
		if(!c||!c.CurrentGood||!b)return a;
		var d;
		return c.CurrentGood,b.vec||[],a.CurrentGoodInfo=a.CurrentGoodInfo||{},d=a.CurrentGoodInfo.Hold||[],a.getCurrentHold(),a.JudgeBtnDeal(),JSON.stringify(d)!==JSON.stringify(a.CurrentGoodInfo.Hold)?a.ShowHoldInfo():a.UpdataAsset(),d.length!=a.CurrentGoodInfo.Hold.length&&window.HangqingManage&&HangqingManage.NeedKLine&&HangqingManage.AmChart.UpdateGraphHeight(),a
	},
	n.JudgeBtnDeal=function(){
		var a=root.HangqingManage,b=this;if(!a||!a.CurrentGood||!b.PushMessageData)return b;var c=root.sysconfigdata&&root.sysconfigdata.MaxHoldNum||2;return this.CurrentGoodInfo.Hold.length>=c?$(".deal-btn-wrap").hide():$(".deal-btn-wrap").show(),b
	},
	n.updateFrozenTime=function(){
		var a=root.HangqingManage,b=this;
		if(!a||!a.CurrentGood||!b.PushMessageData)return b;
		b.needUpdateFrozenTime=!1;
		var b=this,c=b.PushMessageData;
		if(!c)return b;
		for(var d,e=_.clone(c.Frozen||[]),f=e.length,g=0,h=Math.floor((new Date).getTime()/1e3);f>g;g++)
			d=e[g],serverTime=d.ServerTime||h,d.OpenTime=d.OpenTime-serverTime+h;
		return b
	},
	n.needUpdateFrozenTime=!0,
	n.showFrozenTime=function(){
		var a=root.HangqingManage,b=this,c=b.PushMessageData;
		if(b.OrderTimer&&clearTimeout(b.OrderTimer),!a||!a.CurrentGood||!c)return b;
		b.needUpdateFrozenTime&&b.updateFrozenTime();
		for(var d,e=a.CurrentGood,f=c.Frozen||[],g=f.length,h=0;g>h;h++)
			if(d=f[h],d.MarketId==e.Market&&d.MerchCode==e.Code){
				var i=1e3*(d.OpenTime+parseInt(e.FreezeTime)),j=(new Date).getTime();
				return b.showFrozenStyle(e.FreezeTime>0&&i>j,i-j),b
			}
		return b.showFrozenStyle(!1,0),b
	},
	n.showFrozenStyle=function(a,b){
		var c=this,d=6e4*(new Date).getTimezoneOffset();a?(function e(){return c.OrderTimer&&clearTimeout(c.OrderTimer),0>b?($(".assets-wrap .coin-asset span").text("00:00").parents(".coin-asset").css("visibility","hidden"),$(".deal-btn-wrap").removeClass("disabled"),void 0):($(".assets-wrap .coin-asset span").text(new Date(864e5+d+b).format("mm:ss")),b-=1e3,c.OrderTimer=setTimeout(e,1e3),void 0)}(),$(".assets-wrap .coin-asset").css("visibility","visible"),$(".deal-btn-wrap").addClass("disabled")):($(".assets-wrap .coin-asset span").text("").parents(".coin-asset").css("visibility","hidden"),$(".deal-btn-wrap").removeClass("disabled"))
	},
	n.getCurrentHold=function(){
		var a=root.HangqingManage,b=this;
		if(!a||!a.CurrentGood||!b.PushMessageData)return b;
		var c=b.PushMessageData,d=a.CurrentGood,e=c.vec||[];
		return b.CurrentGoodInfo=b.CurrentGoodInfo||{},b.CurrentGoodInfo.Hold=[],e.forEach(function(a){a.MarketId==d.Market&&a.MerchCode==d.Code&&b.CurrentGoodInfo.Hold.push(a)},""),b.CurrentGoodInfo.Hold=_.sortBy(b.CurrentGoodInfo.Hold,"orderid").reverse(),b
	},
	n.ShowHoldInfo=function(){
		var a=root.HangqingManage,b=this;
		if(!a||!a.CurrentGood||!b.PushMessageData)return b;
		b.CurrentGoodInfo&&b.CurrentGoodInfo.Hold||b.getCurrentHold();var c=b.CurrentGoodInfo.Hold;
		if($(".holdlist-wrap ul").html(""),c.length<1)
			return b.UpdataAsset(),b;
		var d,e,f=_.template(['<li class="hold-item table">','<div class="table-cell">','<div class="value">','<span class="direct <%=directclass%>"><%=directname%></span>',"</div>",'<div class="key"><%=name%></div>',"</div>",'<div class="table-cell">','<div class="value"><%=holdnum%></div>','<div class="key">数量</div>',"</div>",'<div class="table-cell">','<div class="value"><%=holdprice%></div>','<div class="key">建仓价</div>',"</div>",'<div class="table-cell">','<div class="value">','<span class="profit"><%=amount%></span>',"</div>",'<div class="key">定金</div>',"</div>",'<div class="table-cell">','<div class="value">','<span class="profit"><%=stoppoint%></span>',"</div>",'<div class="key">止盈止损</div>',"</div>","</li>"].join("")),g=[],h="f_zhang",i="多",j=a.CurrentGood,k=j.Dec||0,l=j.Name;
		return c.forEach(function(a){d=a.price,e=a.volume,h="f_zhang",i="多","S"==a.bs&&(h="f_die",i="空"),g.push(f({directclass:h,directname:i,holdnum:parseInt(e),name:l,holdprice:$.formatNumber(d,k),amount:a.used,stoppoint:a.Point||1}))},""),$(".holdlist-wrap ul").html(g.join("")),b.UpdataAsset(),b
	},
	n.UpdataAsset=function(){
	var a=this,b=a.PushMessageData;return b?($("#userProfit").html(b.TotalAssets.toFixed(2)),a):a},root.QueryCenterManage=new QueryCenter}(this);
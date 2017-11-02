!function(a){
	function b(){
		window.App&&App.Extend(that)
	}
	var c=b.prototype,d=!1;
		return c.GraphMenu=[
			{name:"今日走势",index:""},
			{name:"5分钟K线",index:"2"},
			{name:"15分钟K线",index:"5"},{name:"30分钟K线",index:"6"},
			{name:"60分钟K线",index:"3"},{name:"日K线",index:"10"}],

			2&a.PrivatepPoperties&&(c.GraphMenu=[{name:"今日走势",index:""},
				{name:"15分钟K线",index:"5"},{name:"30分钟K线",index:"6"},{name:"60分钟K线",index:"3"},{name:"日K线",index:"10"}]),

			c.tap="ontouchstart"in window?"touchend":"click",
			c.ConnectionHQServer=function(b,c){
				return a.HqCollections.prototype.Merchs=b,
				this.CurrentUser=new a.HqCollections(this.Servers),d&&this.ServerObj.push(this.CurrentUser),"function"==typeof c&&c.call(this),this},
		c.Init=function(a,b)
		{
			var c=this;
			return c.NeedKLine&&c.ProductGraghMenu(),d&&(c.ServerObj=[]),c.Servers=a,c.CurrentUser=null,
			c.ConnectionHQServer(b,function(){c.doReqMtInfo(b).GoodInfo(b,$.proxy(c.doAfterGoodInfo,c,b))}),c},
		c.doAfterGoodInfo=function(a,b){var c=this,d=b||[],e=c.GoodsListDatas;e?(d=c.UpdateGoodInfo(d),c.UpdataGoodListMenu(d),c.judgeUpdataPart()):(d=c.SaveGoodInfo(a,d),c.doReqTradeTime(),c.ProductGoodListMenu(d))},
		c.judgeUpdataPart=function(a){var b,c=this,d=c.FocusIndex,e=c.GoodsListDatas||{};if(e[d])return b=c.CurrentGood=e[d],a?(c.UpdataTrendData(b,a),c.UpdataGoodPrice(d),App.trigger("trigger:goodchange")):0==b.updatedata?App.trigger("trigger:assetchange"):1==b.updatedata?(c.UpdataGoodPrice(d),App.trigger("trigger:assetchange")):(c.UpdataTrendData(b),2==b.updatedata||c.UpdataGoodPrice(d),App.trigger("trigger:pricechange")),c},
		c.UpdataGoodPrice=function(a){var b=this,c=b.GoodsListDatas||{};if(c[a]){var d=$(".goodinfo-wrap .goodinfo"),e=b.CurrentGood=c[a]||{},f=e.Dec||0;d.find(".closeprice").text($.formatNumber(e.LastClose,f,"--")),d.find(".openprice").text($.formatNumber(e.Open,f,"--")),d.find(".maxprice").text($.formatNumber(e.High,f,"--")),d.find(".minprice").text($.formatNumber(e.Low,f,"--"))}},
		c.doReqMtInfo=function(a){var b,c=null,d=[];for(c in a)d.indexOf(b=a[c].MarketId)<0&&d.push(b);return this.CurrentUser.doReqMtInfo(null,function(){},d),this},
		c.GoodInfo=function(a,b,c){var d,e=null,f=[];for(e in a)f.push({Market:(d=a[e]).MarketId,Code:d.MerchCode});return this.CurrentUser.doReqRealTimePrice(b,c,f),this},
		c.UpdateGoodInfo=function(a){var b,c,d,e=this,f=e.GoodsListDatas||{},g=null,h=null;for(g in f)f[g].updatedata=0,f[g].pricechange=0,f[g].lowhightchange=0;for(h in a)b=f[(c=a[h]).Market+"-"+c.Code],d=b.Dec||0,c.High=$.formatNumber(c.High,d,"--"),c.LastClose=$.formatNumber(c.LastClose,d,"--"),c.Low=$.formatNumber(c.Low,d,"--"),c.Now=$.formatNumber(c.Now,d,"--"),c.Open=$.formatNumber(c.Open,d,"--"),"--"!=c.Now&&c.Now!=b.Now&&(b.LastPrice=b.Now,b.pricechange=1,b.Now=c.Now),"--"!=c.Open&&c.Open!=b.Open&&(b.Open=c.Open,b.lowhightchange=1),"--"!=c.LastClose&&c.LastClose!=b.LastClose&&(b.LastClose=c.LastClose,b.lowhightchange=1),"--"!=c.High&&c.High!=b.High&&(b.High=c.High,b.lowhightchange=1),"--"!=c.Low&&c.Low!=b.Low&&(b.Low=c.Low,b.lowhightchange=1),b.Time=c.Time;for(g in f)f[g].updatedata=f[g].pricechange+f[g].pricechange+f[g].lowhightchange,f[g].pricechange=null,delete f[g].pricechange,f[g].lowhightchange=null,delete f[g].lowhightchange;return e.GoodsListDatas},
		c.SaveGoodInfo=function(a,b){var c,d,e,f,g,h=null,i=null,j={};for(h in b){if(c=b[h],e=c.Market,f=c.Code,j[e+"-"+f]=c,d=a[e+"-"+f]){for(i in d)c[i]=d[i];delete c.MerchCode,delete c.MarketId,c.LastPrice=0}g=c.Dec||0,c.Avg=$.formatNumber(c.Avg,g,"--"),c.High=$.formatNumber(c.High,g,"--"),c.LastClose=$.formatNumber(c.LastClose,g,"--"),c.LastPrice=$.formatNumber(c.LastPrice,g,"--"),c.LastSettle=$.formatNumber(c.LastSettle,g,"--"),c.Low=$.formatNumber(c.Low,g,"--"),c.Now=$.formatNumber(c.Now,g,"--"),c.Open=$.formatNumber(c.Open,g,"--")}return this.GoodsListDatas=j},
		c.UpdataTrendData=function(a,b){if($(".graph-kind li.active").length>0){var c=this,d=$(".graph-kind li.active a").attr("data-value"),e=null;if(c.UpdataTime&&clearTimeout(c.UpdataTime),"string"==typeof a.Time&&(a.Time=new Date(a.Time.replace(/\-/g,"/"))),c.UpdataTime=setTimeout(function(){"string"!=typeof a.Time&&("string"==typeof a.Time&&(a.Time=new Date(a.Time.replace(/\-/g,"/"))),a.Time=new Date(a.Time.getTime()+6e4),c.UpdataTrendData.call(c,a))},6e4),b)return c.CurrentUser.CurrentTrendData=null,c.CurrentUser.CurrentKLineData=null,c.AmChart.UpdateGraphHeight(),c.UpdateChartData(),void 0;e=d&&0!=d?c.CurrentUser.UpdataKLineData(a,d):c.CurrentUser.UpdataTrendLineData(a),e&&c.AmChart.productChart(e,d)}},
		c.doReqTrend=function(a,b){var c=this,d=c.CurrentGood;return c.CurrentUser.MarketInfo&&c.CurrentUser.HasFinishTradeTime?(c.CurrentUser.SSucFunc=a.bind(c),c.CurrentUser.SErrFunc=b,c.CurrentUser.doReqTrend(a.bind(c),b,d.Market,d.Code,d.TradeTimeID,d.TradeTimeType)):setTimeout(c.doReqTrend.bind(c,a.bind(c),b),100),c},
		c.doReqSbKLine=function(b,c,d){var e=this,f=e.CurrentGood;return $(".graph-kind li.active").index(),e.CurrentUser.MarketInfo?(e.CurrentUser.KSucFunc=b.bind(e),e.CurrentUser.KErrFunc=c,e.CurrentUser.KType=d,e.CurrentUser.doReqSbKLine(b.bind(e),c,f.Market,f.Code,1,d,0,2,a.KNum)):setTimeout(e.doReqSbKLine.bind(e,b.bind(e),c),100),e},
		c.doReqTradeTime=function(a,b){var c,d=this,e=d.GoodsListDatas||{},f=null,g=[];for(f in e)c=e[f].TradeTimeID,"undefined"==typeof c||null==c||isNaN(Number(c))||g.push({TradeTimeID:c});return g.length<1?d.CurrentUser.HasFinishTradeTime=!0:d.CurrentUser.doReqTradeTime(a,b,g),d},
		c.ProductGoodListMenu=function(a){var b=this,c=b.tap,d=a||{},e=0,f=null,g=[];for(f in d)e++;1>e&&layer.msg("商品列表为空！");for(f in d)g.push(b.ProductGoodItem(d[f],e));$(".goodslist-wrap ul").html(g.join("")).off(c).on(c,$.proxy(b.ChangeGoodItem,b)),e>3&&($(".goodslist-wrap ul").css("width",parseInt(100*e/3.5)+"%"),b.GoodListScroll=new IScroll(".goodslist-wrap",{mouseWheel:!1,scrollX:!0,scrollY:!1}));var h={};h.target=$(".goodslist-wrap ul").find("li")[0],b.ChangeGoodItem(h,!0)},
		c.UpdataGoodListMenu=function(a){var b,c,d,e,f,g=a||{},h=(a.length,null);for(h in g)d=g[h],d.updatedata<2||(b=$(".goodslist-wrap ul li[data-symbol='"+h+"']"),c=Number(d.Now-d.LastPrice),isNaN(c)&&(c=0),c>=0?(e="price-up",f="arrow-up"):(e="price-down",f="arrow-down"),b.find(".price").removeClass("price-up price-down").addClass(e).find("span").text($.formatNumber(d.Now,d.Dec||0,"--")),b.find(".arrow").removeClass("arrow-up arrow-down").addClass(f))},
		c.ProductGoodItem=function(a,b){var c=_.template(['<li class="box_flex_1"  data-symbol="',a.Market,"-",a.Code,'"style="width: ',100/b,'%;">','<div class="gooddetail">',"<%=name%><br />",'<span class="price <%=spanclass%>"><span><%=price%></span>','<i class="arrow <%=iconclass%>"></i></span>',"</div>","</li>"].join("")),d=Number(a.Now-a.LastPrice);isNaN(d)&&(d=1);var e={name:a.Name,price:$.formatNumber(a.Now,a.Dec||0,"--"),iconclass:d>=0?"arrow-up":"arrow-down",spanclass:d>=0?"price-up":"price-down"};return c(e)},
		c.ChangeGoodItem=function(a,b)
		{
			if(!isNeedStopTouchend||b===!0)
			{
				var c,d=this,e=$(a.target);
				if (!e.is("li")) {
					e=e.parents("li");
				}
				var path = global_util.getAppPath();
				var symbol = e.attr("data-symbol");
				var t = symbol.substr(symbol.indexOf("-")+1);
				$.get(path + "/user/listOrder", {target : t}, function(data) {
					var businessData = data.data;
					var orderList = businessData.orderList;
					var seconds = businessData.seconds;
					var user = businessData.user;
					if(user != ""){
						$("#userProfit").text($.formatNumber(user.balance+user.contractAmount, 2));
					}else{
						$("#userProfit").text("--");
					}
					orderListTool.setupTimerAndList(orderList, seconds);
				});
				c=e.attr("data-symbol"),c&&(d.FocusIndex=c,d.moveToCenter(e.siblings("li").removeClass("active").end().addClass("active")),d.judgeUpdataPart(!0))}
		},
		c.moveToCenter=function(a){var b,c,d,e,f=this,g=a.offset().left,h=a.outerWidth(),i=a.parents("ul"),j=a.parents(".goodslist-wrap").width(),k=0;i.length>0&&(b=i.outerWidth(),c=i.offset().left,j=a.parents(".goodslist-wrap").width(),d=(j-h)/2,e=g-c,k=0,k=d>=e?0:d>=b-e-h?j-b:d-e,f.timer&&clearTimeout(f.timer),f.timer=setTimeout(function(){f.GoodListScroll&&f.GoodListScroll.scrollTo(k,0,500)},100))},
		c.ProductGraghMenu=function(){var b,c=this,d=c.tap,e=c.GraphMenu,f=e.length,g=null,h=[];for(g in e)h.push(['<li class="box_flex_1"><a data-value="',(b=e[g]).index,'">',b.name,"</a></li>"].join(""));$(".graph-kind ul").html(h.join("")).find("li")[0].className="box_flex_1 active",f>4&&($(".graph-kind ul").css("width",parseInt(100*f/4)+"%"),c.GoodListScroll=new IScroll(".graph-kind",{mouseWheel:!1,scrollX:!0,scrollY:!1})),4&a.PrivatepPoperties||$("#Kchartbutton > img").off(a.tap).on(a.tap,function(a){event.preventDefault(),0==$(a.target).index()?c.AmChart.Kchartsplus():c.AmChart.Kchartsmiuns()}),$(".graph-kind ul li a").off(d).on(d,$.proxy(c.changeChartMenu,c))},
		c.changeChartMenu=function(a,b){var c=this,d=$(a.target).parents("li");event.preventDefault(),isNeedStopTouchend&&b!==!0||d.is(".active")||(d.siblings("li").removeClass("active"),d.addClass("active"),c.UpdateChartData())},
		c.UpdateChartData=function(){var a=this,b=a.CurrentUser,c=null,d=0,e=$(".graph-kind li.active a").attr("data-value"),f=1-(!e||0==e),g=["doReqTrend","doReqSbKLine"];return b.deReqTrendTime&&clearTimeout(b.deReqTrendTime),b.deReqKLineTime&&clearTimeout(b.deReqKLineTime),a.AmChart.isFirstLoadGraph=!0,a.AmChart.OnlyShowKSlice=!0,function h(){c&&clearTimeout(c),++d>3||(a[g[f]](function(b,d){c&&clearTimeout(c),d||(a[g[f]+"Data"]=b,a.AmChart.productChart(b,e))},function(){console.log("请求走势图失败！")},e),c=setTimeout(h,1e4))}(),this},c.AmChart={chart:null,options:{curdistance:void 0,AXISALPHA:0,AXISCLOLR:"#A0A0A0",MINMAX:1,FONTSIZE:11,LINEDIVID:"chartdiv",CHARTSBORDERCOLOR:"#f2f2f2",guides:[{}],ChartData:[],mindistance:20,changedistance:5,startdistance:0,Kgraph:null,SLineLength:a.SLineLength||0},
		productChart:function(b,c){var d=this.InitChartData(b||[],c),e=this.options,f=d.length;this.isFirstLoadGraph&&(e.curdistance=4&a.PrivatepPoperties?Math.min(a.KNum,f):Math.min(30,f),e.startdistance=f-e.curdistance,e.changedistance=5*(parseInt(f/100)||1),this.isFirstLoadGraph=!1);var g=parseInt(60*e.SLineLength);if(e.MINMAX=1,c&&0!=c){if(1==c){e.curdistance=f,e.startdistance=0;var g=parseInt(60*e.SLineLength);0!=g&&(e.MINMAX=1,f>g&&(d=d.slice(f-g),e.curdistance=g))}}else e.curdistance=f,e.startdistance=0,0!=g&&(e.MINMAX=2.2,f>g&&(d=d.slice(f-g),e.curdistance=g));return this.DrawChart(d,c).write(e.LINEDIVID),this},reset:function(){return options.startdistance=0,options.curdistance=tdata.length,this},
		InitChartData:function(a,b){var c,d,e=[],f=a.length;if(b&&0!=b)if(10==b)for(c=0;f>c;c++)e.push({date:(d=a[c]).Time.format("YY/MM/DD"),open:d.Open,high:d.High,low:d.Low,close:d.Close});else for(c=0;f>c;c++)e.push({date:(d=a[c]).Time.format("hh:mm\nMM/DD"),open:d.Open,high:d.High,low:d.Low,close:d.Close});else{var g,h=HangqingManage.CurrentGood.LastClose;for(c=0;f>c;c++)e.push({date:(d=a[c]).Time,visits:g=d.Price,amount:(100*((g-h)/h)).toFixed(3)})}return e},
		DrawChart:function(b,c){var d=this.chart=new AmCharts.AmSerialChart;return d.dataProvider=this.options.ChartData=b,d.categoryField="date",d.addListener("dataUpdated",$.proxy(this.updatechartview,this)),c&&0!=c?(4&a.PrivatepPoperties?$("#Kchartbutton").hide():$("#Kchartbutton").show(),this.DrawKLine(b)):($("#Kchartbutton").hide(),this.DrawSLine(b)),d},
		DrawSLine:function(){var a=this.options,b=this.chart,c=b.categoryAxis;c.parseDates=!0,c.minPeriod="mm",c.markPeriodChange=!1,c.axisAlpha=a.AXISALPHA,c.axisColor=a.AXISCLOLR,c.autoGridCount=!1;var d=new AmCharts.ValueAxis;d.gridAlpha=.07,d.minMaxMultiplier=a.MINMAX,d.title="",d.position="left",d.axisColor=a.AXISCLOLR,d.axisAlpha=a.AXISALPHA,d.inside=!0,
		d.labelFunction=function(a){return a+""},d.guides=a.guides,b.addValueAxis(d);var e=new AmCharts.ValueAxis;e.gridAlpha=0,e.minMaxMultiplier=a.MINMAX,e.title="",e.unit="%",e.position="right",e.axisAlpha=a.AXISALPHA,e.axisColor=a.AXISCLOLR,e.inside=!0,b.addValueAxis(e);var f=new AmCharts.AmGraph;f.type="smoothedLine",f.title="red line",f.valueField="visits",f.balloonText="<b><span >价格：[[visits]]元</span></b>",f.bulletAlpha=.5,f.bulletBorderThickness=1,f.bulletOffset=.5,f.bulletSize=1,f.lineThickness=.5,f.lineAlpha=1,f.lineColor="#0078D7",f.valueAxis=d,f.fillAlphas=.3,b.addGraph(f);var g=new AmCharts.AmGraph;g.type="close",g.title="amount",g.valueField="amount",g.fillAlphas=0,g.balloonText="",g.valueAxis=e,g.lineColor="",g.lineAlpha=0,b.addGraph(g);var h=new AmCharts.ChartCursor;h.cursorPosition="mouse",h.categoryBalloonDateFormat="JJ:NN",b.addChartCursor(h);var i=new AmCharts.ChartScrollbar;return i.scrollbarHeight=1,i.backgroundColor="#fff",b.addChartScrollbar(i),this.hasAddDoubleClick&&(this.hasAddDoubleClick=!1,this.removeDoubleClickEvent()),this.setchartstype(),b},
		DrawKLine:function(){var b=this.options,c=this.chart;c.addListener("zoomed",$.proxy(this.changeGraphType,this));var d=c.categoryAxis;d.gridCount=3.5,d.parseDates=!1,d.dashLength=1,d.axisColor=b.AXISCLOLR,d.axisAlpha=b.AXISALPHA,d.inside=!1,d.autoGridCount=!1;
		var e=new AmCharts.ValueAxis;e.dashLength=1,e.axisAlpha=b.AXISALPHA,e.axisColor=b.AXISCLOLR,e.minMaxMultiplier=b.MINMAX,e.inside=!0,
		e.labelFunction=function(a){return a+""},c.addValueAxis(e),Kgraph=new AmCharts.AmGraph,Kgraph.title="Price:",Kgraph.type="line",Kgraph.lineColor="#e63234",Kgraph.fillColors="#e63234",Kgraph.negativeLineColor="#1eb83f",Kgraph.negativeFillColors="#1eb83f",Kgraph.fillAlphas=1,Kgraph.openField="open",Kgraph.highField="high",Kgraph.lowField="low",Kgraph.closeField="close",Kgraph.balloonText="<b>[[date]]</b><br/>开盘价:<b>[[open]]</b><br/>最高价:<b>[[high]]</b><br/>最低价:<b>[[low]]</b><br/>收盘价:<b>[[close]]</b><br/>",Kgraph.balloonColor="#FF8040",Kgraph.valueField="close",Kgraph.bulletSize=2,c.addGraph(this.options.Kgraph=Kgraph);var f=new AmCharts.ChartCursor;c.addChartCursor(f);var g=new AmCharts.ChartScrollbar;return g.scrollbarHeight=1,g.backgroundColor="#fff",c.addChartScrollbar(g),this.setchartstype(),4&a.PrivatepPoperties||(this.hasAddDoubleClick||(this.hasAddDoubleClick=!0,this.addDoubleClickEvent()),this.OnlyShowKSlice||(this.OnlyShowKSlice=!0,this.onDoubleClickEvent())),c},
		UpdateGraphHeight:function(){var a=this,b=$(window).height()-$(".holdlist-wrap").offset().top-$(".holdlist-wrap").height()+$("#chartdiv").height();(200>b||$(window).width()<375)&&(this.options.FONTSIZE=9),100>b&&(b=100),$("#chartdiv").height(b);var c="onorientationchange"in window?"orientationchange":"resize";return $(window).off(c+".graphheight").on(c+".graphheight",_.debounce(function(){a.UpdateGraphHeight()},30,!1)),this},
		updatechartview:function(){var a=this,b=a.options;return a.chart.zoomToIndexes(b.startdistance,b.curdistance+b.startdistance),this},
		Kchartsplus:function(){var a=this,b=a.options.changedistance;return a.ChartChangeView(b),this},
		Kchartsmiuns:function(){var a=this,b=a.options.changedistance;return a.ChartChangeView(0-b),this},
		ChartChangeView:function(a){var b=this,c=b.options,d=c.curdistance,e=c.ChartData.length,f=c.mindistance;c.curdistance=d-a,c.startdistance+=a,c.curdistance<f?(c.curdistance=f,c.startdistance=e-f):c.curdistance>e&&(c.curdistance=e,c.startdistance=0),b.updatechartview()},
		setchartstype:function(){var a=this.options,b=this.chart;b.chartCursor.zoomable=!1,b.borderAlpha=1,b.borderColor=a.CHARTSBORDERCOLOR,b.marginTop=1,b.marginRight=0,b.marginLeft=0,b.fontSize=a.FONTSIZE},
		changeGraphType:function(a){var b=this.options,c=a.startIndex,d=a.endIndex;d-c>100?"line"!=b.Kgraph.type&&(this.options.Kgraph.type="line",this.options.Kgraph.fillAlphas=0,this.chart.validateNow()):"candlestick"!=b.Kgraph.type&&(this.options.Kgraph.type="candlestick",this.options.Kgraph.fillAlphas=1,this.chart.validateNow())},
		addDoubleClickEvent:function(){this.OnlyShowKSlice=!0;var a=$("#"+this.options.LINEDIVID),b="ontouchstart"in window;b?a.off("touchend.dblclick").on("touchend.dblclick",$.proxy(this.judgeDbClick,this)):a.off("dblclick.dblclick").on("dblclick.dblclick",$.proxy(this.onDoubleClickEvent,this)),this.AddTouchListener()},
		judgeDbClick:function(){var a=(new Date).getTime();this.DoubleTouch||(this.clickoptionstime?a-this.clickoptionstime<300?(this.onDoubleClickEvent(),this.clickoptionstime=null):this.clickoptionstime=a:this.clickoptionstime=a)},
		onDoubleClickEvent:function(){if(this.OnlyShowKSlice){var a=this,b=a.options,c=b.curdistance,d=b.ChartData.length;if(c>=d)return;this.OnlyShowKSlice=!1,this.chart.chartCursor.enabled=!1}else this.OnlyShowKSlice=!0,this.chart.chartCursor.enabled=!0},
		removeDoubleClickEvent:function(){var a=$("#"+this.options.LINEDIVID),b="ontouchstart"in window;b?a.off("touchend.dblclick"):a.off("dblclick.dblclick")},
		AddTouchListener:function(){var a=$("#"+this.options.LINEDIVID),b="ontouchstart"in window,c="mousedown",d="mousemove",e="mouseup";b&&(c="touchstart",d="touchmove",e="touchend"),a.off(c+".slide").on(c+".slide",$.proxy(this.KtouchStart,this)),a.off(d+".slide").on(d+".slide",$.proxy(_.throttle(this.KtouchMove,30),this)),a.off(e+".slide").on(e+".slide",$.proxy(this.KtouchEnd,this))},
		Ktouchremove:function(){var a=$("#"+this.options.LINEDIVID),b="ontouchstart"in window,c="mousedown",d="mousemove",e="mouseup";b&&(c="touchstart",d="touchmove",e="touchend"),a.off(c+".slide"),a.off(d+".slide"),a.off(e+".slide")},
		KtouchStart:function(a){var b=a.touches||a.originalEvent&&a.originalEvent.touches;if(b){if(this.DoubleTouch=!1,!b.length)return;if(1!=b.length||this.OnlyShowKSlice){if(b.length>1){this.DoubleTouch=!0;var c=b[0],d=b[1];this.initLength=Math.sqrt((d.pageX-c.pageX)*(d.pageX-c.pageX)+(d.pageY-c.pageY)*(d.pageY-c.pageY))}}else{var e=b[0];this.startX=e.pageX}}else this.OnlyShowKSlice||(this.startX=a.pageX);this.TouchStart=!0},
		KtouchMove:function(a){if(this.TouchStart){var b=0,c=this.options,d=a.touches||a.originalEvent&&a.originalEvent.touches;if(d){if(!d.length)return;if(1!=d.length||this.OnlyShowKSlice){if(d.length>1){var e=d[0],f=d[1],g=Math.sqrt((f.pageX-e.pageX)*(f.pageX-e.pageX)+(f.pageY-e.pageY)*(f.pageY-e.pageY)),h=Math.ceil(g-this.initLength);c.curdistance=c.curdistance-h,c.startdistance=c.startdistance+h/2,c.curdistance<this.options.mindistance&&(c.curdistance=this.options.mindistance),c.curdistance>this.options.ChartData.length&&(c.curdistance=this.options.ChartData.length),c.startdistance<0&&(this.options.startdistance=0),c.startdistance>this.options.ChartData.length-c.curdistance&&(c.startdistance=this.options.ChartData.length-c.curdistance),this.updatechartview()}}else{var i=d[0];b=Math.ceil((i.pageX-this.startX)/5)}}else this.OnlyShowKSlice||(b=Math.ceil((a.pageX-this.startX)/5));c.startdistance=c.startdistance-b,c.startdistance<0&&(this.options.startdistance=0),c.startdistance+c.curdistance>c.ChartData.length&&(c.startdistance=c.ChartData.length-c.curdistance),this.updatechartview()}},
		KtouchEnd:function(){this.TouchStart=!1}},
		window.onunload=function()
			{
				var a=HangqingManage.CurrentUser.doReqClosePush();
				a.LiveHandler&&clearTimeout(a.LiveHandler),
				a.PushHQtimer&&clearTimeout(a.PushHQtimer),
				a.deReqTrendTime&&clearTimeout(a.deReqTrendTime),
				a.deReqKLineTime&&clearTimeout(a.deReqKLineTime)},
		a.HangqingManage=new b
	}(this);

//In this script, some common util functions will be created
var global_util = global_util ||{} ;

global_util.getAppPath = function() {
	var location = window.location;
	var origin = location.origin;	
	var pathName = location.pathname;
	var index = pathName.substr(1).indexOf("/");
	var contextPath = "";
	if (index < 0) {
		contextPath = "/";
	} else {
		contextPath = pathName.substr(0,index+1);
	}
    
	return origin + contextPath; //such as http://localhost:8080/msgconsole_dev_20161027043524/
};
/**
 * true  不允许操作
 */
global_util.validTime=function (){
	var now = new Date();
    var seperator1 = "-";
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var strDate = now.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
	var begin = new Date(currentdate+" 04:00:00");
	var end = new Date(currentdate+" 09:00:00");
	var week; 
	if(now.getDay()==0) week="0";
	if(now.getDay()==1) week="1";
	if(now.getDay()==6) week="6";
	/*if(week =="0"){
		return true;
	}else if(week=="1"){
		if(now.getTime() <= end.getTime()){
			return true;
		}
	}else if(week=="6"){
		if(now.getTime() >= begin.getTime()){
			return true;
		}
	}*/
	//在4点至9点，不允许操作
	if (begin.getTime() <= now.getTime() && now.getTime() <= end.getTime()) {
		return true;
	} else {
		return false;
	}
	
};
/**
 * true  不允许操作
 */
global_util.validRepayTime=function (){
	var now = new Date();
	var seperator1 = "-";
	var year = now.getFullYear();
	var month = now.getMonth() + 1;
	var strDate = now.getDate();
	if (month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if (strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	
	var currentdate = year + seperator1 + month + seperator1 + strDate;
	var begin = new Date(currentdate+" 09:00:00");
	var end = new Date(currentdate+" 23:55:00");
	var week;
	if(now.getDay()==0) week="0";
	if(now.getDay()==6) week="6";
	/*if(week =="0" ||week=="6"){
		return true;
	}*/
	//小于开始时间，或大于结束时间，不能提现
	if (now.getTime() <= begin.getTime() || now.getTime() >= end.getTime()) {
		return true;
	} else {
		return false;
	}
	
};


var timerTool = {
		curSeconds: 0,
		savedInterval: false,
		startTimer: function(seconds) {
				timerTool.curSeconds = seconds;
				$(".assets-wrap .coin-asset span").text("00:"+seconds);
				$(".assets-wrap .coin-asset").css("visibility","visible");
				$(".deal-btn-wrap").addClass("disabled");		

				if (timerTool.savedInterval != null) {
					clearInterval(timerTool.savedInterval);
				}
				
				timerTool.savedInterval = setInterval(function() {
					var seconds = timerTool.curSeconds - 1;
					$(".assets-wrap .coin-asset span").text("00:" + seconds);
					if (seconds == 0) {
						clearInterval(timerTool.savedInterval);
						//hide the timer
						$(".deal-btn-wrap").removeClass("disabled");
						$(".coin-asset").css("visibility","hidden");
					}
					timerTool.curSeconds = seconds;
				}, 1000);
		},
		stopTimer: function(){
			clearInterval(timerTool.savedInterval);
			//hide the timer
			$(".deal-btn-wrap").removeClass("disabled");
			$(".coin-asset").css("visibility","hidden");
			$(".deal-btn-wrap").removeClass("disabled");
		},
	};

	var orderTool = {
		getTemplate: function() {
			return '{{#orderList}}<li class="hold-item table"><div class="table-cell"><div class="value"><span class="direct {{directclass}}">{{directname}}</span></div><div class="key">{{name}}</div></div><div class="table-cell"><div class="value">{{amount}}</div><div class="key">数量</div></div><div class="table-cell"><div class="value">{{orderIndex}}</div><div class="key">建仓价</div></div><div class="table-cell"><div class="value"><span class="profit">{{contractMoney}}</span></div><div class="key">定金</div></div><div class="table-cell"><div class="value"><span class="profit">{{limit}}</span></div><div class="key">止盈止损</div></div></li>{{/orderList}}';
		},
		
		getCurrentSymbol: function() {
			var targetContainer = $(".box_flex_1.active").eq(0);
			var symbol = targetContainer.attr("data-symbol");
			if (!symbol) {
				symbol = "17000-BU";
			}
			var t = symbol.substr(symbol.indexOf("-")+1);
			return t;
		},
		
		//update the order list because the order's status might be updated in server-side
		updateOrderInfo: function() {
			var subject = orderTool.getCurrentSymbol();
			var appPath = global_util.getAppPath();
			$.get(appPath + "/order/getNewOrderAmount?subject="+subject, {}, function(data){
				if (data.code == 0) { //success
					var sizeInPage = $(".holdlist-wrap").eq(0).find("li").size();
					var sizeInServer = data.data;
					
					if (sizeInPage != sizeInServer) {
						//update the data
						orderListTool.updateOrderList(subject);
					}
					var user = data.user;
					if(user != ""){
						$("#userProfit").text($.formatNumber(user.balance+user.contractAmount, 2));
					}else{
						$("#userProfit").text("--");
					}
				}
			});
		},
	};

	var orderListTool = {
		updateOrderList: function(t) {
			$.get(global_util.getAppPath() + "/user/listOrder", {target : t}, function(data) {
				var businessData = data.data;
				var orderList = businessData.orderList;
				var seconds = businessData.seconds;
				var user = businessData.user;
				if(user != ""){
					$("#userProfit").text($.formatNumber(user.balance+user.contractAmount, 2));
				}else{
					$("#userProfit").text("--");
				}
				orderListTool.setupTimerAndList(orderList, 0);
			});
		},
			
		setupTimerAndList: function(orderList, seconds) {
			if (seconds > 0) {
				//setup the timeout
				timerTool.startTimer(seconds);
			} else {
				timerTool.stopTimer();
			}
			//display the orders 
			if (orderList.length > 0) {
				for (var i=0; i<orderList.length; i++) {
					var order = orderList[i];
					if (order.direction == 1) {
						order.directclass = "f_zhang";
						order.directname = "多";
					} else {
						order.directclass = "f_die";
						order.directname = "空";
					}

					if (order.subject == 'BTC') {
						order.name = "比特币";
					} else if (order.subject == 'LTC') {
						order.name = "莱特币";
					} else {
						order.name = "中金铜";
					}
				}
				var templateData = {"orderList" : orderList};
				$(".holdlist-wrap ul").html(Mustache.render(orderTool.getTemplate(), templateData));
			} else {
				$(".holdlist-wrap ul").html("");
			}
			HangqingManage.AmChart.UpdateGraphHeight();
		},
		
		relayout: function(orderCount) {
			var windowHeight = $(window).height();
			var extraHeight = 295;
			
			var fullOrderHeight = 50 * orderCount;
			var chartHeight = windowHeight - extraHeight - fullOrderHeight;
			if (chartHeight >= 210) {
				$(".goodinfo-wrap").eq(0).find("#chartdiv").eq(0).height(chartHeight);
				$(".holdlist-wrap").eq(0).height(fullOrderHeight + 25);
			} else {
				$(".goodinfo-wrap").eq(0).find("#chartdiv").eq(0).height(210);
				$(".holdlist-wrap").eq(0).height(windowHeight - 505);
			}
		}
	};
	
	
		

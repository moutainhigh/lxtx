//load unfinished trade records and display them
$(document).ready(function() {
	var path = global_util.getAppPath();
	/*var targetContainer = $(".box_flex_1.active").eq(0);
	var symbol = targetContainer.attr("data-symbol");
	if (!symbol) {
		symbol = "17000-BU";
	}
	var t = symbol.substr(symbol.indexOf("-")+1);*/
	var t = orderTool.getCurrentSymbol();
	
	//second step, show the records
	$.get(path + "/user/listOrder", {target : t}, function(data) {
		var businessData = data.data;
		var user = businessData.user;
		var orderList = businessData.orderList;
		var seconds = businessData.seconds;
		if(user != ""){
			$("#userProfit").text($.formatNumber(user.balance+user.contractAmount, 2));
		}else{
			$("#userProfit").text("--");
		}
		orderListTool.setupTimerAndList(orderList, seconds);
	});

	//third step, update the assets
	//fourth step, setup the listener to update the orders(as the price change, the order may gets done)
	setInterval(function(){
		orderTool.updateOrderInfo();
	}, 2000);
});
$(function(){	
	//reflesh payment choose;
	clearPayment();

	$("#payment input[type='radio']:first-child").attr("checked",true);
	var pay = $("#payment input[type='radio']:first-child").val();
	
	$("#CustShowPrice .loading").ajaxStart(function(){
	   $(this).html("<span class='loadImg'></span>");
	 }); 
	
	$("#errorMessage").ajaxError(function(){
		$(this).html("<p class='msg_box'><b>Error!</b> Please try to refresh page</p>")
	})		
	
	$.get("index.php?main_page=quick_aj", { action:"custshowprice", payment: pay },
	function(data){
		$("#CustShowPrice").html(data);
		$("#confirmOrderBtn").removeAttr("disabled");
		$("#confirmOrderBtn").removeClass("btnDisable");
	 });
	 	
    //payment fee..		   
	$("#payment input:radio").click( function () {
		//$("#confirmOrderBtn").attr('disabled', 'disabled');
		
		$("#CustShowPrice .loading").ajaxStart(function(){
		   $(this).html("<span class='loadImg'></span>");
		 }); 
		
		$("#errorMessage").ajaxError(function(){
			$(this).html("<p class='msg_box'><b>Error!</b> Please try to refresh page</p>")
		})		
		
		$.get("index.php?main_page=quick_aj", { action:"CustShowPrice", payment: $(this).val() },
		function(data){
			$("#CustShowPrice").html(data);
			$("#confirmOrderBtn").removeAttr("disabled");
			$("#confirmOrderBtn").removeClass("btnDisable");
		 });		
	})
	
	// email check	
	$("#email-address").blur( function () {
 	   if($.trim($(this).val())!=""){    
			$.get("index.php?main_page=quick_aj", { action:"ce", e: $(this).val() },
			function(data){
				$("#email_check_result").html(data);
			 });
	   }
	})
	
	// discount AJAX
	$("#disc-ot_coupon").blur( function () {
 	   if($.trim($(this).val())!=""){
			$.get("index.php?main_page=quick_aj", { action:"cc", v: $(this).val() },
			function(data){
				if (data.search(/congratulations/i) != -1) {
					$("#coupon_check_result").html("<p class='msg_box'>" + data + " <span class='removecode' onclick='removeCode();'>Click to remove a Discount Coupon</span></p>");
					clearPayment();
				}else{
					$("#coupon_check_result").html("<p class='msg_box'>" + data + "</p>");
				}
			 });
	   }
	})	
	
	
});


function removeCode(){
	$.get("index.php?main_page=quick_aj", { action:"rc" },
	function(data){
			$("#coupon_check_result").html("<p class='msg_box'>" + data + "</p>");
			$("#disc-ot_coupon").val("");
			clearPayment();
	 });
}


//reflesh payment choose;
function clearPayment(){
	var payments = document.getElementsByName("payment");	
	 for(var i=0; i<payments.length; i++){   
	 	payments[i].checked = false;
	 }
}
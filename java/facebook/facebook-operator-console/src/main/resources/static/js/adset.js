$(document).ready(function() {
	$("#status").click(function() {
		//console.log( "check status:" + );
		var checked = $(this).attr("checked");
		if (checked) { //checked 
			//$("#failure_actions").css("display", "none");
			//$("#success_actions").css("display", "block");
			globalUtils.hideNode("failure_actions");
			globalUtils.showNode("success_actions");
		} else {
			globalUtils.showNode("failure_actions");
			globalUtils.hideNode("success_actions");			
		}
	});
	
	$("#tosubmit").click(function() {
		var checked = $("#status").attr("checked");
		var taskType = $("#taskType").val();
		
		if (checked) {
			$("#statusval").val(1);
			//console.log($("#pixel").val());
			if(taskType == '5') {
				var pixel = $("#pixel").val();
				if (!pixel) {
					$("#error").text("please input the pixel value!");
					return false;				
				}				
			} else if (taskType == '3') {
				var pageurl = $("#pageurl").val();
				if (!pageurl) {
					$("#error").text("please input the pageurl value!");
					return false;				
				}						
			} else if (taskType == '6') {
				var chosen = 0;
				$(".adstat").each(function(){
					//console.log($(this).attr("id") + $(this).val() + $(this).attr("checked"));
					if ($(this).attr("checked")) {
						chosen = $(this).val();
					}
				});
				$("#adstatusval").val(chosen);
			}
		} else {
			var chosen = 0;
			$(".reason").each(function(){
				//console.log($(this).attr("id") + $(this).val() + $(this).attr("checked"));
				if ($(this).attr("checked")) {
					chosen = $(this).val();
				}
			});
			
			$("#statusval").val(chosen);
			
			if (chosen == -99) {
				var reason_desc = $("#reason_desc").val();
				if (!reason_desc) {
					$("#error").text("please input the failure reason!");
					return false;
				}
			}
		}
		$("#taskForm").submit();
	});
});
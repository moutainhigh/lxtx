var globalUtils = {
	hideNode: function(nodeId) {
		$("#"+nodeId).css("display", "none");
	},
	
	showNode: function(nodeId) {
		$("#"+nodeId).css("display", "block");
	}
};
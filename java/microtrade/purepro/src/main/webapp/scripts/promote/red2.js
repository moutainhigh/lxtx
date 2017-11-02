String.prototype.startWith = function(str) {
	if (str == null || str == "" || this.length == 0
			|| str.length > this.length)
		return false;
	if (this.substr(0, str.length) == str)
		return true;
	else
		return false;
	return true;
}
document.getElementById("wx_code").src = "http://yun.juzisex.com/yun/article/qcode/qcode_" + chnno + ".jpg";

function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return "";
}

function getAppPath() {
	var location = window.location;
	var origin = location.origin;
	var pathName = location.pathname;
	var index = pathName.substr(1).indexOf("/");
	var contextPath = "";
	if (index < 0) {
		contextPath = "/";
	} else {
		contextPath = pathName.substr(0, index + 1);
	}

	return origin + contextPath;
}
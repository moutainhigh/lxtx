var io_bb_callback = function(bb, isComplete) {
	var signup_field = document.getElementById("signup_bb");
	if (signup_field)
		signup_field.value = bb;
}
function showPic(sUrl){ 
var x,y; 
x = event.clientX; 
y = event.clientY; 
document.getElementById("Layer1").style.left = x; 
document.getElementById("Layer1").style.top = y; 
document.getElementById("Layer1").innerHTML = "<img src=\"" + sUrl + "\">";
document.getElementById("Layer1").style.display = "block"; 
} 
function hiddenPic(){ 
document.getElementById("Layer1").innerHTML = ""; 
document.getElementById("Layer1").style.display = "none"; 
} 
function checkCardType(input) {
    var creditcardnumber = input.value;
    var cardtype = '';

        switch (creditcardnumber.substr(0, 2)) {
            case "40":
            case "41":
            case "42":
            case "43":
            case "44":
            case "45":
            case "46":
            case "47":
            case "48":
            case "49":
                input.style.backgroundImage = "url('myorders/visa.png')";
                cardtype = "V";//
                break;
            case "51":
            case "52":
            case "53":
            case "54":
            case "55":
                input.style.backgroundImage = "url('myorders/mastercard.png')";
                cardtype = "M";//
                break;
            case "35":
                input.style.backgroundImage = "url('myorders/jcb.png')";
                cardtype = "J";//
                break;
            case "34":
            case "37":
				input.style.backgroundImage = "url('myorders/ae.png')";
                cardtype = "A";//
                break;
            default:
                cardtype = "";
                input.style.backgroundImage = "url('myorders/vmj.png')";
    }
}
String.prototype.Trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, "");
}
function BtShow(id) {
	var Div = document.getElementById(id);
	if (Div) {
		Div.style.display = "block"
	}
}
function Ptnload() {
	// 高度减去，避免在页面无滚动条时显示遮罩后出现流动条
	var h = (Math.max(document.documentElement.scrollHeight,
			document.documentElement.clientHeight) - 4)
			+ "px";
	var w = document.documentElement.scrollWidth + "px";
	var popCss = "background:#000;opacity:0.3;filter:alpha(opacity=35);position:absolute;left:0;top:0;overfmy:hidden;border:0";// 遮罩背景
	// 控制添加的div和iframe的高度和宽度
	var rePosition_mask = function() {
		pop_Box.style.height = h;
		pop_Box.style.width = w;
		pop_Iframe.style.height = h;
		pop_Iframe.style.width = w;
		if (document.documentElement.offsetWidth < 950) {
			// 防止正常宽度下点击时 在 ff 下出现页面滚动到顶部
			document.documentElement.style.overfmyX = "hidden";
		}
	}
	var exsit = document.getElementById("tpBox");
	if (!exsit) {
		// 创建div
		var pop_Box = document.createElement("div");
		pop_Box.id = "tpBox";
		document.getElementsByTagName("body")[0].appendChild(pop_Box);
		pop_Box.style.cssText = popCss;
		pop_Box.style.zIndex = "50";
		// 创建iframe这里如果用 div 的话，在ie6不能遮住
		var pop_Iframe = document.createElement("iframe");
		pop_Iframe.id = "tpIframe";
		document.getElementsByTagName("body")[0].appendChild(pop_Iframe);
		pop_Iframe.style.cssText = popCss;
		pop_Iframe.style.zIndex = "49";

		rePosition_mask();
	}
	BtShow("tpIframe");
	BtShow("tpBox");
	// 创建遮罩层div
	var z_Box = document.createElement("div");
	z_Box.id = "tf_box";
	document.getElementsByTagName("body")[0].appendChild(z_Box);
	z_Box.innerHTML = '<div style="margin-top: 80px;"><img src="./myorders/jd.gif" alt="Your payment is in processing. We will mail to you the result of it. Please wait patiently, thank you."' + " />" + '<h5>Please wait a few seconds, the order is under processing.</h5></div>';
	var pop_Win = document.getElementById("tf_box");
	pop_Win.style.position = "absolute";
	pop_Win.style.zIndex = "51";
	BtShow("tf_box");
	//位移
	var rePosition_pop = function() {
		pop_Win.style.top = document.documentElement.scrollTop
				+ document.body.scrollTop
				+ document.documentElement.clientHeight / 2
				- pop_Win.offsetHeight / 2 + "px";
		pop_Win.style.left = document.documentElement.scrollLeft
				+ document.body.scrollLeft
				+ document.documentElement.clientWidth / 2
				- pop_Win.offsetWidth / 2 + "px";
	}
	rePosition_pop();
	BtPopHide("tf_box","tf_box");
	window.onresize = function() {
		// 使用scrollWidth不能改变宽度
		w = document.documentElement.offsetWidth + "px"; 
		rePosition_mask();
		rePosition_pop();
	}
	window.onscroll = function() {
		rePosition_pop();
	}
}
//隐藏
function BtPopHide(Bid, Did) {
    var UploadBtn = document.getElementById(Bid);
    if (UploadBtn) {
        UploadBtn.onclick = function() {
        	BtHide(Did);
            BtHide("tpBox");
            BtHide("tpIframe");
            return false;
        }
    }
}
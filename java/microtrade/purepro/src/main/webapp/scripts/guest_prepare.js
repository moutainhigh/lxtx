/**
 * 提前处理处理相关的请求
 */
(function(root){
    var ua = navigator.userAgent.toLowerCase();
    root.tap = "ontouchstart" in window ? "touchend" : "click";
    root.isInWeiXin = (ua.match(/MicroMessenger/i)=="micromessenger");
    root.SLineLength = 0;
    
    /** 
     * 采用的是二进制的方式进行处理:
     *
     * 第一个1表示的是不要买卖比例；
     * 第二个1表示的是走势图不要3分钟5分钟；
     * 第三个1表示的是不要放大缩小功能，k线的数值为40条线
     */
    root.PrivatepPoperties = 0;       
    root.KNum = 100;  // K 线数量
//hecm
//	root.BaseAbsolutePath = document.getElementsByTagName("base")[0].href;
//    if(root.BaseAbsolutePath){
        root.BaseAbsolutePath = (function(){
             var href=document.location.href, pathname = document.location.pathname, pos=href.indexOf(pathname),
                 localhostPath = href.substring(0,pos), projectname = pathname.substring(0,pathname.substr(1).indexOf('/')+1);
             return localhostPath+projectname+"/";
        }());
//    }
//	if(!isInWeiXin) {
//        window.location.href = BaseAbsolutePath + "999.jsp"; 
//        return;
//    }

    // 获取参数值
    function getQueryStr(str, addr){
        var rs = new RegExp("(^|)" + str + "=([^&#]*)(#|&|$)", "gi").exec(addr || String(window.document.location.href)), tmp;
        if (tmp = rs) {
            return tmp[2];
        }
        return "";
    }

    function getOpenId(){
        var openid = getQueryStr("openid") || null,
            url = window.document.location.href.replace(/\?openid=([^&#]*)&/i, "\?").replace(/[\?\&]openid=([^&#]*)/i, "");
        history.replaceState({},"",url);
        return openid;
    }
    root.BaseCurrentUrl = window.document.location.href;
    getOpenId();

    root.ShareObj = {
        shareTitle: "别刷朋友圈了，有时间和我一起赚钱吧！",
        shareContent: "送参与本金、亏钱我买单，赚钱你拿走！这好事儿哪儿找去！是好友，才告你！信我就试试吧！",
        shareImg: BaseAbsolutePath + "styles/images/share_msg.jpg"
    }
}(this));
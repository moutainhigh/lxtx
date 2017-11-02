<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
      	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="expires" content="2678400" />
        <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
        <meta name="keywords" content="" />
        <meta name="description" content="" />
        <title>云平台-建仓</title>
        <link rel="stylesheet" type="text/css" href="<c:url value='/styles/main.css?t=${version}' />">
        <link rel="stylesheet" type="text/css" href="<c:url value='/styles/createorder.css?t=${version}' />">
    </head>
    <body>
    	<%@include file="/WEB-INF/jsp/head/common.jsp"%>
        <div id="createorderbox">
            <div class="createorder-content">
                <div class="createchoose-wrap"></div>
                <div class="key-value boxflex">
                    <div class="key">合约定金:</div>
                    <div id="definecashnum" class="box_flex_1"></div>
                </div>
                <div class="key-value boxflex">
                    <label class="key">数量:</label>
                    <div class="box_flex_1 num-wrap">
                        <span class="btn-coin btn-minute" data-value="-1">-</span> 
                        <input type="tel" value="1" onpaste="return false" oncontextmenu="return false" oncopy="return false" oncut="return false"/>
                        <span class="btn-coin btn-add" data-value="1">+</span> 
                    </div>
                </div>
                <div class="key-value boxflex">
                    <label class="key">止盈/止损点:</label>
                    <div  class="box_flex_1" id="setting-point">
                        <ul class="table"></ul>
                    </div>
                </div>
                <div class="sure-btn-wrap">
                    <div class="table">
                        <div class="table-cell cancel"> <label>取消</label></div>
                        <div class="table-cell determine"> <label>确定</label></div>
                    </div>
                    <p class="ptipstorage">暂停服务时对于等待成交订单将自动处理，合约定金全额返还</p>
                    <p>服务时间：每日9:00~次日4:00 每日4:00~9:00暂停服务</p>
                </div>
            </div>
        </div> 
        <script src="<c:url value='/scripts/zepto.min.js?t=${version}' />" type="text/javascript"></script>
        <script src="<c:url value='/scripts/createorder.js?t=${version}' />" type="text/javascript"></script>
        <script src="<c:url value='/scripts/jquery.js' />"></script>
        <script src="<c:url value='/scripts/main.js?t=${version}' />" type="text/javascript"></script>
        <script src="<c:url value='/scripts/prepare.js?t=${version}' />" type="text/javascript"></script>
        <script src="<c:url value='/scripts/mustache.js' />" type="text/javascript"></script>
         <script src="<c:url value='/scripts/util.js?t=${version}' />" type="text/javascript"></script>
    </body>
</html>

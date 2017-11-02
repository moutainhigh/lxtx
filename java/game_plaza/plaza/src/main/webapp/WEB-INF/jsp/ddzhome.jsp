<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <!--<meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">-->
    <script type="text/javascript" src="http://www.miliduobao.com/style/mobile/js/jquery-1.8.3.min.js" ></script>
    <script type="text/javascript" src="http://www.miliduobao.com/style/mobile/js/nmwp.js" ></script>
    <link rel="stylesheet" href="http://www.miliduobao.com/style/mobile/css/nmwp.css?v=1" />
    <title>斗地主</title>
</head>
<body>
   <style>
       body,html{ background: #f4f4f4}
       .pasts{  background: #f4f4f4; }
       .pasts .ul{ width: 94%; margin: 0 auto;padding-bottom: 10px; padding-top: 10px;}
       .pasts .ul .li{ height: 40px; border: 1px solid #eeeeee; margin-bottom: 5px; background: #fff}
       .pasts .ul .li .lista{ width: 20%; height: 30px; float: left;margin-top: 10px; position: relative; }
       .pasts .ul .li .lista span{ width: 100%; height: 20px; line-height: 18px; text-align: center; color: #fff;font-size: 12px;position: absolute; left: -6%;
           background: url(https://www.miliduobao.com/style/mobile/images/nmwp/jx1.png) no-repeat ;display: block; background-size:100% 100% ; }
       .pasts .ul .li .listb{ width: 44%; height: 30px; float: left; margin-top: 10px}
       .pasts .ul .li .listb p{ width: 20%; float: left; }
       .pasts .ul .li .listb span{ height: 20px;width: 20px; line-height: 18px;border-radius: 50%;border: 1px solid #fc9799;color: #fe4a4e; font-weight: 900;font-size: 16px;
           text-align:center; display: block; margin: 0 auto;font-family:Arial Bold;}
       .pasts .ul .li .listc{ height: 20px; float: right;margin-top: 10px;padding-right: 3px; background: #f9f9f9;}
       .pasts .ul .li .listc i{ display: block;height: 18px; width: 12px; background: url(https://www.miliduobao.com/style/mobile/images/nmwp/jx2.png) no-repeat 0 4px;background-size: 100% ;float: left; }
       .pasts .ul .li .listc span{ font-size: 12px; display: block; height: 20px; line-height: 20px;  padding-left: 5px;float: left;  color: #c5c5c5;}
       .pasts .dootm { height: 30px; width: 100%; overflow: hidden; }
       .pasts .dootm p { height: 30px; line-height: 30px; color: #6d6c6c;text-align: center; font-size: 12px;}

   </style> 
   <script src="<c:url value='/scripts/mustache.js'/>"></script>
	<script src="<c:url value='/scripts/util.js' />"></script>   
   <script>
   	$(document).ready(function() {
   		var path = global_util.getAppPath();
   		$.get(path + "/platform/ddzLoginCheck", {}, function(data) {
   			if(data.code == 0){
   				window.location.href = data.ddzpath;
   			}else{
   				alert(data.Message);
   			}
   		});	
   		
   	});
   </script>
</body>
</html>

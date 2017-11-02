<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="expires" content="2678400" />
        <meta name="format-detection" content="telephone=no" /> 
        <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
        <meta name="keywords" content="" />
        <meta name="description" content="" />
        <title>出入金查询</title>
		<link href="<c:url value='/payment/styles/main.css?t=${version}' />" type="text/css" rel="stylesheet" />
        <link href="<c:url value='/payment/styles/jilu.css?t=${version}' />" type="text/css" rel="stylesheet" />
<%--         <script src="<c:url value='/payment/scripts/prepare.js' />" type="text/javascript"></script> --%>
<script type="text/javascript">
	<c:set var="basePath" value="${pageContext.request.contextPath}" />
</script>
	</head>
	<body>
		<%@include file="/WEB-INF/jsp/head/common.jsp"%>
		<div class="recording" >
			<div class="rec_content" id="bean_list" ></div>
		    <div class="deta_more" id="deta_more_div">
		        <img src="<c:url value='/payment/styles/images/deta_jz.gif' />" style="display:;" />努力加载中
		    </div>
		</div>
	    <script src="<c:url value='/payment/scripts/main.js?t=${version}' />" type="text/javascript"></script>
	    <script type="text/javascript">
		    (function(root){
	            var hasnextpage = false;
	            //请求操盘记录
	            root.getCoinRecord = function(lastid){
	            	$.get("${basePath}/user/queryAssetRecord", {id:lastid||"0"}, function(data) {
		            	root.setlist(data.Data);
		            });
	            };
	            
	            // 设置列表
	            root.setlist = function(data){
	            	var content = "";
	            	if(data.length>0){
		            	$.each(data,function(index,ops){
	            			var flag = "",deal="",method="",platform="";
		            		if(index==data.length-1){
		            			root.orderid = ops.id;
		            		}
		            		if(ops.type=='1'){
		            			method = "充值";
	 	                		platform = "微信充值";
	 	                		flag='<span class="chong">充</span>';
	 	                	}else if(ops.type=='2'){
	 	                		method = "提现";
	 	                		platform = "微信提现";
	 	                		flag='<span class="ti2">提</span>';
	 	                	}else if(ops.type=='3'){
	 	                		method = "提现";
	 	                		platform = "充值卡兑换";
	 	                		flag='<span class="ti2">提</span>';
	 	                	}else {
	 	                		method = "资金调整";
	 	                		platform = "资金调整";
	 	                		flag='<span class="tiao">调</span>';
	 	                	}
// 		            		if(ops.type=='1'){
// 		            			if(ops.status=='0'){
// 		            				deal='<div class="rec_blo2"></div><div class="chuo"><img src="${basePath}/payment/styles/images/shibai.png" /></div>';
// 		            			}
// 		            		}else{
// 			            		if(ops.status=="0"){
// 		 	                		deal='<div class="rec_blo2"></div><div class="chuo"><img src="${basePath}/payment/styles/images/shibai.png" /></div>';
// 		                        }else if(ops.Status=="1"){
// 		                        	deal='<div class="rec_blo2"></div><div class="chuo"><img src="${basePath}/payment/styles/images/chuli.png" /></div>';
// 		                        }
// 		            		}
		            		
		            		content+="<div class=\"rec_blo\"><div class=\"clearfix\"><div class=\"left rec_bank\">【"+ method+"】"+ platform+"</div><div class=\"right liushuihao1\">"+
		            		"订单号：<span>"+ ops.wxTradeNo+"</span></div></div><div class=\"clearfix record\"><div class=\"left rec_jilu\">"+flag +" "+ $.formatNumber(ops.amount,2)+
		            		"</div><div class=\"right rec_time\">"+ ops.time1+"</div>"+deal+"</div></div>";
		            	})
		            	$("#bean_list").append(content);
		            	$('#deta_more_div').html('');
	            	}else{
	            		$('#deta_more_div').html('没有更多了');
	            	}
	            	if(data.length==10){
	            		hasnextpage = true;
	            	}
	            };
	            window.onload = function(){
	                window.document.body.scrollTop = 0;
	                root.getCoinRecord();
	                window.onscroll=_.debounce(function(){
	                    var docuElem = document.documentElement, bodyElem = document.body,
	                        clientheight = docuElem.clientHeight, docuTop = docuElem.scrollTop,
	                        scrolltop = docuTop==0? bodyElem.scrollTop : docuTop,
	                        scrollheight = docuTop==0? bodyElem.scrollHeight : docuElem.scrollHeight;
	                    if(scrolltop+clientheight>=scrollheight-5){
	                        if(hasnextpage){
	                            root.getCoinRecord(root.orderid);
	                        }
	                    }
	                }, 300);
	            };
	            
	        }(this));
	    </script>
		<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
        <script src="<c:url value='/payment/scripts/share.js' />" type="text/javascript"></script>
        <script type="text/javascript" src="http://pingjs.qq.com/h5/stats.js" name="MTAH5" sid="500049057" ></script> 
        <script type="text/javascript">
//             var ShareHost = "zhongjin.zjwlce.cn"; 
//             $(function(){
                //initShare();
//             });
        </script>
	</body>
</html>
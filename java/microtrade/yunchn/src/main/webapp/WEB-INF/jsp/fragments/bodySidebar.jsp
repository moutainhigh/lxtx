<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/tld/fn.tld" prefix="fn"%>
<style>
button.active {
	color: yellow !important
}

.ui-jqgrid .ui-jqgrid-bdiv {
	overflow-x: hidden;
}

.ui-jqgrid tr.jqgrow td {
	white-space: normal !important;
	height: auto;
	vertical-align: middle;
}
</style>
<div class="sidebar" id="sidebar">
	<script type="text/javascript">
		try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
	</script>
	<ul class="nav nav-list">
<!-- 		   	<li> -->
<!-- 			    <a href="#" class="dropdown-toggle"> -->
<!-- 					<i class="icon-list-alt"></i> <span class="menu-text"> 渠道统计查询 </span> <b class="arrow icon-angle-down"></b> -->
<!-- 			    </a> -->
		
<!-- 				<ul class="submenu"> -->
<%-- 					<li id="qdtjcx"><a href="<c:url value='/chn/enterChnManagePage' />"> <i class="icon-double-angle-right"></i> 统计列表</a></li> --%>
<%-- 					<li id="ddmxcx"><a href="<c:url value='/chn/account/enterAccountDetailAllPage' />"> <i class="icon-double-angle-right"></i> 订单明细查询</a></li> --%>
<!-- 				</ul> -->
<!-- 			</li> -->
		
			<li id="qdtj">
				<a href="<c:url value='/chn/enterChnManage' />">
					<i class="icon-list-alt"></i>
					<span class="menu-text"> 渠道统计查询</span>
				</a>
			</li>
			<c:if test="${userProfitFlag=='on'}">
				<li id="yhtjcx">
					<a href="<c:url value='/chn/enterUserProfitManage' />">
						<i class="icon-list-alt"></i>
						<span class="menu-text"> 用户统计查询</span>
					</a>
				</li>
				<li id="yhhzcx">
					<a href="<c:url value='/chn/enterChnAllUserSum' />">
						<i class="icon-list-alt"></i>
						<span class="menu-text"> 用户汇总查询</span>
					</a>
				</li>
			</c:if>
			<c:if test="${session_user.uCode=='ww_admin'}">
				<li id="profitAdd">
					<a href="<c:url value='/chn/enterProfitManage' />">
						<i class="icon-list-alt"></i>
						<span class="menu-text"> 收益录入</span>
					</a>
				</li>
			</c:if>
			
<!-- 			<li id="ddmxcx"> -->
<%-- 				<a href="<c:url value='/chn/account/enterAccountDetailAllPage' />"> --%>
<!-- 					<i class="icon-list-alt"></i> -->
<!-- 					<span class="menu-text"> 订单明细查询 </span> -->
<!-- 				</a> -->
<!-- 			</li> -->
		
		
	</ul>
<!-- /.nav-list -->
	<div class="sidebar-collapse" id="sidebar-collapse">
		<i class="icon-double-angle-left"
			data-icon1="icon-double-angle-left"
			data-icon2="icon-double-angle-right"></i>
	</div>

<script type="text/javascript">
	try{ace.settings.check('sidebar' , 'collapsed')}catch(e){}
</script>
<script>
console.log("${session_user.uCode}");
var selectedFucMenu;
if($.cookie("selectedFucMenu")){
	selectedFucMenu = $.cookie("selectedFucMenu");
	changeFucMenu(selectedFucMenu);
}else{
	changeFucMenu("null");
}

//点击功能块触发
function changeFucMenu(fucName){
	/* $(".system").css("display","none");
// 	$hotelall").css("display","none");
	$(".hotel").css("display","none");
	$(".custody").css("display","none");
	$(".instportal").css("display","none");
	$(".platform").css("display","none");
	$(".research").css("display","none");
	$("."+fucName).css("display","");
	$(".sidebar-shortcuts-large button").removeClass("active");
	$("#"+fucName).addClass("active");
	$.cookie("selectedFucMenu",fucName, {path:"/"}); //写cookie */
}


</script>
	<script>
		var id = "<%=request.getParameter("id")%>";
		var $son = $("#" + id).addClass("active");
		$("li:has(#" + id + ")").addClass("active open");
		/*千分位处理*/
		function commafy(s) {
			 s= s+"";
			 s=s.replace(/^(\d*)$/,"$1.");  
			    s=(s+"00").replace(/(\d*\.\d\d)\d*/,"$1");  
			    s=s.replace(".",",");  
			    var re=/(\d)(\d{3},)/;  
			    while(re.test(s)){
			        s=s.replace(re,"$1,$2");  
			    }  
			    s=s.replace(/,(\d\d)$/,".$1");  
			    return s.replace(/^\./,"0.")  
			}
		/*千分位处理,jqgird调用*/
		function convertQfw(el){
			return commafy(el);
		}
	</script>
</div>

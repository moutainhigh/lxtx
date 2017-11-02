<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<jsp:include page="/WEB-INF/jsp/fragments/headTag.jsp" />
<body>
<head>
<style type="text/css">
span#tt {
	
	font-size:20px;
	cursor:pointer;
	padding-left:6px;
	padding-right:6px;
	display:inline-block;
}
span#tt P{
	height:5px;
	font-size:14px;
}
.ui-jqgrid tr.jqgrow td {
  white-space: normal !important;
  height:auto;
  vertical-align:middle;
 }
 
 .ui-jqgrid .ui-jqgrid-labels th {
 	text-align: center;
}
</style>

<script type="text/javascript">
<c:set var="basePath" value="${pageContext.request.contextPath}" />
</script>
</head>
	
<%@include file="/WEB-INF/jsp/fragments/bodyHeader.jsp"%>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
		</script>

		<div class="main-container-inner">
			<a class="menu-toggler" id="menu-toggler" href="#"> <span class="menu-text"></span></a>
			
			<jsp:include page="/WEB-INF/jsp/fragments/bodySidebar.jsp">
					<jsp:param name="id" value="yhhzcx"/>
			</jsp:include>

			<div class="main-content">
				<div class="breadcrumbs" id="breadcrumbs">
					<script type="text/javascript">
							try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
						</script>

					<ul class="breadcrumb">
						<li><i class="icon-home home-icon"></i> <a href="<c:url value='/blank' />">Home</a>
						</li>

						<li><a href="<c:url value='/chn/enterChnAllUserSum' />">用户汇总查询</a></li>
						<li class="active">用户订单数据</li>
					</ul>
					<!-- .breadcrumb -->
				</div>
				<jsp:include page="/WEB-INF/jsp/fragments/bodyAlertInfo.jsp" />
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<!-- PAGE CONTENT BEGINS -->
							
							<div id="grid-pager"></div>
							<table id="grid-table"></table>
							<script type="text/javascript">
								var $path_base = "/";//this will be used in gritter alerts containing images
							</script>

							<!-- PAGE CONTENT ENDS -->
						</div><!-- /.col -->
					</div><!-- /.row -->
				</div><!-- /.page-content -->
			</div>
			<!-- /.main-content -->
			<!-- #ace-settings-container -->
			<jsp:include page="/WEB-INF/jsp/fragments/bodySettings.jsp" />
			<!-- /#ace-settings-container -->
		</div>
		<!-- /.main-container-inner -->
	</div>
	<!-- /.main-container -->


		
<script src="<c:url value='/js/appjs/jqgrid.js' />"></script>
<!-- inline scripts related to this page -->
<script type="text/javascript">	
	var grid_selector = "#grid-table";
	var pager_selector = "#grid-pager";
	jQuery(function($){
		jQuery(grid_selector).jqGrid({
			url:"queryForPageUserOrderList.json",
			datatype: "json",
			postData:{
				"uid":'${uid}'
			     },
			mtype:'GET',
			height: '420',
			colNames:[ '','微信昵称','订单时间','产品名称','看涨/看跌','止损/止盈','委托价','成交价','止盈价','止损价','委托金额','手续费','订单状态','盈亏金额','用券金额','成交时间'],
			colModel:[
				{key:false,name:'id', index:'id', width:10, editable:false,hidden:true,sortable:false},
				{         name:'wxnm',index:'wxnm', align:'center', width:10, editable:false,sortable:false},
				{         name:'orderTime', index:'orderTime', align:'center',width:10,editable:false,sortable:false},
				{         name:'subject', index:'subject', align:'center',width:5,editable:false,sortable:false},
				{         name:'direction', index:'direction', align:'center',width:5,editable:false,sortable:false,formatter:setDirect},
				{         name:'limit',index:'limit',  width:5, editable:false,sortable:false},
				{         name:'orderIndex',index:'orderIndex',  width:6, editable:false,sortable:false},
				{         name:'clearIndex',index:'clearIndex',  width:6, editable:false,sortable:false},
				{         name:'clearUpperLimit',index:'clearUpperLimit',  width:6, editable:false,sortable:false},
				{         name:'clearDownLimit',index:'clearDownLimit',  width:6, editable:false,sortable:false},
				{         name:'cash',index:'cash',  width:6, editable:false,sortable:false},
				{         name:'commission',index:'commission',  width:6, editable:false,sortable:false},
				{         name:'status',index:'status',  width:6, editable:false,sortable:false,formatter:setStatus},
				{         name:'fProfit',index:'fProfit',  width:6, editable:false,sortable:false,formatter:setProfit},
				{         name:'coupouMoney',index:'coupouMoney',  width:5, editable:false,sortable:false},
				{         name:'clearTime',index:'clearTime',  width:10, editable:false,sortable:false},
			], 
			viewrecords : true,
			rowNum:10,
			rowList:[10,20,30],
			pager : pager_selector,
			altRows: true,
			//shrinkToFit: true,
			//toppager: true,
			overflow:'scroll',
			jsonReader : {
			      root:"data",
			      page: "page.pagenum",//当前页
			      total: "page.totalpage",//总页数
			      records: "page.totalrecords",//总记录数
			      rows:"page.pagesize"//每页记录数
			   },
			
			multiselect: false,
			//multikey: "ctrlKey",
	        multiboxonly: false,
	
			loadComplete : function(data) {
				if(!common.checkSession(data,"<c:url value='/login' />")){
					return ;
				}
				var table = this;
				setTimeout(function(){
					styleCheckbox(table);
					
					updateActionIcons(table);
					updatePagerIcons(table);
					enableTooltips(table);
				}, 0);
				
			},
			editurl: "",//nothing is saved
			caption: "用户订单数据",
	
			autowidth: true,
			//实现单选
		   	 onCellSelect: function (id, status) {
	               $.each($("tr.ui-state-highlight"), function () {
	                   if (id != $(this).attr("id")) {
	                	   jQuery(grid_selector).jqGrid('setSelection',$(this).attr("id"),false);
	                   }
	               });
	            }
		});
		//初始化工具栏，详见 appjs/jqgrid.js
		initNavGrid(grid_selector,pager_selector,false,false,false,false,false,false);
	});
	
	function setDirect(el, cellval, opts){
		if(opts.direction==1){
			return "<span style='color:red'>看涨</span>";
		}else if(opts.direction==2){
			return "<span style='color:green'>看跌</span>";
		}
	}
	function setStatus(el, cellval, opts){
		if(opts.status==0){
			return "等待成交";
		}else if(opts.status==1){
			return "处理中";
		}else if(opts.status==2){
			return "<span style='color:#0080FF'>盈利</span>";
		}else if(opts.status==3){
			return "<span style='color:#FF0000'>亏损</span>";
		}
	}
	function setProfit(el, cellval, opts){
		if(opts.status==0){
			return "--";
		}else if(opts.status==1){
			return "--";
		}else if(opts.status==2){
			return "<span style='color:#0080FF'>"+opts.fProfit+"</span>";
		}else if(opts.status==3){
			return "<span style='color:#FF0000'>"+opts.fProfit+"</span>";
		}
	}
	
	
	function reload() {//查询用
		$("#grid-table").jqGrid('setGridParam', {
			url : 'queryForPageUserOrderList.json',
			mtype : 'POST',
			postData : {
				"uid":'${uid}'
			},
			page : 1,
			rows : 10
		}).trigger("reloadGrid");

	}
</script>
</body>
</html>

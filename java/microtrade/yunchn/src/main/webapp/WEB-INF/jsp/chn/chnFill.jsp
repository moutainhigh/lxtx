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
					<jsp:param name="id" value="qdtj"/>
			</jsp:include>

			<div class="main-content">
				<div class="breadcrumbs" id="breadcrumbs">
					<script type="text/javascript">
							try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
						</script>

					<ul class="breadcrumb">
						<li><i class="icon-home home-icon"></i> <a href="<c:url value='/blank' />">Home</a>
						</li>

						<li><a href="<c:url value='/chn/enterChnManage' />">渠道统计</a></li>
						<li class="active">渠道入金数据</li>
					</ul>
					<!-- .breadcrumb -->
				</div>
				<jsp:include page="/WEB-INF/jsp/fragments/bodyAlertInfo.jsp" />
				<div class="page-content">
					<div class="" style="padding: 0px 0px 10px 0px;width:98%;">
		            	<table border="0"  cellpadding="2" cellspacing="0" style="min-width: 768px;">
							<tr>
								<td align="right" style="width:160px;">查询时间段：</td>
								<td align="left" style="width:190px;">
									<div style="width:190px;padding-left: 0px;">
										<div class="input-group" style="width:180px;">
											<input class="form-control date-picker" id="startDate" type="text" data-date-format="yyyy-mm-dd" />
											<span class="input-group-addon">
												<i class="icon-calendar bigger-110"></i>
											</span>&nbsp;&nbsp;-
										</div>
									</div>
								</td>
								<td style="width:190px;">
									<div style="width:190px;">
										<div class="input-group" style="width:180px;">
											<input class="form-control date-picker" id="endDate" type="text" data-date-format="yyyy-mm-dd" />
											<span class="input-group-addon">
												<i class="icon-calendar bigger-110"></i>
											</span>
										</div>
									</div>
								</td>
								
								<td align="left" style="padding-left: 20px;">
									<input type="button" class="search" title="查询" id="searchBtn"  onclick="reload()"></input>
								</td>
								<td></td>
							</tr>
						</table>
					</div>
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
		$(".chosen-select").chosen();
		$("#startDate").datepicker({
	        autoclose: true,
	        todayHighlight:true,
	        todayBtn: 'linked',
	        endDate:new Date()
		})
		$("#startDate").datepicker("setDate",new Date('${begin}'))
		$("#endDate").datepicker({
	        autoclose: true,
	        todayHighlight:true,
	        todayBtn: 'linked',
	        endDate:new Date()
		})
		$("#endDate").datepicker("setDate",new Date('${end}'))
		$(document).keydown(function(event){ 
			if(event.keyCode == 13){ //绑定回车 
				$('#searchBtn').click(); //自动/触发登录按钮 
			} 
		}); 
		jQuery(grid_selector).jqGrid({
			url:"queryForPageChnFillList.json",
			datatype: "json",
			postData:{
				"chnno":'${chnno}',
				"begin":$("#startDate").val(),
				"end":$("#endDate").val()
			     },
			mtype:'GET',
			height: '420',
			colNames:[ '','','微信昵称','渠道编号','入金金额','时间'],
			colModel:[
				{key:false,name:'id', index:'id', width:10, editable:false,hidden:true,sortable:false},
				{		  name:'uid', index:'uid', width:10, editable:false,hidden:true,sortable:false},
				{         name:'wxnm',index:'wxnm', align:'center', width:10, editable:false,sortable:false,formatter:setOrderHref},
				{         name:'chnno', index:'chnno', align:'center',width:10,editable:false,sortable:false},
				{         name:'amount', index:'d.amount', align:'center',width:10,editable:false,sortable:true},
				{         name:'time',index:'time',  width:10, editable:false,sortable:false},
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
				
				$("#grid-table").jqGrid("addRowData", "id", data.obj, "last");
			},
			editurl: "",//nothing is saved
			caption: "渠道入金数据",
	
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
	
	
	function setOrderHref(el, cellval, opts){
		var oper = "";
		oper += "<span id=\"tt\" title=\"订单数据\" class=\"oprbutton blue\" onclick=\"orderObj('"+opts.uid+"')\" ><p> "+opts.wxnm+"</p></span>";
		return oper;
	}
	function orderObj(id){
		location.href="${basePath}/chn/enterUserOrder?uid="+id;
	}
	
	function reload() {//查询用
		if($("#startDate").val()==""||$("#endDate").val()==""){
			alertMsg("查询时段不能为空！");
			return;
		}
		$("#grid-table").jqGrid('setGridParam', {
			url : 'queryForPageChnFillList.json',
			mtype : 'POST',
			postData : {
				"chnno":'${chnno}',
				"begin":$("#startDate").val(),
				"end":$("#endDate").val()
			},
			page : 1,
			rows : 10
		}).trigger("reloadGrid");

	}
</script>
</body>
</html>

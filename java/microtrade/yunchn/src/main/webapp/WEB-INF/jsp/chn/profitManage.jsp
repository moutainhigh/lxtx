<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<jsp:include page="/WEB-INF/jsp/fragments/headTag.jsp" />
<head>
<style type="text/css">
	span#tt {
		font-size:20px;
		cursor:pointer;
		padding-left:6px;
		padding-right:6px;
		display:inline-block;
	}
	
	.tt{
		margin:0 auto;
		padding-bottom: 20px;
	}
	span#tt P{
		height:5px;
		font-size:14px;
	}
	#attrForm{
		padding-left: 20px;
	}
</style>
</head>
<body style="min-width: 768px;">
	<%@include file="/WEB-INF/jsp/fragments/bodyHeader.jsp"%>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>

		<div class="main-container-inner">
			<a class="menu-toggler" id="menu-toggler" href="#"> <span
				class="menu-text"></span></a>

			<jsp:include page="/WEB-INF/jsp/fragments/bodySidebar.jsp">
				<jsp:param name="id" value="profitAdd" />
			</jsp:include>

			<div class="main-content" style="min-width: 680px;">
				<div class="breadcrumbs" id="breadcrumbs">
					<script type="text/javascript">
						try {
							ace.settings.check('breadcrumbs', 'fixed')
						} catch (e) {
						}
					</script>

					<ul class="breadcrumb">
						<li><i class="icon-home home-icon"></i> <a href="<c:url value='/blank' />">Home</a>
						</li>

						<li class="active">新增</li>
					</ul>
					<!-- .breadcrumb -->
				</div>
				<jsp:include page="/WEB-INF/jsp/fragments/bodyAlertInfo.jsp" />
				<div class="page-content" style="height: 100%;">
					<div class="row">
							<form id="attrForm" class="form-horizontal" role="form" >
								<h4 class="header green">新增</h4>
									<div class="form-group" style="height: 37px;">
										<label class=" col-xs-3 col-sm-4 control-label no-padding-right" for="chnno"><span style="color:red;">*</span>4位渠道号：</label>
										<input type="text" id="chnno" name="chnno" placeholder="4位渠道号" class="col-xs-3" onfocus="removeMsg(1)" maxlength="4"/><span id="chnnoMsg" class="eMsg"  style="color:red;font-size:15px;"><li class="icon-remove-sign" style="display:none"></li></span>
									</div>
									
									<div class="form-group" style="height: 37px;">
										<label class=" col-xs-3 col-sm-4 control-label no-padding-right" for="orderMoney"><span style="color:red;">*</span>金额（10,100,500）：</label>
										<input type="text" id="orderMoney" name="orderMoney" placeholder="金额（10,100,500）" class="col-xs-3" onfocus="removeMsg(2)" maxlength="3"/><span id="orderMoneyMsg" class="eMsg"  style="color:red;font-size:15px"><li class="icon-remove-sign" style="display:none"></li></span>
									</div>
									<div class="form-group" style="height: 37px;">
										<label class=" col-xs-3 col-sm-4 control-label no-padding-right" for="orderNum"><span style="color:red;">*</span>笔数（1-6）：</label>
										<input type="text" id="orderNum" name="orderNum" placeholder="笔数（1-6）" class="col-xs-3" onfocus="removeMsg(3)" maxlength="1"/><span id="orderNumMsg" class="eMsg"  style="color:red;font-size:15px"><li class="icon-remove-sign" style="display:none"></li></span>
									</div>
									<div class="form-group" style="height: 37px;">	
										<label class=" col-xs-3 col-sm-4  control-label no-padding-right" for="needRunCount"><span style="color:red;">*</span>执行次数 ：</label>
										<input type="text" id="needRunCount" name="needRunCount" placeholder="执行次数" class="col-xs-3" onfocus="removeMsg(4)" maxlength="2"/><span id="needRunCountMsg" class="eMsg"  style="color:red;font-size:15px"><li class="icon-remove-sign" style="display:none"></li></span>
									</div>
									<div class="form-group" style="height: 37px;">	
										<label class=" col-xs-3 col-sm-4  control-label no-padding-right" for="uid"><span style="color:red;">*</span>用户id ：</label>
										<input type="text" id="uid" name="uid" placeholder="用户id" class="col-xs-3" maxlength="10"/>
									</div>
									
							<div class="hr hr-24"></div>
							<div class="tt">
									<button id="submitButton" class="btn btn-info" type="button" onclick="submitObj()" form="attrForm">
										<i class="icon-ok bigger-110"></i>
										<span>添加</span>
									</button>
									&nbsp; &nbsp; &nbsp;
									<button id="resetButton" class="btn btn-purple" type="reset" onclick="resetData()">
										<i class="icon-undo bigger-110"></i>
										重置
									</button>
							</div>
							</form>
						</div>
				</div><!-- /.page-content -->
				</div><!-- /.main-content -->
				</div><!-- /.main-container-inner -->
		</div><!-- /.main-container -->
		
		<script type="text/javascript">
			window.jQuery || document.write("<script src='assets/js/jquery-2.0.3.min.js'>"+"<"+"/script>");
		</script>
		<script type="text/javascript">
		function removeMsg(index){
			switch(index){
				case 1:
					$("#chnnoMsg li").attr("style","display:none").empty();
				case 2:
					$("#orderMoneyMsg li").attr("style","display:none").empty();
				case 3:
					$("#orderNumMsg li").attr("style","display:none").empty();
				case 4:
					$("#needRunCountMsg li").attr("style","display:none").empty();
			}
		}
		function resetData(){
			$("#chnno").val("");
			$("#orderMoney").val("");
			$("#orderNum").val("");
			$("#needRunCount").html("");
			$("#uid").html("");
		}
		function submitObj() {
			if($("#chnno").val() == ""){
				$("#chnnoMsg li").attr("style","display:").text("渠道号不能为空");
				return ;
			}
			if($("#orderMoney").val() == ""){
				$("#orderMoneyMsg li").attr("style","display:").text("金额不能为空（10,100,500）");
				return ;
			}
			if($("#orderNum").val() == ""){
				$("#orderNumMsg li").attr("style","display:").text("笔数不能为空（1-6）");
				return ;
			}
			
			if($("#needRunCount").val() == ""){
				$("#needRunCountMsg li").attr("style","display:").text("执行次数不能为空");
				return ;
			}
			
			//添加
			confirmMsg("确定添加么？",function(){
				$.ajax({
					url:"addProfit.json",
					data:$("#attrForm").serialize(),
					type:'POST',
					success:function(data){
						if(!common.checkSession(data,"<c:url value='/login' />")){
							return ;
						}
						if(data.code==-111){
							alertMsg(data.data);
						}
						if(common.checkResponseMsg(data,"")){
							setTimeout(function(){
								history.back(1)
							},3000);
						}
					},
					error:function(XMLHttpRequest,textStatus,errorThrown){alertMsg(textStatus);}
				});
			});
		}
		
		
		</script>
</body>
</html>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<jsp:include page="fragments/headTag.jsp" />
<body>
	
<%@include file="fragments/bodyHeader.jsp"%>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
		</script>

		<div class="main-container-inner">
			<a class="menu-toggler" id="menu-toggler" href="#"> <span class="menu-text"></span></a>
			
			<jsp:include page="fragments/bodySidebar.jsp" />

			<div class="main-content">
				<div class="breadcrumbs" id="breadcrumbs">
					<script type="text/javascript">
							try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
						</script>

					<ul class="breadcrumb">
						<li><i class="icon-home home-icon"></i> <a href="#">Home</a>
						</li>

						<li><a href="#">首页</a></li>
						<li class="active">首页</li>
					</ul>
				</div>

				<div class="page-content">

					<div class="row">
						<div class="col-xs-12">
						
							<!-- PAGE CONTENT BEGINS -->
							<jsp:include page="fragments/bodyAlertInfo.jsp" />
							<div class="alert alert-info" hidden>
								<i class="icon-hand-right"></i>

								这是提示信息栏
								<button class="close" data-dismiss="alert">
									<i class="icon-remove"></i>
								</button>
								
							</div>
							<div style="padding:20px">
							<span style="font-size:1.5em;font-weight:bold">欢迎登录信息平台系统</span>
							</div>
							<!-- PAGE CONTENT ENDS -->
						</div><!-- /.col -->
					</div><!-- /.row -->
				</div><!-- /.page-content -->
			</div>
			<!-- /.main-content -->
			<!-- #ace-settings-container -->
			<jsp:include page="fragments/bodySettings.jsp" />
			<!-- /#ace-settings-container -->
		</div>
		<!-- /.main-container-inner -->

	</div>
	<!-- /.main-container -->
</body>
</html>

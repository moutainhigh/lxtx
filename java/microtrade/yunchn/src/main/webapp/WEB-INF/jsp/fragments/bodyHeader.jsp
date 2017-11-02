<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>

<div class="navbar navbar-default" id="navbar">
		<script type="text/javascript">
				try{ace.settings.check('navbar' , 'fixed')}catch(e){}
			</script>

		<div class="navbar-container" id="navbar-container">
			<div class="navbar-header pull-left">
				<a href="#" class="navbar-brand"> <small> <i
						class=""><img style="width:32px;height:32px;" src="<c:url value='/images/gallery/thumb-6.jpg'/>" /></i> 信息平台
				</small>
				</a>
				<!-- /.brand -->
			</div>
			<!-- /.navbar-header -->

			<div class="navbar-header pull-right" role="navigation">
				<ul class="nav ace-nav">
					<li class="light-blue"><a data-toggle="dropdown" href="#"
						class="dropdown-toggle"> <img class="nav-user-photo"
							src="<c:url value='/images/Researcher/researcher_example.png' />" /> <span
							class="user-info"> <small>您好,</small> ${session_user.uName}
						</span> <i class="icon-caret-down"></i>
					</a>

						<ul
							class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
							<li><a href="#" onclick="changePwd()"> <i class="icon-cog"></i> 修改密码
							</a></li>
							<!--
							<li><a href="#"> <i class="icon-user"></i> Profile
							</a></li>

							<li class="divider"></li> -->

							<li><a href="<c:url value='/logout' />"> <i class="icon-off"></i> 退出
							</a></li>
						</ul></li>
				</ul>
				<!-- /.ace-nav -->
			</div>
			<!-- /.navbar-header -->
		</div>
		<!-- /.container -->
	</div>
	<div id="pwd" style="display: none;" align="center">
		<br/><br/>
		请输入原密码：<input id="oldPwd" type="password" /><br/><br/>
		请输入新密码：<input id="newPwd" type="password" /><br/><br/>
		再输入新密码：<input id="newPwd2" type="password" /><br/><br/><span id="new" style="color:red"></span>
	</div> 
	<div id="out" style="display: none;" align="center">
		<p><span style="font-family:华文中宋;color:green;font-size:18px">修改成功!</span></p>
	</div>
	<script type="text/javascript">
		function changePwd(){
				$("#pwd").dialog({
					height : 330,
					width : 350,
					resizable : false,
					title:"修改密码",
					modal : true, //这里就是控制弹出为模态 
					buttons:{
						"确定" : function() {
							var uCode="${session_user.uCode}";
							var password=$("#newPwd").val();
							if(password==$("#newPwd2").val()&&password!=""&&password!=null){
								$.ajax({
									url:"${pageContext.request.contextPath}/public/user/changePwdByUserName.json",
									data:{"uCode":uCode,"password":password,"oldPwd":$("#oldPwd").val()},
									type:"POST",
									success : function(data) {
										if(!common.checkSession(data,"<c:url value='/login' />")){
											return ;
										}
										if(data.code=="-10"){
											$("#new").text("原密码错误");
										}else{
											$("#out").dialog({
												height : 110,
												width : 250,
												resizable : false,
												title:"提示",
												modal : true, //这里就是控制弹出为模态 
												close:function(){
													location.href="${pageContext.request.contextPath}/logout";
												}
											});
										}
									},
									error:function(XMLHttpRequest,textStatus,errorThrown){alert("error,textStatus:"+textStatus);}
								});
							}else{
								$("#new").text("新密码不能为空 或 两次输入新密码不一致");
							}
							
						},
						"取消" : function() {
							$(this).dialog("close");
						}
					},
				});
				$("#new").empty();
				$("#oldPwd").val("");
				$("#newPwd").val("");
				$("#newPwd2").val("");
		}
	</script>
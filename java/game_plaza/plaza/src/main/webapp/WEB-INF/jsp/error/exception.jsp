<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="applicationnd.wap.xhtml+xml;charset= UTF-8" />
        <meta http-equiv="expires" content="2678400" />
		<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />
		<title></title>
		<link href="<c:url value='/styles/tishi.css?t=${version}' />" type="text/css" rel="stylesheet" />
	</head>

	<body>
		<div class="error">
			<div class="reeor_box">
				<div class="reeor500">
					<img src="<c:url value='/styles/images/yemian.png' />" />
				</div>
				<h1>呃呃~~加载失败了！</h1>
				<p><c:out value="${ex}" /></p>
			</div>
		</div>
	</body>
</html>

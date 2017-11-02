<%@ page contentType="text/html; charset=UTF-8"%><%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%><%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%><!DOCTYPE html><html>
<head>
<meta charset="UTF-8">
<title>全民生肖</title>
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
<style type="text/css">
body{
background-color:#24262e;
margin:0;
padding:0;
}
</style>
<div id="shiershengxiao">loading……</div><script src="<c:url value='/scripts/util.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/lufylegend-1.10.1.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/lufylegend.ui-0.14.1.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/LTextField.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/Config.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SPoint.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SMath.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SLabel.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SLazy.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SLotteryResult.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SChip.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SResource.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SMessageBox.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/Protocol.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SConnection.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/loaderEx.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SoundMgr.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/Main.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/OpenList.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/ChatRoom.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/Bank.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/Helper.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/MasterList.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/SMessageBox.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/animal/fb.js?t=${version}' />"></script>

</head>
<body><input type="hidden" id="wxid" value="${session_user.wxid}"><input type="hidden" id="chnno" value="${session_user.chnno}"></body>
</html>
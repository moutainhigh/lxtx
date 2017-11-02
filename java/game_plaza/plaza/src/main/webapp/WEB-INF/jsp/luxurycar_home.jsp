<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">

<meta http-equiv="Content-Type" content="no-cache">

<title>豪车俱乐部</title>

<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">

<style type="text/css">

body{

background-color:#24262e;

margin:0;

padding:0;

}

</style>

<div id="car">loading……</div>
<script src="<c:url value='/scripts/util.js?t=${version}' />"></script>
<script type="text/javascript" src="<c:url value='/scripts/luxury_car/lufylegend-1.10.1.min.js'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/lufylegend.ui-0.14.1.min.js'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/LLoadManage.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/LTextField.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/Config.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SPoint.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SMath.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SLabel.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SLazy.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SLotteryResult.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SChip.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SResource.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SMessageBox.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/Protocol.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SConnection.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/loaderEx.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SoundMgr.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/Main.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/OnLineList.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/OpenList.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/ChatRoom.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/Bank.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/Helper.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/MasterList.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/SMessageBox.js?t=${version}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/luxury_car/fb.js?t=${version}'/>"></script>

</head>

<body>
<input type="hidden" id="wxid" value="${session_user.wxid}">
<input type="hidden" id="chnno" value="${session_user.chnno}">
</body>

</html>
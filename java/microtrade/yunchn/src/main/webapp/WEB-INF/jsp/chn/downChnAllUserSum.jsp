<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<body>
<head>
<style type="text/css">
td{border:solid 1px}
</style>
</head>

<div class="main-container" id="main-container">
	<table style="border-spacing: solid 1px;">
		<tr></tr>
		<thead>
			<th>微信昵称</th>
			<th>账户余额</th>
			<th>日期</th>
			<th>渠道号</th>
			<th>交易次数</th>
			<th>盈次数</th>
			<th>亏次数</th>
			<th>盈利金额</th>
			<th>亏损金额</th>
			<th>手续费</th>
			<th>充值金额</th>
			<th>提现金额</th>
		</thead>
		<tbody>
			<c:forEach var="item" items="${list}" varStatus="status">   
			 	<tr>
			 		<td align="center" <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.wxnm}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.balance}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.date}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.chnno}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.orderCount}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.profitCount}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.lossCount}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.profitAmount}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.lossAmount}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.commission}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.fillAmount}</td>
			 		<td <c:if test="${status.count%2==0}">bgcolor="#c7d3a9"</c:if>>${item.repayAmount}</td>
            	</tr>   
			</c:forEach>
		</tbody>
	</table>
</div>
</body>
</html>

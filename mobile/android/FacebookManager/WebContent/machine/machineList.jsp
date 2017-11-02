<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<form id="pagerForm" method="post" action="machine!list.do">
	<input type="hidden" name="pageNum" value="1" />
	<input type="hidden" name="numPerPage" value="${condition.fetchNum }" />
</form>
<div class="pageHeader">
	<form rel="pagerForm" onsubmit="return navTabSearch(this);" action="machine!list.do" method="post">
	<div class="searchBar">
		<ul class="searchContent">
			<li>
				<label>国家：</label>
				<select  class="combox"  name="country">
					<option value="">请选择</option>
					<s:iterator value="countryList">
					<option value="${code }" <s:if test="code==country">selected='selected'</s:if>>${name}</option>
					</s:iterator>
				</select>
			</li>
			<li>
				<label>状态：</label>
				<select  class="combox"  name="status">
					<option value="0">请选择</option>
					<option value="-1" <s:if test="-1==searchStatus">selected='selected'</s:if>>禁止</option>
				 	<option value="1" <s:if test="1==searchStatus">selected='selected'</s:if>>启用</option>
			 	</select>
			</li>
		</ul>
		<div class="subBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">检索</button></div></div></li>
			</ul>
		</div>
	</div>
	</form>
</div>

<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
			<li><a class="add" href="machine!add.do" target="navTab" rel="addPassage" ><span>添加</span></a></li>
			<li><a class="edit" href="machine!edit.do?id={id}" target="navTab" warn="请选择一条记录" rel="editPassage"><span>修改</span></a></li>
			<li class="line">line</li>
			<li style="display:none"><a class="icon" href="#" target="dwzExport" targetType="navTab" title="要导出这些记录吗?"><span>导出EXCEL</span></a></li>
		</ul>
	</div>

	<div id="w_list_print">
	<table class="list" width="98%" targetType="navTab" asc="asc" desc="desc" layoutH="116">
		<thead>
			<tr>
				<th>国家</th>
				<th>Ip</th>
				<th>总用户数</th>
				<th>待分配用户数</th>
				<th>状态</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="machinePage.result">
			<tr target="id" rel="${id}">
				<td>
				<a href="machine!edit.do?id=${id}" target="navTab" rel="passage_${id}">${name}</a>
				</td>
				<td class="center">${countryName}</td>
				<td class="center">${ip}</td>
				<td class="center"><a href="machine!listUser.do?machineId=${id}">${num}</a></td>
				<td class="center"><a href="machine!listUser.do?machineId=${id}&status=0">${remainNum}</a></td>
				<td class="center"><s:if test="status==-1">禁止</s:if><s:else>启用</s:else></td>
				<td class="center">
					<s:if test="status!=-1">
					<a href="machine!setStatus.do?id=${id}&status=-1" target="ajaxTodo">禁止</a>&nbsp;&nbsp;
					</s:if>
					<s:if test="status!=1">
					<a href="machine!setStatus.do?id=${id}&status=1" target="ajaxTodo">启用</a>
					</s:if>
				</td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
	</div>
	
	<div class="panelBar" >
		<div class="pages">
			<span>显示</span>
			<select name="numPerPage" onchange="navTabPageBreak({numPerPage:this.value})">
				<option value="15" <s:if test="condition.fetchNum == 15" > selected="selected"</s:if>>15</option>
				<option value="30" <s:if test="condition.fetchNum == 30" > selected="selected"</s:if>>30</option>
				<option value="50" <s:if test="condition.fetchNum == 50" > selected="selected"</s:if>>50</option>
				<option value="100" <s:if test="condition.fetchNum == 100" > selected="selected"</s:if>>100</option>
			</select>
			<span>条，共${machinePage.totalCount}条</span>
		</div>
		
		<div class="pagination" targetType="navTab" totalCount="${machinePage.totalCount}" numPerPage="${condition.fetchNum }" pageNumShown="10" currentPage="${condition.curPage }"></div>
		<input type="hidden" id="totalPage" value="${machinePage.totalPage }"/>

	</div>

</div>

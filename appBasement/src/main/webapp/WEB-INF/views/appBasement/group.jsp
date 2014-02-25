<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Group management</title>
</head>
<body>
	<h2>Group management</h2>
	<div id="createUser">
		<s:url value="/appBasement/group" var="new_group_url">
			<s:param name="new">
			</s:param>
		</s:url>
		<a href="${new_group_url}">New group</a>
	</div>
	<ul>
		<c:forEach items="${groups}" var="group">
			<s:url value="/appBasement/group/${group.id}" var="group_url" />
			<li><sf:form method="delete">
					<a href="${group_url}"><c:out value="${group.name}" /></a> | <fmt:formatDate
						value="${group.createdAt}" pattern="hh:mma MMM d, yyyy" />
				</sf:form></li>
		</c:forEach>
	</ul>
</body>
</html>
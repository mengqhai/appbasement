<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User management</title>
</head>
<body>
	<h2>User management</h2>
	<dir id="createUser">
		
	</dir>
	<div id="userList">
		<ul>
			<c:forEach var="user" items="${users}">
				<li><c:out value="${user.id }"/> | <c:out value="${user.username}"></c:out> | <c:out
						value="${user.email}" /> | <fmt:formatDate
						value="${user.createdAt}" pattern="hh:mma MMM d, yyyy" /></li>
			</c:forEach>
		</ul>
	</div>
</body>
</html>
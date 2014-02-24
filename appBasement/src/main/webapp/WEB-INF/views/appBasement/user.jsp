<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
				<s:url value="/user/{user_id}" var="user_url">
					<s:param name="user_id" value="${user.id}">
					</s:param>
				</s:url>
				<c:url value="user/${user.id}" var="user_url"></c:url>
				
				<li><c:out value="${user.id}" /> | <a href="<c:out value="${user_url}" />"><c:out
							value="${user.username}" /> </a> | <c:out value="${user.email}" />
					| <fmt:formatDate value="${user.createdAt}"
						pattern="hh:mma MMM d, yyyy" /></li>
			</c:forEach>
		</ul>
	</div>
</body>
</html>
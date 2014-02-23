<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User management</title>
</head>
<body>
	<h2>User management</h2>
	<div>
		<ol>
			<c:forEach var="user" items="${users}">
				<li><c:out value="${user.username}"></c:out></li>
			</c:forEach>
		</ol>
	</div>
</body>
</html>
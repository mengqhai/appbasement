<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User management</title>
</head>
<body>
	<h2>
		User
		<c:out value="${user.username}" />
		's Groups
	</h2>
	<ul>
		<c:forEach items="${user.groups}" var="group">
			<li><c:out value="${group.name}" /> | <fmt:formatDate
					value="${group.createdAt}" pattern="hh:mma MMM d, yyyy" /></li>
		</c:forEach>
	</ul>

</body>
</html>
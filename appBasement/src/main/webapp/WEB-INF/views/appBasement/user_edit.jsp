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
	<h2>User management</h2>
	<h2>Edit User</h2>
	<div id="editUser">
		<s:url value="/appBasement/user" var="user_url"></s:url>
		<sf:form method="POST" modelAttribute="user" action="${user_url}">
			<table cellspacing="0">
				<tr>
					<th><label for="username">Username:</label></th>
					<td><label><sf:input type="text" path="username"
								id="username" disabled="${(empty user.id) ? false : true}" /></label></td>
				</tr>
				<tr>
					<th><label for="password">Password:</label></th>
					<td><sf:input path="password" size="15" id="password" /></td>
				</tr>
				<tr>
					<th><label for="email">Email:</label></th>
					<td><sf:input path="email" size="15" id="email" /></td>
				</tr>
				<tr>
					<th><input type="reset" /></th>
					<td><input type="submit" value="Submit"> <sf:input
							type="hidden" path="id" value="${user.id}" /></td>
				</tr>
			</table>
		</sf:form>
	</div>
</body>
</html>
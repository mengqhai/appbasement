<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>User management</title>
<script type="text/javascript">
	function confirmDelete(username) {
		var agree = confirm("Are you sure to delete user " + username + "?");
		if (agree)
			return true;
		else
			return false;
	}
</script>
</head>
<body>
	<h2>User management</h2>
	<div id="createUser">
		<s:url value="/appBasement/user" var="new_user_url">
			<s:param name="new">
			</s:param>
		</s:url>
		<a href="${new_user_url }">New user</a>
	</div>
	<div id="userList">
		<ul>
			<c:forEach var="user" items="${users}">
				<s:url value="/appBasement/user/{user_id}" var="user_url">
					<s:param name="user_id" value="${user.id}">
					</s:param>
				</s:url>

				<li><sf:form method="delete" action="${user_url}">
						<c:out value="${user.id}" /> | <a href="${user_url}"><c:out
								value="${user.username}" /> </a> | <c:out value="${user.email}" />
					| <fmt:formatDate value="${user.createdAt}"
							pattern="hh:mma MMM d, yyyy" /> | 
						<input type="submit" value="Delete"
							onclick="return confirmDelete('${user.username}')" /> | 
							<s:url value="/appBasement/user/${user.id}/group"
							var="user_group_url" />
						<a href="${user_group_url}">Groups</a>
					</sf:form></li>
			</c:forEach>
		</ul>
	</div>
</body>
</html>
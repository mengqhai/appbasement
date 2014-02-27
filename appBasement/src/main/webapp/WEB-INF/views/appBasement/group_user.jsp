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
<script type="text/javascript">
	function confirmDelete(username, groupName) {
		var agree = confirm("Are you sure to delete user " + username
				+ " from group " + groupName + "?");
		if (agree)
			return true;
		else
			return false;
	}
</script>
<link rel="stylesheet"
	href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script
	src="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
</head>
<body>
	<h2>
		Users in group
		<c:out value="${group.name}" />
	</h2>
	<ul>
		<c:forEach items="${group.users}" var="user">
			<s:url value="/appBasement/group/${group.id}/user/${user.id}"
				var="group_user_url" />
			<li><sf:form method="delete" action="${group_user_url}">
					<c:out value="${user.id}" /> |  
					<c:out value="${user.username}" /> | <c:out value="${user.email}" /> | 
					<fmt:formatDate value="${user.createdAt}"
						pattern="hh:mma MMM d, yyyy" />
					<input type="submit" value="Remove"
						onclick="return confirmDelete('${user.username}', '${group.name}')" />
				</sf:form></li>
		</c:forEach>
	</ul>

	<h4>Add users to group:</h4>
	<sf:form method="PUT" modelAttribute="addUserToGroup">
		<sf:select multiple="multiple" size="20" path="addTo" name="addTo">
			<sf:options items="${avaUsers}" itemValue="id" itemLabel="username" />
		</sf:select>
		<input type="reset" value="Reset" />
		<input type="submit" value="Add to" />
	</sf:form>


</body>
</html>
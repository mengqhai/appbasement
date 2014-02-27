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
<title>Group management</title>
<style type="text/css">
span.error {
	color: red;
	font-size: 8pt;
}
</style>
<link rel="stylesheet"
	href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script
	src="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
</head>
<body>
	<h2>Group management</h2>
	<h2>Edit Group</h2>
	<div id="editGroup">
		<s:url value="/appBasement/group" var="group_url"></s:url>
		<sf:form method="POST" modelAttribute="group" action="${group_url}">
			<table cellspacing="0">
				<tr>
					<th><label for="name">Group name:</label></th>
					<td><label><sf:input type="text" path="name" id="name" /><br />
							<sf:errors path="name" cssClass="error" /></label></td>
				</tr>
				<tr>
					<th><input type="reset" /></th>
					<td><input type="submit" value="Submit"> <sf:input
							type="hidden" path="id" value="${group.id}" /></td>
				</tr>
			</table>
		</sf:form>
	</div>
</body>
</html>
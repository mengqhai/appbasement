<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AppBasement</title>
</head>
<body>
	<h2>Welcome to AppBasement</h2>
	<s:url value="/appBasement/user" var="user_url"></s:url>
	<h4><a href="${user_url}">User</a></h4>
</body>
</html>
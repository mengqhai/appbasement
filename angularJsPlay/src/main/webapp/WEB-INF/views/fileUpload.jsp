<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>File Upload</title>
</head>
<body>
<div>
<sf:form method="POST" enctype="multipart/form-data">
	<label for="files">File to upload:</label>
	<input name="files" type="file" multiple="multiple">
	<input type="submit" value="Upload" required="required">
</sf:form>
</div>

</body>
</html>
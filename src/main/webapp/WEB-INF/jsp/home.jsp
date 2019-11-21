<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
Balancing Menu
<ul>
	<% String upload_url = "localhost:8080/balancing/uploadfile";%>
	<li>Upload new transactions file <a href="<%= upload_url %>"><%= upload_url %></a></li>
	<% String edit_staged_transactions_url = "localhost:8080/balancing/staged-transactions";%>
	<li>Edit and Commit staged transactions <a href="<%= edit_staged_transactions_url %>"><%= edit_staged_transactions_url %></a></li>
</ul>

</body>
</html>
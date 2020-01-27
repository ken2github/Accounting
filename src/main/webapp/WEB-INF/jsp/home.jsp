<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Home - Index</title>
</head>
<body>
<% 
	String indexMenuHTML = "";
	String indexMenuItem = "<a href=\"%s\">%s</a>&nbsp;&nbsp;";
	List<String> indexMenu = (List<String>) pageContext.findAttribute("indexMenu");
	for(String item : indexMenu){
		String[] values = item.split("[!]");
		indexMenuHTML += String.format(indexMenuItem, values[1],values[0]) ;
	}
%>
<%= indexMenuHTML %>
<p/>
Balancing Menu
<ul>
	<% String upload_url = "localhost:8080/balancing/uploadfile";%>
	<li>Upload new transactions file <a href="<%= upload_url %>"><%= upload_url %></a></li>
	<% String edit_staged_transactions_url = "localhost:8080/balancing/staged-transactions";%>
	<li>Edit and Commit staged transactions <a href="<%= edit_staged_transactions_url %>"><%= edit_staged_transactions_url %></a></li>
	<% String status_url = "localhost:8080/balancing/status";%>
	<li>View count update status<a href="<%= status_url %>"><%= status_url %></a></li>
	<% String graph_and_diagrams_url = "localhost:8080/balancing/to-do-page";%>
	<li>View graphs and diagrams<a href="<%= edit_staged_transactions_url %>"><%= graph_and_diagrams_url %></a></li>
	<% String user_guide_url = "localhost:8080/balancing/user-guide";%>
	<li>User Guide<a href="<%= user_guide_url %>"><%= user_guide_url %></a></li>
</ul>

</body>
</html>
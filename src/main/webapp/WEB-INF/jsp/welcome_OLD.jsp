<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
Balancing 
<ul>
	<% String home_url = "localhost:8080/home/home";%>
	<li>Home <a href="<%= home_url %>"><%= home_url %></a></li>
	<% String index_url = "localhost:8080/indexes/nets/years/months/counts/categories/subcategories/commons/flows?year=2018&month=2";%>
	<li>Indexes <a href="<%= index_url %>"><%= index_url %></a></li>
		<ul>
		<li>indexes/{indexes}/years/months/counts/categories/subcategories/commons/flows</li>
		<li>indexes = averages|nets|balances</li>
		<li>commons = y|n</li>
		<li>flows = INPUT|OUTPUT</li>
		</ul>
	<% String report_errors_url = "localhost:8080/reports/checking/years/2018/errors";%>
	<li>Errors Report <a href="<%= report_errors_url %>"><%= report_errors_url %></a></li>
	<% String report_status_url = "localhost:8080/reports/checking/years/2018/status";%>
	<li>Status Report <a href="<%= report_status_url %>"><%= report_status_url %></a></li>
	<% String report_table_url = "localhost:8080/reports/tables/indexes/nets/years/months/counts?year=2018";%>
	<li>Table Report <a href="<%= report_table_url %>"><%= report_table_url %></a></li>
		<ul>
		<li>two axes, whose one is months</li>
		</ul>
	<% String swagger_url = "localhost:8080/swagger-ui.html";%>
	<li>Swagger UI <a href="<%= swagger_url %>"><%= swagger_url %></a></li>
	<% String month_transactions_url = "localhost:8080/reports/month-transactions/2019/1.html";%>
	<li>Month Transactions <a href="<%= month_transactions_url %>"><%= month_transactions_url %></a></li>
</ul>

</body>
</html>
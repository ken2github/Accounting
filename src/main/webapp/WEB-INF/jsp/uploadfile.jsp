<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="controllers.WebAppController.UPLOAD_PAGE_STATUS"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload</title>
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
	<h1><a>Upload new transaction files</a></h1>
	<form enctype="multipart/form-data" method="post" action="/balancing/uploadedfile">
		<p>Choose local file to upload: <input id="file" name="file" class="element file" type="file"/> </p>
		<p><small>Ensure to use Canonical Format filename: <strong>count_COUNT_from_DDMMYYYY_to_DDMMYYYY_balance_####_##.xls</strong></small></p> 
		
		<input id="saveForm" type="submit" name="submit" value="Upload File" />
	</form>	
	<%	
		if (pageContext.findAttribute("status")!=null){
			String color = "black";
			if(pageContext.findAttribute("status").equals(UPLOAD_PAGE_STATUS.ERROR)){
				color="red";		
			}
			if(pageContext.findAttribute("result")!=null){
				List<String> items =(List<String>) pageContext.findAttribute("result");
				for(String item : items){
					%>
					<p color="<%=color%>"><%=item %></p>
				<%
				}
			}
		}
		%>	
</body>
</html>
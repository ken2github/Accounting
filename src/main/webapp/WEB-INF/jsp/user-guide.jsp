<!DOCTYPE html>
<%@page import="java.util.List"%>
<html>
<head>
<meta charset="ISO-8859-1">
<title>User Guide</title>
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
This is the User Guide of Balancing Project<br><br>You should:<br>- for any month:<br>- -  for any Bank Account:<br>- - - create your XLS or CSV file<br>- - - store/save it in proper directory<br>- - - upload it (put it in staging area)<br>- - - add isCommon and Sector<br>- - - commit it<br><br>NOTE: you have to upload and commit (empty staging area) before upload a new file</body>
</html>
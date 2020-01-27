<%@page import="java.time.Instant"%>
<%@page import="model2.DetailedSector"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Status</title>
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

Status

<% 
	Map<String, Date> countLastUpdateDateMap = (Map<String, Date>) pageContext.findAttribute("countLastUpdateDateMap");
	

	Date today = Date.from(Instant.now());
	
	Integer todayYear = today.getYear() + 1900;
	Integer todayMonth = today.getMonth() + 1;
	
	String yearsLine ="";
	String monthsLine ="";
	int max = 15;
	for(int i=0; i<max; i++){
		yearsLine+="&nbsp;";
		monthsLine+="&nbsp;";
	}
	for(int k=2018; k<=todayYear; k++){
		yearsLine += String.format("|&nbsp;&nbsp;&nbsp;&nbsp;%s&nbsp;&nbsp;&nbsp;&nbsp;|",k);
		if(k==todayYear){
			monthsLine += "|GFMAMGLASOND|".substring(0, todayMonth+1);
		}else{
			monthsLine += "|GFMAMGLASOND|";
		}
	}
	
	
	
%>

<ul>
	<li><font face="courier"><%= yearsLine %></font></li>
	<li><font face="courier"><%= monthsLine %></font></li>
	
<% 
	for(String count : countLastUpdateDateMap.keySet()){
		Date date = countLastUpdateDateMap.get(count);
		Integer year = date.getYear() + 1900;
		Integer month = date.getMonth() + 1;
		String countLine ="";
		for(int k=2018; k<=year; k++){
			if(k==year){
				countLine += "|";
				for(int j=1; j<=month; j++){
					countLine += "-";
				}
			}else{
				countLine += "|------------|";
			}
		}
		String countName = "";
		for(int i=0; i<max-count.length(); i++){
			countName+="&nbsp;";
		}
		countName+= count;
		
		%>
		<li><font face="courier"><%= countName %><%= countLine %><font></li>
		<%
	}	
%>


</ul>

</body>
</html>
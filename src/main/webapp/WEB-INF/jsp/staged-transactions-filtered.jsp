<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="model2.DetailedSector"%>
<%@page import="javax.swing.text.html.parser.DTD"%>
<%@page import="model2.DetailedTransaction"%>
<%@page import="java.util.List"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="dao.StagedTransactionDAO"%>
<%@page import="org.apache.catalina.core.ApplicationContext"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="model2.Transaction"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Stage Area</title>
<style type="text/css">
.tg  {border-collapse:collapse;border-spacing:0;}
.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}
.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}
.tg .tg-bfng{font-family:Verdana, Geneva, sans-serif !important;;background-color:#efefef;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-bfng-right{font-family:Verdana, Geneva, sans-serif !important;;background-color:#efefef;border-color:inherit;text-align:right;vertical-align:top}
.tg .tg-bfngred{font-family:Verdana, Geneva, sans-serif !important;;background-color:#ff7979;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-iu63{font-weight:bold;font-family:Verdana, Geneva, sans-serif !important;;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-r0kq{font-family:Verdana, Geneva, sans-serif !important;;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-r0kq-right{font-family:Verdana, Geneva, sans-serif !important;;border-color:inherit;text-align:right;vertical-align:top}
.tg .tg-r0kqred{font-family:Verdana, Geneva, sans-serif !important;;background-color:#ff7979;border-color:inherit;text-align:left;vertical-align:top}
</style>
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

<form enctype="multipart/form-data" method="post" action="/balancing/staged-transactions-filtered/search">
 Filter: <input type="text" name="filter" width="100"> <input type="submit" value="submit-filter">
</form>

<form enctype="multipart/form-data" method="post" action="/balancing/staged-transactions-filtered/update">
<table class="tg">
  <tr>
    <th class="tg-iu63">Count<br></th>
    <th class="tg-iu63">Date</th>
    <th class="tg-iu63">Amount<br></th>
    <th class="tg-iu63">Title</th>
    <th class="tg-iu63">Is Common</th>
    <th class="tg-iu63">Sector</th>
    <th class="tg-iu63">Action</th>
  </tr>
  
   <%
   WebApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);
   StagedTransactionDAO stdao = (StagedTransactionDAO) aC.getBean("stagedTransactionDAO");
   List<DetailedSector> sectors = (List<DetailedSector>) pageContext.findAttribute("sectors");
        
   List<DetailedTransaction> stagedTransactionsFiltered = (List<DetailedTransaction>) pageContext.findAttribute("stagedTransactionsFiltered");
   String appliedFilter = (String) pageContext.findAttribute("appliedFilter");
   
   DecimalFormat df = new DecimalFormat("####.00");
   
   boolean flipflop = false;
   boolean allTransactionClassified=true;
   if(stagedTransactionsFiltered!=null){
	   stagedTransactionsFiltered=stagedTransactionsFiltered.stream().sorted((dt1,dt2)->dt1.getDate().compareTo(dt2.getDate())).collect(Collectors.toList());
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	   for(DetailedTransaction dt : stagedTransactionsFiltered){
		   String tdclass=(flipflop)?"tg-r0kq":"tg-bfng";
		   String tdclassRed=tdclass+"red";
		   String tdClassIsCommon=(dt.isCommon()!=null)?tdclass:tdclassRed;
		   String tdClassSectorName=(dt.getSectorName()!=null)?tdclass:tdclassRed;
		   
		   if(dt.isCommon()==null || dt.getSectorName()==null){
			   allTransactionClassified=false;
		   }
		   
		   %>
		   <tr>
		    <td class="<%= tdclass%>"><%= dt.getCountName()%></td>
		    <td class="<%= tdclass%>"><%= sdf.format(dt.getDate())%></td>
		    <td class="<%= tdclass%>-right"><%= df.format(dt.getAmount()).toString()%></td>
		    <td class="<%= tdclass%>"><%= dt.getTitle()%></td>
		    <td class="<%= tdClassIsCommon%>"><%= (dt.isCommon()==null)?"":((dt.isCommon())?"Yes":"No") %></td>
		    <td class="<%= tdClassSectorName%>"><%= (dt.getSectorName()==null)?"":dt.getSectorName()%></td>
		    <td class="<%= tdclass%>"><a href="/balancing/staged-transactions/edit/<%= dt.getId()%>">Edit</a></td>
		  </tr>
		<%	   
		flipflop=!flipflop;
	   }
   }   
   %>
</table>
<input id="appliedFilter" name="appliedFilter" type="hidden" value="<%=appliedFilter%>">


<select id="isCommon" name="isCommon">    			
	<option value="" selected="selected"></option>
	<option value="true" >Yes</option>
	<option value="false" >No</option>	
</select>
<select id="sectorName" name="sectorName"> 	
<option value="" selected="selected"></option>	    			
<%
	if(sectors!=null){
    	for(DetailedSector ds :sectors){
    		%>
		    <option value="<%=ds.getName()%>" ><%=ds.getName()%></option>
		    <%
		};
	}
%>
</select>
<input type="submit" value="updateInAllFilteredTransactions">
</form>


</body>
</html>
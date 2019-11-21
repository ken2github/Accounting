<%@page import="java.text.DecimalFormat"%>
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
<title>Insert title here</title>
</head>
<body>
<style type="text/css">
.tg  {border-collapse:collapse;border-spacing:0;}
.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}
.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:black;}
.tg .tg-bfng{font-family:Verdana, Geneva, sans-serif !important;;background-color:#efefef;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-bfngred{font-family:Verdana, Geneva, sans-serif !important;;background-color:#ff7979;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-iu63{font-weight:bold;font-family:Verdana, Geneva, sans-serif !important;;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-r0kq{font-family:Verdana, Geneva, sans-serif !important;;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-r0kqred{font-family:Verdana, Geneva, sans-serif !important;;background-color:#ff7979;border-color:inherit;text-align:left;vertical-align:top}
.tg .tg-r0kqgreen{font-family:Verdana, Geneva, sans-serif !important;;background-color:#c8fbb6;border-color:inherit;text-align:left;vertical-align:top}
</style>
<%
   WebApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);
   StagedTransactionDAO stdao = (StagedTransactionDAO) aC.getBean("stagedTransactionDAO");
   
   List<DetailedSector> sectors = (List<DetailedSector>) pageContext.findAttribute("sectors");
       
	DetailedTransaction dt = (DetailedTransaction) request.getAttribute("detailed_staged_transaction") ;        
   	List<DetailedTransaction> transactions = stdao.findAll();  
	transactions=transactions.stream().sorted((dt1,dt2)->dt1.getDate().compareTo(dt2.getDate())).collect(Collectors.toList());
   	
	DecimalFormat df = new DecimalFormat("#,###.00");
	
	boolean isLast = true;
	boolean isFirst = true;
	String nextTrnsactionId = null;
	String previousTrnsactionId = null;
	for(int i=0; i<transactions.size(); i++){
		if(transactions.get(i).getId().equals(dt.getId())){
			if(i<transactions.size()-2){
				isLast = false;
				nextTrnsactionId = transactions.get(i+1).getId();
			}
			if(i>0){
				isFirst = false;
				previousTrnsactionId = transactions.get(i-1).getId();
			}
		}
	}
	
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		   String tdclass="tg-r0kq";
		   String tdclassRed=tdclass+"red";
		   String tdclassGreen=tdclass+"green";
		   %>
<form enctype="multipart/form-data" method="post" action="/balancing/staged-transactions/save/<%= dt.getId()%>">
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
		   <tr>
		    <td class="<%= tdclass%>"><%= dt.getCountName()%></td>
		    <td class="<%= tdclass%>"><%= sdf.format(dt.getDate())%></td>
		    <td  class="<%=(dt.getAmount().signum() <= 0)?tdclassRed:tdclassGreen%>"><%= df.format(dt.getAmount()).toString() %></td>
		    <td class="<%= tdclass%>"><%= dt.getTitle()%></td>
		    <% 
		    	if(dt.isCommon()==null){
		    		%>
		    		<td class="<%= (tdclass+"red")%>">
		    		<select id="isCommon" name="isCommon"> 
		    			<option value="" selected="selected"></option>
		    			<option value="true" >Yes</option>
		    			<option value="false" >No</option>
		    		</select>
		    		<%		    		
		    	}else{
		    		%>
		    		<td class="<%= tdclass%>"><%= (dt.isCommon())?"Yes":"No"%>
		    		<select id="isCommon" name="isCommon"> 
		    			<option value="<%= (dt.isCommon())?"true":"false"%>" selected="selected"></option>
		    			<option value="true" >Yes</option>
		    			<option value="false" >No</option>
		    		</select>		    		
		    		<%
		    	}
		    %>
		    </td>
		    <% 
		    	if(dt.getSectorName()==null){
		    		%>
		    		<td class="<%= (tdclass+"red")%>">
		    		<select id="sectorName" name="sectorName"> 
		    			<option value="" selected="selected"></option>
		    			<%
		    			if(sectors!=null){
		    				for(DetailedSector ds :sectors){
		    				%>
		    					<option value="<%= ds.getName()%>" ><%= ds.getName()%></option>
		    				<%
		    				};
		    			}
		    			%>
		    		</select>
		    		<%		    		
		    	}else{
		    		%>
		    		<td class="<%= tdclass%>">
		    		<%= dt.getSectorName()%>
		    		<select id="sectorName" name="sectorName"> 
		    			<option value="<%= dt.getSectorName()%>" selected="selected"></option>
		    			<%
		    			if(sectors!=null){
		    				for(DetailedSector ds :sectors){
		    				%>
		    					<option value="<%= ds.getName()%>" ><%= ds.getName()%></option>
		    				<%
		    				};
		    			}
		    			%>
		    		</select>
		    		<%
		    	}
		    %>
		    </td>
		    <td class="<%= tdclass%>"><a href="/balancing/staged-transactions">Show ALL</a></td>
		  </tr>
		<tr>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td>

		</td>
		<td>
		
		</td>
		<td>
		<%
if(!isFirst){
	%>
	<a href="/balancing/staged-transactions/edit/<%= previousTrnsactionId%>"><button type="button">&#60;&#60;</button></a>	
	<%
}
%>
<input id="save" name="save" type="hidden" value="save"> 
<input id="saveForm" type="submit" name="submit" value="Save changes" />
		<%
if(!isLast){
	%>
	<a href="/balancing/staged-transactions/edit/<%= nextTrnsactionId%>"><button type="button">&#62;&#62;</button></a>	
	<%
}
%>
		</td>
		</tr>
</table>

</form>

</body>
</html>
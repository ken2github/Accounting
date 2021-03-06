<%@page import="java.util.Optional"%>
<%@page import="java.util.Map"%>
<%@page import="restapi.transactionsoracle.api.SimilarityRelevance"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.stream.IntStream"%>
<%@page import="dao.AccountedYearDAO"%>
<%@page import="restapi.transactionsoracle.service.TransactionsOracle"%>
<%@page import="dao.TransactionDAO"%>
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
<title>Stage Area</title>
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
<%=indexMenuHTML%>
<p/>
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
   	
	AccountedYearDAO aydao = (AccountedYearDAO) aC.getBean("accountedYearDAO");
	TransactionDAO tdao = (TransactionDAO) aC.getBean("transactionDAO");
	TransactionsOracle bta = (TransactionsOracle) aC.getBean("transactionOracle");
		
	DecimalFormat df = new DecimalFormat("####.00");
	
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
<form enctype="multipart/form-data" method="post" action="/balancing/staged-transactions/save/<%=dt.getId()%>">
<table width="100%" class="tg">
  <tr>
    <th class="tg-iu63">Count<br></th>
    <th class="tg-iu63">Date</th>
    <th class="tg-iu63">Amount<br></th>
    <th class="tg-iu63">Title</th>
    <th class="tg-iu63">Is Common</th>
    <th class="tg-iu63">Sector</th>
    <th width="200" class="tg-iu63">Action</th>
  </tr>
		   <tr>
		    <td class="<%=tdclass%>"><%=dt.getCountName()%></td>
		    <td class="<%=tdclass%>"><%=sdf.format(dt.getDate())%></td>
		    <td  class="<%=(dt.getAmount().signum() <= 0)?tdclassRed:tdclassGreen%>" align="right"><%=df.format(dt.getAmount()).toString()%></td>
		    <td class="<%=tdclass%>"><%=dt.getTitle()%></td>
		    <%		    
		    	if(dt.isCommon()==null){
		    %>
		    		<td class="<%=(tdclass+"red")%>">
		    		<select id="isCommon" name="isCommon"> 		    			
		    			<%
 		    				Optional<Boolean> suggestedIsCommon = bta.suggestIsCommonForTransaction(dt, SimilarityRelevance.MEDIUM);
 		    				String suggestedWithHighRelevance="";
 		    				String suggestedWithHighRelevanceValue="";
 		    				if(suggestedIsCommon.isPresent()){
 		    					suggestedWithHighRelevance=suggestedIsCommon.get()?"Yes":"No";
 		    					suggestedWithHighRelevanceValue=suggestedIsCommon.get().toString();
 		    				}
 		    				System.out.println("suggestedIs="+suggestedWithHighRelevance);
 		    			%>		    			
		    			<option value="<%=suggestedWithHighRelevanceValue%>" selected="selected"><%=suggestedWithHighRelevance%></option>
		    			<%
		    				suggestedIsCommon = bta.suggestIsCommonForTransaction(dt, SimilarityRelevance.VERY_LOW);
		    				if(suggestedIsCommon.isPresent()){
		    					%>
		    					<option value="<%=suggestedIsCommon.get().toString()%>" ><%=suggestedIsCommon.get()?"Yes":"No"%></option>
		    					<%		    					
		    				}
		    			%>
    					<option value="" >-------</option>
    					<option value="true" >Yes</option>
		    			<option value="false" >No</option>	
		    		</select>
		    		<%
		    			}else{
		    		%>
		    		<td class="<%=tdclass%>"><%=(dt.isCommon())?"Yes":"No"%>
		    		<select id="isCommon" name="isCommon"> 
		    			<option value="<%=(dt.isCommon())?"true":"false"%>" selected="selected"></option>		    			
		    			<%
		    				Optional<Boolean> suggestedIsCommon = bta.suggestIsCommonForTransaction(dt, SimilarityRelevance.VERY_LOW);
		    				if(suggestedIsCommon.isPresent()){
		    					%>
		    					<option value="<%=suggestedIsCommon.get().toString()%>" ><%=suggestedIsCommon.get()?"Yes":"No"%></option>
		    					<%		    					
		    				}
		    			%>
		    			<option value="" >-------</option>
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
		    		<td class="<%=(tdclass+"red")%>">
		    		<select id="sectorName" name="sectorName"> 
		    			<%
 		    				List<String> suggestedSectors = bta.suggestSectorsForTransaction(dt, 3, SimilarityRelevance.MEDIUM);
 		    					    			String suggestedWithHighRelevance="";
 		    					    			if(suggestedSectors!=null && suggestedSectors.size()>0){
 		    					    				suggestedWithHighRelevance=suggestedSectors.get(0);
 		    					    			}
 		    					    			System.out.println("suggestedIs="+suggestedWithHighRelevance);
 		    			%>		    			
		    			<option value="<%=suggestedWithHighRelevance%>" selected="selected"><%=suggestedWithHighRelevance%></option>
		    			<%
		    				suggestedSectors = bta.suggestSectorsForTransaction(dt, 3, SimilarityRelevance.VERY_LOW);
		    					    			if(suggestedSectors!=null){
		    					    				if(!suggestedWithHighRelevance.equals("")){
		    					    					suggestedSectors=suggestedSectors.subList(1, suggestedSectors.size());
		    				    					}
		    					    				for(String ss :suggestedSectors){
		    			%>
		    					<option value="<%=ss%>" ><%=ss%></option>
		    				<%
		    					};
		    						    			}
		    				%>
    					<option value="" >-------</option>
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
		    		<%
		    			}else{
		    		%>
		    		<td class="<%=tdclass%>">
		    		<%=dt.getSectorName()%>
		    		<select id="sectorName" name="sectorName"> 
		    			<option value="<%=dt.getSectorName()%>" selected="selected"></option>
		    			<%
		    				List<String> suggestedSectors = bta.suggestSectorsForTransaction(dt, 3, SimilarityRelevance.LOWEST);
		    					    			if(suggestedSectors!=null){
		    					    				for(String ss :suggestedSectors){
		    			%>
		    					<option value="<%= ss%>" ><%= ss%></option>
		    				<%
		    				};
		    			}
		    			%>
    					<option value="" >-------</option>
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
<input id="saveForm" type="submit" name="submit" value="Save" />
<input id="next" name="next" type="hidden" value="<%= nextTrnsactionId%>">
		<%
if(!isLast){
	%>
	
	<input id="saveForm" type="submit" name="submit" value="Save >>" />
	<a href="/balancing/staged-transactions/edit/<%= nextTrnsactionId%>"><button type="button">&#62;&#62;</button></a>	
	<%
}
%>
		</td>
		</tr>
</table>

</form>
<p></p>
<p>Notice on 'XXX' special categories: </p>
<ul>
<li>'XXX.MISSINGINFO' : transaction is NOT IDENTIFIED (*),</li>
<li>'XXX.EXTRA' : transaction is IDENTIFIED (*) but does not yet exist corresponding category for it,</li>
<li>'XXX.FAKE' : transaction is not real one, example: for MONEY transaction is often added to accomodate actual and calculated balances.</li>
</ul>
<p></p>
<p> (*) 'IDENTIFIED' means that we know transaction amount/date but we don't know/remember/recognize the reason (no idea of source/destination, no idea of context, ...).</p>
</body>
</html>
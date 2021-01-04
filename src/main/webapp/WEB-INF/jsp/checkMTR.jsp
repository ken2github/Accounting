<%@page import="model2.DetailedTransaction"%>
<%@page import="java.util.stream.IntStream"%>
<%@page import="dao.AccountedYearDAO"%>
<%@page import="dao.TransactionDAO"%>
<%@page import="dao.MonthAverageFlowMetric"%>
<%@page import="model2.CountYearMonthBalance"%>
<%@page import="dao.FlowMetric"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.text.DateFormatSymbols"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="dao.IndexesDAO2"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="java.util.stream.IntStream"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%	
	WebApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);
    AccountedYearDAO aydao = (AccountedYearDAO) aC.getBean("accountedYearDAO");
    List<Integer> accountedYears = aydao.findAll();  
    
    String year = (String) pageContext.findAttribute("year");
           
    TransactionDAO tdao = (TransactionDAO) aC.getBean("transactionDAO");
    
    List<String> months = Arrays.asList(new DateFormatSymbols().getShortMonths()).subList(0, 12);
    List<BigDecimal> monthlyMTRs = IntStream.range(1, 13).mapToObj(month -> {    	
    	List<DetailedTransaction> monthMTRTransactions = tdao.findByYearMonthSector(Integer.parseInt(year), month, "MTR");
    	BigDecimal monthlyMTR = monthMTRTransactions.stream().map(dt -> dt.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
    	return monthlyMTR;
    }).collect(Collectors.toList());
    List<List<DetailedTransaction>> MTRs = IntStream.range(1, 13).mapToObj(month -> {    	
    	List<DetailedTransaction> monthMTRTransactions = tdao.findByYearMonthSector(Integer.parseInt(year), month, "MTR");    	
    	return monthMTRTransactions;
    }).collect(Collectors.toList()); 
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Check MTR</title>
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
<form enctype="multipart/form-data" method="post" action="/balancing/checking/mtr/submit">
<table class="tg">
<tr>			
<td>		
 <select id="year" name="year">
  <%
	for(Integer accountedYear : accountedYears){
		String ay = accountedYear.toString();
		%>
			<option value="<%=ay%>"><%=ay%></option>
		<%
	}  
  %>
</select> 
</td>
<td>
<input id="saveForm" type="submit" name="submit" value="Submit" />	
</td>
</tr>		
</table>
</form>		
<table style="border-color: black; margin-left: auto; margin-right: auto; border-collapse: collapse; border: 1px solid;" border="1">
<tr>
	<td><%=year%></td>
<%
	for(String month : months){	
		%>
		<td><%=month%></td>
		<%
	}  
%>
</tr>
<tr>
<td>MTR</td>
<%
	DecimalFormat df = new DecimalFormat("####.00");
	for(BigDecimal monthlyMTR : monthlyMTRs){	
		%>
		<td><%=df.format(monthlyMTR).toString()%></td>
		<%
	}  
%>
</tr>
<tr>
<td>Details</td>
<%	
	for(int pos=0; pos<monthlyMTRs.size(); pos++){
		%>
		<td>		
		<%
		if(monthlyMTRs.get(pos).compareTo(BigDecimal.ZERO) != 0){
			%>
			<table style="border-color: black; margin-left: auto; margin-right: auto; border-collapse: collapse; border: 1px solid;" border="1">
			<tr><td>Amount</td><td>Date</td><td>Count</td><td>Title</td></tr>
			<%	
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<DetailedTransaction> monthMTRs = MTRs.get(pos);
			
			for(DetailedTransaction dt : monthMTRs){
				String amount = df.format(dt.getAmount()).toString();
				String date = sdf.format(dt.getDate());
				%>
				<tr><td><%=amount%></td><td><%=date%></td><td><%=dt.getCountName()%></td><td><%=dt.getTitle()%></td></tr>				
				<%
			}
			%>
			</table>		
			<%
		}
		%>		
		</td>
		<%	
	}  
%>
</tr>
</table>
</body>
</html>
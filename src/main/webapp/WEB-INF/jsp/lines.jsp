<%@page import="java.util.stream.IntStream"%>
<%@page import="dao.AccountedYearDAO"%>
<%@page import="dao.CountsBalancesByYearMonthDAO"%>
<%@page import="dao.MonthAverageFlowMetric"%>
<%@page import="model2.CountYearMonthBalance"%>
<%@page import="dao.FlowMetric"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.math.BigDecimal"%>
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
           
    CountsBalancesByYearMonthDAO cymbdao = (CountsBalancesByYearMonthDAO) aC.getBean("countsBalancesByYearMonthDAO");
    List<CountYearMonthBalance> balances = cymbdao.findByYear(Integer.parseInt(year));    
    
    // Adding 'Total' balances to retrieved balances
    List<CountYearMonthBalance> totalBalances = IntStream.range(1,13).mapToObj(i -> new CountYearMonthBalance().setCountName("Total").setYear(Integer.parseInt(year)).setMonth(new Integer(i)).setAmount(new BigDecimal(0))).collect(Collectors.toList());
    balances.stream().forEach(countYearMonthBalance -> {
    	CountYearMonthBalance totalMonthBalance = totalBalances.get(countYearMonthBalance.getMonth()-1);    
    	totalMonthBalance.setAmount(totalMonthBalance.getAmount().add(countYearMonthBalance.getAmount()));
    });
    balances.addAll(totalBalances);
    
    // Split balances in three orders sets
    Map<String,BigDecimal> mediumBalance = new HashMap<>();
    balances.stream().forEach(countYearMonthBalance -> {
    	String count = countYearMonthBalance.getCountName();
    	if(!mediumBalance.containsKey(count)){
    		mediumBalance.put(count,new BigDecimal(0));
    	}
    	mediumBalance.replace(count,mediumBalance.get(count).add(countYearMonthBalance.getAmount().abs()));
    });
    
    BigDecimal max = mediumBalance.keySet().stream().map(count -> mediumBalance.get(count)).max(BigDecimal::compareTo).get();
    BigDecimal threshol_1 = max.divide(new BigDecimal(10));
    BigDecimal threshol_2 = threshol_1.divide(new BigDecimal(10));
    
    List<String> largeCounts = mediumBalance.keySet().stream().filter(count -> mediumBalance.get(count).compareTo(threshol_1) == 1).collect(Collectors.toList());
    List<String> mediumCounts = mediumBalance.keySet().stream().filter(count -> (mediumBalance.get(count).compareTo(threshol_1) < 1) && (mediumBalance.get(count).compareTo(threshol_2) == 1)).collect(Collectors.toList());
    List<String> smallCounts = mediumBalance.keySet().stream().filter(count -> mediumBalance.get(count).compareTo(threshol_2) < 1).collect(Collectors.toList());
    
    List<List<String>> countSets = Arrays.asList(largeCounts,mediumCounts,smallCounts); 
    String[] dataColumns = {"","",""};
    String[] dataTables = {"","",""};
    
    for(int k=0; k<3; k++){
	    Map<Integer,Map<String,CountYearMonthBalance>> monthBalances = new HashMap<>();	       
	    Map<String,List<CountYearMonthBalance>> countBalances = new HashMap<>();	    
	    
	    int pos = k;
	    balances.stream().filter(balance -> countSets.get(pos).contains(balance.getCountName())).forEach(balance -> {
	    	String countName = balance.getCountName();    	
	    	if(!countBalances.containsKey(countName)){
	    		countBalances.put(countName,new ArrayList<>());
	    	}
	    	countBalances.get(countName).add(balance);
	    	
	    	Integer month = balance.getMonth();
	    	if(!monthBalances.containsKey(month)){
	    		monthBalances.put(month,new HashMap<>());
	    	}
	    	monthBalances.get(month).put(countName,balance);
	    });   	    
	    
	    List<String> sortedCountList = countBalances.keySet().stream().sorted( (c1,c2) -> mediumBalance.get(c2).compareTo(mediumBalance.get(c1))).collect(Collectors.toList());	    
	    DecimalFormat df = new DecimalFormat("####.00");
	    dataTables[k] = monthBalances.keySet().stream().map(month -> 
	    	"[new Date(" + year + ", "+ (month-1) + "), " + (sortedCountList.stream().map(
	    			countName ->  df.format(monthBalances.get(month).get(countName).getAmount()).toString().replaceAll(",",".")
	    			).collect(Collectors.joining(", "))) +"]"
	    	).collect(Collectors.joining(", "));            
	    
	    dataColumns[k] = "data.addColumn('date', 'Month');\n" + (sortedCountList.stream().map(
				countName -> String.format(" data.addColumn('number', '%s');",countName)).collect(Collectors.joining("\n")));
    }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Graph Tree Map</title>    
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script> 
<script type="text/javascript">
google.charts.load('current', {'packages':['line']});
google.charts.setOnLoadCallback(drawChart);
function drawChart() {
	var data = new google.visualization.DataTable();    	  
	<%=dataColumns[0]%>
	data.addRows([
    	<%=dataTables[0]%>
	]);
    var options = {
    	chart: {
        	title: 'Year Balance (L)',
            subtitle: 'of large balances'
		},
        width: 1000,
        height: 500
	};
    var chart = new google.charts.Line(document.getElementById('linechart_material_0'));
	chart.draw(data, google.charts.Line.convertOptions(options));
	
	
	var data = new google.visualization.DataTable();    	  
	<%=dataColumns[1]%>
	data.addRows([
    	<%=dataTables[1]%>
	]);
    var options = {
    	chart: {
        	title: 'Year Balance (M)',
            subtitle: 'of medium balances'
		},
        width: 1000,
        height: 500
	};
    var chart = new google.charts.Line(document.getElementById('linechart_material_1'));
	chart.draw(data, google.charts.Line.convertOptions(options));
	
	
	var data = new google.visualization.DataTable();    	  
	<%=dataColumns[2]%>
	data.addRows([
    	<%=dataTables[2]%>
	]);
    var options = {
    	chart: {
        	title: 'Year Balance (S)',
            subtitle: 'of small balances'
		},
        width: 1000,
        height: 500
	};
    var chart = new google.charts.Line(document.getElementById('linechart_material_2'));
	chart.draw(data, google.charts.Line.convertOptions(options));
	
	
	
}
</script>
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
<form enctype="multipart/form-data" method="post" action="/balancing/diagram/lines/submit">
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
<table>
<tr><td><div id="linechart_material_0"></div></td></div><td><div id="linechart_material_1"></td></tr>
<tr><td><div id="linechart_material_2"></div></td><td></td></tr>
</table>
</body>
</html>
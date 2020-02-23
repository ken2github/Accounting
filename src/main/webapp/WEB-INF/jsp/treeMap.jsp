<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%	
	List<String> rowsList = new ArrayList();
	
	//rows+=String.format("['[SPS]',    		'Flows',       0,     	            0]");

	String rows = "";
	for(int i=0; i<rowsList.size(); i++){		
		rows+=String.format("['%s','%s',%s,%s]");
		if(i<rowsList.size()-1){
			rows+=",";
		}
	}
	String treeMapTitle = "Titolo Grafico";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Graph Tree Map</title>    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
      google.charts.load('current', {'packages':['treemap']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
 	      ['Settore', 		'SuperSector', 		'Size', 			'Flow'],
    	  ['Flows',    	null,           0,                  0],
          <%=rows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_uscite'));
        tree.draw(data, {
          minColor: '#f00',
          midColor: '#ddd',
          maxColor: '#0d0',
          headerHeight: 20,
          fontColor: 'black',
          fontSize: 15,
          maxDepth: 4,
          showScale: true,
          headerHeight: 20,
          title: '<%=treeMapTitle%>',
          useWeightedAverageForAggregation: false,
          showTooltips: true,
          showScale: false
        });
      }
</script>
<script type="text/javascript">
      google.charts.load('current', {'packages':['treemap']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
 	      ['Settore', 		'SuperSector', 		'Size', 			'Flow'],
    	  ['Flows',    	null,           0,                  0],
          <%=rows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_entrate'));
        tree.draw(data, {
          minColor: '#f00',
          midColor: '#ddd',
          maxColor: '#0d0',
          headerHeight: 20,
          fontColor: 'black',
          fontSize: 15,
          maxDepth: 4,
          showScale: true,
          headerHeight: 20,
          title: '<%=treeMapTitle%>',
          useWeightedAverageForAggregation: false,
          showTooltips: true,
          showScale: false
        });
      }
</script>
<script type="text/javascript">
      google.charts.load('current', {'packages':['treemap']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
 	      ['Settore', 		'SuperSector', 		'Size', 			'Flow'],
    	  ['Flows',    	null,           0,                  0],
          <%=rows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_netto'));
        tree.draw(data, {
          minColor: '#f00',
          midColor: '#ddd',
          maxColor: '#0d0',
          headerHeight: 20,
          fontColor: 'black',
          fontSize: 15,
          maxDepth: 4,
          showScale: true,
          headerHeight: 20,
          title: '<%=treeMapTitle%>',
          useWeightedAverageForAggregation: false,
          showTooltips: true,
          showScale: false
        });
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
<%
// 	WebApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);
//    StagedTransactionDAO stdao = (StagedTransactionDAO) aC.getBean("stagedTransactionDAO");
   
//    List<DetailedSector> sectors = (List<DetailedSector>) pageContext.findAttribute("sectors");
       
// 	DetailedTransaction dt = (DetailedTransaction) request.getAttribute("detailed_staged_transaction") ;        
//    	List<DetailedTransaction> transactions = stdao.findAll();  
// 	transactions=transactions.stream().sorted((dt1,dt2)->dt1.getDate().compareTo(dt2.getDate())).collect(Collectors.toList());
   	
// 	AccountedYearDAO aydao = (AccountedYearDAO) aC.getBean("accountedYearDAO");
// 	TransactionDAO tdao = (TransactionDAO) aC.getBean("transactionDAO");
// 	TransactionsOracle bta = (TransactionsOracle) aC.getBean("transactionOracle");
		
// 	DecimalFormat df = new DecimalFormat("####.00");	
%>
<form enctype="multipart/form-data" method="post" action="/balancing/graph/treeMap%>">
<table width="100%" class="tg">
<tr>			
<td>		
 <select id="year" name="year">
  <option value="2018">2018</option>
  <option value="2019">2019</option>
  <option value="2020">2020</option>
</select> 
</td>	
<td>		
 <select id="month" name="month">
  <option value="1">Gen</option>
  <option value="2">Feb</option>
  <option value="3">Mar</option>
  <option value="4">Apr</option>
  <option value="5">Mag</option>
  <option value="6">Giu</option>
  <option value="7">Lug</option>
  <option value="8">Ago</option>
  <option value="9">Set</option>
  <option value="10">Ott</option>
  <option value="11">Nov</option>
  <option value="12">Dic</option>
</select> 
</td>	
<td>
<input id="saveForm" type="submit" name="submit" value="Submit" />	
</td>
</tr>		
</table>
</form>		
	
<table>
<tr>
<td>
<div id="chart_uscite" style="width: 900px; height: 900px;"></div>
</td>
<td>
<div id="chart_entrate" style="width: 900px; height: 900px;"></div>
</td>
</tr>
<tr>
<td>
<div id="chart_netto" style="width: 900px; height: 900px;"></div>
</td>
</tr>
</table>
</body>
</html>
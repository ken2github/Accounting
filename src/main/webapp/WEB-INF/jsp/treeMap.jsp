<%@page import="dao.MonthAverageFlowMetric"%>
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
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%	

	WebApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);
	IndexesDAO2 idao = (IndexesDAO2) aC.getBean("indexDAO2");
	List<MonthAverageFlowMetric> flows = (List<MonthAverageFlowMetric>) idao.findAverages(2018);
	flows = flows.stream().filter(fm -> fm.isCommon).collect(Collectors.toList());
	
	flows.stream().forEach(fm -> System.out.println(fm.toString()));

	String SUPERSECTOR_TABLE_ROW_FORMAT = "['[%s]','Flows',0,0]";
	String TABLE_ROW_FORMAT = "['%s %s','[%s]',%s,%s]";
	DecimalFormat df = new DecimalFormat("####.00");
	
	String entrateRows = flows.stream().filter(fm -> fm.sign.intValue() > 0).map(fm -> {
		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
		return Arrays.asList(
			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName),
			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,abs,amount));
	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
	System.out.println(entrateRows);	
	
	String usciteRows = flows.stream().filter(fm -> fm.sign.intValue() < 0).map(fm -> {
		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
		return 
	Arrays.asList(
			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName),
			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,abs,amount));
	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
	System.out.println(usciteRows);
			
	Map<MonthAverageFlowMetric,BigDecimal> nettiFlowsMap = new HashMap<MonthAverageFlowMetric,BigDecimal> ();
	flows.stream().forEach(fm -> {
		MonthAverageFlowMetric newFm = fm.doClone();		
		newFm.sign=null;
		newFm.amount=null;
		if(!nettiFlowsMap.containsKey(newFm)){
			nettiFlowsMap.put(newFm,fm.amount);
		}else{
			nettiFlowsMap.replace(newFm,nettiFlowsMap.get(newFm).add(fm.amount));
		}
	});	
	
	String nettiRows = nettiFlowsMap.keySet().stream().map(fm -> {
		MonthAverageFlowMetric newFm = fm.doClone();	
		newFm.amount = nettiFlowsMap.get(fm);
		return newFm;
	}).map(fm -> {
		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
		return 
	Arrays.asList(
			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName),
			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,abs,amount));
	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
	System.out.println(nettiRows);
	
	String treeMapTitleEntrate = "Entrate";
	String treeMapTitleUscite = "Uscite";
	String treeMapTitleNetti = "Netti";
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
          <%=usciteRows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_uscite'));
        tree.draw(data, {
        	minColor: '#ff0000',
            midColor: '#ff7676',
            maxColor: '#ffe2e2',
          headerHeight: 20,
          fontColor: 'black',
          fontSize: 15,
          maxDepth: 4,
          showScale: true,
          headerHeight: 20,
          title: '<%=treeMapTitleUscite%>',
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
          <%=entrateRows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_entrate'));
        tree.draw(data, {          
          minColor: '#dbffdb',
          midColor: '#83ff83',
          maxColor: '#00ff00',
          headerHeight: 20,
          fontColor: 'black',
          fontSize: 15,
          maxDepth: 4,
          showScale: true,
          headerHeight: 20,
          title: '<%=treeMapTitleEntrate%>',
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
          <%=nettiRows%>
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
          title: '<%=treeMapTitleNetti%>',
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
<td>
</td>
</tr>
</table>
</body>
</html>
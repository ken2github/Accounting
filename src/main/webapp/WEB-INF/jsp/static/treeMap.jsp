<%@page import="dao.AccountedYearDAO"%>
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
<style type="text/css">
table tbody tr td div div {
  display: table-cell;
}
</style>
<%	

	WebApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);
    AccountedYearDAO aydao = (AccountedYearDAO) aC.getBean("accountedYearDAO");
    List<Integer> accountedYears = aydao.findAll();
    
    
    String year = (String) pageContext.findAttribute("year");
    String month = (String) pageContext.findAttribute("month");
    String isCommon = (String) pageContext.findAttribute("isCommon");
    String flow = (String) pageContext.findAttribute("flow");
    String auto = (String) pageContext.findAttribute("auto");    
    
    IndexesDAO2 idao = (IndexesDAO2) aC.getBean("indexDAO2");
    List<MonthAverageFlowMetric> flows = new ArrayList<MonthAverageFlowMetric>();
    if(year==null || year.equals("") || year.equals("All")){
    	if(month==null || month.equals("") || month.equals("All")){
    		//
        	
        }else{
        	// month
        	
        }
    }else{ 
    	if(month==null || month.equals("") || month.equals("All")) {
    		// year
    		flows = (List<MonthAverageFlowMetric>) idao.findAverages(Integer.parseInt(year));
    	}else {    		
    		// year month
    		
    	}    	
    }

	
	//List<MonthAverageFlowMetric> flows = (List<MonthAverageFlowMetric>) idao.findAverages(2018);
	
	flows = flows.stream()
			.filter(fm -> (flow==null || flow.equals("") || flow.equals("All"))?
					true
					:(flow.equals("Incoming") && fm.sign.intValue() > 0) || (isCommon.equals("Outcoming") && fm.sign.intValue() < 0))
			.filter(fm -> (isCommon==null || isCommon.equals("") || isCommon.equals("All"))?
					true
					:(isCommon.equals("True") && fm.isCommon) || (isCommon.equals("False") && !fm.isCommon))						 
			.collect(Collectors.toList());
	
	//flows.stream().forEach(fm -> System.out.println(fm.toString()));

	String SUPERSECTOR_TABLE_ROW_FORMAT = "['[%s] %s','Flows %s',0,0]";
	String TABLE_ROW_FORMAT = "['%s %s','[%s] %s',%s,%s]";
	String FLOWS_ROW_FORMAT = "['Flows %s',    	null,           0,                  0]";
	DecimalFormat df = new DecimalFormat("####.00");
	
	List<MonthAverageFlowMetric> incomingFlows = flows.stream().filter(fm -> fm.sign.intValue() > 0).collect(Collectors.toList());
	List<MonthAverageFlowMetric> outcomingFlows = flows.stream().filter(fm -> fm.sign.intValue() < 0).collect(Collectors.toList());
	
	double sumOfIncomingAmounts = incomingFlows.stream().mapToDouble(fm -> fm.amount.doubleValue()).sum();
	double sumOfOutcomingAmounts = outcomingFlows.stream().mapToDouble(fm -> fm.amount.doubleValue()).sum();
	long maxOfAmounts = (long) Math.max(sumOfIncomingAmounts, Math.abs(sumOfOutcomingAmounts));
	
	int maxPixels = 750;
	int incomingMapPixels = (int)((maxPixels * sumOfIncomingAmounts)/maxOfAmounts);
	int outcomingMapPixels = (int)((maxPixels *  Math.abs(sumOfOutcomingAmounts))/maxOfAmounts);
	
	final Map<String,BigDecimal> totals = new HashMap<String,BigDecimal>();
	totals.put("Flows", BigDecimal.ZERO);
	incomingFlows.stream().forEach(fm -> {
		if(!totals.containsKey(fm.sectorFatherName)){
			totals.put(fm.sectorFatherName,BigDecimal.ZERO);
		}
		totals.replace(fm.sectorFatherName, totals.get(fm.sectorFatherName).add(fm.amount));
		totals.replace("Flows", totals.get("Flows").add(fm.amount));
	});
	final String flowsTotals = df.format(totals.get("Flows")).toString().replaceAll(",", ".");
	String incomingRows = incomingFlows.stream().map(fm -> {
		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
		String signum = String.valueOf(fm.amount.signum());
		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
		String fatherAmount =df.format(totals.get(fm.sectorFatherName)).toString().replaceAll(",", ".");
		return 
	Arrays.asList(
			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName,fatherAmount,flowsTotals),
			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,fatherAmount,abs,abs));
	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));

	incomingRows = String.format(FLOWS_ROW_FORMAT,flowsTotals) + "," + incomingRows;
	
	final Map<String,BigDecimal> totals2 = new HashMap<String,BigDecimal>();
	totals2.put("Flows", BigDecimal.ZERO);
	outcomingFlows.stream().forEach(fm -> {
		if(!totals2.containsKey(fm.sectorFatherName)){
			totals2.put(fm.sectorFatherName,BigDecimal.ZERO);
		}
		totals2.replace(fm.sectorFatherName, totals2.get(fm.sectorFatherName).add(fm.amount));
		totals2.replace("Flows", totals2.get("Flows").add(fm.amount));
	});
	final String flowsTotals2 = df.format(totals2.get("Flows")).toString().replaceAll(",", ".");
	String outcomingRows = outcomingFlows.stream().map(fm -> {
		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
		String signum = String.valueOf(fm.amount.signum());
		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
		String fatherAmount =df.format(totals2.get(fm.sectorFatherName)).toString().replaceAll(",", ".");
		return 
	Arrays.asList(
			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName,fatherAmount,flowsTotals2),
			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,fatherAmount,abs,abs));
	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));

	outcomingRows = String.format(FLOWS_ROW_FORMAT,flowsTotals2) + "," + outcomingRows;
	
	
// 	String entrateRows = flows.stream().filter(fm -> fm.sign.intValue() > 0).map(fm -> {
// 		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
// 		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
// 		return Arrays.asList(
// 			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName),
// 			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,abs,amount));
// 	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
// 	System.out.println(entrateRows);	
	
// 	String usciteRows = flows.stream().filter(fm -> fm.sign.intValue() < 0).map(fm -> {
// 		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
// 		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
// 		return 
// 	Arrays.asList(
// 			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName),
// 			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,abs,amount));
// 	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
// 	System.out.println(usciteRows);
			
	Map<MonthAverageFlowMetric,BigDecimal> nettiFlowsMap = new HashMap<MonthAverageFlowMetric,BigDecimal> ();
	flows.stream().forEach(fm -> {
		MonthAverageFlowMetric newFm = fm.doClone();
		newFm.isCommon=null;
		newFm.sign=null;
		newFm.amount=null;
		if(!nettiFlowsMap.containsKey(newFm)){
			nettiFlowsMap.put(newFm,fm.amount);
		}else{
			nettiFlowsMap.replace(newFm,nettiFlowsMap.get(newFm).add(fm.amount));
		}
	});	
	
	List<MonthAverageFlowMetric> nettiFlows = nettiFlowsMap.keySet().stream().map(fm -> {
		MonthAverageFlowMetric newFm = fm.doClone();	
		newFm.amount = nettiFlowsMap.get(fm);
		newFm.sign = newFm.amount.signum();  		
		return newFm;
	}).collect(Collectors.toList());
	
	List<MonthAverageFlowMetric> incomingNettiFlows = nettiFlows.stream().filter(fm -> fm.sign > 0).collect(Collectors.toList());
	List<MonthAverageFlowMetric> outcomingNettiFlows = nettiFlows.stream().filter(fm -> fm.sign < 0).collect(Collectors.toList());
	
	double sumOfIncomingNettiAmounts = incomingNettiFlows.stream().mapToDouble(fm -> fm.amount.doubleValue()).sum();
	double sumOfOutcomingNettiAmounts = outcomingNettiFlows.stream().mapToDouble(fm -> fm.amount.doubleValue()).sum();
	int incomingNettiMapPixels = (int)((maxPixels * sumOfIncomingNettiAmounts)/maxOfAmounts);
	int outcomingNettiMapPixels = (int)((maxPixels * Math.abs(sumOfOutcomingNettiAmounts))/maxOfAmounts);
	
	final Map<String,BigDecimal> totals3 = new HashMap<String,BigDecimal>();
	totals3.put("Flows", BigDecimal.ZERO);
	incomingNettiFlows.stream().forEach(fm -> {
		if(!totals3.containsKey(fm.sectorFatherName)){
			totals3.put(fm.sectorFatherName,BigDecimal.ZERO);
		}
		totals3.replace(fm.sectorFatherName, totals3.get(fm.sectorFatherName).add(fm.amount));
		totals3.replace("Flows", totals3.get("Flows").add(fm.amount));
	});
	final String flowsTotals3 = df.format(totals3.get("Flows")).toString().replaceAll(",", ".");
	String incomingNettiRows = incomingNettiFlows.stream().map(fm -> {
		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
		String signum = String.valueOf(fm.amount.signum());
		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
		String fatherAmount =df.format(totals3.get(fm.sectorFatherName)).toString().replaceAll(",", ".");
		return 
	Arrays.asList(			
			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName,fatherAmount,flowsTotals3),
			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,fatherAmount,abs,abs));
	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
	incomingNettiRows = String.format(FLOWS_ROW_FORMAT,flowsTotals3) + "," + incomingNettiRows;
	
	final Map<String,BigDecimal> totals4 = new HashMap<String,BigDecimal>();
	totals4.put("Flows", BigDecimal.ZERO);
	outcomingNettiFlows.stream().forEach(fm -> {
		if(!totals4.containsKey(fm.sectorFatherName)){
			totals4.put(fm.sectorFatherName,BigDecimal.ZERO);
		}
		totals4.replace(fm.sectorFatherName, totals4.get(fm.sectorFatherName).add(fm.amount));
		totals4.replace("Flows", totals4.get("Flows").add(fm.amount));
	});
	final String flowsTotals4 = df.format(totals4.get("Flows")).toString().replaceAll(",", ".");
	String outcomingNettiRows = outcomingNettiFlows.stream().map(fm -> {
		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
		String signum = String.valueOf(fm.amount.signum());
		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
		String fatherAmount =df.format(totals4.get(fm.sectorFatherName)).toString().replaceAll(",", ".");
		return 
	Arrays.asList(			
			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName,fatherAmount,flowsTotals4),
			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,fatherAmount,abs,abs));
	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
	outcomingNettiRows = String.format(FLOWS_ROW_FORMAT,flowsTotals4) + "," + outcomingNettiRows;
	
	//System.out.println(nettiRows);
	System.out.println(incomingMapPixels);
	System.out.println(outcomingMapPixels);
	System.out.println(incomingNettiMapPixels);
	System.out.println(outcomingNettiMapPixels);
	
	
	
	
	
// 	String incomingNettiRows = nettiFlowsMap.keySet().stream().map(fm -> {
// 		MonthAverageFlowMetric newFm = fm.doClone();	
// 		newFm.amount = nettiFlowsMap.get(fm);
// 		return newFm;
// 	}).map(fm -> {
// 		String amount = df.format(fm.amount).toString().replaceAll(",", ".");
// 		String signum = String.valueOf(fm.amount.signum());
// 		String abs = df.format(fm.amount.abs()).toString().replaceAll(",", ".");
// 		return 
// 	Arrays.asList(
// 			String.format(SUPERSECTOR_TABLE_ROW_FORMAT,fm.sectorFatherName),
// 			String.format(TABLE_ROW_FORMAT,fm.sectorName,amount,fm.sectorFatherName,abs,signum));
// 	}).flatMap(sl -> sl.stream()).distinct().collect(Collectors.joining(","));
	
// 	String treeMapTitleEntrate = "Entrate";
// 	String treeMapTitleUscite = "Uscite";
	String treeMapTitleNetti = String.format("Year:%s Month:%s Flow:%s IsCommon:%s Auto:%s",year,month,flow,isCommon,auto);
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
          <%=incomingNettiRows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_incomingNetti'));
        tree.draw(data, {
          minColor: '#efffee',
          midColor: '#8eff8b',
          maxColor: '#08ff00',
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
<script type="text/javascript">
      google.charts.load('current', {'packages':['treemap']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
 	      ['Settore', 		'SuperSector', 		'Size', 			'Flow'],
          <%=outcomingNettiRows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_outcomingNetti'));
        tree.draw(data, {
          minColor: '#fff6f6',
          midColor: '#ff9494',
          maxColor: '#ff0000',
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
<script type="text/javascript">
      google.charts.load('current', {'packages':['treemap']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
 	      ['Settore', 		'SuperSector', 		'Size', 			'Flow'],
          <%=incomingRows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_incoming'));
        tree.draw(data, {
        	 minColor: '#efffee',
             midColor: '#8eff8b',
             maxColor: '#08ff00',
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
<script type="text/javascript">
      google.charts.load('current', {'packages':['treemap']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
 	      ['Settore', 		'SuperSector', 		'Size', 			'Flow'],
          <%=outcomingRows%>
        ]);
        tree = new google.visualization.TreeMap(document.getElementById('chart_outcoming'));
        tree.draw(data, {
        	minColor: '#fff6f6',
            midColor: '#ff9494',
            maxColor: '#ff0000',
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
<form enctype="multipart/form-data" method="post" action="/balancing/graph/treeMap/submit">
<table class="tg">
<tr>			
<td>		
 <select id="year" name="year">
  <option value="All" selected="selected">Year</option>
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
 <select id="month" name="month">
  <option value="All" selected="selected">Month</option>
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
 <select id="isCommon" name="isCommon">
  <option value="All" selected="selected">isCommon?</option>
  <option value="True">Common</option>
  <option value="False">Not Common</option>
</select> 
</td>
<td>
 <select id="flow" name="flow">
  <option value="All" selected="selected">Flow Type</option>
  <option value="Incoming">Entrate</option>
  <option value="Outcoming">Uscite</option>
</select> 	
</td>
<td>
 <select id="auto" name="auto">
  <option value="All" selected="selected">IsAuto?</option>
  <option value="Auto">Auto</option>
  <option value="NotAuto">NotAuto</option>
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
<td style="vertical-align: top;">
<div id="chart_incomingNetti" style="width: 1350px; height: <%=String.valueOf(incomingNettiMapPixels)%>px; vertical-align: top;"></div>
</td>
<td style="vertical-align: top;">
<div id="chart_outcomingNetti" style="width: 1350px; height: <%=String.valueOf(outcomingNettiMapPixels)%>px; vertical-align: top;"></div>
</td>
</tr>
<tr>
<td style="vertical-align: top;">
<div id="chart_incoming" style="width: 1350px; height: <%=String.valueOf(incomingMapPixels)%>px; vertical-align: top;"></div>
</td>
<td style="vertical-align: top;">
<div id="chart_outcoming" style="width: 1350px; height: <%=String.valueOf(outcomingMapPixels)%>px; vertical-align: top;"></div>
</td>
</tr>
</table>
</body>
</html>
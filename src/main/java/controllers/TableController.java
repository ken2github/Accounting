package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import checking.CheckException;
import checking.CheckingEngine;
import checking.ExecutionStatus;
import checking.RuleGroup;
import model.books.MasterBook;
import model.books.YearBook;
import reporting.ReportException;
import reporting.Reportable;
import reporting.VelocityReporter;

@RestController
@RequestMapping("/reports/tables/indexes/{index}/")
public class TableController {

	Logger logger = LoggerFactory.getLogger(TableController.class);
	
	@Autowired
	private MasterBook mb;
		
		@RequestMapping(value= {
				"/*",
				"/*/*",
				"/*/*/*",
				"/*/*/*/*",
				"/*/*/*/*/*",
				"/*/*/*/*/*/*",
				"/*/*/*/*/*/*/*"})	
		public String method(
				@PathVariable String index,
				@RequestParam(required=false, defaultValue = "none") String year,
				@RequestParam(required=false, defaultValue = "none") String month,
				@RequestParam(required=false, defaultValue = "none") String count,
				@RequestParam(required=false, defaultValue = "none") String category,
				@RequestParam(required=false, defaultValue = "none") String subcategory,
				@RequestParam(required=false, defaultValue = "none") String common,
				@RequestParam(required=false, defaultValue = "none") String flow,
				HttpServletRequest request) throws Exception {
			
	
		
		//identify title
		String title = "";
		String matchExp = "";
		List<String> axes = new ArrayList<>();
		
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		
		List<String> pathItems = 
				Arrays.asList(path.split("/"));
		List<String> pathValues = 
				new ArrayList<>(Arrays.asList("indexes",	"years",	"months",	"counts",	"categories",	"subcategories",	"commons",	"flows"));
		
		List<String> paramValues = 
				new ArrayList<>(Arrays.asList("index",		"year",		"month",	"count",	"category",		"subcategory",		"common",	"flow"));
		List<String> params = 
				new ArrayList<>(Arrays.asList(index,		year,		month,		count,		category,		subcategory,		common,		flow));
				
		int counter = 1;
			
		for (int i = 1; i < pathValues.size(); i++) {
			if(pathItems.contains(pathValues.get(i))) {
				if (!params.get(i).equals("none")) {
					title+=params.get(i)+" ";
					matchExp+=params.get(i)+"_"; 
				}else {
					axes.add(pathValues.get(i));
					matchExp+="%"+(counter++)+"_";
				}				
			}
		}	
		
		switch(index) {
		case "averages": 
			matchExp="AVERAGE_"+matchExp;
			title+="AVERAGES";
			break;
		case "balances": 
			matchExp="BALANCE_"+matchExp;
			title+="BALANCES";
			break;
		case "nets": 
			matchExp=""+matchExp;
			title+="NETS";break;
		default: break;
		}
		
		if(axes.size()!=2) {
			throw new IllegalNumberOfAxesInTables("Illegal number of axes ["+(String.join(", ", axes))+"]. Tables MUST two axes.");
		}
		
//		String value = "AVERAGE_2018_01_SPS_OUTPUT";
//		
//		
//		int pos1_beginIndex = matchExp.indexOf("%1");
//		int pos1_endIndex= value.indexOf("_",pos1_beginIndex);
//		String v1_columnValue = value.substring(pos1_beginIndex, pos1_endIndex);
//		
//		int pos2_beginIndex = pos1_endIndex + (matchExp.indexOf("%2") - pos1_beginIndex - 2);
//		int pos2_endIndex= value.indexOf("_",pos2_beginIndex);
//		if(pos2_endIndex<0)
//			pos2_endIndex = value.length();
//		String v2_columnValue = value.substring(pos2_beginIndex, pos2_endIndex);
			
		
//		return 
//				"title=["+title+"] "+
//				"matchExp=["+matchExp+"] "+
//				"indexValue=["+value+"] "+
//				"p1=["+pos1_beginIndex+","+pos1_endIndex+"] v1=["+v1_columnValue+"] "+
//				"p2=["+pos2_beginIndex+","+pos2_endIndex+"] v2=["+v2_columnValue+"] "+
//				"axes=["+(String.join(", ", axes))+"] ";
		
		//invokes indexing client
//		List<Map<String,String>> indexes = new ArrayList<>();//callToIndexingClient();
		
		final String uri = "http://localhost:8080/indexes/nets/years/months/categories/flows?year=2018&flow=OUTPUT";

	    RestTemplate restTemplate = new RestTemplate();
	    List<Map<String,String>> indexes = restTemplate.getForObject(uri, List.class);
		
		//create rows for table
		
		List<String> columns = new ArrayList<>(); 
		List<String> rows = new ArrayList<>();
		for (Map<String, String> map : indexes) {
			for (String indexName : map.keySet()) {
								
				int pos1_beginIndex = matchExp.indexOf("%1");
				int pos1_endIndex= indexName.indexOf("_",pos1_beginIndex);
				String v1_columnValue = indexName.substring(pos1_beginIndex, pos1_endIndex);
				
				int pos2_beginIndex = pos1_endIndex + (matchExp.indexOf("%2") - pos1_beginIndex - 2);
				int pos2_endIndex= indexName.indexOf("_",pos2_beginIndex);
				if(pos2_endIndex<0)
					pos2_endIndex = indexName.length();
				String v2_rowValue = indexName.substring(pos2_beginIndex, pos2_endIndex);
						
				if(!columns.contains(v1_columnValue)) {columns.add(v1_columnValue);};
				if(!rows.contains(v2_rowValue)) {rows.add(v2_rowValue);};
						
			}
		}
		
		columns.sort(null); //columns.add(0,"");
		rows.sort(null);
		
		String[][] table = new String[rows.size()+1][columns.size()+1];
		
		table[0][0]="";
		for (int i = 0; i < columns.size(); i++) {
			table[0][i+1]=columns.get(i);
		}
		for (int i = 0; i < rows.size(); i++) {
			table[i+1][0]=rows.get(i);
		}
		
		for (Map<String, String> map : indexes) {
			for (String indexName : map.keySet()) {
								
				int pos1_beginIndex = matchExp.indexOf("%1");
				int pos1_endIndex= indexName.indexOf("_",pos1_beginIndex);
				String v1_columnValue = indexName.substring(pos1_beginIndex, pos1_endIndex);
				
				int pos2_beginIndex = pos1_endIndex + (matchExp.indexOf("%2") - pos1_beginIndex - 2);
				int pos2_endIndex= indexName.indexOf("_",pos2_beginIndex);
				if(pos2_endIndex<0)
					pos2_endIndex = indexName.length();
				String v2_rowValue = indexName.substring(pos2_beginIndex, pos2_endIndex);
				
				table[rows.indexOf(v2_rowValue)+1][columns.indexOf(v1_columnValue)+1]=map.get(indexName);
						
			}
		}
		
		List<List<String>> values = new ArrayList<>();
		for (int i = 0; i < table.length; i++) {
			List<String> row = new ArrayList<>();
			for (int j = 0; j < table[0].length; j++) {
				row.add(table[i][j]);
			}
			values.add(row);
		}
		
		
		VelocityReporter vr = new VelocityReporter();
		return vr.createTableReport(values, title,Reportable.Color.RED); 
	}
		
//	@RequestMapping("/years/{year}/status")	
//	public String method(@PathVariable(required=true) String year) throws Exception {
//		ic.method(index, year, month, count, category, subcategory, common, flow, request);
//		
//		VelocityReporter vr = new VelocityReporter();
//		return vr.createTableReport(getTableValues(), "Example table","INPUT"); 
//	}
//	
//	private List<List<String>> getTableValues(){
//		List<List<String>> result = new ArrayList<>();
//		
//		result.add(Arrays.asList(new String[]{"","Column1","Column2","Column3"}));
//		result.add(Arrays.asList(new String[]{"Row1","Cell","Cell","Cell"}));
//		result.add(Arrays.asList(new String[]{"Row2","Cell","Cell","Cell"}));
//		result.add(Arrays.asList(new String[]{"Row3","Cell","Cell","Cell"}));
//		result.add(Arrays.asList(new String[]{"Row4","Cell","Cell","Cell"}));
//		
//		return result;
//	}
		
		
//	private List<Map<String,String>> callToIndexingClient(){
//		List<Map<String,String>> result;
//		
//		return result;
//	}
	
	public MasterBook getMb() {
		return mb;
	}

	public void setMb(MasterBook mb) {
		this.mb = mb;
	}
	
	
	
	private boolean containsValuesNotNone(String indexToCheck,List<String> values) {
		for (String param : values) {
			if((!param.equals("none")) && (!indexToCheck.contains(param))) {
				return false;
			}
		}
		return true;
	}
	
}

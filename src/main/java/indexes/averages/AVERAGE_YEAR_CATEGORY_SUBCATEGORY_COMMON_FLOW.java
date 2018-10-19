package indexes.averages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import indexes.MapIndex;
import indexes.Utils;
import indexes.nets.YEAR_MONTH_CATEGORY_SUBCATEGORY_COMMON_FLOW;
import model.books.YearBook;
import model.schema.Transaction;

public class AVERAGE_YEAR_CATEGORY_SUBCATEGORY_COMMON_FLOW extends MapIndex {
	
	public static Map<String,Double> getIndexMap(YearBook yb){		
		Optional<Map<String, Double>> optResult = MapIndex.alreadyComputedIndex(yb,getMyClass());
		if(optResult.isPresent()) {
			return optResult.get();
		}else {
			Map<String,Double> map = computeYearIndex(yb);
			MapIndex.addComputedIndex(yb, getMyClass(), map);
			return map;
		}
	}
	
	private static Class<? extends MapIndex> getMyClass(){
		return AVERAGE_YEAR_CATEGORY_SUBCATEGORY_COMMON_FLOW.class;
	}

	private static Map<String,Double> computeYearIndex(YearBook yb) {
		Map<String,Double> map = new HashMap<>();
		
		//double amount = 0;
		//Vector<String> counts = yb.getSchema().getCounts(); 		
		Vector<String> sectors = yb.getSchema().getSectors();
		List<String> commons = Arrays.asList(new String[]{"Y","N"});
		List<String> flows = Arrays.asList(new String[]{"INPUT","OUTPUT"});
				for (String s : sectors) {
					for (String cmm : commons) {
						for (String f : flows) {
							String year = String.valueOf(yb.getYear());
							//String month = (m>9)? ""+m:"0"+m; 
							//String count = c;
							String category = s.split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
							String subcategory = (s.split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":s.split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
							String common =cmm;
							String flow = f;
							
							String id = Utils.delimiterizing(Constants.AVERAGE,year,category,subcategory,common,flow);
							
							if(!map.containsKey(id)) {
								map.put(id, 0d);
							}
							
						}
					}
					
				}
				
				Map<String,Double> depMap = YEAR_MONTH_CATEGORY_SUBCATEGORY_COMMON_FLOW.getIndexMap(yb);
				
				Map<String,Double> tmpAmountMap = new HashMap<>();
				Map<String,Integer> tmpIncrementMap = new HashMap<>();
				
				for (String key : depMap.keySet()) {
					String[] fields = Utils.undelimiterizing(key);
					
					String year = fields[0];//String.valueOf(yb.getYear());
					//String month = fields[1];//(m>9)? ""+m:"0"+m; 
					//String count = c;
					String category = fields[2];//s.split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
					String subcategory = fields[3];//(s.split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":s.split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
					String common = fields[4];//
					String flow = fields[5];//
					
					String id = Utils.delimiterizing(Constants.AVERAGE,year,category,subcategory,common,flow);
					
					if(!tmpAmountMap.containsKey(id)) {
						tmpAmountMap.put(id, 0d);
					}
					tmpAmountMap.put(id,tmpAmountMap.get(id)+depMap.get(key));
					
					if(!tmpIncrementMap.containsKey(id)) {
						tmpIncrementMap.put(id, 0);
					}
					tmpIncrementMap.put(id,tmpIncrementMap.get(id)+1);
					
					
					
				}
				
				for (String id : tmpAmountMap.keySet()) {
					map.put(id, tmpAmountMap.get(id)/tmpIncrementMap.get(id));
				}
		
		return map;
 	}
	
}

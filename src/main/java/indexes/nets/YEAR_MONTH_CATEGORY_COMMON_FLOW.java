package indexes.nets;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import indexes.MapIndex;
import indexes.Utils;
import model.books.YearBook;

public class YEAR_MONTH_CATEGORY_COMMON_FLOW extends MapIndex {

	public static Map<String,Long> getIndexMap(YearBook yb){		
		Optional<Map<String, Long>> optResult = MapIndex.alreadyComputedIndex(yb,getMyClass());
		if(optResult.isPresent()) {
			return optResult.get();
		}else {
			Map<String,Long> map = computeYearIndex(yb);
			MapIndex.addComputedIndex(yb, getMyClass(), map);
			return map;
		}
	}
	
	private static Class<? extends MapIndex> getMyClass(){
		return YEAR_MONTH_CATEGORY_COMMON_FLOW.class;
	}

	private static Map<String,Long> computeYearIndex(YearBook yb) {
		Map<String,Long> map = new HashMap<>();
		
		Map<String,Long> depMap = YEAR_MONTH_CATEGORY_SUBCATEGORY_COMMON_FLOW.getIndexMap(yb);
		
		for (String key : depMap.keySet()) {
			String[] fields = Utils.undelimiterizing(key);
			
			String year = fields[0];//String.valueOf(yb.getYear());
			String month = fields[1];//(m>9)? ""+m:"0"+m; 
			//String count = c;
			String category = fields[2];//s.split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
			//String subcategory = fields[4];//(s.split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":s.split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
			String common = fields[4];//
			String flow = fields[5];//
			
			String id = Utils.delimiterizing(year,month,category,common,flow);
						
			if(!map.containsKey(id)) {
				map.put(id, 0L);
			}
			
			map.put(id,map.get(id)+depMap.get(key));
		}
		
		return map;
 	}
	
}

package indexes.nets;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import indexes.MapIndex;
import indexes.Utils;
import model.books.YearBook;

public class YEAR_MONTH extends MapIndex {

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
		return YEAR_MONTH.class;
	}

	private static Map<String,Double> computeYearIndex(YearBook yb) {
		Map<String,Double> map = new HashMap<>();
		
		Map<String,Double> depMap = YEAR_MONTH_COMMON.getIndexMap(yb);
		
		for (String key : depMap.keySet()) {
			String[] fields = Utils.undelimiterizing(key);
			
			String year = fields[0];//String.valueOf(yb.getYear());
			String month = fields[1];//(m>9)? ""+m:"0"+m; 
			//String count = c;
			//String category = fields[3];//s.split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
			//String subcategory = fields[4];//(s.split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":s.split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
			//YEAR_MONTH_COMMON.javaString common = fields[2];//
			//String flow = fields[4];//
			
			String id = Utils.delimiterizing(year,month);
						
			if(!map.containsKey(id)) {
				map.put(id, 0d);
			}
			
			map.put(id,map.get(id)+depMap.get(key));
		}
		
		return map;
 	}
	
}

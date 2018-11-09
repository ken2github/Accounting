package indexes.averages;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import indexes.MapIndex;
import indexes.Utils;
import indexes.nets.YEAR_MONTH;
import model.books.YearBook;

public class AVERAGE_YEAR extends MapIndex {
	
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
		return AVERAGE_YEAR.class;
	}

	private static Map<String,Long> computeYearIndex(YearBook yb) {
		Map<String,Long> map = new HashMap<>();
		
		//double amount = 0;
		//Vector<String> counts = yb.getSchema().getCounts(); 		
		//Vector<String> supersectors = yb.getSchema().getSuperSectors();
		//List<String> commons = Arrays.asList(new String[]{"Y","N"});
		//List<String> flows = Arrays.asList(new String[]{"INPUT","OUTPUT"});
				//for (String ss : supersectors) {
					//for (String cmm : commons) {
						//for (String f : flows) {
							String year = String.valueOf(yb.getYear());
							//String month = (m>9)? ""+m:"0"+m; 
							//String count = c;
							//String category = ss;
							//String common = cmm;
							//String flow = f;
							
							String id = Utils.delimiterizing(Constants.AVERAGE,year);
							
							if(!map.containsKey(id)) {
								map.put(id, 0L);
							}
							
						//}
					//}
					
				//}
				
				Map<String,Long> depMap = YEAR_MONTH.getIndexMap(yb);
				
				Map<String,Long> tmpAmountMap = new HashMap<>();
				Map<String,Integer> tmpIncrementMap = new HashMap<>();
				
				for (String key : depMap.keySet()) {
					String[] fields = Utils.undelimiterizing(key);
					
					year = fields[0];//String.valueOf(yb.getYear());
					//String month = fields[1];//(m>9)? ""+m:"0"+m; 
					//String count = c;
					//String category = fields[2];//s.split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
					//String subcategory = fields[3];//(s.split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":s.split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
					//String common = fields[2];//
					//String flow = fields[4];//
					
					id = Utils.delimiterizing(Constants.AVERAGE,year);
					
					if(!tmpAmountMap.containsKey(id)) {
						tmpAmountMap.put(id, 0L);
					}
					tmpAmountMap.put(id,tmpAmountMap.get(id)+depMap.get(key));
					
					if(!tmpIncrementMap.containsKey(id)) {
						tmpIncrementMap.put(id, 0);
					}
					tmpIncrementMap.put(id,tmpIncrementMap.get(id)+1);
					
					
					
				}
				
				for (String idk : tmpAmountMap.keySet()) {
					map.put(idk, tmpAmountMap.get(idk)/tmpIncrementMap.get(idk));
				}
		
		return map;
 	}
	
}

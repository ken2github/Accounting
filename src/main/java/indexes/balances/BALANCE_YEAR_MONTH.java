package indexes.balances;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import indexes.MapIndex;
import indexes.Utils;
import model.books.CountMonthTransactions;
import model.books.MonthBook;
import model.books.YearBook;

public class BALANCE_YEAR_MONTH extends MapIndex {
	
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
		return BALANCE_YEAR_MONTH.class;
	}

	private static Map<String,Long> computeYearIndex(YearBook yb) {
		Map<String,Long> map = new HashMap<>();
		
		//double amount = 0;
		//Vector<String> counts = yb.getSchema().getCounts(); 
		Vector<String> months = new Vector<>();
		String year = String.valueOf(yb.getYear());
		for (MonthBook mb : yb.getMonthBooks()) {
			String prefix = (mb.getMonth()<=9)?"0":"";
			String month =prefix+mb.getMonth();
			months.add(month);
			for (CountMonthTransactions cmt : mb.getListOfcountMonthTransactions()) {
				String id = Utils.delimiterizing(Constants.BALANCE,year,month);
								
				// Int with balance of month
				if(!map.containsKey(id)) {
					map.put(id, 0L);
				}
				
				map.put(id, map.get(id)+cmt.getFinalBalance());
			}
			
			
		}
		//Vector<String> sectors = yb.getSchema().getSectors();
		//List<String> commons = Arrays.asList(new String[]{"Y","N"});
		//List<String> flows = Arrays.asList(new String[]{"INPUT","OUTPUT"});
				for (String mm : months) {
					//for (String cmm : commons) {
						//for (String c : counts) {
							//year = String.valueOf(yb.getYear());
							//String month = (m>9)? ""+m:"0"+m; 
							//String count = c;
							//String category = s.split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
							//String subcategory = (s.split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":s.split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
							String month =mm;
							//String count = c;
							
							String id = Utils.delimiterizing(Constants.BALANCE,year,month);
							
							
							// Int with balance of month
							if(!map.containsKey(id)) {
								map.put(id, 0L);
							}
							
						//}
					//}
					
				}
		
		return map;
 	}
	
}

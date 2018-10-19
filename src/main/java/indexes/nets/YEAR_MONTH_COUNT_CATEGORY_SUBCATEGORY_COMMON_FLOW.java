package indexes.nets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import indexes.MapIndex;
import indexes.Utils;
import model.books.CountMonthTransactions;
import model.books.MonthBook;
import model.books.YearBook;
import model.schema.Transaction;

public class YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON_FLOW extends MapIndex {

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
		return YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON_FLOW.class;
	}

	private static Map<String,Double> computeYearIndex(YearBook yb) {
		Map<String,Double> map = new HashMap<>();
		
		//double amount = 0;
		Vector<String> counts = yb.getSchema().getCounts(); 		
		Vector<String> sectors = yb.getSchema().getSectors();
		List<String> commons = Arrays.asList(new String[]{"Y","N"});
		List<String> flows = Arrays.asList(new String[]{"INPUT","OUTPUT"});
		for (int m=1; m<=12; m++) {
			for (String c : counts) {
				for (String s : sectors) {
					for (String cmm : commons) {
						for (String f : flows) {
							String year = String.valueOf(yb.getYear());
							String month = (m>9)? ""+m:"0"+m; 
							String count = c;
							String category = s.split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
							String subcategory = (s.split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":s.split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
							String common =cmm;
							String flow = f;
							
							String id = Utils.delimiterizing(year,month,count,category,subcategory,common,flow);
							
							if(!map.containsKey(id)) {
								map.put(id, 0d);
							}
							
						}
					}
					
				}
			}
		}
		
		for (MonthBook mb : yb.getMonthBooks()) {
			for (CountMonthTransactions cmt : mb.getListOfcountMonthTransactions()) {
				for (Transaction t : cmt.getTransactions()) {
					String year = String.valueOf(yb.getYear());
					String month = (mb.getMonth()>9)? ""+mb.getMonth():"0"+mb.getMonth(); 
					String count = cmt.getCount();
					String category = t.getSector().split(Transaction.SECTOR_DELIMITER_REGEXP)[0];
					String subcategory = (t.getSector().split(Transaction.SECTOR_DELIMITER_REGEXP).length==1)?"":t.getSector().split(Transaction.SECTOR_DELIMITER_REGEXP)[1];
					String common =t.isCommon()?"Y":"N";
					String flow = (t.getAmount()>0)?"INPUT":"OUTPUT";
					
					String id = Utils.delimiterizing(year,month,count,category,subcategory,common,flow);
					System.out.println(id);
					map.put(id,map.get(id)+t.getAmount());
				}
			}
		}
		return map;
 	}
	
}

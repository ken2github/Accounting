package indexes;

import java.util.Map;

public interface Index {

	//AVERAGE.YEAR.MONTH.COUNT.CATEGORY.SUBCATEGORY.COMMON.BALANCE
	
	Map<String, Double> getMap();
	
}

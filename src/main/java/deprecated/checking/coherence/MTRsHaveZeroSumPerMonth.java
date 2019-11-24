package deprecated.checking.coherence;

import deprecated.checking.AbstractExecutableRule;
import deprecated.checking.CheckException;
import deprecated.checking.CheckType;
import deprecated.indexes.nets.YEAR_MONTH_CATEGORY;
import deprecated.model.books.YearBook;

public class MTRsHaveZeroSumPerMonth extends AbstractExecutableRule {
		
	public MTRsHaveZeroSumPerMonth() {
		type = CheckType.FINANCE;
	}
	
	protected void _execute(YearBook yb) throws Exception{
		for(String index : YEAR_MONTH_CATEGORY.getIndexMap(yb).keySet()){
			if(index.endsWith("mtr") && YEAR_MONTH_CATEGORY.getIndexMap(yb).get(index)!=0) {
				exceptions.add(new CheckException(this,yb,null,null,null, "index ["+index+"] has value ["+(YEAR_MONTH_CATEGORY.getIndexMap(yb).get(index))+"] that is not zero"));
			}
		}		
	}

	protected void _skip() {}

}

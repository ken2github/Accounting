package deprecated.checking.coherence;

import deprecated.checking.AbstractExecutableRule;
import deprecated.checking.CheckException;
import deprecated.checking.CheckType;
import deprecated.model.books.CountMonthTransactions;
import deprecated.model.books.MonthBook;
import deprecated.model.books.YearBook;

public class AllCountsInEachMonthEnforcer extends AbstractExecutableRule {
		
	public AllCountsInEachMonthEnforcer() {
		type = CheckType.COMPLETENESS;
	}
	
	protected void _execute(YearBook yb) throws Exception{
		int expectedCounts = yb.getSchema().getCounts().size();
		String expectedCountList = "";
		for (String c : yb.getSchema().getCounts()) {
			expectedCountList+=c+",";
		}
		for (MonthBook mb : yb.getMonthBooks()) {
			//System.out.println(expectedCountList);
			//System.out.println(expectedCounts);
			//System.out.println(mb.getListOfcountMonthTransactions().size());
			if(mb.getListOfcountMonthTransactions().size()<expectedCounts) {
				String availableCounts = "";
				for (CountMonthTransactions cmt : mb.getListOfcountMonthTransactions()) {
					availableCounts+=cmt.getCount()+",";
				}
				exceptions.add(new CheckException(this, yb, mb, null, null, 
						"The filed COUNTs ["+(availableCounts)+"] are in number ["+(mb.getListOfcountMonthTransactions().size())+"] lesser than expected ["+(expectedCounts)+"], full expected list is ["+(expectedCountList)+"]"));				
			}
			
		}
	}

	protected void _skip() {}

}

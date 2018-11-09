package checking.coherence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import checking.AbstractExecutableRule;
import checking.CheckException;
import checking.CheckType;
import indexes.Utils;
import indexes.nets.YEAR_MONTH_COUNT;
import model.books.CountMonthTransactions;
import model.books.MonthBook;
import model.books.YearBook;

public class BalanceEnforcer extends AbstractExecutableRule {
		
	public BalanceEnforcer() {
		type = CheckType.FINANCE;
	}
	
	protected void _execute(YearBook yb) throws Exception{
		Map<String, Map<Integer,Long>> partials = new HashMap<>();
		for (String c : yb.getSchema().getInitialBalances().keySet()) {
			partials.put(c, new HashMap<>());
			partials.get(c).put(0, yb.getSchema().getInitialBalances().get(c));
		}
		
		// Copy month books in list
		// sort list by months
		// compute
		List<MonthBook> sortedMonthBooks = new ArrayList<>(yb.getMonthBooks());
		sortedMonthBooks.sort(new Comparator<MonthBook>() {
			@Override
			public int compare(MonthBook o1, MonthBook o2) {
				return Integer.signum(o1.getMonth()-o2.getMonth());
			}			
		});
		
		for (MonthBook mb : sortedMonthBooks) {
			for (CountMonthTransactions cmt : mb.getListOfcountMonthTransactions()) {			
				String id = Utils.delimiterizing((yb.getYear()+""),Utils.intToMonth(mb.getMonth()),cmt.getCount());	
				//System.out.println(id);
				long computedBalance = partials.get(cmt.getCount()).get(mb.getMonth()-1) + YEAR_MONTH_COUNT.getIndexMap(yb).get(id);
				partials.get(cmt.getCount()).put(mb.getMonth(), computedBalance);				
				if(!(Math.pow(computedBalance-cmt.getFinalBalance(),2)<0.0001d)) {
					long delta = computedBalance - cmt.getFinalBalance();
					exceptions.add(new CheckException(this, yb, mb, cmt, null, 
							"The file reported BALANCE ["+(cmt.getFinalBalance())+"] does not equal computed BALANCE ["+(computedBalance)+"], computed using listed TRANSACTIONS and BALANCE at begin of YEAR. Delta is ["+(delta)+"]"));
				}
			}
		}
		
	}

	protected void _skip() {}

}

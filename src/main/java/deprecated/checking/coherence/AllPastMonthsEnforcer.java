package deprecated.checking.coherence;

import java.time.Instant;
import java.util.Date;

import deprecated.checking.AbstractExecutableRule;
import deprecated.checking.CheckException;
import deprecated.checking.CheckType;
import deprecated.model.books.MonthBook;
import deprecated.model.books.YearBook;

public class AllPastMonthsEnforcer extends AbstractExecutableRule {
		
	public AllPastMonthsEnforcer() {
		type = CheckType.COMPLETENESS;
	}
	
	@SuppressWarnings("deprecation")
	protected void _execute(YearBook yb) throws Exception{
		Date today = Date.from(Instant.now());
		if(yb.getMonthBooks().size()<12) {
			int expectedMonths = 12; 
			if(yb.getYear() == (today.getYear()+1900)) {
				expectedMonths = today.getMonth(); 
				if(yb.getMonthBooks().size()<today.getMonth()) {
					String listedMonths="";
					for (MonthBook mb : yb.getMonthBooks()) {
						listedMonths+=mb.getMonth()+",";
					}
					exceptions.add(new CheckException(this, yb, null, null, null, "The filed months ["+(listedMonths)+"] are in number ["+(yb.getMonthBooks().size())+"] lesser than expected ["+(expectedMonths)+"]"));
				}
			}
		}
	}

	protected void _skip() {}

}

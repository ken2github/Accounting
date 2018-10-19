package checking.coherence;

import model.books.YearBook;

public interface CheckRule {

	CheckType getType();
	
	boolean apply(YearBook yb);

}

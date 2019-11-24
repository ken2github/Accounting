package test.on.data00;
import static org.junit.Assert.fail;
import org.junit.Test;

import deprecated.model.books.CountMonthTransactions;
import deprecated.model.books.MonthBook;

import java.io.File;
import static org.junit.Assert.assertEquals;

import test.TestPath;

public class MonthBookTest {

	@Test
	public void test() {
		try {
			MonthBook m = new MonthBook(new File(TestPath.BASE_PATH+"data-00\\2018"),1);
			myAsserts(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}		
	}
	
	public static void myAsserts(MonthBook m) {
		assertEquals(1,m.getMonth());
		assertEquals(2018,m.getYear());
		assertEquals(1,m.getListOfcountMonthTransactions().size());			
		
		CountMonthTransactions c = m.getListOfcountMonthTransactions().get(0);
		CountMonthTransactionsTest.myAsserts(c);
	}

}

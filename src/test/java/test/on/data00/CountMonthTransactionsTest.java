package test.on.data00;

import static org.junit.Assert.fail;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.File;


import books.CountMonthTransactions;
import schema.Transaction;
import test.TestPath;

public class CountMonthTransactionsTest {

	@Test
	public void test() {
		try {
			//CountMonthTransactions c = new CountMonthTransactions(new File("C:\\Users\\primo\\eclipse-workspace\\Accounting\\resources\\data-00\\2017\\2017.01.BPN.501.00.db")); 
			CountMonthTransactions c = new CountMonthTransactions(new File(TestPath.BASE_PATH+"data-00\\2017\\2017.01.BPN.501.00.db")); 
			myAsserts(c);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}		
	}
	
	public static void myAsserts(CountMonthTransactions c) {
		assertEquals("BPN",c.getCount());
		assertEquals(501.00D,c.getFinalBalance(),0.001d);
		assertEquals(1,c.getMonth());
		assertEquals(2017,c.getYear());
		assertEquals(1,c.getTransactions().size());
		
		Transaction t= c.getTransactions().get(0);
		TransactionTest.myAsserts(t);
	}
	
	
}

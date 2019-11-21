package test.on.data00;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

import model.books.MonthBook;
import model.books.YearBook;

import java.io.File;

import test.TestPath;

public class YearBookTest {

	@Test
	public void test() {
		try {
			YearBook y = new YearBook(new File(TestPath.BASE_PATH+"data-00\\2018"));
			myAsserts(y);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}		
	}
	
	public static void myAsserts(YearBook y) {
		assertEquals(2018,y.getYear());
		assertNotNull(y.getSchema());
		assertEquals(1,y.getSchema().getCounts().size());
		assertEquals(2+2,y.getSchema().getSectors().size());
		assertEquals("sps",y.getSchema().getSectors().get(0));
		assertEquals("sps.cibo",y.getSchema().getSectors().get(1));
		assertEquals("sps.rimborsi",y.getSchema().getSectors().get(2));
		assertEquals("sps.extra",y.getSchema().getSectors().get(3));
		assertEquals(1,y.getSchema().getSuperSectors().size());
		assertEquals("BPN",y.getSchema().getCounts().get(0));
		assertEquals(1,y.getMonthBooks().size());
		assertEquals(1,y.getMonthBooks().get(0).getListOfcountMonthTransactions().size());
		
		MonthBook m = y.getMonthBooks().get(0);
		MonthBookTest.myAsserts(m);
	}

}

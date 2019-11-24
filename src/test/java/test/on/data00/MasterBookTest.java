package test.on.data00;
import static org.junit.Assert.fail;
import org.junit.Test;

import deprecated.model.books.MasterBook;
import deprecated.model.books.YearBook;

import static org.junit.Assert.assertEquals;

import java.io.File;

import test.TestPath;

public class MasterBookTest {

	@Test
	public void test() {
		try {
			MasterBook b = new MasterBook(new File(TestPath.BASE_PATH+"data-00"));			
			myAsserts(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}		
	

	}

	public static void myAsserts(MasterBook b) {	
		assertEquals(1,b.getYearBooks().size());
		
		YearBook y = b.getYearBooks().get(0); 	
		YearBookTest.myAsserts(y);
	}
}

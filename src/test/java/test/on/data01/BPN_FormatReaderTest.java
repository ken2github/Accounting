package test.on.data01;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.File;

import store.io.FormatMapper;
import store.io.FormatReader;
import store.io.TransactionFilterException;
import store.io.readers.Parametric_FormatReader;
import test.TestPath;

public class BPN_FormatReaderTest {

	@Test
	public void test() {
		FormatReader r = new Parametric_FormatReader(FormatMapper.BPN_FORMAT,new TransactionFilterException() {});
		try {
			r.loadSourceFile(new File(TestPath.BASE_PATH+"data-01\\2018 source\\bpn.2018.01.02.03.balance.6381.18.csv"));
			assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
		
		
	}

}

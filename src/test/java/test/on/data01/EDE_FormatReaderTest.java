package test.on.data01;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.File;

import store.io.FormatMapper;
import store.io.FormatReader;
import store.io.readers.Parametric_FormatReader;
import test.TestPath;

public class EDE_FormatReaderTest {

	@Test
	public void test() {
		FormatReader r = new Parametric_FormatReader(FormatMapper.EDENRED_FORMAT);
		try {
			r.loadSourceFile(new File(TestPath.BASE_PATH+"data-01\\2017 source\\ede.2017.05.06.07.08.09.balance.2211.08.csv"));
			assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
		
		
	}

}

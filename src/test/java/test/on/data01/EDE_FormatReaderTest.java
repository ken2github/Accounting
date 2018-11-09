package test.on.data01;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.File;

import store.io.FormatMapper;
import store.io.FormatReader;
import store.io.readers.EdenredValidator;
import store.io.readers.Parametric_FormatReader;
import test.TestPath;

public class EDE_FormatReaderTest {

	@Test
	public void test() {
		FormatReader r = new Parametric_FormatReader(FormatMapper.EDENRED_FORMAT,new EdenredValidator());
		try {
			r.loadSourceFile(new File(TestPath.BASE_PATH+"data-01\\2018 source\\ede.2018.01.02.03.04.05.06.07.08.09.10.balance.2219.28.csv"));
			assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
		
		
	}

}

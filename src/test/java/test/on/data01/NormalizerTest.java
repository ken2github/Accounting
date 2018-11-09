package test.on.data01;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import store.io.Normalizer;
import test.TestPath;

public class NormalizerTest {

	@Test
	public void test() {
		File directorySrc = new File(TestPath.BASE_PATH+"data-01\\2018 source");
		File directoryDest = new File(TestPath.BASE_PATH+"data-01\\2018");
		
		try {			
			Normalizer.directoryNormalize(directorySrc,directoryDest);
			assertEquals(30+2, directoryDest.listFiles().length);
			File[] files = directoryDest.listFiles();
			Arrays.sort(files);
			
			List<String> filenames = new ArrayList<>();
			for (File file : files) {
				filenames.add(file.getName());
			}
			
//			assertEquals(true,(findFileStartingWith(filenames,"2018.01.BNP")).isPresent());
			assertEquals(true,filenames.contains("2018.01.BNP.6159.94.db"));
			assertEquals(true,filenames.contains("2018.02.BNP.6089.59.db"));
			assertEquals(true,filenames.contains("2018.03.BNP.8551.00.db"));
			assertEquals(true,filenames.contains("2018.04.BNP.8551.00.db"));
			assertEquals(true,filenames.contains("2018.05.BNP.8561.00.db"));
			assertEquals(true,filenames.contains("2018.06.BNP.8561.00.db"));
			assertEquals(true,filenames.contains("2018.07.BNP.8546.01.db"));
			assertEquals(true,filenames.contains("2018.08.BNP.8561.00.db"));
			assertEquals(true,filenames.contains("2018.09.BNP.8561.00.db"));
			assertEquals(true,filenames.contains("2018.10.BNP.8561.00.db"));
			
			assertEquals(true,filenames.contains("2018.01.BPN.982.97.db"));
			assertEquals(true,filenames.contains("2018.02.BPN.3894.54.db"));
			assertEquals(true,filenames.contains("2018.03.BPN.6389.61.db"));
			assertEquals(true,filenames.contains("2018.04.BPN.6399.61.db"));
			assertEquals(true,filenames.contains("2018.05.BPN.6389.61.db"));
			assertEquals(true,filenames.contains("2018.06.BPN.6399.61.db"));
			assertEquals(true,filenames.contains("2018.07.BPN.6389.61.db"));
			assertEquals(true,filenames.contains("2018.08.BPN.6399.61.db"));
			assertEquals(true,filenames.contains("2018.09.BPN.6389.61.db"));
			assertEquals(true,filenames.contains("2018.10.BPN.6389.61.db"));
			
			assertEquals(true,filenames.contains("2018.01.EDE.2749.91.db"));
			assertEquals(true,filenames.contains("2018.02.EDE.2749.91.db"));
			assertEquals(true,filenames.contains("2018.03.EDE.2749.91.db"));
			assertEquals(true,filenames.contains("2018.04.EDE.2749.91.db"));
			assertEquals(true,filenames.contains("2018.05.EDE.2361.99.db"));
			assertEquals(true,filenames.contains("2018.06.EDE.2442.55.db"));
			assertEquals(true,filenames.contains("2018.07.EDE.2627.80.db"));
			assertEquals(true,filenames.contains("2018.08.EDE.2808.80.db"));
			assertEquals(true,filenames.contains("2018.09.EDE.2254.38.db"));
			assertEquals(true,filenames.contains("2018.10.EDE.2219.28.db"));
			
		} catch (Exception e) {
			// TODO Auto.generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}
	}	
	
//	private void assertFile(String filename) {
//		
//	}
//	
//	private Optional<String> findFileStartingWith(List<String> filenames,String prefix) {
//		return filenames.stream().filter(fn -> fn.startsWith(prefix)).findFirst();
//	}
	

}

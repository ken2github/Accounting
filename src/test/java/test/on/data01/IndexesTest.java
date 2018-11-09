package test.on.data01;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.Test;

import com.google.common.reflect.ClassPath;

import indexes.*;
import indexes.averages.AVERAGE_YEAR_CATEGORY_FLOW;
import indexes.averages.AVERAGE_YEAR_CATEGORY_SUBCATEGORY;
import indexes.nets.YEAR;
import indexes.nets.YEAR_MONTH;
import indexes.nets.YEAR_MONTH_CATEGORY;
import indexes.nets.YEAR_MONTH_CATEGORY_COMMON_FLOW;
import indexes.nets.YEAR_MONTH_CATEGORY_FLOW;
import indexes.nets.YEAR_MONTH_CATEGORY_SUBCATEGORY;
import indexes.nets.YEAR_MONTH_CATEGORY_SUBCATEGORY_COMMON_FLOW;
import indexes.nets.YEAR_MONTH_CATEGORY_SUBCATEGORY_FLOW;
import indexes.nets.YEAR_MONTH_COMMON;
import indexes.nets.YEAR_MONTH_COMMON_FLOW;
import indexes.nets.YEAR_MONTH_COUNT;
import indexes.nets.YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON;
import indexes.nets.YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON_FLOW;
import indexes.nets.YEAR_MONTH_COUNT_FLOW;
import indexes.nets.YEAR_MONTH_FLOW;
import model.books.YearBook;
import test.TestPath;

public class IndexesTest {

	@Test
	public void test() {
		try {
			YearBook yb = new YearBook(new File(TestPath.BASE_PATH+"data-01\\2018"));
			
			int years = 1;
			int months = 12;
			int counts = yb.getSchema().getCounts().size();
			assertEquals(3, counts);
			int categories = yb.getSchema().getSuperSectors().size();
			assertEquals(1, categories);
			int subcategories = yb.getSchema().getSectors().size();
			assertEquals(2, subcategories);
			int commons = 2;
			int flows = 2;
			
			Map<String, Long> map = YEAR_MONTH_CATEGORY_COMMON_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_CATEGORY_COMMON_FLOW.class);
			assertOnIndex(years*months*categories*commons*flows,map);
			
			map = YEAR_MONTH_CATEGORY_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_CATEGORY_FLOW.class);
			assertOnIndex(years*months*categories*flows,map);
			
			
			map = YEAR_MONTH_CATEGORY_SUBCATEGORY_COMMON_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_CATEGORY_SUBCATEGORY_COMMON_FLOW.class);
			assertOnIndex(years*months*subcategories*commons*flows,map);
			
			
			map = YEAR_MONTH_CATEGORY_SUBCATEGORY_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_CATEGORY_SUBCATEGORY_FLOW.class);
			assertOnIndex(years*months*subcategories*flows,map);
			
			
			map = YEAR_MONTH_CATEGORY_SUBCATEGORY.getIndexMap(yb);
			printValues(map,YEAR_MONTH_CATEGORY_SUBCATEGORY.class);
			assertOnIndex(years*months*subcategories,map);
			
			
			map = YEAR_MONTH_CATEGORY.getIndexMap(yb);
			printValues(map,YEAR_MONTH_CATEGORY.class);
			assertOnIndex(years*months*categories,map);
			
			
			map = YEAR_MONTH_COMMON_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_COMMON_FLOW.class);
			assertOnIndex(years*months*commons*flows,map);
			
			
			map = YEAR_MONTH_COMMON.getIndexMap(yb);
			printValues(map,YEAR_MONTH_COMMON.class);
			assertOnIndex(years*months*commons,map);
			
			
			map = YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON_FLOW.class);
			assertOnIndex(years*months*counts*subcategories*commons*flows,map);
			
			
			map = YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON.getIndexMap(yb);
			printValues(map,YEAR_MONTH_COUNT_CATEGORY_SUBCATEGORY_COMMON.class);
			assertOnIndex(years*months*counts*subcategories*commons,map);
			
			
			map = YEAR_MONTH_COUNT_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_COUNT_FLOW.class);
			assertOnIndex(years*months*counts*flows,map);
			
			
			map = YEAR_MONTH_COUNT.getIndexMap(yb);
			printValues(map,YEAR_MONTH_COUNT.class);
			assertOnIndex(years*months*counts,map);
			
			
			map = YEAR_MONTH_FLOW.getIndexMap(yb);
			printValues(map,YEAR_MONTH_FLOW.class);
			assertOnIndex(years*months*flows,map);
			
			
			map = YEAR_MONTH.getIndexMap(yb);
			printValues(map,YEAR_MONTH.class);
			assertOnIndex(years*months,map);
			
			
			map = YEAR.getIndexMap(yb);
			printValues(map,YEAR.class);	
			assertOnIndex(years,map);
			
			map = AVERAGE_YEAR_CATEGORY_FLOW.getIndexMap(yb);
			printValues(map,AVERAGE_YEAR_CATEGORY_FLOW.class);	
			assertOnIndex(years*categories*flows,map);
					
			
			map = AVERAGE_YEAR_CATEGORY_SUBCATEGORY.getIndexMap(yb);
			printValues(map,AVERAGE_YEAR_CATEGORY_SUBCATEGORY.class);	
			assertOnIndex(years*subcategories,map);
			
			printAllIndexes(yb);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Got unexpected exception: "+e.toString());
		}		
	}
	
	private void printAllIndexes(YearBook yb) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		printIndexesInPackage("indexes",yb);
	}
	
	private void printIndexesInPackage(String packagename,YearBook yb) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();

		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
		  if (info.getName().startsWith(packagename+".")) {
		    final Class<?> clazz = info.load();
		    if(MapIndex.class.isAssignableFrom(clazz) && (!clazz.getName().equals(MapIndex.class.getName()))) {
		    	//System.out.println(clazz.getName());
		    				    	
		    	Method method = clazz.getMethod("getIndexMap", YearBook.class);
		    	Map<String, Long> map = (Map<String, Long>) method.invoke(null, yb);
		    	printValues(map,(Class<? extends MapIndex>)clazz);
		    }			    
		  }
		}
	}
	
	private void assertOnIndex(int expectedValues,Map<String, Long> map) {
		assertNotNull(map);
		assertTrue(map.entrySet().size()>0);
		assertEquals(expectedValues, map.entrySet().size());
	}
	
	private void printValues(Map<String, Long> map,Class<? extends MapIndex> index) {
//		System.out.println("["+index.getName()+"] Index of ["+map.values().size()+"] values:");	
//				
//		for (String key : map.keySet()) {
//			System.out.println(" ["+key + "] = ["+map.get(key)+"]");
//		}
	}

}

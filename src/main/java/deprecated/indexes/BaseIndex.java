package deprecated.indexes;

import java.util.Map;

import deprecated.model.books.MasterBook;
import deprecated.model.books.YearBook;

public abstract class BaseIndex implements Index{
	
	protected MasterBook mb;
	protected static Map<String, Long> map = null;
	
	public BaseIndex(MasterBook mb) {
		this.mb=mb;
	}
	
	
	
	public Map<String, Long> getMap(){
//		System.out.println("Stupppppp2");
		if(map==null) {			
			this.initMap();
//			System.out.println("Stupppppp");
		}
		return map;
	}
	
	protected abstract void initMap();
	
	public static Map<String,Long> getIndexMap(YearBook yb){
		return null;
	}
	
}

package indexes;

import java.util.Map;

import model.books.MasterBook;
import model.books.YearBook;

public abstract class BaseIndex implements Index{
	
	protected MasterBook mb;
	protected static Map<String, Double> map = null;
	
	public BaseIndex(MasterBook mb) {
		this.mb=mb;
	}
	
	
	
	public Map<String, Double> getMap(){
//		System.out.println("Stupppppp2");
		if(map==null) {			
			this.initMap();
//			System.out.println("Stupppppp");
		}
		return map;
	}
	
	protected abstract void initMap();
	
	public static Map<String,Double> getIndexMap(YearBook yb){
		return null;
	}
	
}
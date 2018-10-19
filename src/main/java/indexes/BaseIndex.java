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
		if(map==null) {			
			this.initMap();
		}
		return map;
	}
	
	protected abstract void initMap();
	
	public static Map<String,Double> getIndexMap(YearBook yb){
		return null;
	}
	
}

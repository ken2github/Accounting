package model.schema;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import store.IO;
import utils.MoneyConverter;

import static store.io.Format.*;

public class Schema {

	public static final String SECTOR_DELIMITER = ".";
	public static final String SECTOR_DELIMITER_REGEXP = "[.]";
	
	private Vector<String> counts = new Vector<>();
	private Map<String,Long> initialBalances = new HashMap<>();
	private Vector<String> sectors = new Vector<>();
	private Vector<String> superSectors = new Vector<>();
	private int year;
	
	public Schema(File yeardirectory) throws IOException {
		this.year=Integer.parseInt(yeardirectory.getName());
		
		Vector<String[]> items = IO.readItems(yeardirectory.getAbsolutePath()+"\\"+this.year+NORMALIZED_SEPARATOR+"counts."+DB_EXTENSION, 3);
		for (String[] strings : items) {
			counts.add(strings[0]);
			initialBalances.put(strings[0], MoneyConverter.parseDecimalStringToLong(strings[1],strings[2]));
			//System.out.println(strings[0]);
		}
		
		items = IO.readItems(yeardirectory.getAbsolutePath()+"\\"+this.year+NORMALIZED_SEPARATOR+"sectors."+DB_EXTENSION, 1);

		for (String[] strings : items) {
			sectors.add(strings[0]);
			
			String supersector = strings[0].split(SECTOR_DELIMITER_REGEXP)[0]; 
			if(!superSectors.contains(supersector)) {
				superSectors.add(supersector);
			}
		}
		
	}
	
	public Vector<String> getSuperSectors(){
		return superSectors;
	}

	public Vector<String> getCounts() {
		return counts;
	}

	public Map<String, Long> getInitialBalances() {
		return initialBalances;
	}

	public Vector<String> getSectors() {
		return sectors;
	}

	public int getYear() {
		return year;
	}

	
}

package deprecated.model.schema;

import static deprecated.store.io.Format.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import deprecated.store.IO;
import deprecated.utils.MoneyConverter;

public class Schema {

	public static final String SECTOR_DELIMITER = ".";
	public static final String SECTOR_DELIMITER_REGEXP = "[.]";
	
	public static final String RIMBORSI_SECTOR_SUFFIX = "rimborsi";
	public static final String EXTRA_SECTOR_SUFFIX = "extra";
	
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
		
		for (String superSector : this.superSectors) {
			sectors.add(superSector+SECTOR_DELIMITER+RIMBORSI_SECTOR_SUFFIX);
			sectors.add(superSector+SECTOR_DELIMITER+EXTRA_SECTOR_SUFFIX);
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

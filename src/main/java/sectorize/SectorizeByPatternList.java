package sectorize;

import java.io.File;
import java.util.Optional;
import java.util.Vector;

import model.schema.Transaction;

public class SectorizeByPatternList implements Sectorizer {

	private Vector<SectorRule> rules = new Vector<>();
	
	public SectorizeByPatternList(File mapList) {
		//read mapList
		//init chech rules
	}
	
	@Override
	public Optional<String> map(Transaction transaction, String count) {
		for (SectorRule sectorRule : rules) {
			if(sectorRule.match(transaction, count)) {
				return Optional.of(sectorRule.getSector());
			}
		}
		return Optional.empty();
	}

}

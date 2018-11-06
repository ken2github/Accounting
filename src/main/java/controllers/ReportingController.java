package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import checking.CheckException;
import checking.CheckingEngine;
import checking.ExecutionStatus;
import checking.RuleGroup;
import model.books.MasterBook;
import model.books.YearBook;

@RestController
@RequestMapping("/reports/checking")
public class ReportingController {

	Logger logger = LoggerFactory.getLogger(ReportingController.class);
	
	@Autowired
	private MasterBook mb;
	
	@Autowired
	private CheckingEngine ce;
		
	@RequestMapping("/years/{year}/status")	
	public String method(@PathVariable(required=true) String year) throws Exception {		
		YearBook yb = mb.getYearBooks().stream().filter(y -> (y.getYear()==(Integer.parseInt(year)))).findFirst().get();	
		ExecutionStatus status = ce.execute(yb);				
		return status.name();
	}
	
	@RequestMapping("/years/{year}/errors")	
	public List<Map<String, List<String>>>  method2(@PathVariable(required=true) String year) throws Exception {		
		List<Map<String, List<String>>> result = new ArrayList<>();
		YearBook yb = mb.getYearBooks().stream().filter(y -> (y.getYear()==(Integer.parseInt(year)))).findFirst().get();	
		ce.execute(yb);	
		for (RuleGroup cp : ce.getPhases()) {
			List<String> l = new ArrayList<>();
			for (CheckException cpe : cp.getExceptions()) {
				l.add(cpe.getMessage());
			}
			Map<String, List<String>> m = new HashMap<>();
			m.put(cp.getType().name(), l);
			result.add(m);
		}
		
		return result;
	}

	public MasterBook getMb() {
		return mb;
	}

	public void setMb(MasterBook mb) {
		this.mb = mb;
	}
	
	public CheckingEngine getCe() {
		return ce;
	}

	public void setCe(CheckingEngine ce) {
		this.ce = ce;
	}
	
}

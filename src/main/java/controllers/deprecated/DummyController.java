package controllers.deprecated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import indexes.nets.YEAR;
import model.books.MasterBook;

@RestController
public class DummyController {

	Logger logger = LoggerFactory.getLogger(DummyController.class);
	
	@Autowired
	private MasterBook mb;
	
	@RequestMapping(method = RequestMethod.GET,value="/hi")
	public String method() {
//		logger.info("Message from meeeeee");
//		return"Ciao";
		System.out.println("In controller");
		return ""+YEAR.getIndexMap(mb.getYearBooks().get(0)).get("2018");
	}

	public MasterBook getMb() {
		return mb;
	}

	public void setMb(MasterBook mb) {
		this.mb = mb;
	}
	
	
	
}

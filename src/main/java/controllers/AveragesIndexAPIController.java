package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import controllers.exceptionhandler.ApiError;
import controllers.exceptionhandler.ApiException;
import dao.DAOException;
import dao.IndexQuery;
import dao.IndexesDAO;
import model2.Metric;

@RestController
@RequestMapping("/indexes/averages/")
public class AveragesIndexAPIController {

	Logger logger = LoggerFactory.getLogger(AveragesIndexAPIController.class);

	@Autowired
	private IndexesDAO indexDAO;

	private static final String _NONE_ = "none";
	private static final String _ASTERISK_ = "*";

	@RequestMapping(method = RequestMethod.GET)
	public List<Metric> method(@RequestParam(required = true) String firstYear,
			@RequestParam(required = true) String firstMonth, @RequestParam(required = true) String lastYear,
			@RequestParam(required = true) String lastMonth,
			@RequestParam(required = false, defaultValue = _NONE_) String sector,
			@RequestParam(required = false, defaultValue = _NONE_) String super_sector,
			@RequestParam(required = false, defaultValue = _NONE_) String is_common,
			@RequestParam(required = false, defaultValue = _NONE_) String sign, HttpServletRequest request)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		logger.info("BOOOO");

		IndexQuery query = makeQueryFromParams(sector, super_sector, is_common, sign);

		List<Metric> lm = new ArrayList<>();
		try {
			lm = indexDAO.findAverages(query, Integer.parseInt(firstYear), Integer.parseInt(firstMonth),
					Integer.parseInt(lastYear), Integer.parseInt(lastMonth));
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return lm;

	}

	private IndexQuery makeQueryFromParams(String sector, String super_sector, String is_common, String flow) {
		IndexQuery query = new IndexQuery();

		addParameter(Metric.KEY.sector_name, sector, query);
		addParameter(Metric.KEY.sector_father_name, super_sector, query);
		addParameter(Metric.KEY.is_common, is_common, query);
		addParameter(Metric.KEY.sign, flow, query);

		return query;
	}

	private void addParameter(Metric.KEY key, String value, IndexQuery query) {
		if (!value.equals(_NONE_)) {
			if (value.equals(_ASTERISK_)) {
				query.addFreeKey(key);
			} else {
				query.addFixedKey(key, value);
			}
		}
	}
}

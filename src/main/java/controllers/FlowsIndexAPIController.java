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
@RequestMapping("/indexes/flows/")
public class FlowsIndexAPIController {

	Logger logger = LoggerFactory.getLogger(FlowsIndexAPIController.class);

	@Autowired
	private IndexesDAO indexDAO;

	private static final String _NONE_ = "none";
	private static final String _ASTERISK_ = "*";

	@RequestMapping(method = RequestMethod.GET)
	public List<Metric> method(@RequestParam(required = false, defaultValue = _NONE_) String year,
			@RequestParam(required = false, defaultValue = _NONE_) String month,
			@RequestParam(required = false, defaultValue = _NONE_) String count,
			@RequestParam(required = false, defaultValue = _NONE_) String sector,
			@RequestParam(required = false, defaultValue = _NONE_) String super_sector,
			@RequestParam(required = false, defaultValue = _NONE_) String is_common,
			@RequestParam(required = false, defaultValue = _NONE_) String sign, HttpServletRequest request)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		IndexQuery query = makeQueryFromParams(year, month, count, sector, super_sector, is_common, sign);

		List<Metric> lm = new ArrayList<>();
		try {
			lm = indexDAO.findFlows(query);
		} catch (DAOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new ApiException(ApiError.parseFromDBError(e.getDBError()));
		}
		return lm;

	}

	private IndexQuery makeQueryFromParams(String year, String month, String count, String sector, String super_sector,
			String is_common, String flow) {
		IndexQuery query = new IndexQuery();

		addParameter(Metric.KEY.year, year, query);
		addParameter(Metric.KEY.month, month, query);
		addParameter(Metric.KEY.count_name, count, query);
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

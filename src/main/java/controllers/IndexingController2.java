package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import deprecated.model.books.MasterBook;
import deprecated.model.books.YearBook;

@RestController
@RequestMapping("/indexes2/{index}/")
public class IndexingController2 {

	Logger logger = LoggerFactory.getLogger(IndexingController2.class);

	@Autowired
	private MasterBook mb;

	@RequestMapping(method = RequestMethod.GET, value = { "/*", "/*/*", "/*/*/*", "/*/*/*/*", "/*/*/*/*/*",
			"/*/*/*/*/*/*", "/*/*/*/*/*/*/*" })
	public List<Map<String, String>> method(@PathVariable String index,
			@RequestParam(required = false, defaultValue = "none") String year,
			@RequestParam(required = false, defaultValue = "none") String month,
			@RequestParam(required = false, defaultValue = "none") String count,
			@RequestParam(required = false, defaultValue = "none") String category,
			@RequestParam(required = false, defaultValue = "none") String subcategory,
			@RequestParam(required = false, defaultValue = "none") String common,
			@RequestParam(required = false, defaultValue = "none") String flow, HttpServletRequest request)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		List<String> paramValues = new ArrayList<>(
				Arrays.asList(year, month, count, category, subcategory, common, flow));

		String indexClassName = getIndexName(paramValues, path, index);

		YearBook yb = mb.getYearBooks().stream().filter(y -> (y.getYear() == (Integer.parseInt(year)))).findFirst()
				.get();

		Class<?> klass = Class.forName(indexClassName);
		Method m = klass.getDeclaredMethod("getIndexMap", YearBook.class);
		@SuppressWarnings("unchecked")
		Map<String, Long> values = (Map<String, Long>) m.invoke(null, yb);

		Map<String, Long> filteredValues = new HashMap<>();
		for (String key : values.keySet()) {
			if (containsValuesNotNone(key, paramValues)) {
				filteredValues.put(key, values.get(key));
			}
		}

		List<Map<String, String>> response = new ArrayList<>();
		List<String> sortedKeys = new ArrayList<>(filteredValues.keySet());
		sortedKeys.sort(null);
		for (String key : sortedKeys) {
			Map<String, String> mss = new HashMap<>();
			mss.put(key, "" + filteredValues.get(key));
			response.add(mss);
			// response.add(key + " = " + (filteredValues.get(key)));
		}
		// response.sort(null);

		return response;
	}

	public MasterBook getMb() {
		return mb;
	}

	public void setMb(MasterBook mb) {
		this.mb = mb;
	}

	private String getIndexName(List<String> paramValues, String path, String index) {
		String indexClassName = "";

		List<String> pathItems = Arrays.asList(path.split("/"));
		if (pathItems.contains("years")) {
			indexClassName += "YEAR_";
		}
		if (pathItems.contains("months")) {
			indexClassName += "MONTH_";
		}
		if (pathItems.contains("counts")) {
			indexClassName += "COUNT_";
		}
		if (pathItems.contains("categories")) {
			indexClassName += "CATEGORY_";
		}
		if (pathItems.contains("subcategories")) {
			indexClassName += "SUBCATEGORY_";
		}
		if (pathItems.contains("commons")) {
			indexClassName += "COMMON_";
		}
		if (pathItems.contains("flows")) {
			indexClassName += "FLOW_";
		}
		if (pathItems.size() > 4) {
			indexClassName = indexClassName.substring(0, indexClassName.length() - 1);
		}

		String packageName = "indexes.";
		switch (index) {
		case "averages":
			packageName += "averages.";
			indexClassName = "AVERAGE_" + indexClassName;
			break;
		case "balances":
			packageName += "balances.";
			indexClassName = "BALANCE_" + indexClassName;
			break;
		case "nets":
			packageName += "nets.";
			break;
		default:
			break;
		}

		indexClassName = packageName + indexClassName;

		return indexClassName;
	}

	private boolean containsValuesNotNone(String indexToCheck, List<String> values) {
		for (String param : values) {
			if ((!param.equals("none")) && (!indexToCheck.contains(param))) {
				return false;
			}
		}
		return true;
	}

}

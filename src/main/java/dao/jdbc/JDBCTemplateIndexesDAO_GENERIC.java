package dao.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import dao.DAOException;
import dao.DBError;
import dao.DBError.DBErrorCode;
import dao.IndexQuery;
import dao.IndexesDAO;
import model2.Metric;

public class JDBCTemplateIndexesDAO_GENERIC extends JdbcDaoSupport implements IndexesDAO {

	protected static final String FLOWS_QUERY_WITH_FIXED_KEYS = "SELECT %s,SUM(amount) as amount FROM all_flows WHERE (%s) GROUP BY %s ORDER BY %s";
	protected static final String FLOWS_QUERY_WITHOUT_FIXED_KEYS = "SELECT %s,SUM(amount) as amount FROM all_flows GROUP BY %s ORDER BY %s";
	protected static final String FLOWS_QUERY_WITHOUT_KEYS = "SELECT SUM(amount) as amount FROM all_flows";

	protected static final String BALANCES_QUERY = "SELECT year,month,amount,count_name FROM count_balances_per_accounted_date WHERE %s";
	protected static final String BALANCES_FOR_ALL_COUNTS_QUERY = "SELECT year,month,SUM(amount) as amount FROM count_balances_per_accounted_date WHERE %s";

	protected static final String AVERAGES_QUERY_WITH_FIXED_KEYS = "SELECT %6$s,(sum(amount)/%5$s)as amount FROM all_flows_by_accounted_year_month "
			+ "WHERE %7$s and ((%1$s=%3$s) and year=%1$s and month>=%2$s and month<=%4$s) or ((%1$s<%3$s) and "
			+ "((year=%1$s and month>=%2$s) or (year=%3$s and month<=%4$s) or (year>%1$s and year <%3$s))) "
			+ "GROUP BY %6$s ORDER BY %6$s";
	protected static final String AVERAGES_QUERY_WITHOUT_FIXED_KEYS = "SELECT %6$s,(sum(amount)/%5$s)as amount FROM all_flows_by_accounted_year_month "
			+ "WHERE ((%1$s=%3$s) and year=%1$s and month>=%2$s and month<=%4$s) or ((%1$s<%3$s) and "
			+ "((year=%1$s and month>=%2$s) or (year=%3$s and month<=%4$s) or (year>%1$s and year <%3$s))) "
			+ "GROUP BY %6$s ORDER BY %6$s";
	protected static final String AVERAGES_QUERY_WITHOUT_KEYS = "SELECT (sum(amount)/%5$s) as amount FROM all_flows_by_accounted_year_month "
			+ "WHERE ((%1$s=%3$s) and year=%1$s and month>=%2$s and month<=%4$s) or" + "((%1$s<%3$s) and"
			+ "((year=%1$s and month>=%2$s) or" + "(year=%3$s and month<=%4$s) or" + "(year>%1$s and year <%3$s)))";

	public class MetricMapper implements RowMapper<Metric> {
		private Metric.TYPE metricType;

		public MetricMapper(Metric.TYPE metricType) {
			super();
			this.metricType = metricType;
		}

		@Override
		public Metric mapRow(ResultSet rs, int rowNum) throws SQLException {

			Metric m = new Metric();

			m.setType(this.metricType);
			m.setAmount(rs.getBigDecimal(Metric.KEY.amount.toString()));

			List<AbstractMap.SimpleEntry<Metric.KEY, String>> keys = new ArrayList<>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				Metric.KEY key = Metric.KEY.valueOf(rsmd.getColumnLabel(i));
				if (!key.equals(Metric.KEY.amount)) {
					keys.add(new AbstractMap.SimpleEntry<Metric.KEY, String>(key,
							readMetricKeyValueFromResultSet(rs, key)));
				}
			}
			m.setKeys(keys);

			return m;
		}

		private String readMetricKeyValueFromResultSet(ResultSet rs, Metric.KEY key) throws SQLException {
			switch (key) {
			case year:
			case month:
				return rs.getInt(key.toString()) + "";
			case count_name:
			case sector_name:
			case sector_father_name:
				return rs.getString(key.toString()) + "";
			case is_common:
				return rs.getBoolean(key.toString()) + "";
			case sign:
				return rs.getShort(key.toString()) + "";
			default:
				throw new DAOException(new DBError(DBErrorCode.ILLEGAL_STATUS,
						String.format("Key '%s' is not allowed for this ResultSet", key.toString())));
			}
		}
	}

	private String makeWhereClauseFromFixedKeys(IndexQuery query) {
		return query.getFixedKeys().stream().map(ams -> {
			return ams.getKey().toString() + "='" + ams.getValue() + "'";
		}).collect(Collectors.joining(" and "));
	}

	@Override
	public List<Metric> findAverages(IndexQuery query, int firstYear, int firstMonth, int lastYear, int lastMonth) {
		// Check if valid query for Averages
		if (query.isFixedKey(Metric.KEY.year) || query.isFreeKey(Metric.KEY.year)) {
			throw new DAOException(new DBError(DBErrorCode.ILLEGAL_QUERY,
					String.format("Fixed/Free key key 'year' is forbidden for Average metrics queries")));
		}

		int totalMonths = (lastYear - firstYear) * 12 + lastMonth - firstMonth + 1;

		String columns = Stream
				.concat(query.getFixedKeys().stream().map(ams -> ams.getKey()), query.getFreeKeys().stream()).sorted()
				.map(Metric.KEY::toString).collect(Collectors.joining(", "));

		String whereClause = makeWhereClauseFromFixedKeys(query);

		String queryTemplate = AVERAGES_QUERY_WITHOUT_KEYS;
		if (query.getFixedKeys().size() > 0) {
			queryTemplate = AVERAGES_QUERY_WITH_FIXED_KEYS;
		} else if (query.getFreeKeys().size() > 0) {
			queryTemplate = AVERAGES_QUERY_WITHOUT_FIXED_KEYS;
		}
		String sql = String.format(queryTemplate, firstYear, firstMonth, lastYear, lastMonth, totalMonths, columns,
				whereClause);

		System.out.println(sql);

		List<Metric> lds = this.getJdbcTemplate().query(sql, new MetricMapper(Metric.TYPE.average));

		return lds;
	}

	@Override
	public List<Metric> findBalances(IndexQuery query) {
		String whereClause = makeWhereClauseFromFixedKeys(query);

		String sql = String.format(BALANCES_FOR_ALL_COUNTS_QUERY, whereClause);
		if (query.isFixedKey(Metric.KEY.count_name) || query.isFreeKey(Metric.KEY.count_name))
			sql = String.format(BALANCES_QUERY, whereClause);

		System.out.println(sql);

		List<Metric> lds = this.getJdbcTemplate().query(sql, new MetricMapper(Metric.TYPE.balance));
		return lds;
	}

	@Override
	public List<Metric> findFlows(IndexQuery query) {
		String columns = Stream
				.concat(query.getFixedKeys().stream().map(ams -> ams.getKey()), query.getFreeKeys().stream()).sorted()
				.map(Metric.KEY::toString).collect(Collectors.joining(", "));

		String whereClauses = makeWhereClauseFromFixedKeys(query);

		String sql = String.format(FLOWS_QUERY_WITHOUT_KEYS);
		if (query.getFixedKeys().size() > 0) {
			sql = String.format(FLOWS_QUERY_WITH_FIXED_KEYS, columns, whereClauses, columns, columns);
		} else if (query.getFreeKeys().size() > 0) {
			sql = String.format(FLOWS_QUERY_WITHOUT_FIXED_KEYS, columns, columns, columns);
		}

		System.out.println(sql);

		List<Metric> lds = this.getJdbcTemplate().query(sql, new MetricMapper(Metric.TYPE.flow));

		return lds;
	}

}

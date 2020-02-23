package dao.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import dao.BalanceMetric;
import dao.FlowMetric;
import dao.IndexesDAO2;
import dao.MonthAverageFlowMetric;

public class JDBCTemplateIndexesDAO extends JdbcDaoSupport implements IndexesDAO2 {

	protected static final String SELECT_BALANCES_TEMPLATE_SQL = "SELECT * FROM count_balances_per_accounted_date where year=%s order by year, month";
	protected static final String SELECT_FLOWS_TEMPLATE_SQL = "SELECT * FROM all_flows_by_accounted_year_month where year=%s and ((amount>0.001) or (amount<-0.001)) order by year, month";
	protected static final String SELECT_AVERAGE_FLOWS_TEMPLATE_SQL = "SELECT * FROM (SELECT year,sector_name,sector_father_name,is_common,sign,(sum(amount)/count(distinct(month))) as amount FROM all_flows_by_accounted_year_month WHERE year=%s GROUP BY year,sector_name,sector_father_name,is_common,sign ORDER BY year,sector_name) as A WHERE ((A.amount>0.001) or (A.amount<-0.001))";

	// "SELECT
	// year,sector_name,sector_father_name,is_common,sign,(sum(amount)/count(distinct(month)))
	// as amount FROM all_flows_by_accounted_year_month WHERE year=%s GROUP BY
	// year,sector_name,sector_father_name,is_common,sign ORDER BY
	// year,sector_name";

	@Override
	public List<MonthAverageFlowMetric> findAverages(int year) {
		List<MonthAverageFlowMetric> results = new ArrayList<MonthAverageFlowMetric>();

		results = this.getJdbcTemplate().query(String.format(SELECT_AVERAGE_FLOWS_TEMPLATE_SQL, year),
				BeanPropertyRowMapper.newInstance(MonthAverageFlowMetric.class));

		return results;
	}

	@Override
	public List<BalanceMetric> findBalances(int year) {
		List<BalanceMetric> results = new ArrayList<BalanceMetric>();

		results = this.getJdbcTemplate().query(String.format(SELECT_BALANCES_TEMPLATE_SQL, year),
				BeanPropertyRowMapper.newInstance(BalanceMetric.class));

		return results;
	}

	@Override
	public List<FlowMetric> findFlows(int year) {
		List<FlowMetric> results = new ArrayList<FlowMetric>();

		System.out.println(String.format(SELECT_FLOWS_TEMPLATE_SQL, year));
		results = this.getJdbcTemplate().query(String.format(SELECT_FLOWS_TEMPLATE_SQL, year),
				BeanPropertyRowMapper.newInstance(FlowMetric.class));

		return results;
	}

}

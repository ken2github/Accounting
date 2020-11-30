package dao.jdbc;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import dao.CountsBalancesByYearMonthDAO;
import model2.CountYearMonthBalance;

public class JDBCTemplateCountsBalancesByYearMonth extends JdbcDaoSupport implements CountsBalancesByYearMonthDAO {

	protected static final String SELECT_BY_YEAR_SQL = "SELECT * FROM count_balances_per_accounted_date WHERE year=?";
	protected static final String SELECT_BY_COUNT_YEAR_SQL = "SELECT * FROM count_balances_per_accounted_date WHERE count_name=? and year=?";

	protected static final String SECTOR_DOES_NOT_EXIST_ERRMSG = "The '%s' sector does not exist in DB.";
	protected static final String COUNT_DOES_NOT_EXIST_ERRMSG = "The '%s' count does not exist in DB.";

	@Override
	public List<CountYearMonthBalance> findByYear(int year) {
		List<CountYearMonthBalance> lds = this.getJdbcTemplate().query(SELECT_BY_YEAR_SQL,
				BeanPropertyRowMapper.newInstance(CountYearMonthBalance.class), year);
		return lds;
	}

	@Override
	public List<CountYearMonthBalance> findByCountYear(String countName, int year) {
		List<CountYearMonthBalance> lds = this.getJdbcTemplate().query(SELECT_BY_COUNT_YEAR_SQL,
				BeanPropertyRowMapper.newInstance(CountYearMonthBalance.class), countName, year);
		return lds;
	}

}

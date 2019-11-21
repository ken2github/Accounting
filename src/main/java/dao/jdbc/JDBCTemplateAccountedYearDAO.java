package dao.jdbc;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import dao.AccountedYearDAO;
import dao.DAONotFoundException;
import dao.DBError;
import dao.DBError.DBErrorCode;

public class JDBCTemplateAccountedYearDAO extends JdbcDaoSupport implements AccountedYearDAO {

	protected static final String INSERT_SQL = "INSERT INTO accounted_years (year) VALUES (?)";
	protected static final String SELECT_ALL_SQL = "SELECT	* FROM accounted_years";
	protected static final String DELETE_BY_ID = "DELETE FROM accounted_years where year=?";

	@Override
	public void insert(int year) {
		this.getJdbcTemplate().update(INSERT_SQL, year);
	}

	@Override
	public List<Integer> findAll() {
		List<Integer> lds = this.getJdbcTemplate().queryForList(SELECT_ALL_SQL, Integer.class);
		return lds;
	}

	@Override
	public void delete(int year) {
		try {
			this.getJdbcTemplate().update(DELETE_BY_ID, year);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(
					new DBError(DBErrorCode.RESOURCE_NOT_DELETED, String.format("Year '%s' was not deleted", year)));
		}
	}

}

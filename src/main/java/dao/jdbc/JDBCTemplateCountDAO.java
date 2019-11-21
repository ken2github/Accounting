package dao.jdbc;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import dao.CountDAO;
import dao.DAONotFoundException;
import dao.DBError;
import dao.DBError.DBErrorCode;
import model2.Count;
import model2.DetailedCount;

public class JDBCTemplateCountDAO extends JdbcDaoSupport implements CountDAO {

	protected static final String INSERT_SQL = "INSERT INTO counts (id,name,description,open_date,close_date) VALUES (?,?,?,?,?)";
	protected static final String UPDATE_SQL = "UPDATE counts SET name = ?, description = ?, open_date = ?, close_date = ? WHERE id = ?";
	protected static final String SELECT_BY_ID_SQL = "SELECT * FROM counts WHERE id = ?";
	protected static final String SELECT_BY_NAME_SQL = "SELECT * FROM counts WHERE name = ?";
	protected static final String SELECT_ALL_SQL = "SELECT	* FROM counts";

	protected static final String DELETE_BY_ID = "DELETE FROM counts where id=?";
	protected static final String DELETE_BY_NAME = "DELETE FROM counts where name=?";

	@Override
	public DetailedCount insert(Count s) {
		String id = UUID.randomUUID().toString();

		this.getJdbcTemplate().update(INSERT_SQL, id, s.getName(), s.getDescription(), s.getOpenDate(),
				s.getCloseDate());

		return findById(id);
	}

	@Override
	public DetailedCount update(DetailedCount s) {
		DetailedCount os = findById(s.getId());

		if (os == null) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Count id '%s' name '%s' does not exist", s.getId(), s.getName())));
		}

		this.getJdbcTemplate().update(UPDATE_SQL, s.getName(), s.getDescription(), s.getOpenDate(), s.getCloseDate(),
				s.getId());

		return findById(s.getId());
	}

	@Override
	public DetailedCount findById(String id) {

		DetailedCount ds;

		try {
			ds = this.getJdbcTemplate().queryForObject(SELECT_BY_ID_SQL,
					BeanPropertyRowMapper.newInstance(DetailedCount.class), id);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(
					new DBError(DBErrorCode.RESOURCE_NOT_FOUND, String.format("Count id '%s' does not exist", id)));
		}

		return ds;
	}

	@Override
	public DetailedCount findByName(String countName) {
		DetailedCount ds;
		try {
			ds = this.getJdbcTemplate().queryForObject(SELECT_BY_NAME_SQL,
					BeanPropertyRowMapper.newInstance(DetailedCount.class), countName);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Sector name '%s' does not exist", countName)));
		}

		return ds;
	}

	@Override
	public List<DetailedCount> findAll() {
		List<DetailedCount> lds = this.getJdbcTemplate().query(SELECT_ALL_SQL,
				BeanPropertyRowMapper.newInstance(DetailedCount.class));
		return lds;
	}

	@Override
	public boolean deleteById(String id) {
		try {
			this.getJdbcTemplate().update(DELETE_BY_ID, id);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(
					new DBError(DBErrorCode.RESOURCE_NOT_DELETED, String.format("Count id '%s' was not deleted", id)));
		}
		return true;
	}

	@Override
	public boolean deleteByName(String name) {
		try {
			this.getJdbcTemplate().update(DELETE_BY_NAME, name);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_DELETED,
					String.format("Count name '%s' was not deleted", name)));
		}
		return true;
	}

}

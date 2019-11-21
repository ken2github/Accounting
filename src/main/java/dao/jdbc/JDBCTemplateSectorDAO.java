package dao.jdbc;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import dao.DAOException;
import dao.DAONotFoundException;
import dao.DBError;
import dao.DBError.DBErrorCode;
import dao.SectorDAO;
import model2.DetailedSector;
import model2.Sector;

public class JDBCTemplateSectorDAO extends JdbcDaoSupport implements SectorDAO {

	protected static final String INSERT_SQL = "INSERT INTO sectors (id,name,description,father_id,activation_date,deactivation_date) VALUES (?,?,?,?,?,?)";
	protected static final String UPDATE_SQL = "UPDATE sectors SET name = ?, description = ?, activation_date = ?, deactivation_date = ? WHERE id = ?";
	protected static final String SELECT_BY_ID_SQL = "SELECT * FROM detailed_sectors WHERE id = ?";
	protected static final String SELECT_BY_NAME_SQL = "SELECT * FROM detailed_sectors WHERE name = ?";
	protected static final String SELECT_ALL_SQL = "SELECT	* FROM detailed_sectors";

	protected static final String DELETE_BY_ID = "DELETE FROM sectors where id=?";
	protected static final String DELETE_BY_NAME = "DELETE FROM sectors where name=?";

	protected static final String FATHER_DOES_NOT_EXIST_ERRMSG = "The '%s' father  does not exist in DB.";

	protected static final String ILLEGAL_UPDATE_NAME_ERRMSG = "The actual sector name '%s' cannot be updated with '%s' value.";

	@Override
	public DetailedSector insert(Sector s) {
		String id = UUID.randomUUID().toString();
		String fatherId = null;
		if (!s.getName().equals(s.getFatherName())) {
			fatherId = getActualFatherIdOrThrowException(s);
		}

		this.getJdbcTemplate().update(INSERT_SQL, id, s.getName(), s.getDescription(), fatherId, s.getActivationDate(),
				s.getDeactivationDate());

		return findById(id);
	}

	private String getActualFatherIdOrThrowException(Sector s) {
		String fatherId = null;
		if (s.getFatherName() != null) {
			DetailedSector father = findByName(s.getFatherName());
			if (father == null) {
				throw new RuntimeException(String.format(FATHER_DOES_NOT_EXIST_ERRMSG, s.getFatherName()));
			}
			fatherId = father.getId();
		}
		return fatherId;
	}

	@Override
	public DetailedSector update(DetailedSector s) {
		DetailedSector os = findById(s.getId());

		if (os == null) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Sector id '%s' name '%s' does not exist", s.getId(), s.getName())));
		}

		if (!s.getName().equals(os.getName())) {
			if ((!os.getName().contains(".")) || (!s.getName().startsWith(os.getFatherName() + "."))) {
				throw new DAOException(new DBError(DBErrorCode.ILLEGAL_UPDATE,
						String.format(ILLEGAL_UPDATE_NAME_ERRMSG, os.getName(), s.getName())));
			}

		}

		this.getJdbcTemplate().update(UPDATE_SQL, s.getName(), s.getDescription(), s.getActivationDate(),
				s.getDeactivationDate(), s.getId());

		return findById(s.getId());
	}

	@Override
	public DetailedSector findById(String id) {

		DetailedSector ds;

		try {
			ds = this.getJdbcTemplate().queryForObject(SELECT_BY_ID_SQL,
					BeanPropertyRowMapper.newInstance(DetailedSector.class), id);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(
					new DBError(DBErrorCode.RESOURCE_NOT_FOUND, String.format("Sector id '%s' does not exist", id)));
		}

		return ds;
	}

	@Override
	public DetailedSector findByName(String sectorName) {
		DetailedSector ds;
		try {
			ds = this.getJdbcTemplate().queryForObject(SELECT_BY_NAME_SQL,
					BeanPropertyRowMapper.newInstance(DetailedSector.class), sectorName);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Sector name '%s' does not exist", sectorName)));
		}

		return ds;
	}

	@Override
	public List<DetailedSector> findAll() {
		List<DetailedSector> lds = this.getJdbcTemplate().query(SELECT_ALL_SQL,
				BeanPropertyRowMapper.newInstance(DetailedSector.class));
		return lds;
	}

	@Override
	public boolean deleteById(String id) {
		try {
			this.getJdbcTemplate().update(DELETE_BY_ID, id);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(
					new DBError(DBErrorCode.RESOURCE_NOT_DELETED, String.format("Sector id '%s' was not deleted", id)));
		}
		return true;
	}

	@Override
	public boolean deleteByName(String name) {
		try {
			this.getJdbcTemplate().update(DELETE_BY_NAME, name);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_DELETED,
					String.format("Sector name '%s' was not deleted", name)));
		}
		return true;
	}

}

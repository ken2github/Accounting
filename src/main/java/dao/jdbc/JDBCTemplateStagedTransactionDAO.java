package dao.jdbc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import dao.CountDAO;
import dao.DAONotFoundException;
import dao.DBError;
import dao.DBError.DBErrorCode;
import dao.Observable;
import dao.Observer;
import dao.Observer.Change;
import dao.Observer.ChangeType;
import dao.SectorDAO;
import dao.StagedTransactionDAO;
import model2.DetailedCount;
import model2.DetailedSector;
import model2.DetailedTransaction;
import model2.Transaction;

public class JDBCTemplateStagedTransactionDAO extends JdbcDaoSupport
		implements StagedTransactionDAO, Observable<DetailedTransaction> {

	protected static final String INSERT_SQL = "INSERT INTO staged_transactions (id,amount,title,sector_id,is_common,count_id,date) VALUES (?,?,?,?,?,?,?)";
	protected static final String UPDATE_SQL = "UPDATE staged_transactions SET amount = ?, title = ?, sector_id = ?, is_common = ?, count_id = ?, date = ? WHERE id = ?";
	protected static final String SELECT_ALL_SQL = "SELECT * FROM detailed_staged_transactions";
	protected static final String SELECT_BY_ID_SQL = "SELECT * FROM detailed_staged_transactions WHERE id = ?";
	protected static final String SELECT_BY_YEAR_MONTH_SQL = "SELECT * FROM detailed_staged_transactions WHERE YEAR(date)=? and MONTH(date)=?";

	protected static final String DELETE_BY_ID = "DELETE FROM staged_transactions where id=?";

	protected static final String SECTOR_DOES_NOT_EXIST_ERRMSG = "The '%s' sector does not exist in DB.";
	protected static final String COUNT_DOES_NOT_EXIST_ERRMSG = "The '%s' count does not exist in DB.";

	@Autowired
	private SectorDAO sectorDAO;

	@Autowired
	private CountDAO countDAO;

	private SimpleObservableImpl<DetailedTransaction> observableImpl = new SimpleObservableImpl<>();

	@Override
	public DetailedTransaction insert(Transaction s) {
		String id = UUID.randomUUID().toString();
		String countId = getActualCountIdOrThrowException(s);
		String sectorId = getActualSectorIdOrThrowException(s);

		this.getJdbcTemplate().update(INSERT_SQL, id, s.getAmount(), s.getTitle(), sectorId, s.isCommon(), countId,
				s.getDate());

		DetailedTransaction dt = findById(id);

		observableImpl.getObservers().stream().forEach(
				observer -> observer.update(Arrays.asList(new Change<DetailedTransaction>(dt, ChangeType.mod))));

		return dt;
	}

	private String getActualSectorIdOrThrowException(Transaction s) {
		String sectorId = null;
		if (s.getSectorName() != null) {
			DetailedSector sector = sectorDAO.findByName(s.getSectorName());
			if (sector == null) {
				throw new RuntimeException(String.format(SECTOR_DOES_NOT_EXIST_ERRMSG, s.getSectorName()));
			}
			sectorId = sector.getId();
		}
		return sectorId;
	}

	private String getActualCountIdOrThrowException(Transaction s) {
		String countId = null;
		if (s.getCountName() != null) {
			DetailedCount count = countDAO.findByName(s.getCountName());
			if (count == null) {
				throw new RuntimeException(String.format(COUNT_DOES_NOT_EXIST_ERRMSG, s.getCountName()));
			}
			countId = count.getId();
		}
		return countId;
	}

	@Override
	public DetailedTransaction update(DetailedTransaction t) {
		DetailedTransaction ot = findById(t.getId());

		String countName = t.getCountName();
		String countId = null;
		if (countName != null) {
			DetailedCount c = countDAO.findByName(t.getCountName());
			countId = c.getId();
		}

		String sectorName = t.getSectorName();
		String sectorId = null;
		if (sectorName != null) {
			DetailedSector s = sectorDAO.findByName(t.getSectorName());
			sectorId = s.getId();
		}
		if (ot == null) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Staged Transaction id '%s' does not exist", t.getId())));
		}

		this.getJdbcTemplate().update(UPDATE_SQL, t.getAmount(), t.getTitle(), sectorId, t.isCommon(), countId,
				t.getDate(), t.getId());

		DetailedTransaction ut = findById(t.getId());

		observableImpl.getObservers().stream().forEach(
				observer -> observer.update(Arrays.asList(new Change<DetailedTransaction>(ut, ChangeType.mod))));

		return ut;
	}

	@Override
	public DetailedTransaction findById(String id) {

		DetailedTransaction ds;

		try {
			ds = this.getJdbcTemplate().queryForObject(SELECT_BY_ID_SQL,
					BeanPropertyRowMapper.newInstance(DetailedTransaction.class), id);
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Staged Transaction id '%s' does not exist", id)));
		}

		return ds;
	}

	@Override
	public boolean deleteById(String id) {
		try {
			this.getJdbcTemplate().update(DELETE_BY_ID, id);
			observableImpl.getObservers().stream().forEach(observer -> observer.update(Arrays
					.asList(new Change<DetailedTransaction>(new DetailedTransaction().setId(id), ChangeType.rem))));
		} catch (EmptyResultDataAccessException e) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_DELETED,
					String.format("Staged Transaction id '%s' was not deleted", id)));
		}
		return true;
	}

	@Override
	public List<DetailedTransaction> findByYearMonth(int year, int month) {
		List<DetailedTransaction> lds = this.getJdbcTemplate().query(SELECT_BY_YEAR_MONTH_SQL,
				BeanPropertyRowMapper.newInstance(DetailedTransaction.class), year, month);
		return lds;
	}

	@Override
	public List<DetailedTransaction> findAll() {
		List<DetailedTransaction> lds = this.getJdbcTemplate().query(SELECT_ALL_SQL,
				BeanPropertyRowMapper.newInstance(DetailedTransaction.class));

		return lds;
	}

	@Override
	public void subscribe(Observer<DetailedTransaction> observer) {
		observableImpl.subscribe(observer);
	}

	@Override
	public void unsubscribe(Observer<DetailedTransaction> observer) {
		observableImpl.unsubscribe(observer);
	}
}

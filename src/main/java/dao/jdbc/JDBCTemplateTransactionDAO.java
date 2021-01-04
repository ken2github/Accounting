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
import dao.TransactionDAO;
import model2.DetailedCount;
import model2.DetailedSector;
import model2.DetailedTransaction;
import model2.Transaction;

public class JDBCTemplateTransactionDAO extends JdbcDaoSupport
		implements TransactionDAO, Observable<DetailedTransaction> {

	protected static final String INSERT_SQL = "INSERT INTO transactions (id,amount,title,sector_id,is_common,count_id,date) VALUES (?,?,?,?,?,?,?)";
	protected static final String UPDATE_SQL = "UPDATE transactions SET amount = ?, title = ?, sector_id = ?, is_common = ?, count_id = ?, date = ? WHERE id = ?";
	protected static final String SELECT_BY_ID_SQL = "SELECT * FROM all_transactions WHERE id = ? and is_automatic = false";
	protected static final String SELECT_BY_YEAR_MONTH_SQL = "SELECT * FROM all_transactions WHERE YEAR(date)=? and MONTH(date)=?";
	protected static final String SELECT_BY_YEAR_MONTH_SECTOR_SQL = "SELECT * FROM all_transactions WHERE YEAR(date)=? and MONTH(date)=? and sector_name=?";

	protected static final String DELETE_BY_ID = "DELETE FROM transactions where id=?";

	protected static final String SECTOR_DOES_NOT_EXIST_ERRMSG = "The '%s' sector does not exist in DB.";
	protected static final String COUNT_DOES_NOT_EXIST_ERRMSG = "The '%s' count does not exist in DB.";

	private SimpleObservableImpl<DetailedTransaction> observableImpl = new SimpleObservableImpl<>();

	@Autowired
	private SectorDAO sectorDAO;

	@Autowired
	private CountDAO countDAO;

	@Override
	public DetailedTransaction insert(Transaction s) {
		String id = UUID.randomUUID().toString();
		String countId = getActualCountIdOrThrowException(s);
		String sectorId = getActualSectorIdOrThrowException(s);

		this.getJdbcTemplate().update(INSERT_SQL, id, s.getAmount(), s.getTitle(), sectorId, s.isCommon(), countId,
				s.getDate());

		DetailedTransaction dt = findById(id);

		observableImpl.getObservers().stream().forEach(
				observer -> observer.update(Arrays.asList(new Change<DetailedTransaction>(dt, ChangeType.add))));

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

		DetailedCount c = countDAO.findByName(t.getCountName());
		DetailedSector s = sectorDAO.findByName(t.getSectorName());

		if (ot == null) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Transaction id '%s' does not exist", t.getId())));
		}

		if (c == null) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Count name '%s' does not exist", t.getCountName())));
		}

		if (s == null) {
			throw new DAONotFoundException(new DBError(DBErrorCode.RESOURCE_NOT_FOUND,
					String.format("Sector name '%s' does not exist", t.getSectorName())));
		}

		this.getJdbcTemplate().update(UPDATE_SQL, t.getAmount(), t.getTitle(), s.getId(), t.isCommon(), c.getId(),
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
					String.format("Transaction id '%s' does not exist", id)));
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
					String.format("Transaction id '%s' was not deleted", id)));
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
	public List<DetailedTransaction> findByYearMonthSector(int year, int month, String sector) {
		List<DetailedTransaction> lds = this.getJdbcTemplate().query(SELECT_BY_YEAR_MONTH_SECTOR_SQL,
				BeanPropertyRowMapper.newInstance(DetailedTransaction.class), year, month, sector);
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

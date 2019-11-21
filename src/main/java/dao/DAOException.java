package dao;

public class DAOException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7408212624889033916L;
	private DBError dbError;

	public DAOException(DBError dbError) {
		super();
		this.dbError = dbError;
	}

	public DBError getDBError() {
		return dbError;
	}
}

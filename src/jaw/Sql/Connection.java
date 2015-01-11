package jaw.sql;

import jaw.core.Config;

import java.sql.*;

/**
 * MySqlHelper
 */
public class Connection {

	static {
		try {
			Class.forName(Config.DBMS_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor without database's password
	 * @param host Database's host with database name
	 * @param user User database's name
	 * @throws Exception
	 */
    public Connection(String host, String user) throws Exception {
        this(host, user, "");
    }

	/**
	 * Construct with default host, user and password (from config)
	 * @throws Exception
	 */
	public Connection() throws Exception {
		this(Config.DBMS_HOST, Config.DBMS_USER, Config.DBMS_PASSWORD);
	}

	/**
	 * Constructor with database's password
	 * @param host Database's host with database name
	 * @param user User database's name
	 * @param password User database's password
	 * @throws Exception
	 */
    public Connection(String host, String user, String password) throws Exception {
		try {
			sqlConnection = DriverManager.getConnection(host + "?user=" + user + "&password=" + password);
		} catch (SQLException e) {
			throw new Exception(
				"Connection() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Prepare sql's statement
	 * @param sql - Sql query
	 * @return - Prepared statement
	 * @throws Exception
	 */
	public PreparedStatement createStatementForSelect(String sql) throws Exception {
		try {
			return sqlConnection.prepareStatement(sql,
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY
			);
		} catch (SQLException e) {
			throw new Exception(
				"Connection/createStatementForSelect() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Prepare sql's statement for insert
	 * @param sql - Sql query
	 * @return - Prepared statement
	 * @throws Exception
	 */
	public PreparedStatement createStatementForInsert(String sql) throws Exception {
		try {
			return sqlConnection.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS
			);
		} catch (SQLException e) {
			throw new Exception(
				"Connection/createStatementForInsert() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * @return - Current Postgres's SqlConnection
	 */
    public java.sql.Connection getSqlConnection() {
        return sqlConnection;
    }

	/**
	 * @return - New command
	 */
	public Command createCommand() {
		return new Command(this);
	}

	/**
	 * Check, is connection with database closed
	 * @return - True if connection has been closed
	 * @throws Exception
	 */
	public boolean isClosed() throws Exception {
		try {
			return sqlConnection.isClosed();
		} catch (SQLException e) {
			throw new Exception(
				"Connection/isClosed() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Current database's sqlConnection
	 */
    private java.sql.Connection sqlConnection;
}

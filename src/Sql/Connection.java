package Sql;

import Core.*;
import Core.InternalError;
import org.omg.CORBA.INTERNAL;

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
	 * Constructor without database's
	 * password
	 *
	 * @param host Database's host with database name
	 * @param user User database's name
	 * @throws Exception
	 */
    public Connection(String host, String user) throws Exception {
        this(host, user, "");
    }

	/**
	 * @throws Exception
	 */
	public Connection() throws InternalError {
		this(Config.DBMS_HOST, Config.DBMS_USER, Config.DBMS_PASSWORD);
	}

	/**
	 * Constructor with database's
	 * password
	 *
	 * @param host Database's host with database name
	 * @param user User database's name
	 * @param password User database's password
	 * @throws Core.InternalError
	 */
    public Connection(String host, String user, String password) throws InternalError {
		try {
			sqlConnection = DriverManager.getConnection(host + "?user=" + user + "&password=" + password);
		} catch (SQLException e) {
			throw new InternalError(
				"Connection() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Create new statement for queries
	 * @return Just created statement
	 * @throws Core.InternalError
	 */
    public Statement statement() throws InternalError {
		try {
			return sqlConnection.createStatement();
		} catch (SQLException e) {
			throw new InternalError(
				"Connection/statement() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Prepare sql's statement
	 * @param sql - Sql query
	 * @return - Prepared statement
	 * @throws Core.InternalError
	 */
	public PreparedStatement prepare(String sql) throws Core.InternalError {
		try {
			return sqlConnection.prepareStatement(sql,
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY
			);
		} catch (SQLException e) {
			throw new InternalError(
				"Connection/prepare() : \"" + e.getMessage() + "\""
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
	 * @throws InternalError
	 */
	public boolean isClosed() throws InternalError {
		try {
			return sqlConnection.isClosed();
		} catch (SQLException e) {
			throw new InternalError(
				"Connection/isClosed() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Current database's sqlConnection
	 */
    private java.sql.Connection sqlConnection;
}

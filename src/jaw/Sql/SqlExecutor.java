package jaw.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-11-22
 */
public class SqlExecutor {

	/**
	 * Construct executor with command
	 * @param command - Command to execute
	 */
	public SqlExecutor(Command command, Object[] objects) throws Exception {
		this.command = command;
		this.objects = objects;
	}

	/**
	 * Create new prepared statement with bind parameters
	 * @return - Prepared statement
	 * @throws Exception
	 */
	public PreparedStatement bind(PreparedStatement statement) throws Exception {
		return new SqlTypeBinder(statement).bind(objects).getStatement();
	}

	/**
	 * Execute selection
	 * @return - Set with all results
	 * @throws Exception
	 */
	public ResultSet select() throws Exception {
		try {
			return bind(getCommand().getConnection().createStatementForSelect(
				getCommand().getQuery()
			)).executeQuery();
		} catch (SQLException e) {
			throw new Exception(
				"SqlExecutor/select() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Execute insert
	 * @return - Boolean statement
	 * @throws Exception
	 */
	public boolean insert() throws Exception {
		try {
			return bind(getCommand().getConnection().createStatementForSelect(
					getCommand().getQuery()
			)).execute();
		} catch (SQLException e) {
			throw new Exception(
				"SqlExecutor/insert() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Execute update
	 * @return - Count of updated rows
	 * @throws Exception
	 */
	public int update() throws Exception {
		try {
			return bind(getCommand().getConnection().createStatementForSelect(
					getCommand().getQuery()
			)).executeUpdate();
		} catch (SQLException e) {
			throw new Exception(
					"SqlExecutor/insert() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Execute update
	 * @return - Count of updated rows
	 * @throws Exception
	 */
	public int delete() throws Exception {
		try {
			return bind(getCommand().getConnection().createStatementForSelect(
					getCommand().getQuery()
			)).executeUpdate();
		} catch (SQLException e) {
			throw new Exception(
				"SqlExecutor/insert() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * @return - Executor's command
	 */
	public Command getCommand() {
		return command;
	}

	private Command command;
	private Object[] objects;
}

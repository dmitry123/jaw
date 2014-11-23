package Sql;

import Core.*;
import Core.InternalError;

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
	public SqlExecutor(Command command) {
		this.command = command;
	}

	/**
	 * Execute selection
	 * @return - Set with all results
	 * @throws Core.InternalError
	 */
	public ResultSet select() throws Core.InternalError {
		try {
			return command.getStatement().executeQuery();
		} catch (SQLException e) {
			throw new InternalError(
				"SqlExecutor/select() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Execute insert
	 * @return - Boolean statement
	 * @throws Core.InternalError
	 */
	public boolean insert() throws Core.InternalError {
		try {
			return command.getStatement().execute();
		} catch (SQLException e) {
			throw new InternalError(
				"SqlExecutor/insert() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Execute update
	 * @return - Count of updated rows
	 * @throws Core.InternalError
	 */
	public int update() throws Core.InternalError {
		try {
			return command.getStatement().executeUpdate();
		} catch (SQLException e) {
			throw new InternalError(
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
}

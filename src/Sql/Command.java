package Sql;

import Core.InternalError;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-11-22
 */
public class Command implements CommandProtocol {

	/**
	 * Build command with SQL connection
	 *
	 * @param connection - Sql's connection
	 */
	public Command(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Apply selection from table
	 * @param items - Items to select from table(s)
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol select(String items) throws Core.InternalError {
		return _word("SELECT")._word(items);
	}

	/**
	 * Apply distinct selection from table
	 * @param items - Items to select from table(s)
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol distinct(String items) throws InternalError {
		return _word("SELECT DISTINCT")._word(items);
	}

	/**
	 * From which table we should select
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol from(String table) throws InternalError {
		return _word("FROM")._word(table);
	}

	/**
	 * Update table's rows
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol update(String table) throws InternalError {
		return _word("UPDATE")._word(table);
	}

	/**
	 * From which table we should select
	 * @param table - Table's name
	 * @param as - Table's macros
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol from(String table, String as) throws InternalError {
		return from(table).as(as);
	}

	/**
	 * Update rows in table
	 * @param expression - Update expression
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol set(String expression) throws InternalError {
		return _word("SET")._word(expression);
	}

	/**
	 * Declare table's macros
	 * @param as - Table's macros
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol as(String as) throws InternalError {
		return _word("AS")._word(as);
	}

	/**
	 * Insert where statement to query
	 * @param statement - Where statement
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol where(String statement) throws InternalError {
		return _where(statement);
	}

	/**
	 * Add and statement to where condition
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol and(String condition) throws InternalError {
		return _where(condition, "AND");
	}

	/**
	 * Add or statement to where condition
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol or(String condition) throws InternalError {
		return _where(condition, "OR");
	}

	/**
	 * Join some table with on condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol join(String table, String on) throws InternalError {
		return _word("JOIN")._word(table)._word("ON")._word(on);
	}

	/**
	 * Join some table with on condition
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol join(String table, String as, String on) throws InternalError {
		return ((Command) _word("JOIN")._word(table).as(as))._word("ON")._word(on);
	}

	/**
	 * Left join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol left(String table, String on) throws InternalError {
		return _word("LEFT").join(table, on);
	}

	/**
	 * Left join some table with condition
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol left(String table, String as, String on) throws InternalError {
		return _word("LEFT").join(table, as, on);
	}

	/**
	 * Right join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol right(String table, String on) throws InternalError {
		return _word("RIGHT").join(table, on);
	}

	/**
	 * Right join some table with condition
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol right(String table, String as, String on) throws InternalError {
		return _word("RIGHT").join(table, as, on);
	}

	/**
	 * Inner join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol inner(String table, String on) throws InternalError {
		return _word("INNER").join(table, on);
	}

	/**
	 * Inner join some table with condition
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol inner(String table, String as, String on) throws InternalError {
		return _word("INNER").join(table, as, on);
	}

	/**
	 * Outer join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol outer(String table, String on) throws InternalError {
		return _word("OUTER").join(table, on);
	}

	/**
	 * Outer join some table with condition
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol outer(String table, String as, String on) throws InternalError {
		return _word("OUTER").join(table, as, on);
	}

	/**
	 * Cross join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol cross(String table, String on) throws InternalError {
		return _word("CROSS").join(table, on);
	}

	/**
	 * Cross join some table with condition
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol cross(String table, String as, String on) throws InternalError {
		return _word("CROSS").join(table, as, on);
	}

	/**
	 * Order result with some condition
	 * @param condition - Order condition
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol order(String condition) throws InternalError {
		return _word("ORDER")._word("BY")._word(condition);
	}

	/**
	 * Insert sub-command in query
	 * @param command - Sub-command
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	@Override
	public CommandProtocol command(CommandProtocol command) throws InternalError {
		return _word("(" + ((Command) command)._build() + ")");
	}

	/**
	 * Execute query and replace ? with it's object
	 * @param objects - List with objects
	 * @return - Sql executor
	 * @throws Core.InternalError
	 */
	@Override
	public SqlExecutor execute(Object[] objects) throws InternalError {
		if (getStatement() == null) {
			bind(objects);
		}
		return execute();
	}

	/**
	 * Prepare executor
	 * @return - Sql executor
	 * @throws Core.InternalError
	 */
	public SqlExecutor execute() throws InternalError {
		if (getStatement() == null) {
			bind(new Object[] {
				/* Ignore */
			});
		}
		return new SqlExecutor(this);
	}

	/**
	 * Create new prepared statement with bind parameters
	 * @param objects - Query's parameters
	 * @return - Prepared statement
	 * @throws InternalError
	 */
	public PreparedStatement bind(Object[] objects) throws InternalError {

		PreparedStatement preparedStatement = connection
				.prepare(_build().trim());

		SqlTypeBinder sqlTypeBinder = new SqlTypeBinder(
				preparedStatement
		);

		statement = sqlTypeBinder.bind(objects)
			.getStatement();

		return statement;
	}

	/**
	 * @return - Command connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @return - Command statement
	 */
	public PreparedStatement getStatement() {
		return statement;
	}

	private String _build() {
		return sqlQuery + sqlWhere;
	}

	private Command _where(String string, String clause) {
		if (sqlWhere.length() < 1) {
			return _where(string);
		}
		return _wordWhere(clause)._wordWhere(string);
	}

	private Command _where(String string) {
		if (sqlWhere.length() > 0) {
			return _where(string, "AND");
		}
		return _wordWhere("WHERE")._wordWhere(string);
	}

	private Command _word(String string) {
		sqlQuery += string + " ";
		return this;
	}

	private Command _wordWhere(String string) {
		sqlWhere += string + " ";
		return this;
	}

	private String sqlQuery = "";
	private String sqlWhere = "";

	private PreparedStatement statement;
	private Connection connection;
}

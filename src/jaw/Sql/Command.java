package jaw.Sql;


import java.util.Map;

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
	 * @throws Exception
	 */
	@Override
	public CommandProtocol select(String items) throws Exception {
		if (items == null || items.length() == 0) {
			return this;
		}
		return _word("SELECT")._word(items);
	}

	/**
	 * Apply insert action
	 * @param columns - Column to insert into table
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol insert(String table, String columns) throws Exception {
		if (table == null || columns == null || table.length() == 0 || columns.length() == 0) {
			return this;
		}
		return _word("INSERT")._word("INTO")._word(table)._word("(" + columns + ")");
	}

	/**
	 * Delete rows from table
	 * @param table - Table name
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol delete(String table) throws Exception {
		if (table == null || table.length() == 0) {
			return this;
		}
		return _word("DELETE")._word("FROM")._word(table);
	}

	/**
	 * Set limit to query
	 * @param value - Row's limit
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol limit(int value) throws Exception {
		if (value == 0) {
			return this;
		}
		return _word("LIMIT")._word(Integer.toString(value));
	}

	/**
	 * Insert values to query
	 * @param items - Items to insert into table(s)
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol values(String items) throws Exception {
		if (items == null || items.length() == 0) {
			return this;
		}
		return _word("VALUES")._word("(" + items + ")");
	}

	/**
	 * Apply distinct selection from table
	 * @param items - Items to select from table(s)
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol distinct(String items) throws Exception {
		if (items == null || items.length() == 0) {
			return this;
		}
		return _word("SELECT DISTINCT")._word(items);
	}

	/**
	 * From which table we should select
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol from(String table) throws Exception {
		if (table == null || table.length() == 0) {
			return this;
		}
		return _word("FROM")._word(table);
	}

	/**
	 * Update table's rows
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol update(String table) throws Exception {
		if (table == null || table.length() == 0) {
			return this;
		}
		return _word("UPDATE")._word(table);
	}

	/**
	 * Update rows in table
	 * @param expression - Update expression
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol set(String expression) throws Exception {
		if (expression == null || expression.length() == 0) {
			return this;
		}
		return _word("SET")._word(expression);
	}

	/**
	 * Declare table's macros
	 * @param as - Table's macros
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol as(String as) throws Exception {
		if (as == null || as.length() == 0) {
			return this;
		}
		return _word("AS")._word(as);
	}

	/**
	 * Insert where statement to query
	 * @param statement - Where statement
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol where(String statement) throws Exception {
		if (statement == null || statement.length() == 0) {
			return this;
		}
		return _where(statement);
	}

	/**
	 * Add and statement to where condition
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol andWhere(String condition) throws Exception {
		if (condition == null || condition.length() == 0) {
			return this;
		}
		return _where(condition, "AND");
	}

	/**
	 * Add or statement to where condition
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol orWhere(String condition) throws Exception {
		if (condition == null || condition.length() == 0) {
			return this;
		}
		return _where(condition, "OR");
	}

	/**
	 * Join some table with on condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol join(String table, String on) throws Exception {
		if (table == null || on == null || table.length() == 0 || on.length() == 0) {
			return this;
		}
		return _word("JOIN")._word(table)._word("ON")._word(on);
	}

	/**
	 * Left join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol leftJoin(String table, String on) throws Exception {
		if (table == null || on == null) {
			return this;
		}
		return _word("LEFT").join(table, on);
	}

	/**
	 * Right join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol rightJoin(String table, String on) throws Exception {
		if (table == null || on == null) {
			return this;
		}
		return _word("RIGHT").join(table, on);
	}

	/**
	 * Inner join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol innerJoin(String table, String on) throws Exception {
		if (table == null || on == null) {
			return this;
		}
		return _word("INNER").join(table, on);
	}

	/**
	 * Outer join some table with condition
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol outerJoin(String table, String on) throws Exception {
		if (table == null || on == null) {
			return this;
		}
		return _word("OUTER").join(table, on);
	}

	/**
	 * Cross join some table with condition
	 * @param table - Table name
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol crossJoin(String table) throws Exception {
		if (table == null) {
			return this;
		}
		return _word("CROSS").join(table, "");
	}

	/**
	 * Order result with some condition
	 * @param condition - Order condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol order(String condition) throws Exception {
		if (condition == null || condition.length() == 0) {
			return this;
		}
		return _order("ORDER")._order("BY")._order(condition);
	}

	/**
	 * Insert sub-command in query
	 * @param command - Sub-command
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol command(CommandProtocol command) throws Exception {
		if (command == null) {
			return this;
		}
		return _word("(" + command.getQuery() + ")");
	}

	/**
	 * Add with clause for query
	 * @param command - Command for with
	 * @return - Current self instance
	 * @throws Exception
	 */
	@Override
	public CommandProtocol with(Map<String, CommandProtocol> command) throws Exception {
		int count = command.size();
		_word("WITH");
		for (Map.Entry<String, CommandProtocol> entry : command.entrySet()) {
			_word(entry.getKey())._word("AS")._word(
				"(" + entry.getValue().getQuery() + ")"
			);
			if (count-- > 1) {
				_word(",");
			}
		}
		return this;
	}

	/**
	 * Add with clause to query
	 * @param alias - With alias
	 * @param command - With command
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol with(String alias, CommandProtocol command) throws Exception {
		return _word("WITH")._word(alias)._word("AS")._word("(" + command.getQuery() + ")");
	}

	/**
	 * Execute query and replace ? with it's object
	 * @param objects - List with objects
	 * @return - Sql executor
	 * @throws Exception
	 */
	@Override
	public SqlExecutor execute(Object[] objects) throws Exception {
		return new SqlExecutor(this, objects);
	}

	/**
	 * Build query for statement
	 * @return - Result query
	 */
	@Override
	public String getQuery() {
		return sqlQuery + sqlWhere + sqlOrder;
	}

	/**
	 * @return - Command connection
	 */
	public Connection getConnection() {
		return connection;
	}

	private Command _where(String string, String clause) {
		if (string == null || clause == null) {
			return this;
		}
		if (sqlWhere.length() < 1) {
			return _where(string);
		}
		return _wordWhere(clause)._wordWhere(string);
	}

	private Command _where(String string) {
		if (string == null) {
			return this;
		}
		if (sqlWhere.length() > 0) {
			return _where(string, "AND");
		}
		return _wordWhere("WHERE")._wordWhere(string);
	}

	private Command _word(String string) {
		return _word(string, false);
	}

	private Command _order(String string) {
		if (string == null) {
			return this;
		}
		sqlOrder += string + " ";
		return this;
	}

	private Command _word(String string, boolean woSpace) {
		if (string == null) {
			return this;
		}
		if (!woSpace) {
			sqlQuery += string + " ";
		} else {
			sqlQuery += string ;
		}
		return this;
	}

	private Command _wordWhere(String string) {
		sqlWhere += string + " ";
		return this;
	}

	private String sqlQuery = "";
	private String sqlWhere = "";
	private String sqlOrder = "";

	private Connection connection;
}

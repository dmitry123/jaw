package Sql;



/**
 * Created by Savonin on 2014-11-22
 */
public interface CommandProtocol {

	/**
	 * Apply selection from table
	 * @param items - Items to select from table(s)
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol select(String items) throws Exception;

	/**
	 * Apply insert action
	 * @param columns - Column to insert into table
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol insert(String table, String columns) throws Exception;

	/**
	 * Delete rows from table
	 * @param table - Table name
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol delete(String table) throws Exception;

	/**
	 * Set limit to query
	 * @param value - Row's limit
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol limit(int value) throws Exception;

	/**
	 * Insert values to query
	 *
	 * @param items - Items to insert into table(s)
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol values(String items) throws Exception;

	/**
	 * Apply distinct selection from table
	 *
	 * @param items - Items to select from table(s)
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol distinct(String items) throws Exception;

	/**
	 * From which table we should select
	 *
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol from(String table) throws Exception;

	/**
	 * Update table's rows
	 *
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol update(String table) throws Exception;

	/**
	 * Update rows in table
	 *
	 * @param expression - Update expression
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol set(String expression) throws Exception;

	/**
	 * Declare table's macros
	 *
	 * @param as - Table's macros
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol as(String as) throws Exception;

	/**
	 * Insert where statement to query
	 *
	 * @param statement - Where statement
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol where(String statement) throws Exception;

	/**
	 * Add and statement to where condition
	 *
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol andWhere(String condition) throws Exception;

	/**
	 * Add or statement to where condition
	 *
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol orWhere(String condition) throws Exception;

	/**
	 * Join some table with on condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol join(String table, String on) throws Exception;

	/**
	 * Left join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol leftJoin(String table, String on) throws Exception;

	/**
	 * Right join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol rightJoin(String table, String on) throws Exception;

	/**
	 * Inner join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol innerJoin(String table, String on) throws Exception;

	/**
	 * Outer join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol outerJoin(String table, String on) throws Exception;

	/**
	 * Cross join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol crossJoin(String table, String on) throws Exception;

	/**
	 * Order result with some condition
	 *
	 * @param condition - Order condition
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol order(String condition) throws Exception;

	/**
	 * Insert sub-command in query
	 *
	 * @param command - Sub-command
	 * @return - Current self instance
	 * @throws Exception
	 */
	public CommandProtocol command(CommandProtocol command) throws Exception;

	/**
	 * Execute query and replace ? with it's object
	 *
	 * @param objects - List with objects
	 * @return - Current self instance
	 * @throws Exception
	 */
	public SqlExecutor execute(Object... objects) throws Exception;
}

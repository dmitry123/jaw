package Sql;

import Core.InternalError;

import java.sql.ResultSet;

/**
 * Created by Savonin on 2014-11-22
 */
public interface CommandProtocol {

	/**
	 * Apply selection from table
	 *
	 * @param items - Items to select from table(s)
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	public CommandProtocol select(String items) throws Core.InternalError;

	/**
	 * Apply insert action
	 *
	 * @param columns - Column to insert into table
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	public CommandProtocol insert(String columns) throws Core.InternalError;

	/**
	 * Insert values to query
	 *
	 * @param items - Items to insert into table(s)
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	public CommandProtocol values(String items) throws Core.InternalError;

	/**
	 * Apply distinct selection from table
	 *
	 * @param items - Items to select from table(s)
	 * @return - Current self instance
	 * @throws Core.InternalError
	 */
	public CommandProtocol distinct(String items) throws InternalError;

	/**
	 * From which table we should select
	 *
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol from(String table) throws InternalError;

	/**
	 * Update table's rows
	 *
	 * @param table - Table's name
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol update(String table) throws InternalError;

	/**
	 * From which table we should select
	 *
	 * @param table - Table's name
	 * @param as - Table's macros
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol from(String table, String as) throws InternalError;

	/**
	 * Update rows in table
	 *
	 * @param expression - Update expression
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol set(String expression) throws InternalError;

	/**
	 * Declare table's macros
	 *
	 * @param as - Table's macros
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol as(String as) throws InternalError;

	/**
	 * Insert where statement to query
	 *
	 * @param statement - Where statement
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol where(String statement) throws InternalError;

	/**
	 * Add and statement to where condition
	 *
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol and(String condition) throws InternalError;

	/**
	 * Add or statement to where condition
	 *
	 * @param condition - Where condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol or(String condition) throws InternalError;

	/**
	 * Join some table with on condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol join(String table, String on) throws InternalError;

	/**
	 * Join some table with on condition
	 *
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol join(String table, String as, String on) throws InternalError;

	/**
	 * Left join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol left(String table, String on) throws InternalError;

	/**
	 * Left join some table with condition
	 *
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol left(String table, String as, String on) throws InternalError;

	/**
	 * Right join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol right(String table, String on) throws InternalError;

	/**
	 * Right join some table with condition
	 *
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol right(String table, String as, String on) throws InternalError;

	/**
	 * Inner join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol inner(String table, String on) throws InternalError;

	/**
	 * Inner join some table with condition
	 *
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol inner(String table, String as, String on) throws InternalError;

	/**
	 * Outer join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol outer(String table, String on) throws InternalError;

	/**
	 * Outer join some table with condition
	 *
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol outer(String table, String as, String on) throws InternalError;

	/**
	 * Cross join some table with condition
	 *
	 * @param table - Table name
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol cross(String table, String on) throws InternalError;

	/**
	 * Cross join some table with condition
	 *
	 * @param table - Table name
	 * @param as - Table macros
	 * @param on - Join condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol cross(String table, String as, String on) throws InternalError;

	/**
	 * Order result with some condition
	 *
	 * @param condition - Order condition
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol order(String condition) throws InternalError;

	/**
	 * Insert sub-command in query
	 *
	 * @param command - Sub-command
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public CommandProtocol command(CommandProtocol command) throws InternalError;

	/**
	 * Execute query and replace ? with it's object
	 *
	 * @param objects - List with objects
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public SqlExecutor execute(Object[] objects) throws InternalError;

	/**
	 * Execute query
	 *
	 * @return - Current self instance
	 * @throws InternalError
	 */
	public SqlExecutor execute() throws InternalError;
}

package jaw.Core;

import jaw.Sql.*;
import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * AbstractTable
 * @param <T>
 */
abstract public class Model<T extends CortegeProtocol> extends Component implements ModelProtocol {

	/**
	 * Filter interface, which provides
	 * implementation of 'test' method
	 * to check object's fields
	 * @param <T> - CortegeProtocol implementation
	 */
    public static interface Filter<T> {
        boolean test(T u);
    }

	/**
	 * Basic constructor with helper and
	 * table's name as arguments
	 * @param environment - Current environment
	 * @param tableName - Table's name
	 */
    public Model(Environment environment, String tableName) {
		super(environment); this.tableName = tableName;
    }

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Exception
	 */
	public abstract CortegeProtocol createFromSet(ResultSet result) throws Exception;

	/**
	 * Fetch row from list via it's action name
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - Found row
	 * @throws Exception
	 */
	public T fetchRow(String fetchAction, Object... argumentList) throws Exception {
		Method method;
		Class<?>[] typeList = new Class<?>[
			argumentList.length
		];
		try {
			int i = 0;
			for (Object a : argumentList) {
				typeList[i++] = a.getClass();
			}
			method = getClass().getMethod(
				fetchAction, typeList
			);
		} catch (NoSuchMethodException e) {
			throw new Exception(
				"Model/fetchRow() : \"" + e.getMessage() + "\""
			);
		}
		try {
			Object result = method.invoke(this, argumentList);
			if (result instanceof ResultSet) {
				if (((ResultSet) result).next()) {
					return (T) createFromSet(((ResultSet) result));
				}
				return null;
			} else {
				return ((T) result);
			}
		} catch (IllegalAccessException e) {
			throw new Exception(
				"Model/fetchRow() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new Exception(
				e.getCause().getMessage()
			);
		}
	}

	/**
	 * Fetch row from list via it's action name
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - Found row
	 * @throws Exception
	 */
	public ResultSet fetchSet(String fetchAction, Object... argumentList) throws Exception {
		Method method;
		Class<?>[] typeList = new Class<?>[
				argumentList.length
			];
		try {
			int i = 0;
			for (Object a : argumentList) {
				typeList[i++] = a.getClass();
			}
			method = getClass().getMethod(
					fetchAction, typeList
			);
		} catch (NoSuchMethodException e) {
			throw new Exception(
				"Model/fetchSet() : \"No such method - " + e.getMessage() + "\""
			);
		}
		try {
			Object result = method.invoke(this, argumentList);
			if (result instanceof ResultSet) {
				return ((ResultSet) result);
			} else {
				return null;
			}
		} catch (IllegalAccessException e) {
			throw new Exception(
				"Model/fetchSet() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new Exception(
				e.getCause().getMessage()
			);
		}
	}

	/**
	 * Fetch row from list via it's action name
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - Found row
	 * @throws Exception
	 */
	public Vector<CortegeProtocol> fetchVector(String fetchAction, Object... argumentList) throws Exception {
		Method method;
		Class<?>[] typeList = new Class<?>[
			argumentList.length
		];
		try {
			int i = 0;
			for (Object a : argumentList) {
				typeList[i++] = a.getClass();
			}
			method = getClass().getMethod(
				fetchAction, typeList
			);
		} catch (NoSuchMethodException e) {
			throw new Exception(
				"Model/fetchVector() : \"" + e.getMessage() + "\""
			);
		}
		try {
			final Object result = method.invoke(
				this, argumentList
			);
			if (result instanceof ResultSet) {
				ResultSet rs = ((ResultSet) result);
				Vector<CortegeProtocol> rv = new Vector<CortegeProtocol>(rs.getFetchSize());
				while (rs.next()) {
					rv.add(createFromSet(rs));
				}
				return rv;
			}
			try {
				return (Vector<CortegeProtocol>) result;
			} catch (ClassCastException e) {
				return new Vector<CortegeProtocol>() {{
					add(((T) result));
				}};
			}
		} catch (IllegalAccessException e) {
			throw new Exception(
				"Model/fetchVector() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new Exception(
				e.getCause().getMessage()
			);
		}
	}

	/**
	 * Override that method to return your own columns for fetchTable method
	 * @return - Command with your query
	 * @throws Exception
	 */
	public CommandProtocol getResultSetForTable() throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from(getTableName());
	}

	/**
	 * Get all rows from current table
	 * @return - Result set with all rows
	 * @throws Exception
	 */
	public Vector<HashMap<String, String>> getRows() throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.execute()
			.select();
		Vector<HashMap<String, String>> result
			= new Vector<HashMap<String, String>>();
		while (resultSet.next()) {
			result.add(buildMap(resultSet));
		}
		return result;
	}

	/**
	 * Override that method to return rows with all foreign keys
	 * @return - Command which construct query to fetch table with all references
	 * @throws Exception
	 */
	public CommandProtocol getReferences() throws Exception {
		return null;
	}

	public static class Wrapper extends Vector<LinkedHashMap<String, String>> {

		/**
		 * Construct wrapper with count of pages
		 * @param pages - Count of pages
		 */
		public Wrapper(int pages) {
			this.pages = pages;
		}

		/**
		 * @return - Count of pages
		 */
		public int getPages() {
			return pages;
		}

		private int pages;
	}

	/**
	 * Build vector with associated with it's value, column and table results
	 * @param page - Current page
	 * @param limit - Limit per page
	 * @param where - Where cause
	 * @return - Vector with results
	 * @throws Exception
	 * @throws SQLException
	 */
	public final Collection<LinkedHashMap<String, String>> fetchTable(int page, int limit, String where, String order) throws Exception {
		ResultSet resultSet = getResultSetForTable()
			.where(where)
			.order(order)
			.execute()
			.select();
		Wrapper wrapper;
		int total = 0;
		while (resultSet.next()) {
			++total;
		}
		resultSet.beforeFirst();
		if (page != 0) {
			int pages = total / limit + (total / limit * limit != total ? 1 : 0);
			if (page > pages) {
				return new Wrapper(pages);
			}
			int amount = limit;
			int skip = amount * (page - 1);
			while (skip != 0 && resultSet.next()) {
				if (skip-- == 0) {
					break;
				}
			}
			wrapper = new Wrapper(pages);
			while (amount != 0 && resultSet.next()) {
				wrapper.add(buildMap(resultSet));
				if (amount-- <= 0) {
					break;
				}
			}
		} else {
			wrapper = new Wrapper(1);
			while (resultSet.next()) {
				wrapper.add(buildMap(resultSet));
			}
		}
		return wrapper;
	}

	private boolean compareMaps(Map<String, String> left, Map<String, String> right) {
		for (String key : left.keySet()) {
			if (!right.containsKey(key)) {
				return false;
			}
			if (!left.get(key).equals(right.get(key))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Fetch all references for current table, override getReferences method to
	 * return current table with connected all foreign tables
	 * @return - Collection with associated elements
	 * @throws Exception
	 * @throws SQLException
	 */
	public final HashMap<String, Collection<HashMap<String, String>>> fetchReferences(String alias, String id) throws Exception {
		CommandProtocol command = getReferences();
		HashMap<String, Collection<HashMap<String, String>>> result
			= new HashMap<String, Collection<HashMap<String, String>>>();
		if (command == null) {
			return result;
		}
		Object value = isInteger(id) ? Integer.parseInt(id) : id;
		ResultSet resultSet = command
			.where(alias + " = ?")
			.execute(value)
			.select();
		String skip = alias.substring(0, alias.indexOf("."));
		while (resultSet.next()) {
			LinkedHashMap<String, String> map = buildMap(resultSet);
			HashMap<String, HashMap<String, String>> temporary
				= new HashMap<String, HashMap<String, String>>();
			for (String key : map.keySet()) {
				int index = key.indexOf(".");
				String field = key.substring(0, index);
				String name = key.substring(index + 1);
				if (!temporary.containsKey(field)) {
					temporary.put(field, new HashMap<String, String>());
				}
				temporary.get(field).put(name, map.get(key));
			}
			for (Map.Entry<String, HashMap<String, String>> entry : temporary.entrySet()) {
				if (entry.getKey().equals(skip)) {
					continue;
				}
				if (!result.containsKey(entry.getKey())) {
					result.put(entry.getKey(), new Vector<HashMap<String, String>>());
				}
				Vector<HashMap<String, String>> vector
					= (Vector<HashMap<String, String>>) result.get(entry.getKey());
				boolean found = false;
				for (HashMap<String, String> m : vector) {
					if (compareMaps(m, entry.getValue())) {
						found = true;
						break;
					}
				}
				if (!found) {
					result.get(entry.getKey()).add(entry.getValue());
				}
			}
		}
		return result;
	}

	/**
	 * Delete cortege from table by identifier
	 * @param id - Row's identifier
	 * @throws Exception
	 * @throws SQLException
	 */
	public int deleteByID(Integer id) throws Exception {
		return getConnection().createCommand()
			.delete(getTableName())
			.where("cast(id as text) = cast(? as text)")
			.execute(id)
			.delete();
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	private boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Update cortege by it's primary key
	 * @param id - Row's identifier
	 * @param values - Map with values
	 * @return - Count of updates
	 * @throws Exception
	 * @throws SQLException
	 */
	public int updateByID(Object id, Map<String, String> values) throws Exception {
		String set = "";
		Vector<Object> objects = new Vector<Object>();
		int size = values.size();
		for (Map.Entry<String, String> k : values.entrySet()) {
			set += k.getKey() + " = ?";
			if (--size > 0) {
				set += ", ";
			}
			if (isInteger(k.getValue())) {
				objects.add(Integer.parseInt(k.getValue()));
			} else if (isFloat(k.getValue())) {
				objects.add(Float.parseFloat(k.getValue()));
			} else {
				objects.add(k.getValue());
			}
		}
		objects.add(id);
		return getConnection().createCommand()
			.update(getTableName())
			.set(set)
			.where("cast(id as text) = cast(? as text)")
			.execute(objects.toArray())
			.update();
	}

	/**
	 * Insert item to database from hash map
	 * @param values - Map with values
	 * @return - True if has been inserted successfully
	 * @throws Exception
	 * @throws SQLException
	 */
	public boolean insert(Map<String, String> values) throws Exception {
		String columns = "";
		String variables = "";
		Vector<Object> objects = new Vector<Object>();
		int size = values.size();
		for (Map.Entry<String, String> k : values.entrySet()) {
			columns += k.getKey();
			variables += "?";
			if (--size > 0) {
				columns += ", ";
				variables += ", ";
			}
			if (isInteger(k.getValue())) {
				objects.add(Integer.parseInt(k.getValue()));
			} else if (isFloat(k.getValue())) {
				objects.add(Float.parseFloat(k.getValue()));
			} else {
				objects.add(k.getValue());
			}
		}
		return getConnection().createCommand()
			.insert(getTableName(), columns)
			.values(variables)
			.execute(objects.toArray())
			.insert();
	}

	/**
	 * Get last element from table
	 * @return - Last element
	 * @throws Exception
	 * @throws SQLException
	 */
	public T last() throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.order("id desc")
			.execute()
			.select();
		if (!resultSet.next()) {
			return null;
		}
		return (T) createFromSet(
			resultSet
		);
	}

	/**
	 * Associate columns with tables
	 * @param resultSet - Set with results
	 * @return - Map with names and values
	 */
	public static LinkedHashMap<String, String> buildMap(ResultSet resultSet) throws SQLException{
		ResultSetMetaData columns = resultSet.getMetaData();
		LinkedHashMap<String, String> columnMap
			= new LinkedHashMap<String, String>();
		for (int i = 1; i <= columns.getColumnCount(); i++) {
			String field = columns.getTableName(i) + "." + columns.getColumnName(i);
			if (columnMap.containsKey(field)) {
				String value = columnMap.get(field);
				if (value.startsWith("[")) {
					JSONArray array = new JSONArray(value);
					array.put(resultSet.getString(i));
					columnMap.put(field, array.toString());
				} else {
					JSONArray array = new JSONArray();
					array.put(resultSet.getString(i));
					columnMap.put(field, array.toString());
				}
			} else {
				columnMap.put(field, resultSet.getString(i));
			}
		}
		return columnMap;
	}

	/**
	 * Execute/Update/Prepare query
	 * and * change ${TABLE} macros
	 * to table's name
	 *
	 * @param query
	 * 		String with Sql query
	 * @return Result with executed statement
	 * @throws Exception
	 */
    public PreparedStatement prepareStatement(String query) throws Exception {
		try {
			return getConnection().getSqlConnection().prepareStatement(query);
		} catch (SQLException e) {
			throw new Exception("Model/createStatementForSelect() : \"" + e.getMessage() + "\"");
		}
    }

	/**
	 * Execute/Update/Prepare query
	 * and * change ${TABLE} macros
	 * to table's name
	 *
	 * @param query - String with Sql query
	 *
	 * @param list - Argument fetchList with all elements
	 * 		which have to be appended to query
	 *
	 * @return - Result with executed statement
	 * @throws Exception
	 */
	public ResultSet execute(String query, Object... list) throws Exception {
		try {
			PreparedStatement preparedStatement = getConnection().getSqlConnection()
				.prepareStatement(query);

			SqlTypeBinder sqlTypeBinder = new SqlTypeBinder(
				preparedStatement
			);

			for (Object a : list) {
				sqlTypeBinder.bind(a);
			}

			if (query.startsWith("INSERT")) {
				preparedStatement.execute(); return null;
			}

			return preparedStatement.executeQuery();
		} catch (SQLException e) {
			throw new Exception("Model/execute() : \"" + e.getMessage() + "\"");
		}
	}

	/**
	 * Find object in table by
	 * it's identifier
	 *
	 * @param
	 * 		id Row's identifier
	 * @return Founded object
	 * @throws java.lang.Exception
	 */
    public ResultSet fetchByID(Integer id) throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from(tableName)
			.where("id = ?")
			.execute(new Object[] { id })
			.select();
		if (!resultSet.next()) {
			throw new Exception("Model/fetchByID() : \"Invalid primary key (" + id + ")\"");
		}
		return resultSet;
	}

	/**
	 * Build fetchList with all elements
	 * by 'where' statement
	 *
	 * @param where Where statement
	 * @return Vector with founded rows
	 */
    public Vector<T> fetchList(String where) throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.where(where)
			.execute()
			.select();
		Vector<T> result = new Vector<T>();
		while (resultSet.next()) {
			result.add((T) createFromSet(resultSet));
		}
		return result;
	}

	/**
	 * @return Table's size
	 */
    public int fetchSize(String where) throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("count(*) as c")
			.from(getTableName())
			.where(where)
			.execute()
			.select();
		if (resultSet.next()) {
			return resultSet.getInt("c");
		} else {
			return 0;
		}
	}

	/**
	 * Check object for existence by
	 * name or identifier
	 *
	 * @param id Row's identifier
	 * @return Boolean state
	 */
    public boolean exists(int id) throws Exception {
        return fetchByID(id) != null;
    }

	/**
	 * Get MySql's helper object,
	 * which provides helpful methods
	 * for queries and connection
	 *
	 * @return Helper
	 */
    public Connection getConnection() {
        return getEnvironment().getConnection();
    }

	/**
	 * Get current table' name,
	 * which changes by macros ${TABLE}
	 *
	 * @return Table's name
	 */
    public String getTableName() {
        return tableName;
    }

	/**
	 * Table's name
	 */
    private String tableName;
}

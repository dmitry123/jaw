package jaw.Core;

import jaw.Sql.CommandProtocol;
import jaw.Sql.Connection;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * AbstractTable
 */
abstract public class Model extends Component implements ModelProtocol {

	/**
	 * Filter interface, which provides
	 * implementation of 'test' method
	 * to check object's fields
	 */
    public static interface Filter {
        boolean test(CortegeProtocol u);
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
	 * Fetch row from list via it's action name
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - Found row
	 * @throws Exception
	 */
	public CortegeProtocol fetchRow(String fetchAction, Object... argumentList) throws Exception {
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
				if (!((ResultSet) result).next()) {
					return null;
				}
				return new CortegeRow(((ResultSet) result).getInt("id"));
			} else {
				return ((CortegeProtocol) result);
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
				ResultSet resultSet = ((ResultSet) result);
				Vector<CortegeProtocol> rv = new Vector<CortegeProtocol>(resultSet.getFetchSize());
				while (resultSet.next()) {
					rv.add(new CortegeRow(resultSet.getInt("id")));
				}
				return rv;
			}
			try {
				return (Vector<CortegeProtocol>) result;
			} catch (ClassCastException e) {
				return new Vector<CortegeProtocol>() {{
					add(((CortegeProtocol) result));
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
	 * Perform insert fetch action
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - True if row has been inserted
	 * @throws Exception
	 */
	public Boolean fetchInsert(String fetchAction, Object... argumentList) throws Exception {
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
				"Model/fetchInsert() : \"" + e.getMessage() + "\""
			);
		}
		try {
			return (Boolean) method.invoke(
				this, argumentList
			);
		} catch (IllegalAccessException e) {
			throw new Exception(
				"Model/fetchInsert() : \"" + e.getMessage() + "\""
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
	public CommandProtocol getTable() throws Exception {
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
	 * Get all rows from current table
	 * @return - Result set with all rows
	 * @throws Exception
	 */
	public Vector<HashMap<String, String>> getTableRows() throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.execute()
			.select();
		Vector<HashMap<String, String>> result
				= new Vector<HashMap<String, String>>();
		while (resultSet.next()) {
			Map<String, String> map = buildMap(resultSet);
			HashMap<String, String> clone = new LinkedHashMap<String, String>();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				clone.put(entry.getKey().substring(getTableName().length() + 1),
					entry.getValue());
			}
			result.add(clone);
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
		ResultSet resultSet = getTable()
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
	public int deleteByID(Object id) throws Exception {
		return getConnection().createCommand()
			.delete(getTableName())
			.where("id = ?")
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
	 * Get set with all table's rows
	 * @return - Set with rows
	 * @throws Exception
	 */
	public ResultSet fetchRows() throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.execute()
			.select();
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
	public CortegeProtocol last() throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.order("id desc")
			.execute()
			.select();
		if (!resultSet.next()) {
			return null;
		}
		return new CortegeRow(resultSet.getInt("id"));
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
	 * Associate columns with tables
	 * @param resultSet - Set with results
	 * @return - Map with names and values
	 */
	public static LinkedHashMap<String, String> buildStaticMap(ResultSet resultSet) throws SQLException{
		ResultSetMetaData columns = resultSet.getMetaData();
		LinkedHashMap<String, String> columnMap
				= new LinkedHashMap<String, String>();
		for (int i = 1; i <= columns.getColumnCount(); i++) {
			String field = columns.getColumnName(i);
			if (columnMap.containsKey(field)) {
				String value = columnMap.get(field);
				if (value.startsWith("[")) {
					try {
						JSONArray array = new JSONArray(value);
						array.put(resultSet.getString(i));
						columnMap.put(field, array.toString());
					} catch (JSONException ignored) {
						JSONArray array = new JSONArray();
						array.put(resultSet.getString(i));
						array.put(columnMap.get(field));
						columnMap.put(field, array.toString());
					}
				} else {
					JSONArray array = new JSONArray();
					array.put(resultSet.getString(i));
					array.put(columnMap.get(field));
					columnMap.put(field, array.toString());
				}
			} else {
				columnMap.put(field, resultSet.getString(i));
			}
		}
		return columnMap;
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
		return getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.where("id = ?")
			.execute(id)
			.select();
	}

	/**
	 * Build fetchList with all elements
	 * by 'where' statement
	 *
	 * @param where Where statement
	 * @return Vector with founded rows
	 */
    public Vector<CortegeProtocol> fetchList(String where) throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from(getTableName())
			.where(where)
			.execute()
			.select();
		Vector<CortegeProtocol> result = new Vector<CortegeProtocol>();
		while (resultSet.next()) {
			result.add(new CortegeRow(resultSet.getInt("id")));
		}
		return result;
	}

	/**
	 * @return Table's size
	 */
    public int fetchSize(String where, Object... arguments) throws Exception {
		ResultSet resultSet = getConnection().createCommand()
			.select("count(*) as c")
			.from(getTableName())
			.where(where)
			.execute(arguments)
			.select();
		if (resultSet.next()) {
			return resultSet.getInt("c");
		} else {
			return 0;
		}
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

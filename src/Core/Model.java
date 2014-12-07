package Core;

import Sql.Connection;
import Sql.CortegeProtocol;
import Sql.SqlTypeBinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

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
	 * @throws Core.InternalError
	 */
	public abstract CortegeProtocol createFromSet(ResultSet result) throws InternalError, ExternalError, SQLException;

	/**
	 * Fetch row from list via it's action name
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - Found row
	 * @throws InternalError
	 */
	public T fetchRow(String fetchAction, Object... argumentList) throws InternalError, ExternalError, SQLException {
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
			throw new InternalError(
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
			throw new InternalError(
				"Model/fetchRow() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new InternalError(
				e.getCause().getMessage()
			);
		}
	}

	/**
	 * Fetch row from list via it's action name
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - Found row
	 * @throws InternalError
	 */
	public ResultSet fetchSet(String fetchAction, Object... argumentList) throws InternalError, ExternalError, SQLException {
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
			throw new InternalError(
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
			throw new InternalError(
				"Model/fetchSet() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new InternalError(
				e.getCause().getMessage()
			);
		}
	}

	/**
	 * Fetch row from list via it's action name
	 * @param fetchAction - Fetch action name
	 * @param argumentList - List with arguments
	 * @return - Found row
	 * @throws InternalError
	 */
	public Vector<CortegeProtocol> fetchVector(String fetchAction, Object... argumentList) throws InternalError, ExternalError, SQLException {
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
			throw new InternalError(
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
			throw new InternalError(
				"Model/fetchVector() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new InternalError(
				e.getCause().getMessage()
			);
		}
	}

	/**
	 * Execute/Update/Prepare query
	 * and * change ${TABLE} macros
	 * to table's name
	 *
	 * @param query
	 * 		String with Sql query
	 * @return Result with executed statement
	 * @throws Core.InternalError
	 */
    public ResultSet executeQuery(String query) throws InternalError, ExternalError, SQLException {
		try {
			return getConnection().statement().executeQuery(query);
		} catch (SQLException e) {
			throw new InternalError("Model/executeQuery() : \"" + e.getMessage() + "\"");
		}
    }

	/**
	 * Execute/Update/Prepare query
	 * and * change ${TABLE} macros
	 * to table's name
	 *
	 * @param query
	 * 		String with Sql query
	 * @return Result with executed statement
	 * @throws Core.InternalError
	 */
    public int executeUpdate(String query) throws InternalError, ExternalError, SQLException {
		try {
			return getConnection().statement().executeUpdate(query);
		} catch (SQLException e) {
			throw new InternalError("Model/executeUpdate() : \"" + e.getMessage() + "\"");
		}
    }

	/**
	 * Execute/Update/Prepare query
	 * and * change ${TABLE} macros
	 * to table's name
	 *
	 * @param query
	 * 		String with Sql query
	 * @return Result with executed statement
	 * @throws Core.InternalError
	 */
    public PreparedStatement prepareStatement(String query) throws InternalError, ExternalError, SQLException {
		try {
			return getConnection().getSqlConnection().prepareStatement(query);
		} catch (SQLException e) {
			throw new InternalError("Model/prepare() : \"" + e.getMessage() + "\"");
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
	 * @throws InternalError
	 */
	public ResultSet execute(String query, Object... list) throws InternalError, ExternalError, SQLException {
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
			throw new InternalError("Model/execute() : \"" + e.getMessage() + "\"");
		}
	}

	/**
	 * @return - Last element in db
	 * @throws Exception
	 */
	public T last() throws InternalError, ExternalError, SQLException {
		ResultSet rs = execute("SELECT * FROM " + tableName +
			" ORDER BY id DESC LIMIT 1");
		try {
			if (!rs.next()) {
				throw new InternalError("ModelHelper/last() \"Row hasn't been attached to table\"");
			}
		} catch (SQLException e) {
			throw new InternalError("Model/last() : \"" + e.getMessage() + "\"");
		}
		return ((T) createFromSet(rs));
	}

	/**
	 * Find object in table by
	 * it's identifier
	 *
	 * @param
	 * 		id Row's identifier
	 * @return Founded object
	 * @throws Exception
	 */
    public ResultSet fetchByID(Integer id) throws InternalError, ExternalError, SQLException {
		ResultSet resultSet = getConnection().command()
			.select("*")
			.from(tableName)
			.where("id = ?")
			.execute(new Object[] { id })
			.select();
		if (!resultSet.next()) {
			throw new InternalError("Model/fetchByID() : \"Invalid primary key (" + id + ")\"");
		}
		return resultSet;
	}

	/**
	 * Build fetchList with all elements
	 * by 'where' statement
	 *
	 * @param where Where statement
	 * @return Vector with founded rows
	 * @throws Exception
	 */
    public Vector<T> fetchList(String where) throws InternalError, ExternalError, SQLException {

		ResultSet rs = execute("SELECT * FROM " + tableName + "" +
			(where.length() > 0 ? "WHERE " + where : ""));

		Vector<T> result = new Vector<T>(rs.getFetchSize());

		while (rs.next()) {
			result.add((T) createFromSet(rs));
		}

		return result;
	}

	/**
	 * Drop element from table with
	 * it's handle, name or identifier
	 *
	 * @param t
	 * 		Some object, which implements CollageProtocol
	 * @throws Exception
	 */
    public void erase(T t) throws InternalError, ExternalError, SQLException {
		if (t.getID() != 0) {
			erase(t.getID());
		} else {
			throw new InternalError("Overload 'erase' method to erase this cortege (identifier is 0)");
		}
	}

	/**
	 * Drop element from table with
	 * it's handle, name or identifier
	 *
	 * @param id - Row's identifier
	 * @throws Exception
	 */
    public void erase(int id) throws InternalError, ExternalError, SQLException {
		execute("DELETE FROM " + tableName + "WHERE id=?", id);
	}

	/**
	 * @return Table's size
	 * @throws Exception
	 */
    public int size() throws InternalError, ExternalError, SQLException {
		int size = 0;
		ResultSet resultSet =  executeQuery("SELECT * FROM " + tableName);
		while (resultSet.next()) {
			++size;
		}
		resultSet.close();
		return size;
	}

	/**
	 * Check object for existence by
	 * name or identifier
	 *
	 * @param id Row's identifier
	 * @return Boolean state
	 * @throws Exception
	 */
    public boolean exists(int id) throws InternalError, ExternalError, SQLException {
        return fetchByID(id) != null;
    }

	/**
	 * Overloaded fetchList method with
	 * default 'where' condition
	 *
	 * @return vector with results
	 * @throws Exception
	 */
	public Vector<T> list() throws InternalError, ExternalError, SQLException {
        return fetchList("");
    }

	/**
	 * Filter all collages in table
	 * by some filter
	 *
	 * @param f
	 * 		Filter class, which implements
	 * 		test method
	 * @return Vector with founded rows
	 * @throws Exception
	 */
	public Vector<T> filter(Filter<T> f) throws InternalError, ExternalError, SQLException {
        Vector<T> list = fetchList("");
        for (int i = 0; i < list.size(); i++) {
            if (!f.test(list.get(i))) {
                list.remove(i);
            }
        }
        return list;
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

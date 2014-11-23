package Component;

import Core.*;
import Core.InternalError;
import Sql.Connection;
import Sql.CortegeProtocol;
import Sql.SqlTypeBinder;

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
	public abstract CortegeProtocol createFromSet(ResultSet result) throws Exception;

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
    public ResultSet executeQuery(String query) throws InternalError {
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
    public int executeUpdate(String query) throws InternalError {
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
    public PreparedStatement prepareStatement(String query) throws InternalError {
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
	public ResultSet execute(String query, Object... list) throws InternalError {
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
	 * Insert someone in database, where
	 * 't' is future object
	 *
	 * @param t
	 * 		Some object, which implements CollageProtocol
	 * @throws Exception
	 */
    public abstract T add(T t) throws Exception;

	/**
	 * @return - Last element in db
	 * @throws Exception
	 */
	public T last() throws Exception {
		ResultSet rs = execute("SELECT * FROM " + tableName +
			" ORDER BY id DESC LIMIT 1");
		if (!rs.next()) {
			throw new InternalError("ModelHelper/last() \"Row hasn't been attached to table\"");
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
    public T fetchByID(int id) throws Exception {
		ResultSet rs = execute("SELECT * FROM " + tableName +
			" WHERE id = ?", id);
		if (!rs.next()) {
			throw new ExternalError(ExternalError.Code.InvalidPrimaryKey);
		}
		return ((T) createFromSet(rs));
	}

	/**
	 * Build fetchList with all elements
	 * by 'where' statement
	 *
	 * @param
	 * 		where Where statement
	 * @return Vector with founded rows
	 * @throws Exception
	 */
    public Vector<T> fetchList(String where) throws Exception {

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
    public void erase(T t) throws Exception {
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
    public void erase(int id) throws Exception {
		execute("DELETE FROM " + tableName + "WHERE id=?", id);
	}

	/**
	 * @return Table's size
	 * @throws Exception
	 */
    public int size() throws Exception {
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
    public boolean exists(int id) throws Exception {
        return fetchByID(id) != null;
    }

	/**
	 * Overloaded fetchList method with
	 * default 'where' condition
	 *
	 * @return vector with results
	 * @throws Exception
	 */
	public Vector<T> list() throws Exception {
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
	public Vector<T> filter(Filter<T> f) throws Exception {
        Vector<T> list = fetchList("");
        for (int i = 0; i < list.size(); i++) {
            if (!f.test(list.get(i))) {
                list.remove(i);
            }
        }
        return list;
    }

	/**
	 * Truncate current table
	 *
	 * @throws Exception
	 */
	public void truncate() throws Exception {
		executeUpdate("TRUNCATE " + tableName);
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

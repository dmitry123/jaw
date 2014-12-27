package Sql;



import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-11-22
 */
public class SqlTypeBinder {

	/**
	 * Construct binder with default 0 offset
	 */
	public SqlTypeBinder(PreparedStatement statement) {
		this(statement, 1);
	}

	/**
	 * Construct binder with custom offset
	 * @param  statement - Prepared sql statement
	 * @param indexOffset - Index's offset
	 */
	public SqlTypeBinder(PreparedStatement statement, int indexOffset) {
		this.statement = statement; this.index = indexOffset;
	}

	/**
	 * Bind array to sql statement
	 * @param array - Array with items
	 * @throws Exception
	 */
	public SqlTypeBinder bind(Object[] array) throws Exception {
		for (Object item : array) {
			bind(item);
		}
		return this;
	}

	/**
	 * Bind any parameter to prepared sql statement
	 * @param item - Object which can be converted to sql
	 * @throws Exception
	 */
	public void bind(Object item) throws Exception {
		try {
			if (item instanceof String) {
				statement.setString(index, item.toString());
			} else if (item instanceof Integer) {
				statement.setInt(index, (Integer) item);
			} else if (item instanceof Long) {
				statement.setLong(index, (Long) item);
			} else if (item instanceof Float) {
				statement.setFloat(index, (Float) item);
			} else if (item instanceof Double) {
				statement.setDouble(index, (Double) item);
			} else if (item instanceof Blob) {
				statement.setBlob(index, (Blob) item);
			} else {
				throw new Exception("SqlTypeBinder/bind() : \"Invalid type\"");
			}
		} catch (SQLException e) {
			throw new Exception("SqlTypeBinder/bind() : \"" + e.getMessage() + "\"");
		}
		++index;
	}

	/**
	 * @return - Binder's statement
	 */
	public PreparedStatement getStatement() {
		return statement;
	}

	/**
	 * @return - Current just bind index
	 */
	public int getIndex() {
		return index;
	}

	private PreparedStatement statement;
	private int index = 0;
}

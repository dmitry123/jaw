package models;

import Core.Environment;
import Sql.CortegeRow;
import Core.Model;

import java.sql.ResultSet;

/**
 * GroupTable
 */
public class Group extends Model<Group.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment
	 * 		MySql's helper object
	 */
	public Group(Environment environment) {
		super(environment, "groups");
	}

	/**
	 * Check for group existence by it's name
	 *
	 * @param name - Group's name
	 * @return - Boolean state
	 * @throws java.lang.Exception
	 */
	public boolean exists(String name) throws Exception{
		return find(name) != null;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"), result.getString("name"),
			result.getInt("precedence"), result.getInt("user_id"));
	}

	/**
	 * Insert someone in database, where 't' is future object
	 *
	 * @param row
	 * 		Some object, which implements CollageProtocol
	 * @throws Exception
	 */
	@Override
	public Row add(Row row) throws Exception {
		execute("INSERT INTO groups (name, user_id) VALUES (?, ?)",
			row.getName(), row.getUserID());
		row.changeID(last().getID());
		return row;
	}

	/**
	 * Find object in table by
	 * name or it's identifier
	 *
	 * @param name@return Founded object
	 * @throws Exception
	 */
	public Row find(String name) throws Exception {
		ResultSet rs = execute("SELECT * FROM groups WHERE name = ?",
			name);
		if (!rs.next()) {
			return null;
		}
		return createFromSet(rs);
	}

	public static class Row extends CortegeRow {

		/**
		 * Constructor with three parameters
		 *
		 * @param name Group's name
		 * @param precedence Group's precedence
		 */
		public Row(String name, int precedence, int userID) {
			this(0, name, precedence, userID);
		}

		/**
		 * Constructor with four parameters
		 *
		 * @param id Group's identifier
		 * @param name Group's name
		 * @param precedence Group's precedence
		 */
		public Row(int id, String name, int precedence, int userID) {
			super(id); this.precedence = precedence; this.name = name;
			this.userID = userID;
		}

		/**
		 * @return - Group's name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return - Group's precedence
		 */
		public int getPrecedence() {
			return precedence;
		}

		/**
		 * @return - Reference to user's identifier
		 */
		public int getUserID() {
			return userID;
		}

		private String name;
		private int precedence;
		private int userID;
	}
}

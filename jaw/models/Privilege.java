package models;

import Core.Environment;
import Sql.CortegeRow;
import Sql.Connection;
import Component.Model;

import java.sql.ResultSet;

/**
 * PrivilegeTable
 */
public class Privilege extends Model<Privilege.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment
	 * 		MySql's helper object
	 */
	public Privilege(Environment environment) {
		super(environment, "privilege");
	}

	/**
	 * @param result
	 * 		- Current cortege from query
	 * @return - Created row from bind
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"),
			result.getBoolean("flag"),
			result.getInt("user_id"));
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
		execute("INSERT INTO privilege (user_id, flag) VALUES(?, ?)",
				row.getUserID(), row.getFlag());
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
		ResultSet rs = execute("SELECT * FROM privilege WHERE name = ?",
			name);
		if (!rs.next()) {
			throw new Core.InternalError("PrivilegeModel/fetchByID() : 'Unable to fetchByID element "+name+"'");
		}
		return createFromSet(rs);
	}

	/**
	 * PrivilegeRow
	 */
	public class Row extends CortegeRow {

		/**
		 * @param flag - Privilege state
		 * @param userID - User identifier
		 */
		public Row(boolean flag, int userID) {
			this(0, flag, userID);
		}

		/**
		 * @param id - Identifier
		 * @param userID - User identifier
		 */
		public Row(int id, boolean flag, int userID) {
			super(id); this.flag = flag; this.userID = userID;
		}

		/**
		 * @return Privileges's user identification number
		 */
		public int getUserID() {
			return userID;
		}

		/**
		 * @return Flags for flag
		 */
		public boolean getFlag() {
			return flag;
		}

		private boolean flag;
		private int userID;
	}
}

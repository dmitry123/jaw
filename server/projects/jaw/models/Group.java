package models;

import Core.*;
import Core.InternalError;
import Sql.CortegeProtocol;
import Sql.CortegeRow;

import java.lang.Object;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-12-05
 */
public class Group extends Model<Group.Row> {
	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Group(Environment environment) {
		super(environment, "groups");
	}

	public ResultSet fetchByName(String name) throws InternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("groups")
			.where("name = ?")
			.execute(new Object[] { name })
			.select();
	}

	public ResultSet fetchGroupsByUser(Integer userID) throws InternalError, SQLException {
		return getConnection().command()
			.distinct("g.*")
			.from("users", "u")
			.join("employee", "e", "e.user_id = u.id")
			.join("employee_group", "eg", "eg.employee_id = e.id")
			.join("groups", "g", "g.id = eg.group_id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public ResultSet fetchGroupsByEmployee(Integer employeeID) throws InternalError, SQLException {
		return getConnection().command()
			.distinct("g.*")
			.from("employee", "e")
			.join("employee_group", "eg", "eg.employee_id = e.id")
			.join("groups", "g", "g.id = eg.group_id")
			.where("e.id = ?")
			.execute(new Object[] { employeeID })
			.select();
	}

	public Row bind(Integer groupID, Integer employeeID) throws InternalError, SQLException {
		getConnection().command()
			.insert("employee_group", "employee_id, group_id")
			.values("?, ?")
			.execute(new Object[] { employeeID, groupID })
			.insert();
		return null;
	}

	public Row bindWithGroupName(String groupName, Integer employeeID) throws InternalError, SQLException {
		ResultSet groupRow = fetchByName(groupName);
		if (groupRow.next()) {
			bind(groupRow.getInt("id"), employeeID);
		}
		return null;
	}

	public Row unbind(Integer groupID, Integer employeeID) throws InternalError, SQLException {
		getConnection().command()
			.delete("employee_group")
			.where("group_id = ?")
			.and("employee_id = ?")
			.execute(new Object[] { groupID, employeeID })
			.delete();
		return null;
	}

	public Row unbindWithGroupName(String groupName, Integer employeeID) throws InternalError, SQLException {
		ResultSet groupRow = fetchByName(groupName);
		if (groupRow.next()) {
			unbind(groupRow.getInt("id"), employeeID);
		}
		return null;
	}

	/**
	 *
	 */
	public static class Row extends CortegeRow {

		/**
		 *
		 * @param name
		 */
		public Row(String name) {
			this(0, name);
		}

		/**
		 *
		 * @param id
		 * @param name
		 */
		public Row(int id, String name) {
			super(id); this.name = name;
		}

		/**
		 *
		 * @return
		 */
		public String getName() {
			return name;
		}

		String name;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Core.InternalError
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Core.InternalError, SQLException {
		return new Row(result.getInt("id"), result.getString("name"));
	}
}

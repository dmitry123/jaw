package jaw.models;

import jaw.core.*;

import jaw.sql.CortegeProtocol;
import jaw.sql.CortegeRow;
import jaw.sql.CommandProtocol;

import java.lang.Object;
import java.lang.Override;
import java.sql.ResultSet;

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

	public ResultSet fetchByName(String name) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("groups")
			.where("name = ?")
			.execute(new Object[] { name })
			.select();
	}

	public ResultSet fetchGroupsByUser(Integer userID) throws Exception {
		return getConnection().createCommand()
			.distinct("g.*")
			.from("users as u")
			.join("employee as e", "e.user_id = u.id")
			.join("employee_group as eg", "eg.employee_id = e.id")
			.join("groups as g", "g.id = eg.group_id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public ResultSet fetchGroupsByEmployee(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.distinct("g.*")
			.from("employee as e")
			.join("employee_group as eg", "eg.employee_id = e.id")
			.join("groups as g", "g.id = eg.group_id")
			.where("e.id = ?")
			.execute(new Object[] { employeeID })
			.select();
	}

	public Row bind(Integer groupID, Integer employeeID) throws Exception {
		getConnection().createCommand()
			.insert("employee_group", "employee_id, group_id")
			.values("?, ?")
			.execute(new Object[] { employeeID, groupID })
			.insert();
		return null;
	}

	public Row bindWithGroupName(String groupName, Integer employeeID) throws Exception {
		ResultSet groupRow = fetchByName(groupName);
		if (groupRow.next()) {
			bind(groupRow.getInt("id"), employeeID);
		}
		return null;
	}

	public Row unbind(Integer groupID, Integer employeeID) throws Exception {
		getConnection().createCommand()
			.delete("employee_group")
			.where("group_id = ?")
			.andWhere("employee_id = ?")
			.execute(new Object[] { groupID, employeeID })
			.delete();
		return null;
	}

	public Row unbindWithGroupName(String groupName, Integer employeeID) throws Exception {
		ResultSet groupRow = fetchByName(groupName);
		if (groupRow.next()) {
			unbind(groupRow.getInt("id"), employeeID);
		}
		return null;
	}

	@Override
	public CommandProtocol getReferences() throws Exception {
		return getConnection().createCommand()
			.select("privilege.*, employee.*")
			.from("groups")
			.leftJoin("group_privilege", "group_privilege.group_id = groups.id")
			.join("privilege", "group_privilege.privilege_id = privilege.id")
			.join("employee_group", "employee_group.group_id = groups.id")
			.join("employee", "employee_group.employee_id = employee.id");
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
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"), result.getString("name"));
	}
}

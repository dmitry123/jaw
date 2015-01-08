package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CommandProtocol;
import jaw.Sql.CortegeKey;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.sql.ResultSet;

public class EmployeeGroup extends Model<EmployeeGroup.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public EmployeeGroup(Environment environment) {
		super(environment, "employee_group");
	}

	/**
	 * Override that method to return your own columns for fetchTable method
	 * @return - Command with your query
	 * @throws Exception
	 */
	@Override
	public CommandProtocol getTable() throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("employee_group")
			.join("employee", "employee_group.employee_id = employee.id")
			.join("groups", "employee_group.group_id = groups.id");
	}

	class Row extends CortegeRow {

		public Row(int id, int employeeID, int groupID) {
			super(id); this.employeeID = employeeID; this.groupID = groupID;
		}

		public int getEmployeeID() {
			return employeeID;
		}

		public int getGroupID() {
			return groupID;
		}

		private int employeeID;
		private int groupID;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"), result.getInt("employee_id"), result.getInt("group_id"));
	}
}

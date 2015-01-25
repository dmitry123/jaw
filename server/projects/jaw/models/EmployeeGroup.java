package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CommandProtocol;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.sql.ResultSet;

public class EmployeeGroup extends Model {

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
}

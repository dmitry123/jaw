package jaw.models;

import jaw.Core.*;

import jaw.Sql.CortegeKey;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CommandProtocol;

import java.sql.ResultSet;

public class Privilege extends Model {

	/**
	 * Basic constructor with helper and
	 * table's name as arguments
	 * @param environment - Current environment
	 */
	public Privilege(Environment environment) {
		super(environment, "privilege");
	}

	@Override
	public CommandProtocol getReferences() throws Exception {
		return getConnection().createCommand()
			.select("groups.*, employee.*")
			.from("privilege")
			.join("group_privilege", "group_privilege.privilege_id = privilege.id")
			.join("groups", "group_privilege.group_id = groups.id")
			.join("employee_group", "employee_group.group_id = groups.id")
			.join("employee", "employee_group.employee_id = employee.id");
	}
}

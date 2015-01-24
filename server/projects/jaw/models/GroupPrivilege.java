package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CommandProtocol;
import jaw.Sql.CortegeKey;
import jaw.Sql.CortegeProtocol;

import java.sql.ResultSet;

public class GroupPrivilege extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public GroupPrivilege(Environment environment) {
		super(environment, "group_privilege");
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
			.from("privilege")
			.join("group_privilege", "group_privilege.privilege_id = privilege.id")
			.join("groups", "group_privilege.group_id = groups.id");
	}
}

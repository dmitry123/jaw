package jaw.models;

import jaw.core.*;

import jaw.sql.CortegeKey;
import jaw.sql.CortegeProtocol;
import jaw.sql.CommandProtocol;

import java.sql.ResultSet;

public class Privilege extends Model<Privilege.Row> {

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

	public static class Row extends CortegeKey {

		public Row(String key, String name, String description) {
			super(key); this.name = name; this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		private String name;
		private String description;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
		return new Row(result.getString("id"), result.getString("name"), result.getString("description"));
	}
}

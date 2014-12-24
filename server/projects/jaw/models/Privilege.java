package models;

import Core.*;
import Sql.CortegeKey;
import Sql.CortegeProtocol;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Privilege extends Model<Privilege.Row> {

	/**
	 * Basic constructor with helper and
	 * table's name as arguments
	 * @param environment - Current environment
	 */
	public Privilege(Environment environment) {
		super(environment, "privilege");
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
	 * @throws Core.InternalError
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Core.InternalError, SQLException {
		return new Row(result.getString("id"), result.getString("name"), result.getString("description"));
	}
}

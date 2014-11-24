package models;

import Core.Environment;
import Sql.CortegeRow;
import Core.Model;

import java.sql.ResultSet;

/**
 * Created by Savonin on 2014-11-02
 */
public class Project extends Model<Project.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment
	 * 		MySql's helper object
	 */
	public Project(Environment environment) {
		super(environment, "project");
	}

	/**
	 * @param result
	 * 		- Current cortege from query
	 * @return - Created row from bind
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"),
			result.getString("name"),
			result.getInt("leader_id"),
			result.getInt("company_id"));
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
		execute("INSERT INTO project (name, company_id, leader_id) VALUES(?, ?, ?)",
				row.getName(), row.getCompanyID(), row.getLeaderID());
		return row;
	}

	/**
	 * Check project for existence by it's name
	 * and company's identifier
	 *
	 * @param name - Project's name
	 * @param companyID - Company's identifier
	 * @return - Found project or null
	 * @throws java.lang.Exception
	 */
	public boolean exists(String name, int companyID) throws Exception {
		return fetchByName(name, companyID) != null;
	}

	/**
	 * Fetch project by it's name and company's identifier
	 *
	 * @param name - Project's name
	 * @param companyID - Company's identifier
	 * @return - Found project or null
	 * @throws java.lang.Exception
	 */
	public Row fetchByName(String name, int companyID) throws Exception {
		ResultSet rs = execute("SELECT * FROM project WHERE name=? AND company_id=? LIMIT 1",
				name, companyID);
		if (rs.next()) {
			return createFromSet(rs);
		}
		return null;
	}

	public static class Row extends CortegeRow {

		/**
		 * @param name - Project's name
		 * @param leaderID - Leader's identifier
		 * @param companyID - Company's identifier
		 */
		public Row(String name, int leaderID, int companyID) {
			this(0, name, leaderID, companyID);
		}

		/**
		 * @param id - Row's identifier
		 * @param name - Project's name
		 * @param leaderID - Leader's identifier
		 * @param companyID - Company's identifier
		 */
		public Row(int id, String name, int leaderID, int companyID) {
			super(id); this.name = name; this.leaderID = leaderID;
				this.companyID = companyID;
		}

		/**
		 * @return - Get project's name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return - Get project's leader identifier
		 */
		public int getLeaderID() {
			return leaderID;
		}

		/**
		 * @return - Get project company's identifier
		 */
		public int getCompanyID() {
			return companyID;
		}

		private String name;
		private int leaderID;
		private int companyID;
	}
}

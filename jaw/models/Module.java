package models;

import Core.Environment;
import Sql.CortegeRow;
import Sql.Connection;
import Component.Model;

import java.sql.ResultSet;
import java.util.Vector;

/**
 * Created by Savonin on 2014-11-02
 */
public class Module extends Model<Module.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment - MySql's helper object
	 * 	@throws java.lang.Exception
	 */
	public Module(Environment environment) throws Exception {
		super(environment, "module");
	}

	/**
	 * @param result
	 * 		- Current cortege from query
	 * @return - Created row from bind
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"), result.getString("name"),
			result.getInt("project_id"));
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
		execute("INSERT INTO module (id, name, project_id) VALUES (?, ?, ?)",
			row.getID(), row.getName(), row.getProjectID());
		row.changeID(last().getID());
		return row;
	}

	/**
	 * Fetch vector with all employee, that take a part
	 * in that module development
	 *
	 * @param filter - Filter for employee
	 * @return - Result vector with all found employee
	 * @throws java.lang.Exception
	 */
	public Vector<Employee.Row> fetchEmployee(Filter<Employee.Row> filter) throws Exception {
		ResultSet resultSet = execute("SELECT e.* FROM module_employee_connector AS mc" +
			"JOIN employee AS e ON e.id = mc.employee_id" +
			"JOIN module   AS m ON e.id = mc.module_id");
		Vector<Employee.Row> result =
			new Vector<Employee.Row>(resultSet.getFetchSize());
		while (resultSet.next()) {
			Employee.Row r = employeeModel.createFromSet(
				resultSet);
			if (filter == null || filter.test(r)) {
				result.add(r);
			}
		}
		return result;
	}

	class Row extends CortegeRow {

		/**
		 * @param name - Module's name
		 * @param projectID - Module's project identifier
		 */
		public Row(String name, int projectID) {
			this(0, name, projectID);
		}

		/**
		 * @param id - Identifier
		 * @param name - Module's name
		 * @param projectID - Module's project identifier
		 */
		public Row(int id, String name, int projectID) {
			super(id); this.name = name; this.projectID = projectID;
		}

		/**
		 * @return - Module's name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return - Reference to module's project
		 */
		public int getProjectID() {
			return projectID;
		}

		String name;
		int projectID;
	}

	/**
	 * Employee Model
	 */
	private Employee employeeModel
		= new Employee(getEnvironment());
}

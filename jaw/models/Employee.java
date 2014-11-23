package models;

import Core.Environment;
import Core.InternalError;
import Sql.CortegeRow;
import Sql.Connection;
import Component.Model;

import java.sql.ResultSet;
import java.util.Vector;

/**
 * Created by Savonin on 2014-11-02
 */
public class Employee extends Model<Employee.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment
	 * 		MySql's helper object
	 * 	@throws java.lang.Exception
	 */
	public Employee(Environment environment) throws Exception {
		super(environment, "employee");
	}

	/**
	 * @param result
	 * 		- Current cortege from query
	 * @return - Created row from bind
	 * @throws java.lang.Exception
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(
			result.getInt("id"),
			result.getString("name"),
			result.getString("surname"),
			result.getInt("user_id"),
			result.getInt("manager_id"),
			result.getInt("director_id"),
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
		execute("INSERT INTO employee (name, surname, user_id, manager_id, director_id) VALUES(?, ?, ?, ?, ?)",
			row.getName(), row.getSurname(), row.getUserID(), row.getManagerID(), row.getDirectorID());
		row.changeID(last().getID());
		return row;
	}

	/**
	 * Fetch user from employee's reference
	 *
	 * @return - Found user
	 * @throws java.lang.Exception
	 */
	public User.Row fetchUser(Row row) throws Exception {
		ResultSet resultSet = execute("SELECT u.* FROM employee AS e" +
			"JOIN user AS u ON e.user_id = u.id LIMIT 1");
		if (!resultSet.next()) {
			throw new InternalError("EmployeeModel/add() : \"Unable to get user from database (Wtf?)\"");
		}
		return userModel.createFromSet(resultSet);
	}

	/**
	 * Fetch user from employee's reference
	 *
	 * @return - Found user
	 * @throws java.lang.Exception
	 */
	public Row fetchByUserID(int userID) throws Exception {
		ResultSet resultSet = execute("SELECT * FROM employee " +
			"WHERE user_id=?", userID);
		if (!resultSet.next()) {
			return null;
		}
		return createFromSet(resultSet);
	}

	/**
	 * Fetch manager from employee's reference
	 *
	 * @param employee - Employee
	 * @return - Found manager
	 * @throws java.lang.Exception
	 */
	public Row fetchManager(Row employee) throws Exception {
		ResultSet resultSet = execute("SELECT e.* FROM employee AS e" +
				"WHERE e.manager_id = ?", employee.getDirectorID());
		if (!resultSet.next()) {
			throw new InternalError("EmployeeModel/add() : \"Unable to get manager from database (Wtf?)\"");
		}
		return createFromSet(resultSet);
	}

	/**
	 * Fetch director from employee's reference
	 *
	 * @param employee - Employee
	 * @return - Found director
	 * @throws java.lang.Exception
	 */
	public Row fetchDirector(Row employee) throws Exception {
		ResultSet resultSet = execute("SELECT e.* FROM employee AS e" +
			"WHERE e.director_id = ?", employee.getDirectorID());
		if (!resultSet.next()) {
			throw new Core.InternalError("EmployeeModel/add() : \"Unable to get director from database (Wtf?)\"");
		}
		return createFromSet(resultSet);
	}

	/**
	 * Fetch all employee's modules
	 *
	 * @param filter - Module's filter
	 * @return - Found modules objects
	 * @throws Exception
	 */
	public Vector<Module.Row> fetchModules(Row employee, Filter<Module.Row> filter) throws Exception {
		ResultSet resultSet = execute("SELECT m.* FROM module_employee_connector AS c" +
			"JOIN employee AS e ON c.employee_id = e.id" +
			"JOIN module   AS m ON c.module_id   = m.id WHERE e.id = ?", employee.getID());
		Vector<Module.Row> result = new Vector<Module.Row>(
			resultSet.getFetchSize());
		Module moduleModel
				= new Module(getEnvironment());
		while (resultSet.next()) {
			Module.Row r = moduleModel.createFromSet(resultSet);
			if (filter == null || filter.test(r)) {
				result.add(r);
			}
		}
		return result;
	}

	/**
	 * Fetch all employee's modules
	 *
	 * @return - Found modules objects
	 * @throws Exception
	 */
	public Vector<Module.Row> fetchModules(Row employee) throws Exception {
		return fetchModules(employee, null);
	}

	/**
	 * Fetch all employee's projects
	 *
	 * @param filter - Project's filter
	 * @return - Found modules objects
	 * @throws Exception
	 */
	public Vector<Project.Row> fetchProjects(Row employee, Filter<Project.Row> filter) throws Exception {
		ResultSet resultSet = execute("SELECT p.* FROM project_employee_connector AS c" +
				"JOIN employee AS e ON c.employee_id = e.id" +
				"JOIN project  AS p ON c.project_id  = p.id WHERE e.id = ?\", employee.getID()");
		Vector<Project.Row> result = new Vector<Project.Row>(
				resultSet.getFetchSize());
		while (resultSet.next()) {
			Project.Row r = projectModel.createFromSet(resultSet);
			if (filter == null || filter.test(r)) {
				result.add(r);
			}
		}
		return result;
	}

	/**
	 * Fetch all employee's projects
	 *
	 * @return - Found modules objects
	 * @throws Exception
	 */
	public Vector<Project.Row> fetchProjects(Row employee) throws Exception {
		return fetchProjects(employee, null);
	}

	/**
	 * Change employee's company
	 *
	 * @param row - Employee's cortege
	 * @param companyID - Company's identifier
	 */
	public void changeCompany(Row row, int companyID) throws Exception {
		execute("UPDATE employee SET company_id = " + companyID + " WHERE id = ?",
			row.getID());
		row.companyID = companyID;
	}

	/**
	 * Row
	 */
	public static class Row extends CortegeRow {

		/**
		 * @param name - Employee's first name
		 * @param surname - Employee's surname
		 * @param userID - Reference to user's identifier
		 * @param directorID - Reference to director's identifier
		 */
		public Row(String name, String surname, int userID, int managerID, int directorID, int companyID) {
			this(0, name, surname, userID, managerID, directorID, companyID);
		}

		/**
		 * @param id - Primary key
		 * @param name - Employee's first name
		 * @param surname - Employee's surname
		 * @param userID - Reference to user's identifier
		 * @param directorID - Reference to director's identifier
		 */
		public Row(int id, String name, String surname, int userID, int managerID, int directorID, int companyID) {
			super(id); this.name = name; this.surname = surname; this.userID = userID; this.managerID = managerID;
				this.directorID = directorID; this.companyID = companyID;
		}

		/**
		 * @param companyID - New company's identifier
		 */
		public void changeCompanyID(int companyID) {
			this.companyID = companyID;
		}

		/**
		 * @return - Employee's name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return - Employee's surname
		 */
		public String getSurname() {
			return surname;
		}

		/**
		 * @return - Employee' user identifier
		 */
		public int getUserID() {
			return userID;
		}

		/**
		 * @return - Employee manager's identifier
		 */
		public int getManagerID() {
			return managerID;
		}

		/**
		 * @return - Employee director's identifier
		 */
		public int getDirectorID() {
			return directorID;
		}

		/**
		 * @return - Employee company's identifier
		 */
		public int getCompanyID() {
			return companyID;
		}

		private String name;
		private String surname;

		private int userID;
		private int managerID;
		private int directorID;
		private int companyID;
	}

	private User userModel
			= new User(getEnvironment());

	private Project projectModel
			= new Project(getEnvironment());
}

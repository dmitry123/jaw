package models;

import Core.*;
import Core.ExternalError;
import Core.InternalError;
import Sql.CortegeProtocol;
import Sql.CortegeRow;

import java.lang.Object;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-12-05
 */
public class Employee extends Model<Employee.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Employee(Environment environment) {
		super(environment, "employee");
	}

	public Row register(String name, String surname, String patronymic, Integer userID, Integer managerID, Integer directorID, Integer companyID) throws InternalError, SQLException, ExternalError {
		getConnection().command()
			.insert("employee", "name, surname, user_id, manager_id, director_id, company_id, patronymic")
			.values("?, ?, ?, ?, ?, ?, ?")
			.execute(new Object[] { name, surname, userID, managerID, directorID, companyID, patronymic })
			.insert();
		return last();
	}

	public ResultSet fetchByCompanyID(Integer companyID) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("employee")
			.where("company_id = ?")
			.execute(new Object[] { companyID })
			.select();
	}

	public ResultSet fetchByUserID(Integer userID) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("employee")
			.where("user_id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public ResultSet fetchPrivilege(Integer employeeID, String privilegeKey) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("privilege", "p")
			.join("groups", "g", "p.group_id = g.id")
			.join("employee_group", "eg", "eg.group_id = g.id")
			.where("p.id = ?")
			.and("eg.employee_id = ?")
			.execute(new Object[] { privilegeKey, employeeID })
			.select();
	}

	public ResultSet fetchByUserAndCompanyID(Integer userID, Integer companyID) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.distinct("*")
			.from("employee")
			.where("user_id = ?")
			.and("company_id = ?")
			.execute(new Object[] { userID, companyID })
			.select();
	}

	public ResultSet fetchGroups(Integer employeeID) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("g.*")
			.from("employee", "e")
			.join("employee_group", "eg", "eg.employee_id = e.id")
			.join("groups", "g", "g.id = eg.group_id")
			.where("e.id = ?")
			.execute(new Object[] { employeeID })
			.select();
	}

	public ResultSet fetchCompaniesByUserID(Integer userID) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users", "u")
			.join("employee", "e", "e.user_id = u.id")
			.join("company", "c", "c.id = e.company_id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public ResultSet fetchProjectsByUserID(Integer userID) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("e.*, p.*, r.*")
			.from("project", "p")
			.join("product", "r", "r.id = p.product_id")
			.join("product_employee", "pe", "pe.product_id = r.id")
			.join("employee", "e", "e.id = pe.employee_id")
			.join("users", "u", "e.user_id = u.id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public static class Row extends CortegeRow {

		public Row(String name, String surname, String patronymic, int userID, int managerID, int directorID, int companyID) {
			this(0, name, surname, patronymic, userID, managerID, directorID, companyID);
		}

		public Row(int id, String name, String surname, String patronymic, int userID, int managerID, int directorID, int companyID) {
			super(id); this.name = name; this.surname = surname; this.patronymic = patronymic; this.userID = userID;
				this.managerID = managerID; this.directorID = directorID; this.companyID = companyID;
		}

		public String getName() {
			return name;
		}

		public String getSurname() {
			return surname;
		}

		public String getPatronymic() {
			return patronymic;
		}

		public int getUserID() {
			return userID;
		}

		public int getManagerID() {
			return managerID;
		}

		public int getDirectorID() {
			return directorID;
		}

		public int getCompanyID() {
			return companyID;
		}

		String name;
		String surname;
		String patronymic;

		int userID;
		int managerID;
		int directorID;
		int companyID;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Core.InternalError
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Core.InternalError, ExternalError, SQLException {
		return new Row(
			result.getInt("id"),
			result.getString("name"),
			result.getString("surname"),
			result.getString("patronymic"),
			result.getInt("user_id"),
			result.getInt("manager_id"),
			result.getInt("director_id"),
			result.getInt("company_id")
		);
	}
}

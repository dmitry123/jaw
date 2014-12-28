package models;

import Core.*;

import Sql.CommandProtocol;
import Sql.CortegeProtocol;
import Sql.CortegeRow;

import java.lang.Object;
import java.lang.Override;
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

	public Row register(String name, String surname, String patronymic, Integer userID, Integer managerID, Integer directorID, Integer companyID) throws Exception {
		getConnection().createCommand()
			.insert("employee", "name, surname, user_id, manager_id, director_id, company_id, patronymic")
			.values("?, ?, ?, ?, ?, ?, ?")
			.execute(new Object[] { name, surname, userID, managerID, directorID, companyID, patronymic })
			.insert();
		return last();
	}

	public ResultSet fetchByCompanyID(Integer companyID) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("employee")
			.where("company_id = ?")
			.execute(new Object[] { companyID })
			.select();
	}

	public ResultSet fetchByUserID(Integer userID) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("employee")
			.where("user_id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public ResultSet fetchPrivilegeByUserID(Integer userID, String privilegeID) throws Exception {
		return getConnection().createCommand()
			.select("p.*")
			.from("users as u")
			.join("employee as e", "e.user_id = u.id")
			.join("employee_group as eg", "eg.employee_id = e.id")
			.join("group_privilege as gp", "gp.group_id = eg.group_id")
			.join("privilege as p", "gp.privilege_id = p.id")
			.where("u.id = ? and p.id = ?")
			.execute(new Object[]{ userID, privilegeID })
			.select();
	}

	public ResultSet fetchPrivilege(Integer employeeID, String privilegeID) throws Exception {
		return getConnection().createCommand()
			.select("p.*")
			.from("privilege as p")
			.join("group_privilege as gp", "gp.privilege_id = p.id")
			.join("employee_group as eg", "eg.group_id = gp.group_id")
			.where("p.id = ?")
			.andWhere("eg.employee_id = ?")
			.execute(new Object[] { privilegeID, employeeID })
			.select();
	}

	public ResultSet fetchByUserAndCompanyID(Integer userID, Integer companyID) throws Exception {
		return getConnection().createCommand()
			.distinct("*")
			.from("employee")
			.where("user_id = ?")
			.andWhere("company_id = ?")
			.execute(new Object[] { userID, companyID })
			.select();
	}

	public ResultSet fetchGroups(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("g.*")
			.from("employee as e")
			.join("employee_group as eg", "eg.employee_id = e.id")
			.join("groups as g", "g.id = eg.group_id")
			.where("e.id = ?")
			.execute(new Object[] { employeeID })
			.select();
	}

	public ResultSet fetchCompaniesByUserID(Integer userID) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("users as u")
			.join("employee as e", "e.user_id = u.id")
			.join("company as c", "c.id = e.company_id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public ResultSet fetchProjectsByUserID(Integer userID) throws Exception {
		return getConnection().createCommand()
			.distinct("e.*, p.*, r.*")
			.from("project as p")
			.join("product as r", "r.id = p.product_id")
			.join("product_employee as pe", "pe.product_id = r.id")
			.join("employee as e", "e.id = pe.employee_id")
			.join("users as u", "e.user_id = u.id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	@Override
	public CommandProtocol getResultSetForTable() throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("employee")
			.join("users", "employee.user_id = users.id")
			.join("company", "employee.company_id = company.id");
	}

	@Override
	public CommandProtocol getReferences() throws Exception {
		return getConnection().createCommand()
			.select("users.*, product.*, groups.*, privilege.*")
			.from("employee")
			.join("users", "employee.user_id = users.id")
			.join("product_employee", "product_employee.employee_id = employee.id")
			.join("product", "product_employee.product_id = product.id")
			.join("employee_group", "employee_group.employee_id = employee.id")
			.join("groups", "employee_group.group_id = groups.id")
			.join("group_privilege", "group_privilege.group_id = groups.id")
			.join("privilege", "privilege.id = group_privilege.privilege_id");
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
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
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

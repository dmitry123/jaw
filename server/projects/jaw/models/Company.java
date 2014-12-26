package models;

import Core.*;
import Core.InternalError;
import Sql.CommandProtocol;
import Sql.CortegeProtocol;
import Sql.CortegeRow;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.lang.Integer;
import java.lang.Object;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-12-05
 */
public class Company extends Model<Company.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Company(Environment environment) {
		super(environment, "company");
	}

	public boolean exists(String name) throws InternalError, SQLException {
		ResultSet resultSet = getConnection().createCommand()
			.select("*")
			.from("company")
			.where("name = ?")
			.execute(new Object[] { name })
			.select();
		return resultSet.next();
	}

	public ResultSet fetchByName(String name) throws InternalError, SQLException {
		return getConnection().createCommand()
			.select("*")
			.from("company")
			.where("lower(name) = lower(?)")
			.execute(new Object[] { name })
			.select();
	}

	public ResultSet fetchByUserID(Integer userID) throws InternalError, SQLException {
		return getConnection().createCommand()
			.select("c.*")
			.from("company as c")
			.join("employee as e", "e.company_id = c.id")
			.join("users as u", "e.user_id = u.id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	public ResultSet fetchEmployees(Integer companyID) throws InternalError, SQLException {
		return getConnection().createCommand()
			.select("e.*")
			.from("employee as e")
			.where("company_id = ?")
			.execute(new Object[] { companyID })
			.select();
	}

	public CortegeRow updateDirector(String name, Integer director) throws InternalError, SQLException {
		getConnection().createCommand()
			.update("company")
			.set("director_id = ?")
			.where("name = ?")
			.execute(new Object[] { director, name })
			.update();
		return null;
	}

	public CortegeRow register(String name) throws InternalError, SQLException {
		if (exists(name)) {
			throw new InternalError(
				"Company/register() : \"Company with that name already registered\""
			);
		}
		getConnection().createCommand()
			.insert("company", "name")
			.values("?")
			.execute(new Object[]{name})
			.insert();
		return last();
	}

	public Row delete(Integer projectID) throws InternalError, SQLException {
		getConnection().createCommand()
			.delete("project")
			.where("id = ?")
			.execute(new Object[] { projectID })
			.delete();
		return null;
	}

	@Override
	public CommandProtocol getResultSetForTable() throws InternalError, SQLException {
		return getConnection().createCommand()
			.distinct("*")
			.from("company")
			.join("employee", "company.director_id = employee.id");
	}

	public static class Row extends CortegeRow {

		/**
		 *
		 * @param name
		 */
		public Row(String name, int directorID) {
			this(0, name, directorID);
		}

		/**
		 * @param id Identifier
		 */
		public Row(int id, String name, int directorID) {
			super(id); this.name = name; this.directorID = directorID;
		}

		/**
		 *
		 * @return
		 */
		public String getName() {
			return name;
		}

		/**
		 *
		 * @return
		 */
		public int getDirectorID() {
			return directorID;
		}

		private String name;

		private int directorID;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Core.InternalError
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Core.InternalError, SQLException {
		return new Row(result.getInt("id"), result.getString("name"), result.getInt("director_id"));
	}
}

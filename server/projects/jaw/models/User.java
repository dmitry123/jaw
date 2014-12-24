package models;

import Core.*;
import Core.InternalError;
import Sql.Command;
import Sql.CortegeRow;

import javax.xml.transform.Result;
import java.sql.*;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * UserTable
 */
public class User extends Model<User.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment
	 * 		MySql's helper object
	 */
    public User(Environment environment) throws Exception {
        super(environment, "users");
    }

	public boolean exists(String login) throws InternalError, SQLException {
		try {
			return execute("SELECT * FROM users WHERE login=?", login)
					.next();
		} catch (SQLException e) {
			throw new InternalError("User/exists() : \"Unresolved user's login (" + login + ")\"");
		}
	}

	@Override
	public Row createFromSet(ResultSet result) throws InternalError, SQLException {
		return new Row(result.getInt("id"), result.getString("login"),
			result.getString("hash"), result.getString("email"));
	}

	public Row register(String login, String hash, String email) throws InternalError, SQLException {
		getConnection().command()
			.insert("users", "login, hash, email")
			.values("?, ?, ?")
			.execute(new Object[] { login, hash, email })
			.insert();
		return last();
	}

	public ResultSet fetchByLogin(String login) throws InternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users")
			.where("LOWER(login) = LOWER(?)")
			.execute(new Object[] { login })
			.select();
	}

	public ResultSet fetchByLoginAndHash(String login, String hash) throws InternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users")
			.where("LOWER(login) = LOWER(?)")
			.and("hash = ?")
			.execute(new Object[] { login, hash })
			.select();
	}

	public ResultSet fetchByMail(String email) throws InternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users")
			.where("email = ?")
			.execute(new Object[] { email })
			.select();
	}

	/**
	 * UserRow
	 */
	public static class Row extends Core.User {

		/**
		 * @param login User's Name
		 * @param hash Password hash
		 */
		public Row(String login, String hash, String email) {
			super(0, login, hash); this.email = email;
		}

		/**
		 * @param id - User's ID
		 * @param login - User's Name
		 * @param hash - Password hash
		 */
		public Row(int id, String login, String hash, String email) {
			super(id, login, hash); this.email = email;
		}

		/**
		 * @return - User's email
		 */
		public String getEmail() {
			return email;
		}

		private String email;
	}
}

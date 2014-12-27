package models;

import Core.*;

import Sql.CommandProtocol;

import java.lang.Override;
import java.sql.*;
import java.sql.SQLException;

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
    public User(Environment environment) throws java.lang.Exception {
        super(environment, "users");
    }

	public boolean exists(String login) throws Exception {
		try {
			return execute("SELECT * FROM users WHERE login=?", login)
					.next();
		} catch (SQLException e) {
			throw new Exception("User/exists() : \"Unresolved user's login (" + login + ")\"");
		}
	}

	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"), result.getString("login"),
			result.getString("hash"), result.getString("email"));
	}

	public Row register(String login, String hash, String email) throws Exception {
		getConnection().createCommand()
			.insert("users", "login, hash, email")
			.values("?, ?, ?")
			.execute(new Object[] { login, hash, email })
			.insert();
		return null;
	}

	public ResultSet fetchByLogin(String login) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("users")
			.where("LOWER(login) = LOWER(?)")
			.execute(new Object[] { login })
			.select();
	}

	public ResultSet fetchByLoginAndHash(String login, String hash) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("users")
			.where("LOWER(login) = LOWER(?)")
			.andWhere("hash = ?")
			.execute(new Object[] { login, hash })
			.select();
	}

	public ResultSet fetchByMail(String email) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("users")
			.where("email = ?")
			.execute(new Object[] { email })
			.select();
	}

	@Override
	public CommandProtocol getReferences() throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("users")
			.join("employee", "employee.user_id = users.id");
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

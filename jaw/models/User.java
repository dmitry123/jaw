package models;

import Core.Environment;
import Core.InternalError;
import Sql.CortegeRow;
import Sql.Connection;
import Component.Model;

import java.sql.*;

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

	/**
	 * @param login - User's login
	 * @return - True if user with that login
	 * 		already exists
	 * 	@throws InternalError
	 */
	public boolean exists(String login) throws InternalError {
		try {
			return execute("SELECT * FROM users WHERE login=?", login)
					.next();
		} catch (SQLException e) {
			throw new InternalError("User/exists() : \"Unresolved user's login (" + login + ")\"");
		}
	}

	/**
	 * @param result
	 * 		- Current cortege from query
	 * @return - Created row from bind
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"), result.getString("login"),
			result.getString("hash"));
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
        if (fetchByLogin(row.getLogin()) != null) {
            throw new InternalError("UserTable/add : 'User with that name already exists (" + row.getLogin() + ")'");
        }
		execute("INSERT INTO users (login, hash) VALUES(?, ?)",
			row.getLogin(), row.getHash());
		row.changeID(last().getID());
		return row;
    }

	/**
	 * Find object in table by
	 * login or it's identifier
	 *
	 * @param login - User's login
	 * @return Founded object
	 * @throws Exception
	 */
	public Row fetchByLogin(String login) throws Exception {
		ResultSet rs = execute("SELECT * FROM users WHERE login = ?",
				login);
		if (!rs.next()) {
			return null;
		}
		return createFromSet(rs);
	}

	/**
	 * UserRow
	 */
	public static class Row extends CortegeRow {

		/**
		 * @param login User's Name
		 * @param passwordHash Password hash
		 */
		public Row(String login, String passwordHash) {
			this(0, login, passwordHash);
		}

		/**
		 * @param id - User's ID
		 * @param login - User's Name
		 * @param passwordHash - Password hash
		 */
		public Row(int id, String login, String passwordHash) {
			super(id); this.login = login; this.passwordHash = passwordHash;
		}

		/**
		 * @return User's name
		 */
		public String getLogin() {
			return login;
		}

		/**
		 * @return User's password
		 */
		public String getHash() {
			return passwordHash;
		}

		/**
		 * User's Name
		 */
		private String login;

		/**
		 * Password Hash
		 */
		private String passwordHash;
	}

	/**
	 * Group Table
	 */
	private Group groupModel
		= new Group(getEnvironment());
}

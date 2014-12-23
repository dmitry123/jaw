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

	/**
	 * @param login - User's login
	 * @return - True if user with that login
	 * 		already exists
	 * 	@throws InternalError
	 */
	public boolean exists(String login) throws InternalError, ExternalError, SQLException {
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
	public Row createFromSet(ResultSet result) throws InternalError, ExternalError, SQLException {
		return new Row(result.getInt("id"), result.getString("login"),
			result.getString("hash"), result.getString("email"));
	}

	/**
	 *
	 * @param login
	 * @param hash
	 * @param email
	 * @return
	 * @throws InternalError
	 * @throws ExternalError
	 * @throws SQLException
	 */
	public Row register(String login, String hash, String email) throws InternalError, ExternalError, SQLException {
		if (exists(login)) {
			throw new ExternalError(ExternalError.Code.UserAlreadyRegistered);
		}
		getConnection().command()
			.insert("users", "login, hash, email")
			.values("?, ?, ?")
			.execute(new Object[] { login, hash, email })
			.insert();
		return last();
	}

	/**
	 * Find object in table by
	 * login or it's identifier
	 *
	 * @param login - User's login
	 * @return Founded object
	 * @return
	 * @throws InternalError
	 * @throws ExternalError
	 * @throws SQLException
	 */
	public ResultSet fetchByLogin(String login) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users")
			.where("LOWER(login) = LOWER(?)")
			.execute(new Object[] { login })
			.select();
	}

	/**
	 *
	 * @param login
	 * @param hash
	 * @return
	 * @throws InternalError
	 * @throws ExternalError
	 * @throws SQLException
	 */
	public ResultSet fetchByLoginAndHash(String login, String hash) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users")
			.where("LOWER(login) = LOWER(?)")
			.and("hash = ?")
			.execute(new Object[] { login, hash })
			.select();
	}

	/**
	 *
	 * @param email
	 * @return
	 * @throws InternalError
	 * @throws ExternalError
	 * @throws SQLException
	 */
	public ResultSet fetchByMail(String email) throws InternalError, ExternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users")
			.where("email = ?")
			.execute(new Object[] { email })
			.select();
	}
	/**
	 * Fetch all elements from table
	 * @return - List with elements
	 * @throws InternalError
	 * @throws ExternalError
	 * @throws SQLException
	 */
	public Vector<LinkedHashMap<String, String>> fetchTable(Integer page, Integer limit) throws InternalError, ExternalError, SQLException {
		ResultSet resultSet = getConnection().command()
			.select("*")
			.from("users", "u")
			.join("employee", "e", "e.user_id = u.id")
			.execute()
			.select();
		Vector<LinkedHashMap<String, String>> result
			= new Vector<>();
		while (resultSet.next()) {
			LinkedHashMap<String, String> map =
				buildMap(resultSet);
		}
		return result;
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

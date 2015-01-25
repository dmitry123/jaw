package jaw.models;

import jaw.Core.*;

import jaw.Sql.CommandProtocol;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.lang.Override;
import java.sql.*;

/**
 * UserTable
 */
public class User extends Model {

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
		return getConnection().createCommand()
			.select("id")
			.from(getTableName())
			.where("login = ?")
			.execute(login)
			.select()
			.next();
	}

	public CortegeProtocol register(String login, String hash, String email) throws Exception {
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
}

package models;

import Core.*;
import Core.InternalError;
import Sql.CortegeProtocol;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-11-24
 */
public class Index extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Index(Environment environment) {
		super(environment, null);
	}

	/**
	 *
	 * @param userID
	 * @return
	 */
	public ResultSet fetchUserEmployee(Integer userID) throws InternalError, SQLException {
		return getConnection().command()
			.select("*")
			.from("users", "u")
			.join("employee", "e", "e.user_id = u.id")
			.join("company", "c", "c.id = e.company_id")
			.where("u.id = ?")
			.execute(new Object[] { userID })
			.select();
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Core.InternalError
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws InternalError, ExternalError, SQLException {
		return null;
	}
}

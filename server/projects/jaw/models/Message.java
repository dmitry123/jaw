package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.lang.Exception;
import java.lang.Integer;
import java.sql.ResultSet;

public class Message extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Message(Environment environment) {
		super(environment, "message");
	}

	public ResultSet fetchByEmployeeID(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("m.*, e.*")
			.from("message as m")
			.join("employee as e", "e.id = m.receiver_id")
			.where("e.id = ?")
			.execute(employeeID)
			.select();
	}
}

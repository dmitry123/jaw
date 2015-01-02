package models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.sql.ResultSet;
import java.util.Map;

public class Request extends Model<Request.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Request(Environment environment) {
		super(environment, "request");
	}

	/**
	 * Insert item to database from hash map
	 * @param values - Map with values
	 * @return - True if has been inserted successfully
	 * @throws Exception
	 */
	@Override
	public boolean insert(Map<String, String> values) throws Exception {
		return register(
			Integer.parseInt(values.get("receiverID")),
			Integer.parseInt(values.get("senderID")),
			Integer.parseInt(values.get("productID")),
			values.get("privilegeID"),
			values.get("message")
		);
	}

	public boolean register(Integer receiverID, Integer senderID, Integer productID, String privilegeID, String message) throws Exception {
		return getConnection().createCommand()
			.insert("request", "receiver_id, sender_id, product_id, privilege_id, message")
			.values("?, ?, ?, ?, ?")
			.execute(receiverID, senderID, productID, privilegeID, message)
			.insert();
	}

	public ResultSet fetchByEmployeeID(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("r.*, e.*")
			.from("request as r")
			.where("r.receiver_id = ?")
			.join("employee as e", "e.id = r.sender_id")
			.execute(employeeID)
			.select();
	}

	public ResultSet fetchInfo(Integer requestID) throws Exception {
		return getConnection().createCommand()
			.select("r.*, e.*, p.*, c.*, pr.*, u.*")
			.from("request as r")
			.join("employee as e", "e.id = r.sender_id")
			.join("employee as e2", "e2.id = r.receiver_id")
			.join("company as c", "c.id = e2.company_id")
			.leftJoin("product as p", "p.id = r.product_id")
			.join("privilege as pr", "pr.id = r.privilege_id")
			.join("users as u", "u.id = e.user_id")
			.where("r.id = ?")
			.execute(requestID)
			.select();
	}

	public ResultSet fetchByCompanyID(Integer companyID) throws Exception {
		return getConnection().createCommand()
			.select("r.*")
			.from("request as r")
			.join("employee as e", "r.employee_id = e.id")
			.where("e.company_id = ?")
			.execute(companyID)
			.select();
	}

	public static class Row extends CortegeRow {

		public Row(int id, int receiverID, int senderID, int productID, String privilegeID, String message) {
			super(id);
			this.receiverID = receiverID;
			this.senderID = senderID;
			this.productID = productID;
			this.privilegeID = privilegeID;
			this.message = message;
		}

		public int getReceiverID() {
			return receiverID;
		}

		public int getSenderID() {
			return senderID;
		}

		public int getProductID() {
			return productID;
		}

		public String getPrivilegeID() {
			return privilegeID;
		}

		public String getMessage() {
			return message;
		}

		private int receiverID;
		private int senderID;
		private int productID;
		private String privilegeID;
		private String message;
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
			result.getInt("receiver_id"),
			result.getInt("sender_id"),
			result.getInt("product_id"),
			result.getString("privilege_id"),
			result.getString("message")
		);
	}
}

package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.lang.Exception;
import java.lang.Integer;
import java.sql.ResultSet;

public class Message extends Model<Message.Row> {

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

	public static class Row extends CortegeRow {

		public Row(int id, int senderID, int receiverID, String message) {
			super(id);
			this.senderID = senderID;
			this.receiverID = receiverID;
			this.message = message;
		}

		public int getSenderID() {
			return senderID;
		}

		public int getReceiverID() {
			return receiverID;
		}

		public String getMessage() {
			return message;
		}

		private int senderID;
		private int receiverID;
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
			result.getInt("sender_id"),
			result.getInt("receiver_id"),
			result.getString("message")
		);
	}
}

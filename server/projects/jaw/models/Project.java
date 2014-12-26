package models;

import Core.*;
import Core.InternalError;
import Sql.CortegeProtocol;
import Sql.CortegeRow;

import java.lang.Object;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-12-05
 */
public class Project extends Model<Project.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Project(Environment environment) {
		super(environment, "project");
	}

	public Row register(String name, Integer leaderID, Integer companyID, Integer creatorID) throws InternalError, SQLException {
		Model productModel = getEnvironment().getModelManager().get("Product");
		CortegeProtocol productProtocol = productModel.fetchRow("register", name, companyID, creatorID, 0);
		getConnection().createCommand()
			.insert("project", "leader_id, product_id")
			.values("?, ?")
			.execute(new Object[] { leaderID, productProtocol.getID() })
			.insert();
		return last();
	}

	public Row delete(Integer projectID) throws InternalError, SQLException {
		Model productModel = getEnvironment().getModelManager().get("Product");
		ResultSet projectSet = getConnection().createCommand()
			.select("*")
			.from("project")
			.where("id = ?")
			.execute(new Object[] { projectID })
			.select();
		if (!projectSet.next()) {
			return null;
		}
		int productID = projectSet.getInt("product_id");
		getConnection().createCommand()
			.delete("project")
			.where("id = ?")
			.execute(new Object[] { projectID })
			.delete();
		productModel.fetchRow("delete", productID);
		return null;
	}

	/**
	 *
	 */
	public static class Row extends CortegeRow {

		/**
		 * @param id Identifier
		 */
		public Row(int id) {
			super(id);
		}

		/**
		 *
		 * @return
		 */
		public int getLeaderID() {
			return leaderID;
		}

		/**
		 *
		 * @return
		 */
		public int getProductID() {
			return productID;
		}

		private int leaderID;
		private int productID;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Core.InternalError
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Core.InternalError, SQLException {
		return null;
	}
}

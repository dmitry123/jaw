package models;

import Core.*;

import Sql.CortegeProtocol;
import Sql.CortegeRow;

import java.lang.Object;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-12-05
 */
public class Product extends Model<Product.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Product(Environment environment) {
		super(environment, "product");
	}

	public Row register(String name, Integer companyID, Integer creatorID, Integer parentID) throws Exception{
		getConnection().createCommand()
			.insert("product", "name, company_id, creator_id, parent_id")
			.values("?, ?, ?, ?")
			.execute(new Object[] { name, companyID, creatorID, parentID })
			.insert();
		return null;
	}

	public Row delete(Integer productID) throws Exception {
		getConnection().createCommand()
			.delete("product")
			.where("id = ?")
			.execute(new Object[] { productID })
			.delete();
		return null;
	}

	public Row bind(Integer productID, Integer employeeID) throws Exception {
		getConnection().createCommand()
			.insert("product_employee", "product_id, employee_id")
			.values("?, ?")
			.execute(new Object[] { productID, employeeID })
			.insert();
		return null;
	}

	public Row unbind(Integer productID, Integer employeeID) throws Exception {
		getConnection().createCommand()
			.delete("product_employee")
			.where("product_id = ?")
			.andWhere("employee_id = ?")
			.execute(new Object[] { productID, employeeID })
			.delete();
		return null;
	}

	/**
	 *
	 */
	public static class Row extends CortegeRow {

		/**
		 *
		 * @param name
		 * @param created
		 * @param companyID
		 * @param creatorID
		 * @param parentID
		 */
		public Row(String name, Timestamp created, int companyID, int creatorID, int parentID) {
			this(0, name, created, companyID, creatorID, parentID);
		}

		/**
		 *
		 * @param id
		 * @param name
		 * @param created
		 * @param companyID
		 * @param creatorID
		 * @param parentID
		 */
		public Row(int id, String name, Timestamp created, int companyID, int creatorID, int parentID) {
			super(id); this.name = name; this.created = created; this.companyID = companyID;
				this.creatorID = creatorID; this.parentID = parentID;
		}

		/**
		 *
		 * @return
		 */
		public String getName() {
			return name;
		}

		/**
		 *
		 * @return
		 */
		public Timestamp getCreated() {
			return created;
		}

		/**
		 *
		 * @return
		 */
		public int getCompanyID() {
			return companyID;
		}

		/**
		 *
		 * @return
		 */
		public int getCreatorID() {
			return creatorID;
		}

		/**
		 *
		 * @return
		 */
		public int getParentID() {
			return parentID;
		}

		private String name;
		private Timestamp created;

		private int companyID;
		private int creatorID;
		private int parentID;
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
			result.getString("name"),
			result.getTimestamp("created"),
			result.getInt("company_id"),
			result.getInt("creator_id"),
			result.getInt("parent_id")
		);
	}
}

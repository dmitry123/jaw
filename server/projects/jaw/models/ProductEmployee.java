package jaw.models;

import jaw.core.Environment;
import jaw.core.Model;
import jaw.sql.CommandProtocol;
import jaw.sql.CortegeProtocol;
import jaw.sql.CortegeRow;

import java.sql.ResultSet;

public class ProductEmployee extends Model<ProductEmployee.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public ProductEmployee(Environment environment) {
		super(environment, "product_employee");
	}

	/**
	 * Override that method to return your own columns for fetchTable method
	 * @return - Command with your query
	 * @throws Exception
	 */
	@Override
	public CommandProtocol getTable() throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("product_employee")
			.join("employee", "product_employee.employee_id = employee.id")
			.join("product", "product_employee.product_id = product.id");
	}

	class Row extends CortegeRow {

		public Row(int id, int productID, int employeeID) {
			super(id); this.productID = productID; this.employeeID = employeeID;
		}

		public int getProductID() {
			return productID;
		}

		public int getEmployeeID() {
			return employeeID;
		}

		private int productID;
		private int employeeID;
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
		return new Row(result.getInt("id"), result.getInt("product_id"), result.getInt("employee_id"));
	}
}

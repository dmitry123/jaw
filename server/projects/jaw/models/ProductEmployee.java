package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CommandProtocol;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.sql.ResultSet;

public class ProductEmployee extends Model {

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
}

package jaw.models;

import jaw.Core.*;

import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.lang.Exception;
import java.lang.Object;
import java.sql.Timestamp;
import java.sql.ResultSet;

/**
 * Created by Savonin on 2014-12-05
 */
public class Product extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Product(Environment environment) {
		super(environment, "product");
	}

	public CortegeProtocol register(String name, Integer companyID, Integer creatorID, Integer parentID) throws Exception{
		getConnection().createCommand()
			.insert("product", "name, company_id, creator_id, parent_id")
			.values("?, ?, ?, ?")
			.execute(new Object[] { name, companyID, creatorID, parentID })
			.insert();
		return last();
	}

	public CortegeProtocol delete(Integer productID) throws Exception {
		getConnection().createCommand()
			.delete("product")
			.where("id = ?")
			.execute(new Object[] { productID })
			.delete();
		return null;
	}

	public CortegeProtocol bind(Integer productID, Integer employeeID) throws Exception {
		getConnection().createCommand()
			.insert("product_employee", "product_id, employee_id")
			.values("?, ?")
			.execute(new Object[] { productID, employeeID })
			.insert();
		return null;
	}

	public CortegeProtocol unbind(Integer productID, Integer employeeID) throws Exception {
		getConnection().createCommand()
			.delete("product_employee")
			.where("product_id = ?")
			.andWhere("employee_id = ?")
			.execute(new Object[] { productID, employeeID })
			.delete();
		return null;
	}

	public ResultSet fetchByCompany(Integer companyID) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("product as p")
			.where("p.company_id = ?")
			.execute(companyID)
			.select();
	}
}

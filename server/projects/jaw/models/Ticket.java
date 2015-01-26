package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.lang.Exception;
import java.lang.Integer;
import java.sql.ResultSet;

public class Ticket extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Ticket(Environment environment) {
		super(environment, "ticket");
	}

	public ResultSet fetchByEmployee(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("t.*, p.*, o.*")
			.from("ticket as t")
			.join("product as p", "p.id = t.product_id")
			.join("employee as o", "o.id = t.owner_id")
			.where("t.owner_id = ?")
			.order("t.precedence")
			.execute(employeeID)
			.select();
	}

	public ResultSet fetchCompanyByEmployee(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("t.*, p.*, o.*")
			.from("ticket as t")
			.join("product as p", "p.id = t.product_id")
			.join("employee as e", "e.id = t.creator_id")
			.join("employee as e2", "e2.company_id = e.company_id")
			.join("employee as o", "o.id = t.owner_id")
			.where("e2.id = ?")
			.order("t.precedence")
			.execute(employeeID)
			.select();
	}

	public ResultSet fetchByCompany(Integer companyID) throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("ticket as t")
			.join("employee as e", "e.id = t.creator_id")
			.where("e.company_id = ?")
			.execute(companyID)
			.select();
	}
}

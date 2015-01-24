package jaw.models;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;

import java.lang.Exception;
import java.lang.Integer;
import java.sql.ResultSet;

public class Ticket extends Model<Ticket.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Ticket(Environment environment) {
		super(environment, "ticket");
	}

	public ResultSet fetchByEmployee(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("t.*, r.*")
			.from("ticket as t")
			.join("project as p", "p.id = t.project_id")
			.join("product as r", "r.id = p.product_id")
			.where("t.owner_id = ?")
			.execute(employeeID)
			.select();
	}

	public ResultSet fetchCompanyByEmployee(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("t.*, p.*")
			.from("ticket as t")
			.join("project as r", "r.id = t.project_id")
			.join("product as p", "p.id = r.product_id")
			.join("company as c", "c.id = p.company_id")
			.join("employee as e", "e.id = t.owner_id")
			.where("e.id = ?")
			.execute(employeeID)
			.select();
	}

	public ResultSet fetchProjectByEmployee(Integer employeeID) throws Exception {
		return getConnection().createCommand()
			.select("t.*, p.*")
			.from("ticket as t")
			.join("project as r", "r.id = t.project_id")
			.join("product as p", "p.id = r.product_id")
			.join("employee as e", "e.id = t.owner_id")
			.where("e.id = ?")
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

	public static class Row extends CortegeRow {

		public Row(int id) {
			super(id);
		}
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
		return null;
	}
}

package jaw.models;

import jaw.Core.*;

import jaw.Sql.CortegeProtocol;
import jaw.Sql.CortegeRow;
import jaw.Sql.CommandProtocol;

import java.lang.Exception;
import java.lang.Object;
import java.sql.ResultSet;

/**
 * Created by Savonin on 2014-12-05
 */
public class Project extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Project(Environment environment) {
		super(environment, "project");
	}

	public CortegeProtocol register(String name, Integer leaderID, Integer companyID, Integer creatorID) throws Exception {
		Model productModel = getEnvironment().getModelManager().get("Product");
		CortegeProtocol productProtocol = productModel.fetchRow("register", name, companyID, creatorID, 0);
		getConnection().createCommand()
			.insert("project", "leader_id, product_id")
			.values("?, ?")
			.execute(new Object[] { leaderID, productProtocol.getID() })
			.insert();
		return last();
	}

	@Override
	public CommandProtocol getTable() throws Exception {
		return getConnection().createCommand()
			.select("*")
			.from("project")
			.join("employee", "project.leader_id = employee.id")
			.join("product", "project.product_id = product.id");
	}

	@Override
	public CommandProtocol getReferences() throws Exception {
		return getConnection().createCommand()
			.select("employee.*, product.*")
			.from("project")
			.join("product", "project.product_id = product.id")
			.join("product_employee", "product.id = product_employee.product_id")
			.join("employee", "employee.id = product_employee.employee_id")
			.leftJoin("product as p", "p.parent_id = product.id");
	}

	public ResultSet fetchLeader(Integer projectID) throws Exception {
		return getConnection().createCommand()
			.select("e.*")
			.from("project as p")
			.join("employee as e", "e.id = p.leader_id")
			.where("p.id = ?")
			.execute(projectID)
			.select();
	}

	public ResultSet fetchProductLeader(Integer projectID) throws Exception {
		return getConnection().createCommand()
			.select("e.*")
			.from("project as p")
			.join("employee as e", "e.id = p.leader_id")
			.where("p.product_id = ?")
			.execute(projectID)
			.select();
	}

	public ResultSet fetchByCompany(Integer companyID) throws Exception {
		return getConnection().createCommand()
			.select("p.*, d.*")
			.from("project as p")
			.join("product as d", "d.id = p.product_id")
			.where("d.company_id = ?")
			.execute(companyID)
			.select();
	}
}

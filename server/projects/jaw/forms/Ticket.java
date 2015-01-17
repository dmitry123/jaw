package jaw.forms;

import jaw.Core.Environment;
import jaw.Core.Form;

import java.lang.String;
import java.sql.ResultSet;
import java.util.LinkedHashMap;

/**
 * Created by Savonin on 2015-01-08
 */
public class Ticket extends Form {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Ticket(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to return form's model
	 * @return - Linked hash map associated with collections or object values
	 */
	@Override
	public LinkedHashMap<String, ResultSet> getForm() throws Exception {

		if (getEnvironment().getSession() == null || !getEnvironment().getSession().containsKey("company")) {
			return null;
		}

		return new LinkedHashMap<String, ResultSet>() {{
			put("owner", getModel("Employee").fetchSet("fetchByCompany",
				getEnvironment().getSession().get("company")
			));
			put("product", getModel("Product").fetchSet("fetchByCompany",
				getEnvironment().getSession().get("company")
			));
			put("project", getModel("Project").fetchSet("fetchByCompany",
				getEnvironment().getSession().get("company")
			));
			put("parent", getModel("Ticket").fetchSet("fetchByCompany",
				getEnvironment().getSession().get("company")
			));
		}};
	}
}
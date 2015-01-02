package controllers;

import jaw.Core.*;
import jaw.Core.User;
import jaw.Html.Html;

import java.io.StringWriter;
import java.sql.ResultSet;


/**
 * Created by Savonin on 2014-12-04
 */
public class System extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public System(Environment environment) {
		super(environment);
	}

	@Override
	public void actionFilter(String path, String action) throws Exception {

		User user = getEnvironment().getUserSessionManager().get();

		if (user == null || !user.containsKey("employee")) {
			return;
		}

		StringWriter writer = new StringWriter();

		final ResultSet requests = getModel("Request").fetchSet("fetchByEmployeeID",
			Integer.parseInt(getEnvironment().getUserSessionManager().get().get("employee").toString())
		);

		new Html(writer) {{
			while (requests.next()) {
				tr();
					td();
						a().classAttr("request").id("request-" + requests.getString("id")).text(
							requests.getString("surname") + " " +
							requests.getString("name").substring(0, 1).toUpperCase() + "." +
							requests.getString("patronymic").substring(0, 1).toUpperCase()
						); end();
					end();
					td();
						a(); span()
							.id("request-" + requests.getString("id"))
							.classAttr("request-ok glyphicon glyphicon-ok text-success");
						end();
						end();
						a(); span()
							.id("request-" + requests.getString("id"))
							.classAttr("request-cancel glyphicon glyphicon-remove text-danger");
						end();
						end();
					end();
				end();
			}
		}};

		getEnvironment().getMustacheDefiner().put("Employee.Request.Body",
			writer.toString()
		);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {

		User user = getEnvironment().getUserSessionManager().get();

		if (user == null || !user.containsKey("employee")) {
			redirect("Index", "View");
			return;
		}

		this.actionFilter(null, null);

		render("View");
	}
}

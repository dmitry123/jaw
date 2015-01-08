package jaw.controllers;

import jaw.core.*;
import jaw.core.Session;
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

		Session session = getEnvironment().getSession();

		if (session == null || !session.containsKey("employee")) {
			return;
		}

		StringWriter writer = new StringWriter();

		final ResultSet requests = getModel("Request").fetchSet("fetchByEmployeeID",
			Integer.parseInt(getEnvironment().getSession().get("employee").toString())
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
			writer.toString().replaceAll("[\n\t\r]", "")
		);

		writer = new StringWriter();

		final ResultSet notifications = getModel("Message").fetchSet("fetchByEmployeeID",
			Integer.parseInt(getEnvironment().getSession().get("employee").toString())
		);

		new Html(writer) {{
			while (notifications.next()) {

				String message = notifications.getString("message");

				if (message.length() > 100) {
					message = message.substring(0, 100) + "...";
				}

				tr();
					td().style("width: 100px; vertical-align: middle;");
						a().classAttr("notification").id("notification-" + notifications.getString("id")).text(
							notifications.getString("surname") + " " +
							notifications.getString("name").substring(0, 1).toUpperCase() + "." +
							notifications.getString("patronymic").substring(0, 1).toUpperCase()
						); end();
					end();
					td();
						div().text(message); end();
					end();
				end();
			}
		}};

		getEnvironment().getMustacheDefiner().put("Employee.Notification.Body",
			writer.toString().replaceAll("[\n\r\t]", "")
		);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {

		Session session = getEnvironment().getSession();

		if (session == null || !session.containsKey("employee")) {
			redirect("Index", "View");
			return;
		}

		actionFilter(null, null);

		render("View");
	}
}

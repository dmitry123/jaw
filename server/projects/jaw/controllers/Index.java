package controllers;

import jaw.Core.*;

import jaw.Core.User;
import jaw.Html.Html;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Created by Savonin on 2014-11-08
 */
public class Index extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Index(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {

		// If we have user's session then redirect to system view else render current
		if (getEnvironment().getUserSessionManager().has()) {
			redirect("Index", "Project");
		} else {
			render("View");
		}
	}

	@Override
	public void actionFilter(String path, String action) throws Exception {

		User user = getEnvironment().getUserSessionManager().get();

		if (user != null && user.containsKey("employee")) {
			getEnvironment().getMustacheDefiner().put("Employee.Request.Count",
				"" + getModel("Request").fetchSize("receiver_id = " + user.get("employee"))
			);
			getEnvironment().getMustacheDefiner().put("Employee.Notification.Count",
				"0"
			);
		}
	}

	public void actionProject() throws Exception {

		Model companyModel = getModel("Company");

		// Check for opened session
		if (getEnvironment().getUserSessionManager().has()) {

			final ResultSet companySet = companyModel.fetchSet("fetchByUserID",
				getEnvironment().getUserSessionManager().get().getID()
			);

			StringWriter writer = new StringWriter();

			while (companySet.next()) {
				new Html(writer) {{
					option().value(companySet.getString("id")).text(companySet.getString("name")).end();
				}};
			}

			getEnvironment().getMustacheDefiner().put(
				"Index.Project.Company.List", writer.toString()
			);

			render("Project");
		} else {
			redirect("Index", "View");
		}
	}

	/**
	 *
	 * @throws Exception
	 */
	public void actionGetEmployeeProjects() throws Exception {

		// Load models
		Model employeeModel = getModel("Employee");
		Model groupModel = getModel("Group");

		// Json array with found employees and it's projects
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonEmployees = new JSONArray();

		// Get user's identifier
		jaw.Core.User user = getEnvironment().getUserSessionManager().get();

		if (user != null) {

			// Fetch set with user's employees
			final ResultSet projectEmployees = employeeModel.fetchSet(
				"fetchProjectsByUserID", user.getID()
			);

			// Get result's meta data object with all columns and tables info
			ResultSetMetaData columns = projectEmployees.getMetaData();

			// Build hash map with all fields associated with it's table
			while (projectEmployees.next()) {

				HashMap<String, String> columnMap = new HashMap<String, String>();
				JSONArray jsonGroups = new JSONArray();

				// Initialize column map with fields with format (table.key -> value)
				for (int i = 1; i <= columns.getColumnCount(); i++) {
					columnMap.put(columns.getTableName(i) + "." + columns.getColumnName(i), projectEmployees.getString(i));
				}

				// Fetch list with employee's groups
				ResultSet groups = groupModel.fetchSet(
					"fetchGroupsByEmployee", Integer.parseInt(columnMap.get("employee.id"))
				);

				while (groups.next()) {
					jsonGroups.put(groups.getString("description"));
				}

				// Put groups in employees info
				columnMap.put("groups", jsonGroups.toString());

				// Put found employee in json array
				jsonEmployees.put(columnMap);
			}

			jsonResponse.put("employees", jsonEmployees);
			jsonResponse.put("status", true);

		} else {
			jsonResponse.put("message", "Доступ закрыт. Обновите страницу");
			jsonResponse.put("status", false);
		}

		setAjaxResponse(jsonResponse.toString());
	}

	/**
	 *
	 * @throws Exception
	 */
	public void actionDenied() throws Exception {

		// Render denied page
		render("Denied");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Override
	public void action404() throws Exception {

		// Render 404 error
		if (getView() != null) {
			render("404");
		}
	}
}

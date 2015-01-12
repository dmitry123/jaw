package jaw.controllers;

import jaw.Html.Html;
import jaw.core.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringWriter;
import java.lang.Exception;
import java.sql.ResultSet;
import java.util.Map;

public class Employee extends Controller {

	/**
	 * @param environment - Every core's extension must have environment
	 * with predeclared extensions
	 */
	public Employee(Environment environment) {
		super(environment);
	}

	public void actionLogin() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		int employeeID = Integer.parseInt(GET("id"));

		ResultSet employee = getModel("Employee").fetchByID(employeeID);

		if (!employee.next()) {
			postErrorMessage("Сотрудник с таким идентификатором не зарегистрирован в системе");
			return;
		}

		getEnvironment().getSession().put("company",
			employee.getInt("company_id")
		);
		getEnvironment().getSession().put("employee", employeeID);

		JSONObject json = new JSONObject();
		json.put("status", true);
		setAjaxResponse(json.toString());
	}

	public void actionLogout() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		getEnvironment().getSession()
				.remove("employee");

		JSONObject json = new JSONObject();
		json.put("status", true);
		setAjaxResponse(json.toString());
	}

	public void actionGetUserEmployees() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		int userID = getEnvironment().getSession().getID();

		ResultSet resultSet = getModel().fetchSet("fetchUserEmployee", userID);

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		while (resultSet.next()) {
			JSONObject node = new JSONObject();
			node.put("id", resultSet.getInt("id"));
			node.put("name",
				resultSet.getString("surname") + " " +
				resultSet.getString("name") + " " +
				resultSet.getString("patronymic")
			);
			array.put(node);
		}

		json.put("status", true);
		json.put("employees", array);

		setAjaxResponse(json.toString());
	}

	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}
}

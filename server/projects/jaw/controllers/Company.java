package jaw.controllers;

import jaw.core.*;

import jaw.sql.CortegeProtocol;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.Exception;
import java.lang.String;
import java.util.HashMap;
import java.sql.ResultSet;
import java.util.LinkedHashMap;

public class Company extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Company(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}

	public void actionRegister() throws Exception {

		JSONObject jsonResponse = new JSONObject();
		boolean isAllowed = true;

		// Initialize post variables
		final String companyName = POST("company");
		final String directorSurname = POST("surname");
		final String directorName = POST("name");
		final String directorPatronymic = POST("patronymic");

		// Get models
		final Model companyModel = getModel("Company");
		final Model employeeModel = getModel("Employee");
		final Model groupModel = getModel("Group");

		// Get current user by it's session
		final Session session = getEnvironment().getSessionManager().get();

		// Check for opened user's session
		if (session == null) {
			postErrorMessage("Недостаточно прав для совершения действий");
			return;
		}

		// If company with that name doesn't exist
		if (!companyModel.fetchSet("fetchByName", companyName).next()) {

			// Register row in database
			companyModel.fetchRow("register", companyName);
			CortegeProtocol companyRow = companyModel.fetchRow("fetchByName", companyName);

			// Register new employee as director in this company
			CortegeProtocol directorRow = employeeModel.fetchRow("register", directorName,
				directorSurname, directorPatronymic, session.getID(), 0, 0, companyRow.getID()
			);

			// Bind director to it's group
			groupModel.fetchRow("bindWithGroupName", "company-director", directorRow.getID());
			groupModel.fetchRow("bindWithGroupName", "employee", directorRow.getID());

			// Update company's reference to director
			companyModel.fetchRow("updateDirector", companyName, directorRow.getID());

			// Set error response
			jsonResponse.put("status", true);

		} else {

			// Set error response
			jsonResponse.put("status", false);
			jsonResponse.put("message", "Компания с таким названием уже существует");
		}

		// Return ajax response
		setAjaxResponse(jsonResponse.toString());
	}

	public void actionDelete() throws Exception {

		JSONObject jsonResponse = new JSONObject();
		boolean isAllowed = false;

		// Get models
		final Model companyModel = getModel("Company");
		final Model employeeModel = getModel("Employee");

		// Get company's identifier
		final int companyID = Integer.parseInt(GET("id"));

		// Get current user by it's session
		final Session session = getEnvironment().getSession();

		// Check for opened user's session
		if (session == null) {
			jsonResponse.put("status", false);
			jsonResponse.put("message", "Доступ запрещен, обновите страницу");
			setAjaxResponse(jsonResponse.toString());
			return;
		}

		// Fetch current user's employee
		CortegeProtocol employee = employeeModel.fetchRow("fetchByUserAndCompanyID", session.getID(), companyID);

		// If employee isn't null, then check it's privilege to create companies
		if (employee != null) {

			// Fetch set with privilege
			ResultSet privilegeSet = employeeModel.fetchSet("fetchPrivilege", employee.getID(), "company/delete");

			// Get flag and set to allow boolean cause
			if (privilegeSet.next()) {
				isAllowed = privilegeSet.getBoolean("flag");
			}
		} else {
			isAllowed = true;
		}

		// Don't allow to apply changes to employee without necessary privilege
		if (!isAllowed) {
			jsonResponse.put("status", false);
			jsonResponse.put("message", "Недостаточно прав для совершения действий");
			setAjaxResponse(jsonResponse.toString());
			return;
		}

		// Remove company from database
		companyModel.fetchRow("delete", companyID);

		// Return ajax response
		jsonResponse.put("status", true);
		jsonResponse.put("message", "Проект успешно удален");
		setAjaxResponse(jsonResponse.toString());
	}

	public void actionGetAcceptors() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		final ResultSet resultSet = getModel("Employee").fetchSet("fetchByGroupAndCompany",
			"company-director", Integer.parseInt(GET("companyID"))
		);

		while (resultSet.next()) {
			array.put(new LinkedHashMap<String, Object>() {{
				put("id", resultSet.getInt("id"));
				put("name",
					resultSet.getString("surname") + " " +
					resultSet.getString("name") + " " +
					resultSet.getString("patronymic"));
			}});
		}

		json.put("employees", array);
		json.put("status", true);

		setAjaxResponse(json.toString());
	}

	public void actionGetRows() throws Exception {

		JSONObject json = new JSONObject();

		if (!checkAccessWithResponse()) {
			return;
		}

		json.put("companies", getModel().getTableRows());
		json.put("status", true);

		setAjaxResponse(json.toString());
	}

	public void actionGetProducts() throws Exception {

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		if (!checkAccessWithResponse()) {
			return;
		}

		final ResultSet resultSet = getModel().fetchSet("fetchProducts",
			GET("id")
		);

		while (resultSet.next()) {
			JSONObject node = new JSONObject();
			node.put("id", resultSet.getInt("id"));
			node.put("name", resultSet.getString("name"));
			array.put(node);
		}

		json.put("products", array);
		json.put("status", true);

		setAjaxResponse(json.toString());
	}

	public void actionGetProjects() throws Exception {

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		if (!checkAccessWithResponse()) {
			return;
		}

		final ResultSet resultSet = getModel().fetchSet("fetchProducts",
			Integer.parseInt(GET("id"))
		);

		while (resultSet.next()) {
			JSONObject node = new JSONObject();
			node.put("id", resultSet.getInt("id"));
			node.put("name", resultSet.getString("name"));
			array.put(node);
		}

		json.put("projects", array);
		json.put("status", true);

		setAjaxResponse(json.toString());
	}

	public void actionGetProjectsAndEmployees() throws Exception {

		JSONObject json = new JSONObject();

		if (!checkAccessWithResponse()) {
			return;
		}

		int userID = getEnvironment().getSession().getID();
		int companyID = Integer.parseInt(GET("id"));

		ResultSet resultSet = getModel().fetchSet("fetchProducts", companyID);
		JSONArray projects = new JSONArray();

		while (resultSet.next()) {
			JSONObject node = new JSONObject();
			node.put("id", resultSet.getInt("id"));
			node.put("name", resultSet.getString("name"));
			projects.put(node);
		}

		resultSet = getModel().fetchSet("fetchEmployeesByUser", companyID, userID);
		JSONArray employees = new JSONArray();

		while (resultSet.next()) {
			JSONObject node = new JSONObject();
			node.put("id", resultSet.getInt("id"));
			node.put("name",
				resultSet.getString("surname") + " " +
				resultSet.getString("name") + " " +
				resultSet.getString("patronymic")
			);
			employees.put(node);
		}

		json.put("projects", projects);
		json.put("employees", employees);
		json.put("status", true);

		setAjaxResponse(json.toString());
	}

	public void actionGetCompanyEmployees() throws Exception {

		JSONObject jsonResponse = new JSONObject();

		// Get post company identifier
		final int companyID = Integer.parseInt(POST("company_id"));

		if (getEnvironment().getSessionManager().has()) {

			// Fetch set with company's employees
			final ResultSet employees = getModel("Company").fetchSet("fetchEmployees", companyID);

			JSONArray jsonArray = new JSONArray();

			while (employees.next()) {
				jsonArray.put(new HashMap<String, String>() {
					{
						put("id", employees.getString("id"));
						put("surname", employees.getString("surname"));
						put("name", employees.getString("name"));
					}
				});
			}

			jsonResponse.put("employees", jsonArray.toString());
			jsonResponse.put("status", true);

		} else {

			// Set error response
			jsonResponse.put("status", false);
			jsonResponse.put("message", "Доступ запрещен, обновите страницу");
		}

		// Return ajax response
		setAjaxResponse(jsonResponse.toString());
	}

	public void actionGetCompanies() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		ResultSet resultSet = getModel().fetchRows();

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		while (resultSet.next()) {
			JSONObject node = new JSONObject();
			node.put("id", resultSet.getInt("id"));
			node.put("name", resultSet.getString("name"));
			array.put(node);
		}

		json.put("companies", array);
		json.put("status", true);

		setAjaxResponse(json.toString());
	}
}

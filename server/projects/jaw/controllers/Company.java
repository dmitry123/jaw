package controllers;

import Core.*;

import Sql.CortegeProtocol;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.sql.ResultSet;

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

	@Override
	public void actionGetTable() throws Exception {
		super.actionGetTable();
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
		final Core.User user = getEnvironment().getUserSessionManager().get();

		// Check for opened user's session
		if (user == null) {
			postErrorMessage("Доступ запрещен, обновите страницу");
			return;
		}

		// Fetch current user's employee
		CortegeProtocol employee = employeeModel.fetchRow("fetchByUserID", user.getID());

		// If employee isn't null, then check it's privilege to create companies
		if (employee != null) {
			isAllowed = employeeModel.fetchSet("fetchPrivilege",
				employee.getID(), "company-create"
			).next();
		}

		// Don't allow to apply changes to employee without necessary privilege
		if (!isAllowed) {
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
				directorSurname, directorPatronymic, user.getID(), 0, 0, companyRow.getID()
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
		final Core.User user = getEnvironment().getUserSessionManager().get();

		// Check for opened user's session
		if (user == null) {
			jsonResponse.put("status", false);
			jsonResponse.put("message", "Доступ запрещен, обновите страницу");
			setAjaxResponse(jsonResponse.toString());
			return;
		}

		// Fetch current user's employee
		CortegeProtocol employee = employeeModel.fetchRow("fetchByUserAndCompanyID", user.getID(), companyID);

		// If employee isn't null, then check it's privilege to create companies
		if (employee != null) {

			// Fetch set with privilege
			ResultSet privilegeSet = employeeModel.fetchSet("fetchPrivilege", employee.getID(), "delete-company");

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

	public void actionGetCompanyEmployees() throws Exception {

		JSONObject jsonResponse = new JSONObject();

		// Get post company identifier
		final int companyID = Integer.parseInt(POST("company_id"));

		if (getEnvironment().getUserSessionManager().has()) {

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
}

package controllers;

import Core.*;

import Sql.CortegeProtocol;
import org.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-12-05
 */
public class Project extends Controller {
	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Project(Environment environment) {
		super(environment);
	}

	@Override
	public void actionGetTable() throws Exception {
		super.actionGetTable();
	}

	public void actionRegister() throws Exception {

		// Get post data fields
		final int leaderID = Integer.parseInt(POST("leader_id"));
		final int companyID = Integer.parseInt(POST("company_id"));

		final String name = POST("name");

		// Load models
		final Model employeeModel = getModel("Employee");
		final Model projectModel = getModel("Project");

		// Find user for current session
		final Core.User user = getEnvironment().getUserSessionManager().get();

		if (user == null) {
			postErrorMessage("Недостаточно прав для совершения действий");
			return;
		}

		// Create json response object
		JSONObject jsonResponse = new JSONObject();

		// Fetch cortege with current employee's identifier
		CortegeProtocol employeeProtocol = employeeModel.fetchRow("fetchByUserAndCompanyID",
			user.getID(), companyID
		);

		if (employeeProtocol == null) {
			postErrorMessage("Только сотрудники компании могут создавать проекты");
			return;
		}

		boolean isAllowed = employeeModel.fetchSet("fetchPrivilege",
			employeeProtocol.getID(), "project-create"
		).next();

		if (!isAllowed) {
			postErrorMessage("Недостаточно прав для совершения действий");
			return;
		}

		// Register new project with it's product in database
		projectModel.fetchRow("register", name, leaderID, companyID, employeeProtocol.getID());

		// Set status to true
		jsonResponse.put("message", "Проект был успешно добавлен");
		jsonResponse.put("status", true);

		setAjaxResponse(jsonResponse.toString());
	}

	public void actionDelete() throws Exception {

		// Get post data fields
		final int projectID = Integer.parseInt(POST("id"));
		boolean isDirector = false;

		// Load models
		final Model projectModel = getModel("Project");
		final Model productModel = getModel("Product");
		final Model groupModel = getModel("Group");
		final Model employeeModel = getModel("Employee");

		// Create json response object
		JSONObject jsonResponse = new JSONObject();

		// Find user for current session
		final Core.User user = getEnvironment().getUserSessionManager().get();

		if (user == null) {
			postErrorMessage("Доступ закрыт, обновите страницу");
			return;
		}

		ResultSet projectSet;
		ResultSet productSet;

		try {
			// Fetch project by it's identifier
			projectSet = projectModel.fetchSet("fetchByID", projectID);
			// Fetch project's product row
			productSet = productModel.fetchSet("fetchByID", projectSet.getInt("product_id"));
		} catch (Exception ignored) {
			jsonResponse.put("message", "Проект с таким именем не существует");
			jsonResponse.put("status", false);
			setAjaxResponse(jsonResponse.toString());
			return;
		}

		// Get project's company
		int companyID = productSet.getInt("company_id");

		// Fetch employee by user's id and company's id
		CortegeProtocol employeeProtocol = employeeModel.fetchRow("fetchByUserAndCompanyID",
			user.getID(), companyID
		);

		if (employeeProtocol == null) {
			jsonResponse.put("message", "Только сотрудники компании могут удалять проекты");
			jsonResponse.put("status", false);
			setAjaxResponse(jsonResponse.toString());
			return;
		}

		if (!employeeModel.fetchSet("fetchPrivilege", employeeProtocol.getID(), "project-delete").next()) {
			postErrorMessage("Недостаточно прав для совершения действия");
			return;
		}

		// Find current employee's groups with titles
		ResultSet employeeGroups = groupModel.fetchSet("fetchGroupsByEmployee", employeeProtocol.getID());

		while (employeeGroups.next()) {
			if (employeeGroups.getString("name").equals("company-director")) {
				isDirector = true; break;
			}
		}

		// Check employee for director title
		if (!isDirector) {
			jsonResponse.put("message", "Только директор компании может удалять проекты");
			jsonResponse.put("status", false);
			setAjaxResponse(jsonResponse.toString());
			return;
		}

		// Try to delete project from database
		try {
			projectModel.deleteByID(projectID);
		} catch (SQLException ignored) {
			jsonResponse.put("message", "Нарушение целостности, имеются внешние связи");
			jsonResponse.put("status", false);
		} catch (java.lang.Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			jsonResponse.put("message", "Проект был успешно удален");
			jsonResponse.put("status", true);
		}

		setAjaxResponse(jsonResponse.toString());
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}
}

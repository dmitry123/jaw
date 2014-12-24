package controllers;

import Core.*;
import Core.Controller;
import Core.InternalError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.Override;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin extends Controller {

	public Admin(Environment environment) {
		super(environment);
	}

	@Override
	public void actionView() throws InternalError, SQLException {
		if (checkAccess()) {
			render("View");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionUser() throws InternalError, SQLException {
		if (checkAccess()) {
			render("User");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionEmployee() throws InternalError, SQLException {
		if (checkAccess()) {
			render("Employee");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionCompany() throws InternalError, SQLException {
		if (checkAccess()) {
			render("Company");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionGroup() throws InternalError, SQLException {
		if (checkAccess()) {
			render("Group");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionPrivilege() throws InternalError, SQLException {
		if (checkAccess()) {
			render("Privilege");
		} else {
			redirect("Index", "View");
		}
	}

	/**
	 * Check user's access
	 * @param privileges - List with privileges
	 * @return - True if user has that privileges
	 * @throws Core.InternalError
	 * @throws SQLException
	 */
	@Override
	public boolean checkAccess(String... privileges) throws InternalError, SQLException {

		Core.User user = getEnvironment().getUserSessionManager().get();

		if (user == null) {
			return false;
		}

		Model employeeModel = getEnvironment().getModelManager().get("Employee");
		ResultSet employeeSet = employeeModel.fetchSet("fetchByUserID", user.getID());

		int total = 0;

		// If one of user's employees has 'god' privilege
		while (employeeSet.next()) {
			for (String privilege : privileges) {
				if (employeeModel.fetchSet("fetchPrivilege", employeeSet.getInt("id"), privilege).next()) {
					++total;
				}
			}
			if (total == privileges.length) {
				return true;
			}
			total = 0;
		}

		return false;
	}
}

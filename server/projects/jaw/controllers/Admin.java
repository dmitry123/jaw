package controllers;

import Core.*;
import Core.Controller;

import java.sql.SQLException;

public class Admin extends Controller {

	public Admin(Environment environment) {
		super(environment);
	}

	@Override
	public void actionView() throws Exception {
		if (checkAccess()) {
			render("View");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionUser() throws Exception {
		if (checkAccess()) {
			render("User");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionEmployee() throws Exception {
		if (checkAccess()) {
			render("Employee");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionCompany() throws Exception {
		if (checkAccess()) {
			render("Company");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionGroup() throws Exception {
		if (checkAccess()) {
			render("Group");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionPrivilege() throws Exception {
		if (checkAccess()) {
			render("Privilege");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionProject() throws Exception {
		if (checkAccess()) {
			render("Project");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionReference() throws Exception {
		if (checkAccess()) {
			render("Reference");
		} else {
			redirect("Index", "View");
		}
	}

	public boolean checkAccess() throws Exception {
		return checkAccess("jaw-admin");
	}

	/**
	 * Check user's access
	 * @param privileges - List with privileges
	 * @return - True if user has that privileges
	 * @throws Exception
	 * @throws SQLException
	 */
	@Override
	public boolean checkAccess(String... privileges) throws Exception {

		Core.User user = getEnvironment().getUserSessionManager().get();

		if (user == null) {
			return false;
		}

		Model employeeModel = getEnvironment().getModelManager()
			.get("Employee");

		for (String privilege : privileges) {
			if (!employeeModel.fetchSet("fetchPrivilegeByUserID", user.getID(), privilege).next()) {
				return false;
			}
		}

		return true;
	}
}

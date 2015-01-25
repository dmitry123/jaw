package jaw.controllers;

import jaw.Core.*;
import jaw.Core.Controller;

import java.sql.SQLException;

public class Admin extends Controller {

	public Admin(Environment environment) {
		super(environment);
	}

	@Override
	public void actionView() throws Exception {
		if (checkAccess()) {
			renderVm("View", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionUser() throws Exception {
		if (checkAccess()) {
			renderVm("User", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionEmployee() throws Exception {
		if (checkAccess()) {
			renderVm("Employee", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionCompany() throws Exception {
		if (checkAccess()) {
			renderVm("Company", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionGroup() throws Exception {
		if (checkAccess()) {
			renderVm("Group", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionPrivilege() throws Exception {
		if (checkAccess()) {
			renderVm("Privilege", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionProject() throws Exception {
		if (checkAccess()) {
			renderVm("Project", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionGroupPrivilege() throws Exception {
		if (checkAccess()) {
			renderVm("GroupPrivilege", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionEmployeeGroup() throws Exception {
		if (checkAccess()) {
			renderVm("EmployeeGroup", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public void actionProductEmployee() throws Exception {
		if (checkAccess()) {
			renderVm("ProductEmployee", "AdminMenu", "Table");
		} else {
			redirect("Index", "View");
		}
	}

	public boolean checkAccess() throws Exception {
		return checkAccess("jaw/admin");
	}
}

package jaw.views;

import jaw.Core.*;


import java.util.Map;

public class Admin extends View {

	public Admin(Environment environment) {
		super(environment);
	}

	@Override
	public void renderView(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/view");
		template("common/footer");
	}

	public void renderUser(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/user");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderEmployee(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/employee");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderCompany(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/company");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderGroup(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/group");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderPrivilege(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/privilege");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderProject(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/project");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderGroupPrivilege(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/group-privilege");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderEmployeeGroup(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/employee-group");
		template("modal/jaw-table");
		template("common/footer");
	}

	public void renderProductEmployee(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("admin/menu");
		template("admin/product-employee");
		template("modal/jaw-table");
		template("common/footer");
	}
}

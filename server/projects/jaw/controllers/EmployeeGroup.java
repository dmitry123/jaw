package jaw.controllers;

import jaw.core.Controller;
import jaw.core.Environment;

public class EmployeeGroup extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public EmployeeGroup(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}
}

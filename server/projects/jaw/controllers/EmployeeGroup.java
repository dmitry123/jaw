package controllers;

import jaw.Core.Controller;
import jaw.Core.Environment;

public class EmployeeGroup extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public EmployeeGroup(Environment environment) {
		super(environment);
	}

	/**
	 * Default action to get information from database, to allow access to table from controller override that method
	 * and invoke super method
	 * @throws Exception
	 */
	@Override
	public void actionGetTable() throws Exception {
		super.actionGetTable();
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "404");
	}
}

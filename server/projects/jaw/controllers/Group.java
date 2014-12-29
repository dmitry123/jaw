package controllers;

import jaw.Core.*;

public class Group extends Controller {

	/**
	 * @param environment - Every core's extension must have environment
	 * with predeclared extensions
	 */
	public Group(Environment environment) {
		super(environment);
	}

	@Override
	public void actionGetTable() throws Exception {
		super.actionGetTable();
	}

	@Override
	public void actionView() throws Exception {
		redirect("Index", "View");
	}
}

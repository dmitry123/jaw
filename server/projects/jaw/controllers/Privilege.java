package controllers;

import jaw.Core.*;

public class Privilege extends Controller {

	/**
	 * @param environment - Every core's extension must have environment
	 * with predeclared extensions
	 */
	public Privilege(Environment environment) {
		super(environment);
	}

	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}
}

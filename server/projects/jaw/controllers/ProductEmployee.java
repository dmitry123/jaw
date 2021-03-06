package jaw.controllers;

import jaw.Core.Controller;
import jaw.Core.Environment;

public class ProductEmployee extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public ProductEmployee(Environment environment) {
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

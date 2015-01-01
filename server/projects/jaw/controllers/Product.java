package controllers;

import jaw.Core.Controller;
import jaw.Core.Environment;

public class Product extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Product(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {

	}
}

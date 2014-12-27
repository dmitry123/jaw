package controllers;

import Core.*;


/**
 * Created by Savonin on 2014-12-04
 */
public class System extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public System(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		if (!getEnvironment().getUserSessionManager().has()) {
			redirect("Index", "View");
		} else {
			render("View");
		}
	}
}

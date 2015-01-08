package jaw.controllers;

import jaw.core.Controller;
import jaw.core.Environment;

public class Message extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Message(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}

	public void actionSocket() throws Exception {
	}
}

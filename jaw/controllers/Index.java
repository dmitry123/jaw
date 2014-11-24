package controllers;

import Annotation.Action;
import Annotation.Controller;

import Core.Environment;

/**
 * Created by Savonin on 2014-11-08
 */
@Controller
public class Index extends Core.Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Index(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override @Action
	public void actionIndex() {
		render("index");
	}
}

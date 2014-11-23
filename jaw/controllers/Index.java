package controllers;

import Annotation.Action;
import Component.Controller;
import Component.Model;
import Component.View;

/**
 * Created by Savonin on 2014-11-08
 */
@Annotation.Controller
public class Index extends Controller {

	/**
	 * @param model - Controller's model, have to be bind by child class via super constructor
	 * @param view - Controller's view, have to
	 */
	public Index(Model model, View view) {
		super(model, view);
	}

	/**
	 * Default index action
	 */
	@Override @Action
	public void actionIndex() {
		render("jaw.index");
	}
}

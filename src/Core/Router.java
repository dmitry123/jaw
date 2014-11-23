package Core;

import Component.Component;
import Component.Controller;
import Component.Model;
import Component.View;

/**
 * Created by Savonin on 2014-11-03
 */
public class Router {

	/**
	 * @return - Router's instance
	 */
	public static Router getRouter() {
		return routerInstance;
	}

	/**
	 * jaw/index
	 * @param path - Redirect path
	 */
	public void redirect(String path) throws Exception {

		String[] result = path.split("/");

		if (result.length == 0) {
			throw new InternalError("Router/redirect() : \"Unable to split redirect path\"");
		}

		Controller controller;
		
	}

	/**
	 * @return - Last redirected model
	 */
	public Model getLastModel() {
		return lastModel;
	}

	/**
	 * @return - Last redirected view
	 */
	public View getLastView() {
		return lastView;
	}

	/**
	 * @return - Last redirected controller
	 */
	public Controller getLastController() {
		return lastController;
	}

	private Model lastModel = null;
	private View lastView = null;
	private Controller lastController = null;

	/**
	 * Locked constructor
	 */
	private Router() {
	}

	private static Router routerInstance
			= new Router();
}

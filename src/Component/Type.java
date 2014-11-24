package Component;

import Core.*;
import Router.Router;

/**
 * Created by dmitry on 19.11.14
 */
public enum Type {

	CONTROLLER ("controller", Config.CONTROLLER_PATH),
	TEMPLATE   ("template",   Config.TEMPLATE_PATH),
	MODEL      ("model",      Config.MODEL_PATH),
	VIEW       ("view",       Config.VIEW_PATH),
	SCRIPT     ("script",     Config.SCRIPT_PATH),
	MODULE     ("module",     Config.MODULE_PATH),
	WIDGET     ("widget",     Config.WIDGET_PATH),
	BINARY     ("binary",     Config.BINARY_PATH),
	LOG        ("log",        Config.LOG_PATH);

	/**
	 * @param name - Name
	 * @param path - Path
	 */
	private Type(String name, String path) {
		this.name = name;
		this.path = path;
	}

	/**
	 * Compute absolute path with router
	 * @param environment - Reference to environment
	 * @param path - Path to component
	 * @return - Absolute path to component
	 * @throws Core.InternalError
	 */
	public String getAbsolutePath(Environment environment, String path) throws Core.InternalError {
		return Router.getAbsolutePath(environment.getProjectPath(), path, getPath());
	}

	/**
	 * @return - Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return - Path
	 */
	public String getPath() {
		return path;
	}

	private String name;
	private String path;
}

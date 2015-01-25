package jaw.Core;

/**
 * Created by dmitry on 19.11.14
 */
public enum ComponentType {

	CONTROLLER ("controller", Config.CONTROLLER_PATH),
	TEMPLATE   ("template",   Config.TEMPLATE_PATH),
	MODEL      ("model",      Config.MODEL_PATH),
	VIEW       ("view",       Config.VIEW_PATH),
	SCRIPT     ("script",     Config.SCRIPT_PATH),
	MODULE     ("module",     Config.MODULE_PATH),
	WIDGET     ("widget",     Config.WIDGET_PATH),
	VALIDATOR  ("validator",  Config.VALIDATOR_PATH),
	BINARY     ("binary",     Config.BINARY_PATH),
	LOG        ("log",        Config.LOG_PATH),
	COMPONENT  ("component",  Config.COMPONENT_PATH),
	FORM       ("form",       Config.FORM_PATH);

	/**
	 * @param name - Name
	 * @param path - Path
	 */
	private ComponentType(String name, String path) {
		this.name = name;
		this.path = path;
	}

	/**
	 * Compute absolute path with router
	 * @param environment - Reference to environment
	 * @param path - Path to component
	 * @return - Absolute path to component
	 * @throws Exception
	 */
	public String getAbsolutePath(Environment environment, String path) throws Exception {
		return Router.getAbsolutePath(environment.getProjectPath(), path, getPath());
	}

	/**
	 * Compute binary path with router
	 * @param environment - Reference to environment
	 * @param path - Path to component
	 * @return - Absolute path to component
	 * @throws Exception
	 */
	public String getBinaryPath(Environment environment, String path) throws Exception {
		return Router.getBinaryPath(environment.getProjectPath(), path, getPath());
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

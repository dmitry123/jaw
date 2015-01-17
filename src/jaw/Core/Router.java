package jaw.Core;

import jaw.Server.NanoHttpd;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Savonin on 2014-11-23
 */
public class Router extends Extension {

	/**
	 * @param environment - Every core's extension must have environment
	 * 		with predeclared extensions
	 */
	public Router(Environment environment) {
		super(environment);
	}

	/**
	 * Redirect router to path
	 * @param path - Path to controller
	 * @param action - Controller's action
	 */
	public void redirect(String path, String action) throws Exception {

		if (path.length() == 0) {
			path = action;
			action = "view";
		}

		// Find main controller
		controller = getEnvironment().getControllerManager()
				.get(path);

		if (controller != null) {

			// Find also it's view and model
			View view = getEnvironment().getViewManager().get(path);
			Model model = getEnvironment().getModelManager().get(path);

			// Find action method
			java.lang.reflect.Method[] controllerMethods = controller.getClass().getMethods();
			java.lang.reflect.Method actionMethod = null;

			for (java.lang.reflect.Method m : controllerMethods) {
				if (m.getName().toLowerCase().equals("action" + action.toLowerCase())) {
					actionMethod = m;
					break;
				}
			}

			// Initialize controller
			controller.setView(view);
			controller.setModel(model);

			// Initialize session
			controller.setSession(getSession());

			if (actionMethod != null) {
				try {
					actionMethod.invoke(controller);
				} catch (IllegalAccessException e) {
					throw new Exception(e.getCause().getMessage());
				} catch (InvocationTargetException e) {
					throw new Exception(e.getCause().getMessage());
				}
			} else {
				controller = null;
			}
		}
	}

	/**
	 * Compute absolute path for component by it's project directory. It
	 * will find element in filesystem, which for existence and build it's
	 * absolute path to file with java extension
	 * @param projectPath - Path to project
	 * @param componentPath - Name of component, which you'd like to find
	 * @param componentFolder - Name of component type's folder
	 * @see jaw.Core.Config
	 * @return - Absolute path to file (about project path)
	 * @throws Exception
	 */
	public static String getAbsolutePath(String projectPath, String componentPath, String componentFolder) throws Exception {
		return getPath(projectPath, componentPath, componentFolder, false, false) + ".java";
	}

	/**
	 * Compute binary path for compiled component by it's project directory. It
	 * will find element in filesystem, which for existence and build it's
	 * absolute path to file with java extension
	 * @param projectPath - Path to project
	 * @param componentPath - Name of component, which you'd like to find
	 * @param componentFolder - Name of component type's folder
	 * @see jaw.Core.Config
	 * @return - Absolute path to file (about project path)
	 * @throws Exception
	 */
	public static String getBinaryPath(String projectPath, String componentPath, String componentFolder) throws Exception {
		return getPath(projectPath.replace(Config.PROJECT_PATH, Config.BINARY_PATH), componentPath, componentFolder, true, false);
	}

	/**
	 * Compute static path for compiled component by it's project directory. It
	 * will find element in filesystem, which for existence and build it's
	 * absolute path to file with java extension
	 * @param projectPath - Path to project
	 * @param componentPath - Name of component, which you'd like to find
	 * @param componentFolder - Name of component type's folder
	 * @see jaw.Core.Config
	 * @return - Absolute path to file (about project path)
	 * @throws Exception
	 */
	public static String getStaticPath(String projectPath, String componentPath, String componentFolder) throws Exception {
		return getPath(projectPath, componentPath, componentFolder, false, true);
	}

	/**
	 * Compute path to file
	 * @param projectPath - Path to project
	 * @param componentPath - Name of component, which you'd like to find
	 * @param componentFolder - Name of component type's folder
	 * @see jaw.Core.Config
	 * @return - Path to file (about project path)
	 * @throws Exception
	 */
	private static String getPath(String projectPath, String componentPath, String componentFolder, boolean seekBinary, boolean withDot) throws Exception {
		File handle = new File(projectPath);
		if (!handle.exists()) {
			return null;
		}
		String[] listPath = componentPath.split("\\.");
		String componentName = listPath[listPath.length - 1];
		String absolutePath = projectPath;
		if (listPath.length > 1 && !Config.WIDGET_PATH.equals(listPath[0] + File.separator)) {
			absolutePath += Config.MODULE_PATH;
		}
		for (String s : listPath) {
			if (s == listPath[listPath.length - 1]) {
				break;
			}
			absolutePath += s + File.separator;
			// TODO Add sub modules support
		}
		if (!seekBinary) {
			absolutePath += componentFolder;
		}
		File directoryHandle = new File(
			absolutePath
		);
		if (!directoryHandle.exists()) {
			return null;
		}
		if (componentFolder.endsWith("/") || componentFolder.endsWith("\\")) {
			componentFolder = componentFolder.substring(0, componentFolder.length() - 1);
		}
		if (!seekBinary) {
			return absolutePath + componentName;
		}
		return absolutePath + componentFolder + (
			withDot ? "." : File.separator
		) + componentName;
	}

	/**
	 * @return - Current controller
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 *
	 * @param session
	 */
	public void setSession(NanoHttpd.IHTTPSession session) {
		this.session = session;
	}

	/**
	 *
	 * @return
	 */
	public NanoHttpd.IHTTPSession getSession() {
		return session;
	}

	private NanoHttpd.IHTTPSession session;
	private Controller controller;
}

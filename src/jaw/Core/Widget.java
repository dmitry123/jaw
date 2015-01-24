package jaw.Core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Savonin on 2015-01-16
 */
public abstract class Widget extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Widget(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		HashMap<String, Object> data
			= new HashMap<String, Object>();
		for (Map.Entry<String, String> entry : getSession().getParms().entrySet()) {
			data.put(entry.getKey(), entry.getValue());
		}
		run(data);
	}

	/**
	 * That method will run your widget with default data
	 * @throws Exception
	 */
	public void run() throws Exception {
		run(new HashMap<String, Object>());
	}

	/**
	 * Override that method to run your widget
	 * @param data - Data to run widget
	 */
	public abstract void run(HashMap<String, Object> data) throws Exception;

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	public abstract String getAlias();

	/**
	 * That method will render your method
	 */
	public void render() throws Exception {
		render(new HashMap<String, Object>());
	}

	/**
	 * That method will render your widget
	 * @param data - Render data
	 */
	public void render(HashMap<String, Object> data) throws Exception {

		String action = getClass().getName().substring(
			getClass().getName().lastIndexOf(".") + 1
		);

		data.put("url", "/" + getEnvironment().getProjectName());
		data.put("this", getController());

		loadVm(getVmWidgetPath(getClass().getName().substring(
			getClass().getName().lastIndexOf('.') + 1
		), action), data);
	}

	/**
	 * @return - Controller's view
	 */
	@Override
	public View getView() throws Exception {
		return getController().getView();
	}

	/**
	 * Calculate path ot VM file
	 * @param controllerName - Name of controller (folder with VM file)
	 * @param actionName - Name of controller's action (file with VM content)
	 * @return - Relative path to VM file with HTML content
	 * @throws Exception
	 */
	private String getVmWidgetPath(String controllerName, String actionName) throws Exception {
		return Config.PROJECT_PATH + getEnvironment().getProjectName() + File.separator
			+ Config.WIDGET_PATH + Config.VIEW_PATH + actionName + ".vm";
	}


	/**
	 * Set widget's controller
	 * @param controller - Controller's instance
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Get widget's controller
	 * @return - Controller's instance
	 */
	public Controller getController() {
		return controller;
	}

	private Controller controller;
}

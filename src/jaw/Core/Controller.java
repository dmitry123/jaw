package jaw.Core;

import jaw.Server.HtmlBuilder;
import jaw.Server.NanoHttpd;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Savonin on 2014-11-02
 */
public abstract class Controller extends Component {

	/**
	 * @param environment - Every core's extension must have environment
	 * 		with predeclared extensions
	 */
	public Controller(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	public abstract void actionView() throws Exception;

	/**
	 * Override that method to filter all actions
	 * @throws Exception
	 */
	public void actionFilter(String path, String action) throws Exception {
		/* Ignored */
	}

	/**
	 * Override that method to check privileges for action
	 * @param privileges - List with privileges or rules
	 * @return - True if access accepted else false
	 * @throws Exception
	 */
	public boolean checkAccess(String... privileges) throws Exception {

		Session session = getEnvironment().getSessionManager().get();

		if (session == null) {
			return false;
		}

		Model employeeModel = getEnvironment().getModelManager()
				.get("Employee");

		if (employeeModel == null) {
			return true;
		}

		for (String privilege : privileges) {
			if (!session.containsKey("employee")) {
				if (!employeeModel.fetchSet("fetchPrivilegeByUserID", session.getID(), privilege).next()) {
					return false;
				}
			} else {
				if (!employeeModel.fetchSet("fetchPrivilege", session.get("employee"), privilege).next()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Check access and post error message if it's false
	 * @param privileges - List with privileges or rules
	 * @return - True if access accepted else false
	 * @throws Exception
	 */
	public boolean checkAccessWithResponse(String... privileges) throws Exception {

		if (checkAccess(privileges)) {
			return true;
		}

		postErrorMessage("У Вас нет прав для совершения этого действия");

		return false;
	}

	/**
	 * Action error 404
	 * @throws Exception
	 */
	public void action404() throws Exception {
		getHtmlBuilder().getHtml()
			.h1().text("404");
	}

	/**
	 * Default action to get information from database, to allow access
	 * to table from controller override that method and invoke super method
	 * @throws Exception
	 */
	public void actionGetTable() throws Exception {

		final String action = GET("action");

		if (!checkAccessWithResponse("jaw/admin")) {
			return;
		}

		JSONObject json = new JSONObject();

		if (action.equals("fetch")) {

			String page = getSession().getParms().get("page");
			String limit = getSession().getParms().get("limit");

			Collection<LinkedHashMap<String, String>> wrapper = getModel().fetchTable(
				page != null ? Integer.parseInt(page) : 0,
				limit != null ? Integer.parseInt(limit) : 0,
				getSession().getParms().get("where"),
				getSession().getParms().get("order")
			);

			json.put("table", new JSONArray(wrapper.toArray()));
			json.put("length", json.getJSONArray("table").length());
			json.put("page", page != null ? Integer.parseInt(page) : 0);
			json.put("limit", limit != null ? Integer.parseInt(limit) : 0);
			json.put("pages", ((Model.Wrapper) wrapper).getPages());

		} else if (action.equals("add")) {

			getSession().getParms().remove("action");

			if (getSession().getParms().containsKey("hash")) {
				String password = getSession().getParms().remove("hash");
				getSession().getParms().put("hash",
						PasswordEncryptor.crypt(GET("login"), password)
				);
			}

			getModel().insert(
				getSession().getParms()
			);

		} else if (action.equals("update")) {

			Object id = GET("id");

			try {
				id = Integer.parseInt(id.toString());
			} catch (NumberFormatException ignored) {
			}

			getSession().getParms().remove("id");
			getSession().getParms().remove("action");

			if (getSession().getParms().containsKey("hash")) {
				String password = getSession().getParms().remove("hash");
				getSession().getParms().put("hash",
					PasswordEncryptor.crypt(GET("login"), password)
				);
			}

			getModel().updateByID(
				id, getSession().getParms()
			);

		} else if (action.equals("delete")) {

			Object id = GET("id");

			try {
				id = Integer.parseInt(id.toString());
			} catch (NumberFormatException ignored) {
			}

			getModel().deleteByID(id);

		} else if (action.equals("reference")) {

			String id = GET("id");
			String alias = GET("alias");

			getSession().getParms().remove("id");
			getSession().getParms().remove("action");

			json.put("reference", getModel().fetchReferences(alias, id));

		} else if (action.equals("all")) {

			json.put("rows", getModel().getRows());
			json.put("key", GET("key"));
			json.put("display", GET("display"));
			json.put("separator", GET("separator"));

		} else {
			postErrorMessage("Unknown action");
		}

		json.put("status", true);
		setAjaxResponse(json.toString());
	}

	/**
	 * That action returns model data for form, see jaw-form.js for client
	 * side. Override that method to check privileges and invoke super one
	 * @throws Exception
	 */
	public void actionGetForm() throws Exception {

		final String formName = GET("form");

		JSONObject json = new JSONObject();
		Form form = getForm(formName);

//		if (form != null) {
//			form.setModel(getModel());
//			Map<String, ResultSet> map = form.getForm();
//			Map<String, Collection<Object>> collection
//				= new LinkedHashMap<String, Collection<Object>>();
//			for (Map.Entry<String, ResultSet> entry : map.entrySet()) {
//				Collection<Object> vector = new Vector<Object>();
//				while (entry.getValue().next()) {
//					vector.add(Model.buildStaticMap(entry.getValue()));
//				}
//				collection.put(entry.getKey(), vector);
//			}
//			json.put("model", collection);
//			json.put("status", true);
//		} else {
//			json.put("message", "Невозможно найти форму для получения данных");
//			json.put("status", false);
//		}

		setAjaxResponse(json.toString());
	}

	/**
	 * Test post parameter and get it
	 * @param key - Key
	 * @return - Value
	 * @throws Exception
	 */
	public String POST(String key) throws Exception {
		if (!getSession().getMethod().name().toUpperCase().equals("POST")) {
			throw new Exception("POST." + key);
		}
		if (!getSession().getParms().containsKey(key) || getSession().getParms().get(key).length() == 0) {
			throw new Exception("POST." + key);
		}
		return getSession().getParms().get(key);
	}

	/**
	 * Test post parameter and get it
	 * @param key - Key
	 * @return - Value
	 * @throws Exception
	 */
	public String GET(String key) throws Exception {
		if (!getSession().getMethod().name().toUpperCase().equals("GET")) {
			throw new Exception("GET." + key);
		}
		if (!getSession().getParms().containsKey(key) || getSession().getParms().get(key).length() == 0) {
			throw new Exception("GET." + key);
		}
		return getSession().getParms().get(key);
	}

	/**
	 * Redirect router to path
	 * @param path - Path to controller
	 * @param action - Controller's action
	 */
	public void redirect(String path, String action) throws Exception {
		getEnvironment().getRouter().redirect(path, action);
	}

	/**
	 * Render VM file
	 * @param action - Action's name
	 * @throws Exception
	 */
	public void renderVm(String action) throws Exception {
		renderVm(action, new HashMap<String, Object>());
	}

	/**
	 * Load widget and run
	 * @throws Exception
	 */
	public void runWidget(String path) throws Exception {
		runWidget(path, new HashMap<String, Object>());
	}

	/**
	 * Load widget with json config
	 * @param path - Path to widget
	 * @param json - Json parameters
	 * @throws Exception
	 */
	public void runWidget(String path, String json) throws Exception {
		runWidget(path, jsonToMap(new JSONObject(json)));
	}

	/**
	 * Convert json array to vector
	 * @param node - Json array
	 * @return - Vector
	 * @throws Exception
	 */
	private Vector<Object> jsonToArray(JSONArray node) throws Exception {
		Vector<Object> data = new Vector<Object>();
		for (int i = 0; i < node.length(); i++) {
			Object object = node.get(i);
			if (object instanceof JSONObject) {
				object = jsonToMap((JSONObject) object);
			} else if (object instanceof JSONArray) {
				object = jsonToArray((JSONArray) object);
			}
			data.add(object);
		}
		return data;
	}

	/**
	 * Convert json object to hash map
	 * @param node - Json node
	 * @return - Hash map
	 * @throws Exception
	 */
	private HashMap<String, Object> jsonToMap(JSONObject node) throws Exception {
		HashMap<String, Object> data = new HashMap<String, Object>();
		for (String key : node.keySet()) {
			Object object = node.get(key);
			if (object instanceof JSONObject) {
				object = jsonToMap((JSONObject) object);
			} else if (object instanceof JSONArray) {
				object = jsonToArray((JSONArray) object);
			}
			data.put(key, object);
		}
		return data;
	}

	/**
	 * Load widget and run
	 * @param path - Path to widget
	 * @param data - Data for widget
	 * @throws Exception
	 */
	public void runWidget(String path, HashMap<String, Object> data) throws Exception {
		Widget widget = getEnvironment().getWidgetManager().get(path);
		if (widget == null) {
			throw new Exception("Unresolved widget path (" + path + ")");
		}
		widget.setController(this);
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		widget.run(data);
	}

	/**
	 * Render VM file with action and data
	 * @param action - Action's name
	 * @param hashData - Data to render
	 * @throws Exception
	 */
	public void renderVm(String action, Map<String, Object> hashData) throws Exception {

		action = action.substring(action.lastIndexOf(".") + 1);

		hashData.put("url", "/" + getEnvironment().getProjectName());
		hashData.put("this", this);

		loadVm(getVmPath("common", "header"), new HashMap<String, Object>());

		loadVm(getVmPath(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1
		), action), hashData);

		loadVm(getVmPath("common", "footer"), new HashMap<String, Object>());
	}

	/**
	 * Calculate path ot VM file
	 * @param controllerName - Name of controller (folder with VM file)
	 * @param actionName - Name of controller's action (file with VM content)
	 * @return - Relative path to VM file with HTML content
	 * @throws Exception
	 */
	private String getVmPath(String controllerName, String actionName) throws Exception {
		return Config.PROJECT_PATH + getEnvironment().getProjectName() + File.separator + Config.VIEW_PATH
			+ controllerName + File.separator + actionName + ".vm";
	}

	/**
	 * Load VM file and set view's content
	 * @param vmPath - Path to VM file
	 * @param hashData - Data to render
	 * @throws Exception
	 */
	protected void loadVm(String vmPath, Map<String, Object> hashData) throws Exception {

		if (!new File(vmPath).exists()) {
			throw new Exception("Can't resolve template name (" + getClass().getName() + File.separator + "." + vmPath + ")");
		}

		VelocityEngine engine = new VelocityEngine() {{
			init();
		}};

		VelocityContext context = new VelocityContext();
		StringWriter writer = new StringWriter();

		for (Map.Entry<String, Object> entry : hashData.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}

		engine.getTemplate(vmPath, "UTF-8").merge(context, writer);

		if (this instanceof Widget && getEnvironment().getMustacheDefiner() != null) {
			getEnvironment().getMustacheDefiner().put(((Widget) this).getAlias(),
				writer.toString()
			);
		} else if (getView() != null) {
			if (getView().getHtmlContent() != null) {
				getView().setHtmlContent(getView().getHtmlContent() + writer.toString());
			} else {
				getView().setHtmlContent(writer.toString());
			}
		}
	}

	/**
	 * Run view to render page with empty data map
	 * @throws Exception
	 */
	public void render(String action) throws Exception {
		render(action, new HashMap<String, Object>());
	}

	/**
	 * Run view to render page
	 * @throws Exception
	 */
	public void render(String action, Map<String, Object> hashData) throws Exception {

		// Return if view is null
		if (getView() == null) {
			return;
		}

		// Get all view's methods
		Method[] controllerMethods = getView().getClass().getMethods();
		Method renderMethod = null;

		// Find render method
		for (Method m : controllerMethods) {
			if (m.getName().toLowerCase().equals("render" + action.toLowerCase())) {
				renderMethod = m;
				break;
			}
		}

		// Invoke render method
		if (renderMethod != null) {
			try {
				renderMethod.invoke(getView(), hashData);
			} catch (IllegalAccessException e) {
				throw new Exception(e.getCause().getMessage());
			} catch (InvocationTargetException e) {
				throw new Exception(e.getCause().getMessage());
			}
		} else {
			throw new Exception(
				"Controller/render() : \"Unresolved render method (" + action + ")\""
			);
		}
	}

	/**
	 * Find form and return it's instance
	 * @return - Form's instance
	 * @throws Exception
	 */
	public Form getForm(String form) throws Exception {
		return getEnvironment().getFormManager().get(form);
	}

	/**
	 * @return - Current controller's model
	 */
	public Model getModel() throws Exception {
		return getModel(null);
	}

	/**
	 * @return - Current controller's model if
	 * 		path is null, else model by path
	 */
	public Model getModel(String path) throws Exception {
		if (path != null) {
			return getEnvironment().getModelManager().get(path);
		} else {
			return model;
		}
	}

	/**
	 * @param model - Model to set
	 */
	public void setModel(Model model) throws Exception {
		this.model = model;
	}

	/**
	 * @return - Current controller's view
	 */
	public View getView() throws Exception {
		return getView(null);
	}

	/**
	 * @param path - If path is null, then it will
	 * return default controller's view, else you
	 * can use that method to find another view
 	 * @return - Current controller's view
 	 */
	public View getView(String path) throws Exception {
		if (path != null) {
			return getEnvironment().getViewManager().get(path);
		} else {
			return view;
		}
	}

	/**
	 * @param view - View to set
	 */
	public void setView(View view) throws Exception {
		this.view = view;
	}

	/**
	 * @param message - Message to show
	 */
	public void postErrorMessage(String message) {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status", false);
		jsonResponse.put("message", message);
		setAjaxResponse(jsonResponse.toString());
	}

	/**
	 * @return - Current session
	 */
	public NanoHttpd.IHTTPSession getSession() {
		return session;
	}

	/**
	 * Set http session, you can set it only once
	 * @param session - Http session
	 * @throws Exception
	 */
	public void setSession(NanoHttpd.IHTTPSession session) throws Exception {
		this.session = session;
	}

	/**
	 * Set controller's module
	 * @param module - Module
	 */
	public void setModule(Module module) {
		this.module = module;
	}

	/**
	 * Get controller's module
	 * @return - Module
	 */
	public Module getModule() {
		return module;
	}

	/**
	 * Get html builder
	 * @return - Html builder
	 */
	public HtmlBuilder getHtmlBuilder() {
		return htmlBuilder;
	}

	/**
	 * @return - Ajax response string (JSON)
	 */
	public String getAjaxResponse() {
		return ajaxResponse;
	}

	/**
	 * Set action's ajax response
	 * @param ajaxResponse - Ajax response string (JSON)
	 */
	public void setAjaxResponse(String ajaxResponse) {
		this.ajaxResponse = ajaxResponse;
	}

	private NanoHttpd.IHTTPSession session = null;
	private HtmlBuilder htmlBuilder = new HtmlBuilder();
	private Model model = null;
	private View view  = null;
	private String ajaxResponse = null;
	private Module module = null;
}

package jaw.Core;

import jaw.Server.HtmlBuilder;
import jaw.Server.NanoHttpd;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
	 * Override that method to check privileges for action
	 * @param privileges - List with privileges or rules
	 * @return - True if access accepted else false
	 * @throws Exception
	 * @throws SQLException
	 */
	public boolean checkAccess(String... privileges) throws Exception {
		return true;
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

		if (checkAccess()) {

			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("action", action);

			if (action.equals("fetch")) {

				String page = getSession().getParms().get("page");
				String limit = getSession().getParms().get("limit");

				Collection<LinkedHashMap<String, String>> wrapper = getModel().fetchTable(
					page != null ? Integer.parseInt(page) : 0,
					limit != null ? Integer.parseInt(limit) : 0,
					getSession().getParms().get("where"),
					getSession().getParms().get("order")
				);

				jsonResponse.put("table", new JSONArray(wrapper.toArray()));
				jsonResponse.put("length", jsonResponse.getJSONArray("table").length());
				jsonResponse.put("page", page != null ? Integer.parseInt(page) : 0);
				jsonResponse.put("limit", limit != null ? Integer.parseInt(limit) : 0);
				jsonResponse.put("pages", ((Model.Wrapper)wrapper).getPages());

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

				getModel().deleteByID(
					Integer.parseInt(GET("id"))
				);

			} else if (action.equals("reference")) {

				String id = GET("id");
				String alias = GET("alias");

				getSession().getParms().remove("id");
				getSession().getParms().remove("action");

				jsonResponse.put("reference", getModel().fetchReferences(alias, id));

			} else if (action.equals("all")) {

				jsonResponse.put("rows", getModel().getRows());
				jsonResponse.put("key", GET("key"));
				jsonResponse.put("display", GET("display"));
				jsonResponse.put("separator", GET("separator"));

			} else {
				postErrorMessage("Unknown action");
			}
			jsonResponse.put("status", true);
			setAjaxResponse(jsonResponse.toString());
		} else {
			postErrorMessage("Недостаточно прав");
		}
	}

	/**
	 * Test post parameter and get it
	 * @param key - Key
	 * @return - Value
	 * @throws Exception
	 */
	public String POST(String key) throws Exception {
		if (!getSession().getMethod().name().toUpperCase().equals("POST")) {
			throw new Exception("post." + key);
		}
		if (!getSession().getParms().containsKey(key) || getSession().getParms().get(key).length() == 0) {
			throw new Exception("post." + key);
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
			throw new Exception("get." + key);
		}
		if (!getSession().getParms().containsKey(key) || getSession().getParms().get(key).length() == 0) {
			throw new Exception("get." + key);
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
		if (this.view != null) {
			throw new Exception("Controller/setModel() : \"Controller already has view\"");
		}
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
	 *
	 * @return
	 */
	public HtmlBuilder getHtmlBuilder() {
		return htmlBuilder;
	}

	/**
	 *
	 * @return
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 *
	 * @param inputStream
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 *
	 * @return
	 */
	public String getAjaxResponse() {
		return ajaxResponse;
	}

	/**
	 *
	 * @param ajaxResponse
	 */
	public void setAjaxResponse(String ajaxResponse) {
		this.ajaxResponse = ajaxResponse;
	}

	private NanoHttpd.IHTTPSession session = null;
	private HtmlBuilder htmlBuilder = new HtmlBuilder();
	private Model model = null;
	private View view  = null;
	private InputStream inputStream = null;
	private String ajaxResponse = null;
}
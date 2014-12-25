package Core;

import Server.HtmlBuilder;
import Server.NanoHttpd;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.security.util.Password;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
	public abstract void actionView() throws InternalError, SQLException;

	/**
	 * Override that method to check privileges for action
	 * @param privileges - List with privileges or rules
	 * @return - True if access accepted else false
	 * @throws InternalError
	 * @throws SQLException
	 */
	public boolean checkAccess(String... privileges) throws InternalError, SQLException {
		return true;
	}

	/**
	 * Action error 404
	 * @throws InternalError
	 */
	public void action404() throws InternalError {
		getHtmlBuilder().getHtml()
			.h1().text("404");
	}

	/**
	 * Default action to get information from database, to allow access
	 * to table from controller override that method and invoke super method
	 * @throws InternalError
	 * @throws SQLException
	 */
	public void actionGetTable() throws InternalError, SQLException {
		final String action = GET("action");
		if (checkAccess()) {
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("action", action);
			if (action.equals("fetch")) {
				jsonResponse.put("table", new JSONArray(
					getModel().fetchTable(0, 0)
				));
			} else if (action.equals("add")) {
			} else if (action.equals("update")) {
				int id = Integer.parseInt(GET("id"));
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
	 * @throws InternalError
	 */
	public String POST(String key) throws InternalError {
		if (!getSession().getMethod().name().toUpperCase().equals("POST")) {
			throw new InternalError("post." + key);
		}
		if (!getSession().getParms().containsKey(key) || getSession().getParms().get(key).length() == 0) {
			throw new InternalError("post." + key);
		}
		return getSession().getParms().get(key);
	}

	/**
	 * Test post parameter and get it
	 * @param key - Key
	 * @return - Value
	 * @throws InternalError
	 */
	public String GET(String key) throws InternalError {
		if (!getSession().getMethod().name().toUpperCase().equals("GET")) {
			throw new InternalError("get." + key);
		}
		if (!getSession().getParms().containsKey(key) || getSession().getParms().get(key).length() == 0) {
			throw new InternalError("get." + key);
		}
		return getSession().getParms().get(key);
	}

	/**
	 * Redirect router to path
	 * @param path - Path to controller
	 * @param action - Controller's action
	 */
	public void redirect(String path, String action) throws InternalError {
		getEnvironment().getRouter().redirect(path, action);
	}

	/**
	 * Run view to render page with empty data map
	 * @throws InternalError
	 */
	public void render(String action) throws InternalError {
		render(action, new HashMap<String, Object>());
	}

	/**
	 * Run view to render page
	 * @throws Core.InternalError
	 */
	public void render(String action, Map<String, Object> hashData) throws InternalError {

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
				throw new InternalError(e.getCause().getMessage());
			} catch (InvocationTargetException e) {
				throw new InternalError(e.getCause().getMessage());
			}
		} else {
			throw new InternalError(
				"Controller/render() : \"Unresolved render method (" + action + ")\""
			);
		}
	}

	/**
	 * @return - Current controller's model
	 */
	public Model getModel() throws InternalError {
		return getModel(null);
	}

	/**
	 * @return - Current controller's model if
	 * 		path is null, else model by path
	 */
	public Model getModel(String path) throws InternalError {
		if (path != null) {
			return getEnvironment().getModelManager().get(path);
		} else {
			return model;
		}
	}

	/**
	 * @param model - Model to set
	 */
	public void setModel(Model model) throws InternalError {
		if (this.model != null) {
			throw new InternalError("Controller/setModel() : \"Controller already has model\"");
		}
		this.model = model;
	}

	/**
	 * @return - Current controller's view
	 */
	public View getView() throws InternalError {
		return getView(null);
	}

	/**
	 * @param path - If path is null, then it will
	 * return default controller's view, else you
	 * can use that method to find another view
 	 * @return - Current controller's view
 	 */
	public View getView(String path) throws InternalError {
		if (path != null) {
			return getEnvironment().getViewManager().get(path);
		} else {
			return view;
		}
	}

	/**
	 * @param view - View to set
	 */
	public void setView(View view) throws InternalError {
		if (this.view != null) {
			throw new InternalError("Controller/setModel() : \"Controller already has view\"");
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
	 * @throws InternalError
	 */
	public void setSession(NanoHttpd.IHTTPSession session) throws InternalError {
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

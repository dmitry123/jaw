package Core;

import Server.HtmlBuilder;
import Server.NanoHttpd;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	public abstract void actionView() throws InternalError;

	/**
	 * Action error 404
	 * @throws InternalError
	 */
	public void action404() throws InternalError {
		getHtmlBuilder().getHtml()
			.h1().text("404");
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

package jaw.Core;

import jaw.Sql.Connection;

import java.io.File;

/**
 * Created by Savonin on 2014-11-08
 */
public class Environment {

	/**
	 * Construct environment
	 * @param connection - Database connection
	 * @param projectName - Project's folder name
	 * @throws Exception
	 */
	public Environment(Connection connection, String projectName) throws Exception {

		if (connection == null || connection.isClosed()) {
			connection = new Connection();
		}

		if (projectName.endsWith("\\") || projectName.endsWith("/")) {
			projectName = projectName.substring(0, projectName.length() - 1);
		}

		this.connection = connection;
		this.projectName = projectName;

		if (!projectName.endsWith(File.separator)) {
			projectName += File.separator;
		}

		this.projectPath = Config.PROJECT_PATH + projectName;
	}

	/**
	 * @return - Collection with all managers
	 */
	public ManagerCollection getManagerCollection() {
		return managerCollection;
	}

	/**
	 * @return - Project's model manager
	 */
	public AbstractManager<Model> getModelManager() {
		return modelManager;
	}

	/**
	 * @return - Project's form manager
	 */
	public AbstractManager<Form> getFormManager() {
		return formManager;
	}

	/**
	 * @return - Controller manager
	 */
	public AbstractManager<Controller> getControllerManager() {
		return controllerManager;
	}

	/**
	 * @return - View manager
	 */
	public AbstractManager<View> getViewManager() {
		return viewManager;
	}

	/**
	 * @return - Module manager
	 */
	public AbstractManager<Module> getModuleManager() {
		return moduleManager;
	}

	/**
	 * @return - Widget manager
	 */
	public AbstractManager<Widget> getWidgetManager() {
		return widgetManager;
	}

	/**
	 * @return - Project's manager
	 */
	public ProjectManager getProjectManager() {
		return projectManager;
	}

	/**
	 * @return - Component's factory
	 */
	public ComponentFactory getComponentFactory() {
		return componentFactory;
	}

	/**
	 * @return - Router
	 */
	public Router getRouter() {
		return router;
	}

	/**
	 * @return - User session manager
	 */
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	/**
	 * @return - Current user's session
	 */
	public Session getSession() {
		return getSessionManager().get();
	}

	/**
	 *
	 * @return - Mustache definer
	 */
	public MustacheDefiner getMustacheDefiner() {
		if (getSession() != null) {
			return getSession().getMustacheDefiner();
		} else {
			return mustacheDefiner;
		}
	}

	private MustacheDefiner mustacheDefiner = new MustacheDefiner();

	/**
	 * @return - Path to project
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * @return - Project's name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return - Database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @return - Session's id
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID - Session's id
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	private ManagerCollection managerCollection = new ManagerCollection(this);
	private AbstractManager<Model> modelManager = managerCollection.getModelManager();
	private AbstractManager<Form> formManager = managerCollection.getFormManager();
	private AbstractManager<Controller> controllerManager = managerCollection.getControllerManager();
	private AbstractManager<View> viewManager = managerCollection.getViewManager();
	private AbstractManager<Module> moduleManager = managerCollection.getModuleManager();
	private AbstractManager<Widget> widgetManager = managerCollection.getWidgetManager();
	private String projectName;
	private String projectPath;
	private Connection connection;
	private String sessionID;
	private ProjectManager projectManager = new ProjectManager(this);
	private ComponentFactory componentFactory = new ComponentFactory(this);
	private Router router = new Router(this);
	private SessionManager sessionManager = new SessionManager(this);
}

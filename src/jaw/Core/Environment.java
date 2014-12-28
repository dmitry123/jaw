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
	 * @return - Project's model manager
	 */
	public ModelManager getModelManager() {
		return modelManager;
	}

	private ModelManager modelManager
			= new ModelManager(this);

	/**
	 * @return - Component's manager
	 */
	public ComponentManager getComponentManager() {
		return componentManager;
	}

	private ComponentManager componentManager
			= new ComponentManager(this);

	/**
	 * @return - Controller manager
	 */
	public ControllerManager getControllerManager() {
		return controllerManager;
	}

	private ControllerManager controllerManager
			= new ControllerManager(this);

	/**
	 * @return - View manager
	 */
	public ViewManager getViewManager() {
		return viewManager;
	}

	private ViewManager viewManager
			= new ViewManager(this);

	/**
	 * @return - User's validator
	 */
	public UserValidator getUserValidator() {
		return userValidator;
	}

	private UserValidator userValidator
			= new UserValidator(this);

	/**
	 * @return - Project's manager
	 */
	public ProjectManager getProjectManager() {
		return projectManager;
	}

	private ProjectManager projectManager
			= new ProjectManager(this);

	/**
	 * @return - Component's factory
	 */
	public ComponentFactory getComponentFactory() {
		return componentFactory;
	}

	private ComponentFactory componentFactory
			= new ComponentFactory(this);

	/**
	 * @return - Router
	 */
	public Router getRouter() {
		return router;
	}

	private Router router
			= new Router(this);

	/**
	 * @return - User session manager
	 */
	public UserSessionManager getUserSessionManager() {
		return userSessionManager;
	}

	private UserSessionManager userSessionManager
			= new UserSessionManager(this);

	/**
	 *
	 * @return - Mustache definer
	 */
	public MustacheDefiner getMustacheDefiner() {
		return mustacheDefiner;
	}

	private MustacheDefiner mustacheDefiner
			= new MustacheDefiner(this);

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

	private String projectName;
	private String projectPath;
	private Connection connection;
	private String sessionID;
}

package Core;

import Sql.Connection;

import java.io.File;

/**
 * Created by Savonin on 2014-11-08
 */
public class Environment {

	/**
	 * Construct environment
	 * @param connection - Database connection
	 * @param projectPath - Path to project
	 * @throws InternalError
	 */
	public Environment(Connection connection, String projectPath) throws InternalError {

		if (connection == null || !connection.isClosed()) {
			connection = new Connection();
		}

		this.connection = connection;
		this.projectName = projectPath;

		if (!projectPath.endsWith(File.separator)) {
			projectPath += File.separator;
		}

		this.projectPath = projectPath;
	}

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
	 * @return - Project's model manager
	 */
	public ModelManager getModelManager() {
		return modelManager;
	}

	/**
	 * @return - User's validator
	 */
	public UserValidator getUserValidator() {
		return userValidator;
	}

	/**
	 * @return - Project's manager
	 */
	public ProjectManager getProjectManager() {
		return projectManager;
	}

	/**
	 * @return - Database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	private ModelManager modelManager
			= new ModelManager(this);

	private UserValidator userValidator
			= new UserValidator(this);

	private ProjectManager projectManager
			= new ProjectManager(this);

	private String projectName;
	private String projectPath;
	private Connection connection;
}

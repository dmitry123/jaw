package Core;

import java.io.File;

/**
 * Config
 */
public class Config {

	/**
	 * Main DBMS constants, where :
	 *
	 * 	DBMS_HOST - database's host
	 * 		MySQL : "jdbc:mysql://localhost/jaw"
	 * 		PostgreSQL : "jdbc:postgresql://localhost/jaw"
	 * 	DBMS_USER - database's user
	 * 	DBMS_PASSWORD - database's password
	 * 	DBMS_DRIVER - database's driver
	 * 		MySQL : "com.mysql.jdbc.Driver"
	 * 		PostgreSQL : "org.postgresql.Driver"
	 */
	public final static String DBMS_HOST = "jdbc:postgresql://127.0.0.1/jaw";
	public final static String DBMS_USER = "postgres";
	public final static String DBMS_PASSWORD = "12345";
	public final static String DBMS_DRIVER = "org.postgresql.Driver";

	/**
	 * Basic project directories, where :
	 *
	 *  SERVER_PATH - path to server with all
	 *  	content likes controllers, models etc
	 *
	 *  TEMPLATE_PATH - path to all default
	 *  	templates, which will be used in system
	 *
	 *  CONTROLLER_PATH - path to all project's
	 *  	controllers
	 *
	 *  MODEL_PATH - path to project's models which
	 *  	will be used by controller
	 *
	 *  VIEW_PATH - path to view folder with
	 *  	view class, which will render scene
	 *
	 *  SCRIPT_PATH - all javascript files will be
	 *  	in that path and automatically attached
	 *  	to your view
	 *
	 *  MODULE_PATH - path to all modules, which can
	 *  	be attached to main project
	 *
	 *  WIDGET_PATH - path to widgets (extended templates)
	 *  	which can store own controllers and views as
	 *  	modules, by it can't have inline widgets or
	 *  	other components
	 *  JAVA_PATH - path to java folder with project's
	 *  	sources
	 *
	 *  BINARY_PATH - path to all compiled binaries
	 */
	public static final String SERVER_PATH     = "server"      + File.separator;
	public static final String TEMPLATE_PATH   = "templates"   + File.separator;
	public static final String CONTROLLER_PATH = "controllers" + File.separator;
	public static final String MODEL_PATH      = "models"      + File.separator;
	public static final String VIEW_PATH       = "views"       + File.separator;
	public static final String SCRIPT_PATH     = "scripts"     + File.separator;
	public static final String MODULE_PATH     = "modules"     + File.separator;
	public static final String WIDGET_PATH     = "widgets"     + File.separator;
	public static final String BINARY_PATH     = "binaries"    + File.separator;
	public static final String LOG_PATH        = "log"         + File.separator;

	/**
	 * Default index page
	 */
	public static final String SESSION_PATH = SERVER_PATH + "sessions" + File.separator;
	public static final String INDEX_PATH = SERVER_PATH + "views/index.html";
	public static final String PROJECT_PATH = SERVER_PATH + "projects" + File.separator;

	/**
	 * Default server port
	 */
	public static final int SERVER_PORT = 9000;
}

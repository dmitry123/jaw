import Core.*;
import Core.InternalError;
import Sql.Connection;

/**
 * Entry point
 */
public class Main {

	/**
	 * @param args - List with application arguments
	 */
    public static void main(String[] args) {
		try {
			Environment environment = new Environment(new Connection(
					Config.DBMS_HOST,
					Config.DBMS_USER,
					Config.DBMS_PASSWORD
			), "jaw");

//			environment.getProjectManager().getCompiler().compile();

			ComponentFactory componentFactory = new ComponentFactory(environment);

			Controller c = componentFactory.create("controllers.Index");
			Model m = componentFactory.create("models.Index");

			c.setModel(m);

		} catch (InternalError e) {
			Logger.getLogger().log(e.getMessage());
			e.printStackTrace();
		}
	}
}
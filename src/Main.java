import Core.*;
import Sql.Connection;

/**
 * Entry point
 */
public class Main {

	/**
	 * @param args - List with application arguments
	 */
    public static void main(String[] args) throws Exception {

		Environment environment = new Environment(new Connection(
			Config.DBMS_HOST,
			Config.DBMS_USER,
			Config.DBMS_PASSWORD
		), "jaw");

		environment.getProjectManager().getCompiler().compile();
	}
}
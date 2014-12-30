import jaw.Core.*;
import jaw.Server.WebServer;

/**
 * Entry point
 */
public class Main {

	/**
	 * @param args - List with application arguments
	 */
    public static void main(String[] args) {
		try {
			// Mary's comment here
			WebServer.run();
		} catch (Throwable e) {
			Logger.getLogger().log(e.getMessage()); e.printStackTrace();
		}
	}
}
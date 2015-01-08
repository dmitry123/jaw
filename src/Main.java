import jaw.core.*;
import jaw.server.WebServer;

/**
 * Entry point
 */
public class Main {

	/**
	 * @param args - List with application arguments
	 */
    public static void main(String[] args) {
		try {
			WebServer.run();
		} catch (Throwable e) {
			Logger.getLogger().log(
				e.getMessage()
			);
			e.printStackTrace();
		}
	}
}
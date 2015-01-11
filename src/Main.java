import jaw.core.Logger;
import jaw.server.WebServer;
import org.apache.velocity.app.Velocity;

/**
 * Entry point
 */
public class Main {

	static {
		Velocity.init();
	}

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
import jaw.Core.*;
import jaw.Server.WebServer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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
			Logger.getLogger().log(e.getMessage()); e.printStackTrace();
		}
	}

	/**
	 * The main entry point for all JavaFX applications. The start method is called after the init method has returned,
	 * and after the system is ready for the application to begin running.
	 * <p/>
	 * <p> NOTE: This method is called on the JavaFX Application Thread. </p>
	 * @param stage the primary stage for this application, onto which the application scene can be set. The
	 * primary stage will be embedded in the browser if the application was launched as an applet. Applications may
	 * create other stages, if needed, but they will not be primary stages and will not be embedded in the browser.
	 */
	public void start(Stage stage) throws Exception {
		Circle circle = new Circle(40, 40, 30);
		Group root = new Group(circle);
		Scene scene = new Scene(root, 400, 300);
		stage.setTitle("My JavaFX Application");
		stage.setScene(scene);
		stage.show();
	}
}
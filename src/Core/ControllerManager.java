package Core;

/**
 * Created by dmitry on 26.11.14
 */
public class ControllerManager extends Manager<Controller> {

	/**
	 * @param environment - Project's environment
	 */
	public ControllerManager(Environment environment) {
		super(environment, ComponentType.CONTROLLER);
	}
}

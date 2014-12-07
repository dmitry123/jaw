package Core;

/**
 * Created by dmitry on 26.11.14
 */
public class ViewManager extends Manager<View> {

	/**
	 * @param environment - Project's environment
	 */
	public ViewManager(Environment environment) {
		super(environment, ComponentType.VIEW);
	}
}

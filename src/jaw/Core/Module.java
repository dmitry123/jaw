package jaw.Core;

/**
 * Created by Savonin on 2015-01-08
 */
public abstract class Module extends Component {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Module(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to filter all module's actions
	 * @param controller - Path to controller
	 * @param action - Name of controller's action
	 * @return - False if access denied
	 * @throws Exception
	 */
	public abstract boolean filter(String controller, String action) throws Exception;
}

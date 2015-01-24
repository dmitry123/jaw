package jaw.modules;

import jaw.Core.Environment;
import jaw.Core.Module;

/**
 * Created by Savonin on 2015-01-08
 */
public class Admin extends Module {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Admin(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to filter all module's actions
	 * @param controller - Path to controller
	 * @param action - Name of controller's action
	 * @return - False if access denied
	 * @throws Exception
	 */
	@Override
	public boolean filter(String controller, String action) throws Exception {
		return true;
	}
}

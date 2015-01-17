package jaw.Core;

/**
 * Created by Savonin on 2015-01-08
 */
public class ModuleManager extends Manager<Module> {

	/**
	 * @param environment - Project's environment
	 */
	public ModuleManager(Environment environment) {
		super(environment, ComponentType.MODULE);
	}
}

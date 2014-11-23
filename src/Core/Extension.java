package Core;

/**
 * Created by Savonin on 2014-11-22
 */
public class Extension {

	/**
	 * @param environment - Every core's extension must have environment
	 * 		with predeclared extensions
	 */
	public Extension(Environment environment) {
		this.environment = environment;
	}

	/**
	 * @return - Current extension's environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	private Environment environment;
}

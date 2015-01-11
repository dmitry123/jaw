package jaw.core;

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

	/**
	 * Set environment (only for serializable classes)
	 * @param environment - New environment
	 */
	protected void setEnvironment(Environment environment) {
		if (this.environment != null) {
			this.environment = environment;
		}
	}

	private Environment environment;
}

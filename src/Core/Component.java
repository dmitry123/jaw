package Core;

/**
 * Created by dmitry on 18.11.14
 */
public abstract class Component extends Extension implements Protocol {

	/**
	 * @param environment - Every core's extension must have environment
	 *                    with predeclared extensions
	 */
	public Component(Environment environment) {
		super(environment);
	}
}

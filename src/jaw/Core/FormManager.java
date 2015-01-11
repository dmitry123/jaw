package jaw.core;

/**
 * Created by dmitry on 26.11.14
 */
public class FormManager extends Manager<Form> {

	/**
	 * @param environment - Project's environment
	 */
	public FormManager(Environment environment) {
		super(environment, ComponentType.FORM);
	}
}

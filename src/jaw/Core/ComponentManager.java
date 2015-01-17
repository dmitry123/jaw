package jaw.Core;

public class ComponentManager extends Manager<Component> {

	/**
	 * @param environment - Project's environment
	 */
	public ComponentManager(Environment environment) {
		super(environment, ComponentType.COMPONENT);
	}
}

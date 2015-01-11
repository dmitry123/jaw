package jaw.core;

/**
 * Created by Savonin on 2014-11-08
 */
public class ModelManager extends Manager<Model> {

	/**
	 * @param environment - Project's environment
	 */
	public ModelManager(Environment environment) {
		super(environment, ComponentType.MODEL);
	}
}

package Core;

import java.io.File;

/**
 * Created by Savonin on 2014-11-08
 */
public class ModelManager extends Manager<Model> {

	/**
	 * @param environment - Project's environment
	 */
	public ModelManager(Environment environment) {
		super(environment, Type.MODEL);
	}

	/**
	 * @param directory - Directories to component
	 * @param file
	 * @return - Found component in filesystem
	 */
	@Override
	protected Model find(File directory, String file) {
		return null;
	}
}

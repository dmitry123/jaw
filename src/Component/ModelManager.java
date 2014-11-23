package Component;

import Core.Environment;

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
	 * @param dirs - Directories to component
	 * @return - Found component in filesystem
	 */
	@Override
	protected Model find(File[] dirs) {

		for (File f : dirs) {
			System.out.println(f.getName());
		}

		return null;
	}
}

package Core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Savonin on 2014-11-24
 */
public class ComponentFactory extends Extension {

	/**
	 * Construct factory with basic environment
	 * @param environment - Project's environment
	 */
	public ComponentFactory(Environment environment) {
		super(environment);
	}

	/**
	 * Create new component by it's name (package)
	 * @param className - Component's class name
	 * @param <T> - Component's type (Abstract)
	 * @return - New component's instance
	 * @throws InternalError
	 */
	public <T> T create(String className) throws InternalError {

		Class<T> modelClass = loadClass(className);
		Constructor<T> constructor;

		try {
			constructor = (Constructor<T>) modelClass.getConstructor(
				Environment.class
			);
		} catch (NoSuchMethodException e) {
			throw new InternalError("ComponentFactory/createModel() : \"" + e.getMessage() + "\"");
		}

		return constructClass(constructor, getEnvironment());
	}

	/**
	 * Construct class with default constructor and create new instance
	 * @param constructor - Default constructor
	 * @param arguments - List with arguments
	 * @param <I> - Instance type
	 * @return - New type instance
	 * @throws InternalError
	 */
	private <I> I constructClass(Constructor<I> constructor, Object... arguments) throws InternalError {
		try {
			return constructor.newInstance(
				arguments
			);
		} catch (InstantiationException e) {
			throw new InternalError(
					"ComponentFactory/createModel() : \"" + e.getMessage() + "\""
			);
		} catch (IllegalAccessException e) {
			throw new InternalError(
					"ComponentFactory/createModel() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new InternalError(
					"ComponentFactory/createModel() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Load class from package
	 * @param className - Class's name with package
	 * @param <C> - Class's type
	 * @return - Loaded class
	 * @throws InternalError
	 */
	private <C> Class<C> loadClass(String className) throws InternalError {

		File binaryDir = new File(getEnvironment().getProjectPath() + Config.BINARY_PATH);

		if (!binaryDir.exists()) {
			throw new InternalError(
				"ClassSeeker() : \"Unable to open binary directory\""
			);
		}

		URL url;

		try {
			url = binaryDir.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new InternalError("ComponentFactory/loadClass() : \"" + e.getMessage() + "\"");
		}

		ClassLoader classLoader = new URLClassLoader(new URL[] {
			url
		});

		try {
			return (Class<C>) classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new InternalError("ComponentFactory/loadClass() : \"" + e.getMessage() + "\"");
		}
	}
}

package jaw.Core;

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
	 * @throws Exception
	 */
	public <T> T create(String className) throws Exception, ClassNotFoundException {

		Class<T> modelClass = loadClass(className);
		Constructor<T> constructor;

		try {
			constructor = modelClass.getConstructor(
				Environment.class
			);
		} catch (NoSuchMethodException e) {
			throw new Exception("ComponentFactory/createModel() : \"" + e.getMessage() + "\"");
		}

		return constructClass(constructor, getEnvironment());
	}

	/**
	 * Construct class with default constructor and create new instance
	 * @param constructor - Default constructor
	 * @param arguments - List with arguments
	 * @param <I> - Instance type
	 * @return - New type instance
	 * @throws Exception
	 */
	private <I> I constructClass(Constructor<I> constructor, Object... arguments) throws Exception {
		try {
			return constructor.newInstance(
				arguments
			);
		} catch (InstantiationException e) {
			throw new Exception(
				"ComponentFactory/createModel() : \"" + e.getMessage() + "\""
			);
		} catch (IllegalAccessException e) {
			throw new Exception(
				"ComponentFactory/createModel() : \"" + e.getMessage() + "\""
			);
		} catch (InvocationTargetException e) {
			throw new Exception(
				"ComponentFactory/createModel() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Load class from package
	 * @param className - Class's name with package
	 * @param <C> - Class's type
	 * @return - Loaded class
	 * @throws Exception
	 */
	private <C> Class<C> loadClass(String className) throws Exception, ClassNotFoundException {

		String binaryPath = getEnvironment().getProjectPath() + Config.BINARY_PATH;

		if (className.startsWith(binaryPath)) {
			className = className.substring(binaryPath.length());
		}

		File binaryDir = new File(binaryPath);

		if (!binaryDir.exists()) {
			throw new Exception(
				"ClassSeeker() : \"Unable to open binary directory\""
			);
		}

		URL url;

		try {
			url = binaryDir.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new Exception("ComponentFactory/loadClass() : \"" + e.getMessage() + "\"");
		}

		ClassLoader classLoader = new URLClassLoader(new URL[] {
			url
		});

		String[] names = className.split("\\\\|/");

		if (names.length < 2) {
			throw new ClassNotFoundException(className);
		}

		File fileDir = new File(
			binaryDir.getPath() + File.separator + names[0]
		);

		if (!fileDir.exists()) {
			throw new ClassNotFoundException(className);
		}

		String[] files = fileDir.list();

		for (String s : files) {
			if (s.toLowerCase().equals((names[1] + ".class").toLowerCase())) {
				return (Class<C>) classLoader.loadClass(
					names[0] + "." + s.replace(".class", "")
				);
			}
		}

		return (Class<C>) classLoader.loadClass(className);
	}
}

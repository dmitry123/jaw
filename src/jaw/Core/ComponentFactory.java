package jaw.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Vector;

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
	public <T> T create(String className) throws Exception {

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
	private <C> Class<C> loadClass(String className) throws Exception {

		String binaryPath = Config.BINARY_PATH.substring(0, Config.BINARY_PATH.length() - 1);
		File binaryDir = new File(binaryPath);

		if (className.startsWith(binaryPath)) {
			className = className.substring(binaryPath.length() + 1);
		}

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

		String[] names = className.split("[\\\\|/\\.]");
		String name = names[names.length - 1];

//		if (names.length < 2) {
//			throw new ClassNotFoundException(className);
//		}
//
//		File fileDir = new File(Config.BINARY_PATH);
//
//		if (!fileDir.exists()) {
//			throw new ClassNotFoundException(className);
//		}
//

		Vector<String> files = new Vector<String>(100);
		findFiles(files, Config.BINARY_PATH);

		for (String s : files) {
			s = s.substring(s.lastIndexOf(File.separatorChar) + 1);
			if (s.toLowerCase().equals((name + ".class").toLowerCase())) {
//				s = s.substring((Config.BINARY_PATH + getEnvironment().getProjectName()).length() + 1);
//				s = s.substring(s.indexOf(File.separator) + 1);
				s = names[0] + "." + names[1] + "." + s.replace(".class", "");
				return (Class<C>) classLoader.loadClass(s);
			}
		}

		return (Class<C>) classLoader.loadClass(className);
	}

	/**
	 * Find files at path with depth and store it in collection
	 * @param collection - Collection to store elements
	 * @param path - Path to directory with files
	 * @throws Exception
	 */
	private static void findFiles(Collection<String> collection, String path) throws Exception {
		File handle = new File(path);
		if (!handle.exists()) {
			if (!handle.mkdir()) {
				throw new Exception(
					"ClassSeeker/findFiles() : \"Unable to create directory (" + handle.getPath() + ")\""
				);
			}
			return ;
		}
		File[] files = handle.listFiles();
		if (files == null) {
			return ;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				findFiles(collection, f.getPath());
			} else {
				if (!f.getName().endsWith(".class")) {
					continue;
				}
				collection.add(f.getPath());
			}
		}
	}
}

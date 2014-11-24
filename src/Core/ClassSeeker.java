package Core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by dmitry on 18.11.14
 */
public class ClassSeeker extends Extension {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public ClassSeeker(Environment environment) {
		super(environment);
	}

	/**
	 * Find files at some path with annotation
	 * @param annotation - Annotation's class
	 * @return - Collection with found classes
	 * @throws InternalError
	 */
	public Collection<Class> findByAnnotation(Class annotation) throws InternalError {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		Vector<String> files = new Vector<String>();
		Vector<Class> classes = new Vector<Class>();

		findFiles(files, getEnvironment().getProjectPath() + Config.BINARY_PATH);

		for (String s : files) {
			Class c;
			try {
				c = loader.loadClass(s);
			} catch (ClassNotFoundException e) {
				throw new InternalError("ClassSeeker/findByAnnotation() : \"" + e.getMessage() + "\"");
			}
			System.out.println(c.getAnnotation(annotation));
		}

		return classes;
	}

	/**
	 * Find files at path with depth and store it in collection
	 * @param collection - Collection to store elements
	 * @param path - Path to directory with files
	 * @throws InternalError
	 */
	private void findFiles(Collection<String> collection, String path) throws InternalError {

		File handle = new File(path);

		if (!handle.exists()) {
			if (!handle.mkdir()) {
				throw new InternalError(
					"ClassSeeker/findFiles() : \"Unable to create directory (" + handle.getPath() + ")\""
				);
			}
			return ;
		}

		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}

		File[] files = handle.listFiles();

		if (files == null) {
			return ;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				findFiles(collection, f.getAbsolutePath());
			} else {
				if (!f.getName().endsWith(".class")) {
					continue;
				}
				collection.add(f.getAbsolutePath());
			}
		}
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 *
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Iterable<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();

		while (resources.hasMoreElements()) {
			dirs.add(new File(resources.nextElement().getFile()));
		}

		List<Class> classes = new ArrayList<Class>();

		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}

		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * sub-directories.
	 *
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {

		List<Class> classes = new ArrayList<Class>();

		if (!directory.exists()) {
			return classes;
		}

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}

		return classes;
	}
}

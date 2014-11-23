package Component;

import Core.*;

import java.io.File;
import java.util.HashMap;

/**
 * Created by dmitry on 18.11.14
 */
public abstract class Manager<C extends Component> extends Extension {

	/**
	 * @param environment - Project's environment
	 */
	public Manager(Environment environment, Type type) {
		super(environment); this.type = type;
	}

	/**
	 * @param path - Path to component
	 * @return - Found component
	 */
	public C get(String path) throws Core.InternalError {

		C component = getCached(path);

		if (component != null) {
			return component;
		}

		File handle = new File(
			type.getPath()
		);

		if (!handle.exists()) {
			return null;
		}

		if ((component = find(handle.listFiles())) == null) {
			return null;
		}

		return setCached(path, component);
	}

	/**
	 * @param dirs - Directories to component
	 * @return - Found component in filesystem
	 */
	protected abstract C find(File[] dirs);

	/**
	 * @param path - Path to component
	 * @param component - Component
	 * @return - Registered component
	 */
	private C setCached(String path, C component) {

		if (cachedComponents.containsKey(path)) {
			return cachedComponents.get(path);
		}

		return cachedComponents.put(path, component);
	}

	/**
	 * @param path - Path to component
	 * @return - Found component or null
	 */
	private C getCached(String path) {

		if (cachedComponents.containsKey(path)) {
			return cachedComponents.get(path);
		}

		return null;
	}

	/**
	 * @return - Project's environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * @return - Component type
	 */
	public Type getType() {
		return type;
	}

	private HashMap<String, C> cachedComponents
			= new HashMap<String, C>();

	private Environment environment;
	private Type type;
}

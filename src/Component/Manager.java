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

		return setCached(path, component);
	}

	/**
	 * @param directory - Directories to component
	 * @return - Found component in filesystem
	 */
	protected abstract C find(File directory, String file);

	/**
	 * @param path - Path to component
	 * @param component - Component
	 * @return - Registered component
	 */
	private C setCached(String path, C component) {

		if (component == null) {
			return null;
		}

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
	 * @return - Component type
	 */
	public Type getType() {
		return type;
	}

	private HashMap<String, C> cachedComponents
			= new HashMap<String, C>();

	private Type type;
}

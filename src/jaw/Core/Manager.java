package jaw.Core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by dmitry on 18.11.14
 */
public abstract class Manager<C extends Component> extends Extension {

	/**
	 * @param environment - Project's environment
	 */
	public Manager(Environment environment, ComponentType type) {
		super(environment); this.type = type;
	}

	/**
	 * @param path - Path to component
	 * @return - Found component
	 */
	public C get(String path) throws Exception {

		C component = getCached(path);

		try {
			if (component != null) {
				return (C) component.getClass().getConstructor(
					Environment.class
				).newInstance(getEnvironment());
			}
		} catch (InstantiationException ignored) {
		} catch (IllegalAccessException ignored) {
		} catch (InvocationTargetException ignored) {
		} catch (NoSuchMethodException ignored) {
		}

		String binaryPath = type.getBinaryPath(
			getEnvironment(), path
		);

		if (binaryPath == null) {
			return null;
		}

		int indexOf;

		do {
			try {
				component = getEnvironment().getComponentFactory()
						.create(binaryPath);
				break;
			} catch (NoClassDefFoundError ignore) {
				if ((indexOf = binaryPath.lastIndexOf(File.separator)) != -1) {
					binaryPath = binaryPath.substring(0, indexOf) + "."
						+ binaryPath.substring(indexOf + 1);
				} else {
					return null;
				}
			} catch (ClassNotFoundException ignore) {
				if ((indexOf = binaryPath.lastIndexOf(File.separator)) != -1) {
					binaryPath = binaryPath.substring(0, indexOf) + "."
						+ binaryPath.substring(indexOf + 1);
				} else {
					return null;
				}
			}
		} while (true);

		return setCached(path, component);
	}

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
		} else {
			cachedComponents.put(path, component);
		}

		return component;
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
	 * Cleanup all cached components
	 */
	public void cleanup() {
		cachedComponents.clear();
	}

	/**
	 * @return - Component type
	 */
	public ComponentType getType() {
		return type;
	}

	private HashMap<String, C> cachedComponents
			= new HashMap<String, C>();

	private ComponentType type;
}

package jaw.Core;

import jaw.Sql.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry on 03.12.14
 */
public class EnvironmentManager {

	/**
	 *
	 */
	private EnvironmentManager() {
		// Add shutdown hook to save all active sessions
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<String, Environment> i : EnvironmentManager.getInstance().getHashMap().entrySet()) {
					try {
						i.getValue().getSessionManager().save();
					} catch (Exception ignored) {
					}
				}
			}
		}));
	}

	/**
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Environment get(String name) throws Exception {
		// Find cashed environment
		if (hashMap.containsKey(name)) {
			return hashMap.get(name);
		}
		// Create new environment
		Environment e = new Environment(new Connection(), name);
		// Restore all saved sessions
		e.getSessionManager().load();
		// Put to hash map
		hashMap.put(name, e);
		// Return instance
		return e;
	}

	/**
	 *
	 * @return
	 */
	public HashMap<String, Environment> getHashMap() {
		return hashMap;
	}

	private HashMap<String, Environment> hashMap
			= new HashMap<String, Environment>();

	/**
	 *
	 * @return
	 */
	public static EnvironmentManager getInstance() {
		return environmentManager;
	}

	private static EnvironmentManager environmentManager
			= new EnvironmentManager();
}

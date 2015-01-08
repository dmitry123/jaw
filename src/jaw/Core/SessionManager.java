package jaw.Core;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Savonin on 2014-12-04
 */
public class SessionManager extends Extension {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public SessionManager(Environment environment) {
		super(environment);
	}

	/**
	 * Check for user's session opened
	 * @return - True is user has been logged in
	 */
	public boolean has() {
		return get() != null;
	}

	/**
	 * Put user in session manager
	 * @param user - Reference to user
	 * @return - Just added user's instance
	 */
	public Session put(Session user) {
		String session = getEnvironment().getSessionID();
		if (session == null) {
			return user;
		}
		if (userHashMap.containsKey(session)) {
			return userHashMap.get(session);
		}
		userHashMap.put(session, new Session(
			user.getID(),
			user.getLogin(),
			user.getHash()
		));
		return user;
	}

	/**
	 * Get user from current session
	 * @return - User's instance or null
	 */
	public Session get() {
		String session = getEnvironment().getSessionID();
		if (session == null) {
			return null;
		}
		if (userHashMap.containsKey(session)) {
			return userHashMap.get(session);
		}
		return null;
	}

	/**
	 * Remove user from current session
	 */
	public void remove() {
		String session = getEnvironment().getSessionID();
		if (session != null && userHashMap.containsKey(session)) {
			userHashMap.remove(session);
		}
	}

	/**
	 * Serialize all sessions and save on local drive before
	 * session close
	 */
	public void save() throws Exception {
		File handle = new File(Config.SESSION_PATH + getEnvironment().getProjectName() + ".session");
		if (!handle.exists()) {
			try {
				new File(Config.SESSION_PATH).mkdirs();
				handle.createNewFile();
			} catch (IOException e) {
				throw new Exception(e.getMessage());
			}
		}
		FileOutputStream fileOutputStream;
		ObjectOutputStream objectOutputStream;
		try {
			fileOutputStream = new FileOutputStream(handle);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
		try {
			objectOutputStream.writeInt(userHashMap.size());
			for (HashMap.Entry<String, Session> i : userHashMap.entrySet()) {
				objectOutputStream.write(i.getKey().getBytes());
				objectOutputStream.writeObject(i.getValue());
			}
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Load sessions from local driver after boot
	 */
	public void load() throws Exception {
		File handle = new File(Config.SESSION_PATH + getEnvironment().getProjectName() + ".session");
		if (!handle.exists()) {
			return;
		}
		FileInputStream fileInputStream;
		ObjectInputStream objectInputStream;
		try {
			fileInputStream = new FileInputStream(handle);
			objectInputStream = new ObjectInputStream(fileInputStream);
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
		userHashMap.clear();
		try {
			int countOfUsers = objectInputStream.readInt();
			while (countOfUsers-- > 0) {
				byte[] sessionBytes = new byte[40];
				objectInputStream.read(sessionBytes);
				String sessionID = new String(sessionBytes);
				Session session = ((Session) objectInputStream.readObject());
				userHashMap.put(sessionID, session);
			}
			fileInputStream.close();
		} catch (ClassNotFoundException e) {
			throw new Exception(e.getMessage());
		} catch (IOException ignored) {
		}
	}

	/**
	 * Get hash map with all authorized users
	 * @return - Map with users
	 */
	public HashMap<String, Session> getEmployeeHashMap() {
		return userHashMap;
	}

	private HashMap<String, Session> userHashMap
			= new HashMap<String, Session>();
}

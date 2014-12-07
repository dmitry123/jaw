package Core;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Savonin on 2014-12-04
 */
public class UserSessionManager extends Extension {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public UserSessionManager(Environment environment) {
		super(environment);
	}

	/**
	 *
	 * @return
	 */
	public boolean has() {
		return get() != null;
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public User put(User user) {
		String session = getEnvironment().getSessionID();
		if (session == null) {
			return user;
		}
		if (userHashMap.containsKey(session)) {
			return userHashMap.get(session);
		}
		userHashMap.put(session, new UserAdapter(
			user.getID(),
			user.getLogin(),
			user.getHash()
		));
		return user;
	}

	/**
	 *
	 * @return
	 */
	public User get() {
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
	 *
	 */
	public void remove() {
		String session = getEnvironment().getSessionID();
		if (session != null && userHashMap.containsKey(session)) {
			userHashMap.remove(session);
		}
	}

	/**
	 *
	 */
	public void save() throws InternalError {
		File handle = new File(Config.SESSION_PATH + getEnvironment().getProjectName() + ".session");
		if (!handle.exists()) {
			try {
				new File(Config.SESSION_PATH).mkdirs();
				handle.createNewFile();
			} catch (IOException e) {
				throw new InternalError(e.getMessage());
			}
		}
		FileOutputStream fileOutputStream;
		ObjectOutputStream objectOutputStream;
		try {
			fileOutputStream = new FileOutputStream(handle);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
		} catch (IOException e) {
			throw new InternalError(e.getMessage());
		}
		try {
			objectOutputStream.writeInt(userHashMap.size());
			for (HashMap.Entry<String, User> i : userHashMap.entrySet()) {
				objectOutputStream.write(i.getKey().getBytes());
				objectOutputStream.writeObject(i.getValue());
			}
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 *
	 */
	public void load() throws InternalError {
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
			throw new InternalError(e.getMessage());
		}
		userHashMap.clear();
		try {
			int countOfUsers = objectInputStream.readInt();
			while (countOfUsers-- > 0) {
				byte[] sessionBytes = new byte[40];
				objectInputStream.read(sessionBytes);
				String sessionID = new String(sessionBytes);
				User user = ((User) objectInputStream.readObject());
				userHashMap.put(sessionID, user);
			}
			fileInputStream.close();
		} catch (ClassNotFoundException e) {
			throw new InternalError(e.getMessage());
		} catch (IOException ignored) {
		}
	}

	/**
	 *
	 * @return
	 */
	public HashMap<String, User> getUserHashMap() {
		return userHashMap;
	}

	private HashMap<String, User> userHashMap
			= new HashMap<String, User>();
}

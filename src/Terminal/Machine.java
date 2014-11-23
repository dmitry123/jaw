package Terminal;

import Core.*;
import Core.InternalError;
import java.util.HashMap;

/**
 * Created by dmitry on 23.11.14
 */
public class Machine extends Extension implements Runnable {

	/**
	 * @param environment - Every core's extension must have environment
	 *                    with predeclared extensions
	 */
	public Machine(Environment environment) {
		super(environment);
	}

	/**
	 * Register new station in machine
	 * @param key - Station's unique identifier
	 * @param station - Station instance
	 * @return - Registered station instance
	 * @throws InternalError
	 */
	public synchronized Station register(String key, Station station) throws InternalError {
		if (hashMap.containsKey(key)) {
			throw new Core.InternalError(
				"Machine/register() : \"Station with that name already exists\""
			);
		}
		return hashMap.put(key, station);
	}

	/**
	 * Find station in machine
	 * @param key - Station's unique key
	 * @return - Found station
	 * @throws InternalError
	 */
	public synchronized Station find(String key) throws InternalError {
		if (!hashMap.containsKey(key)) {
			throw new Core.InternalError(
				"Machine/register() : \"Unresolved station name (" + key + ")\""
			);
		}
		return hashMap.get(key);
	}

	/**
	 * Launch machine
	 */
	public synchronized void launch() throws InternalError {
		if (thread != null) {
			if (!thread.isAlive()) {
				thread.interrupt();
				try {
					thread.join();
				} catch (InterruptedException e) {
					throw new InternalError(
						"Machine/launch() : \"Thread has been interrupted\""
					);
				}
			}
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Change current station to another
	 * @param key - Station's unique identifier
	 * @throws InternalError
	 */
	public synchronized void change(String key) throws InternalError {
		launch();
		active = find(key);

	}

	/**
	 * @return - Machine's thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * @return - Active machine's station
	 */
	public Station getActive() {
		return active;
	}

	private HashMap<String, Station> hashMap
			= new HashMap<String, Station>();

	private Station active = null;
	private Thread thread = null;

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p/>
	 * The general contract of the method <code>run</code> is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {

	}
}

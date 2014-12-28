package jaw.Terminal;

import jaw.Core.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
	 * @throws Exception
	 */
	public synchronized Station register(String key, Station station) throws Exception {
		if (hashMap.containsKey(key)) {
			throw new Exception(
				"Machine/register() : \"Station with that name already exists\""
			);
		}
		if (active == null) {
			active = station;
		}
		hashMap.put(key, station);
		return station;
	}

	/**
	 * Find station in machine
	 * @param key - Station's unique key
	 * @return - Found station
	 * @throws Exception
	 */
	public synchronized Station find(String key) throws Exception {
		if (!hashMap.containsKey(key)) {
			throw new Exception(
				"Machine/register() : \"Unresolved station name (" + key + ")\""
			);
		}
		return hashMap.get(key);
	}

	/**
	 * Find station's key (if hasn't been set)
	 * @param station - Station's instance
	 * @return - Found key
	 * @throws Exception
	 */
	public synchronized String getKeyByInstance(Station station) throws Exception {
		for (Map.Entry<String, Station> entry : hashMap.entrySet()) {
			if (station == entry.getValue()) {
				return entry.getKey();
			}
		}
		throw new Exception(
			"Machine/getKeyByInstance() : \"Unresolved station instance\""
		);
	}

	/**
	 * Launch machine
	 */
	public synchronized void start() throws Exception {
		if (active == null) {
			return;
		}
		if (thread != null && !thread.isAlive()) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new Exception(
					"Machine/start() : \"Thread has been interrupted\""
				);
			}
		}
		thread = new Thread(
			this
		);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new Exception(
					"Machine/start() : \"Thread has been interrupted\""
			);
		}
	}

	/**
	 * @return - Machine's thread
	 */
	public Thread getThread() {
		if (thread == null) {
			return Thread.currentThread();
		}
		return thread;
	}

	/**
	 * @return - Active machine's station
	 */
	public Station getActive() {
		return active;
	}

	/**
	 * Set new active terminal's station
	 * @param station - New active station
	 */
	public void setActive(Station station) {
		stationStack.push(active);
		active = station;
	}

	/**
	 * Pop active element from stack and mark as active
	 * @return - If we can't pop anymore, then return false
	 */
	public boolean popActive() {
		if (stationStack.size() == 0) {
			return false;
		}
		active = stationStack.pop();
		return true;
	}

	/**
	 * Get stack with previous not closed stations
	 * @return - Stack with stations
	 */
	public Stack<Station> getStack() {
		return stationStack;
	}

	private HashMap<String, Station> hashMap
			= new HashMap<String, Station>();

	private Stack<Station> stationStack
			= new Stack<Station>();

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
		try {
			getActive().work();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

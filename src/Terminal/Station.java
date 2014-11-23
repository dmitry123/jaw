package Terminal;

import Core.InternalError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Savonin on 2014-11-15
 */
public abstract class Station implements Runnable {

	/**
	 * Construct controller with instructions
	 */
	protected Station(Machine machine, Instruction... instructions) throws Core.InternalError {
		for (Instruction i : instructions) {
			register(i);
		}
	}

	/**
	 * @return - Terminal thread
	 * @throws Core.InternalError
	 */
	public static Thread runTerminal(Station station) throws InternalError {
		Thread thread = new Thread(
				station
		);
		station.thread = thread;
		thread.start();
		if (!thread.isAlive()) {
			throw new Core.InternalError("Unable to start server terminal");
		}
		return thread;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
	 * causes the object's <code>run</code> method to be called in that separately executing thread.
	 * <p/>
	 * The general contract of the method <code>run</code> is that it may take any action whatsoever.
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		String line;
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(System.in)
			);
			System.out.print(" $: ");
			while ((line = bufferedReader.readLine()) != null) {
				String[] list = line.split(" ");
				try {
					if (list.length == 0) {
						throw new Exception("Empty instruction");
					}
					Instruction instruction = find(list[0]);
					String[] argumentList = new String[
							list.length > 1 ? list.length - 1 : 0
						];
					if (list.length > 1) {
						System.arraycopy(list, 1, argumentList, 0, list.length - 1);
					}
					instruction.getEvent().make(argumentList);
				} catch (InterruptedException ignored) {
					break;
				} catch (Error e) {
					System.out.format("Instruction Error [%s] : \"%s\"\n", e.getInstruction().getName(),
							e.getMessage());
				} catch (Exception e) {
					System.out.format("Internal Error: \"%s\"\n", e.getMessage());
				}
				System.out.print(" $: ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Register new instruction in current controller
	 *
	 * @param instruction - Instruction's instance
	 * @throws InternalError
	 */
	public void register(Instruction instruction) throws InternalError {
		if (instructionHashMap.containsKey(instruction.getName())) {
			throw new Core.InternalError("Instruction with that name already exists");
		}
		if (instruction.getStation() != this) {
			instructionHashMap.put(instruction.getName(), instruction);
		}
		instruction.setStation(this);
	}

	/**
	 * Find instruction by it's name (key)
	 * @param name - Instruction's name
	 * @return - Found instruction in hash map
	 * @throws InternalError
	 */
	public Instruction find(String name) throws InternalError {
		if (!instructionHashMap.containsKey(name)) {
			for (Instruction i : getInstructions()) {
				if (i.getTag().equals(name)) {
					return i;
				}
			}
			throw new Core.InternalError("Unknown instruction");
		}
		return instructionHashMap.get(name);
	}

	/**
	 * @return - Collection with hash map values
	 */
	public Collection<Instruction> getInstructions() {
		return instructionHashMap.values();
	}

	/**
	 * @return - Parent's station
	 */
	public Station getParent() {
		return parent;
	}

	/**
	 * @return - Station's machine
	 */
	public Machine getMachine() {
		return machine;
	}

	private HashMap<String, Instruction> instructionHashMap
			= new HashMap<String, Instruction>();

	private Thread thread;
	private Machine machine;
	private Station parent;
}

package Terminal;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Savonin on 2014-11-15
 */
public abstract class Station extends Instruction implements Protocol {

	/**
	 * Construct controller with instructions
	 * @param machine - Station's machine
	 */
	protected Station(Machine machine) throws Exception {
		this(machine, null, null, null);
	}

	/**
	 * Construct controller with instructions
	 * @param machine - Station's machine
	 */
	protected Station(Machine machine, Station parent) throws Exception {
		this(machine, parent, null, null);
	}

	/**
	 * Construct controller with instructions
	 * @param machine - Station's machine
	 */
	protected Station(Machine machine, Station parent, String name, String tag) throws Exception {

		super(null, name, tag);

		this.machine = machine;
		this.parent = parent;

		register(new Instruction(this, "exit", "-e") {
			@Override
			public void run(String[] arguments) throws Exception, InterruptedException {
				throw new InterruptedException();
			}
		});

		register(new Instruction(this, "help", "-h") {
			@Override
			public void run(String[] arguments) throws Exception, InterruptedException {
				if (arguments.length != 0) {
					if (arguments.length != 1) {
						throw new Error(this,
							"That instruction can't assume more than one argument"
						);
					}
					try {
						getAbout(getStation().find(arguments[0]));
					} catch (Exception e) {
						throw new Error(this, e.getMessage());
					}
				} else {
					for (Protocol i : getStation().getInstructions()) {
						getAbout(i);
					}
				}
			}
		});
	}

	/**
	 * Execute instruction or station
	 * @param arguments - Instruction arguments
	 */
	@Override
	public void run(String[] arguments) throws Exception, InterruptedException {
		if (arguments.length > 0) {
			Protocol p = this.find(arguments[0]);
			String[] args = new String[arguments.length - 1];
			System.arraycopy(arguments, 1, args, 0, arguments.length - 1);
			p.run(args);
		} else {
			getMachine().setActive(this);
		}
	}

	/**
	 *
	 */
	public void work() throws Exception {
		String line;
		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(System.in)
		);
		printEntry();
		try {
			while ((line = bufferedReader.readLine()) != null) {
				String[] list = line.split(" ");
				try {
					if (list.length == 0) {
						throw new java.lang.Exception("Empty instruction");
					}
					Protocol protocol = getMachine().getActive().find(list[0]);
					String[] argumentList = new String[
							list.length > 1 ? list.length - 1 : 0
						];
					if (list.length > 1) {
						System.arraycopy(list, 1, argumentList, 0, list.length - 1);
					}
					protocol.run(argumentList);
				} catch (InterruptedException ignored) {
					if (!getMachine().popActive()) {
						break;
					}
				} catch (Error e) {
					System.out.format("Instruction Error [%s] : \"%s\"\n", e.getInstruction().getName(),
							e.getMessage());
				} catch (java.lang.Exception e) {
					System.out.format("Internal Error: \"%s\"\n", e.getMessage());
				}
				printEntry();
			}
		} catch (IOException e) {
			throw new Exception(
				"Station/work() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Print terminal's entry
	 */
	private void printEntry() {
		String path = "";
		if (getMachine().getStack().size() > 1) {
			for (Station s : getMachine().getStack()) {
				if (path.length() < 1) {
					path = " (";
				}
				path += s.getName() + "/";
			}
		} else if (getMachine().getStack().size() > 0) {
			path +=  " (" + getMachine().getActive().getName() + ") ";
		}
		System.out.format(" $%s: ", path);
	}

	/**
	 * Register new instruction in current controller
	 *
	 * @param instruction - Instruction's instance
	 * @throws Exception
	 */
	public void register(Protocol instruction) throws Exception {
		if (hashMap.containsKey(instruction.getName())) {
			throw new Exception("Instruction with that name already exists");
		}
		hashMap.put(instruction.getName(), instruction);
	}

	/**
	 * Find instruction by it's name (key)
	 * @param name - Instruction's name
	 * @return - Found instruction in hash map
	 * @throws Exception
	 */
	public Protocol find(String name) throws Exception {
		if (!hashMap.containsKey(name)) {
			for (Protocol i : getInstructions()) {
				if (!(i instanceof Instruction)) {
					continue;
				}
				if (i.getTag().equals(name)) {
					return i;
				}
			}
			throw new Exception("Unknown instruction");
		}
		return hashMap.get(name);
	}

	/**
	 * @return - Collection with hash map values
	 */
	public Collection<Protocol> getInstructions() {
		return hashMap.values();
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

	private HashMap<String, Protocol> hashMap
			= new HashMap<String, Protocol>();

	private Machine machine;
	private Station parent;
}

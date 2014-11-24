package Server;

import Core.InternalError;
import Core.Logger;
import Terminal.*;
import Terminal.Error;

import java.util.Vector;

/**
 * Created by Savonin on 2014-11-15
 */
public class ServerTerminal extends Station {

	/**
	 * Construct terminal without parent
	 * @param machine - Station's machine
	 * @throws InternalError
	 */
	public ServerTerminal(Machine machine) throws InternalError {
		this(machine, null);
	}

	/**
	 * Construct controller with instructions
	 */
	public ServerTerminal(Machine machine, Station parent) throws InternalError {

		super(machine, parent);

		register(new Instruction(this, "kill", "-k") {
			@Override
			public void run(String[] arguments) throws Error, InterruptedException {
				getMachine().getStack().clear(); throw new InterruptedException();
			}
		});

		register(new Instruction(this, "log", "-l") {
			@Override
			public void run(String[] arguments) throws Error, InterruptedException {
				Vector<String> stringVector;
				if (arguments.length != 0) {
					stringVector = Logger.loadLog(arguments[0]);
				} else {
					stringVector = Logger.loadLog();
				}
				for (String s : stringVector) {
					System.out.format(" + %s", s);
				}
			}
		});

		register(new Instruction(this, "compile", "-c") {
			@Override
			public void run(String[] arguments) throws InternalError, InterruptedException {
				getMachine().getEnvironment().getProjectManager().getCompiler().compile();
			}
		});
	}

	/**
	 * Every instruction must have it's owner (station)
	 * @return - Instruction's owner
	 */
	@Override
	public Station getStation() {
		if (getParent() != null) {
			return getParent();
		}
		return this;
	}

	/**
	 * Every instruction must have name
	 * @return - Instruction or station name
	 */
	@Override
	public String getName() {
		return "jaw";
	}

	/**
	 * Short name for every station's element
	 * @return - Instruction short tag
	 */
	@Override
	public String getTag() {
		return "-j";
	}
}

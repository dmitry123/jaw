package Server;

import Core.*;
import Core.InternalError;
import Terminal.*;
import Terminal.Error;

import java.util.Map;
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
				if (arguments.length != 1) {
					throw new Error(this,
							"That instruction can assume only one argument"
					);
				}
				Environment e = EnvironmentManager.getInstance().get(arguments[0]);
				if (e == null) {
					throw new Error(this,
							"Unresolved project name (" + arguments[0] + ")"
					);
				}
				e.getModelManager().cleanup();
				e.getViewManager().cleanup();
				e.getControllerManager().cleanup();
				e.getProjectManager().getCompiler().cleanup();
				e.getProjectManager().getCompiler().compile();
			}
		});

		register(new Instruction(this, "cleanup", "-r") {
			@Override
			public void run(String[] arguments) throws InternalError, InterruptedException {
				if (arguments.length != 1) {
					throw new Error(this,
							"That instruction can assume only one argument"
					);
				}
				Environment e = EnvironmentManager.getInstance().get(arguments[0]);
				if (e == null) {
					throw new Error(this,
							"Unresolved project name (" + arguments[0] + ")"
					);
				}
				e.getModelManager().cleanup();
				e.getViewManager().cleanup();
				e.getControllerManager().cleanup();
			}
		});

		register(new Station(machine, this) {
			{
				register(new Instruction(this, "show", "-s") {
					@Override
					public void run(String[] arguments) throws InternalError, InterruptedException {
						if (arguments.length != 1) {
							throw new Error(this,
									"That instruction can assume only one argument"
							);
						}
						Environment e = EnvironmentManager.getInstance().get(arguments[0]);
						for (Map.Entry<String, User> i : e.getUserSessionManager().getUserHashMap().entrySet()) {
							System.out.format(" + [%s] -> (%s)\n", i.getKey(), i.getValue().getLogin());
						}
						if (e.getUserSessionManager().getUserHashMap().size() == 0) {
							System.out.println(" + No sessions open");
						}
					}
				});

				register(new Instruction(this, "clear", "-c") {
					@Override
					public void run(String[] arguments) throws InternalError, InterruptedException {
						if (arguments.length != 1) {
							throw new Error(this,
									"That instruction can assume only one argument"
							);
						}
						EnvironmentManager.getInstance().get(arguments[0])
								.getUserSessionManager().getUserHashMap().clear();
					}
				});

				register(new Instruction(this, "drop", "-d") {
					@Override
					public void run(String[] arguments) throws InternalError, InterruptedException {
						if (arguments.length != 2) {
							throw new Error(this,
								"That instruction can assume only two arguments"
							);
						}
						Map<String, User> stringUserMap = EnvironmentManager.getInstance().get(arguments[0])
								.getUserSessionManager().getUserHashMap();
						for (Map.Entry<String, User> i : stringUserMap.entrySet()) {
							if (i.getValue().getLogin().equals(arguments[1])) {
								stringUserMap.remove(i.getKey());
								System.out.println(" + Session dropped [" + i.getKey() + "]");
							}
						}
					}
				});

				register(new Instruction(this, "save", "-sf") {
					@Override
					public void run(String[] arguments) throws InternalError, InterruptedException {
						if (arguments.length != 1) {
							throw new Error(this,
								"That instruction can assume only two arguments"
							);
						}
						EnvironmentManager.getInstance().get(arguments[0]).getUserSessionManager().save();
					}
				});

				register(new Instruction(this, "load", "-lf") {
					@Override
					public void run(String[] arguments) throws InternalError, InterruptedException {
						if (arguments.length != 1) {
							throw new Error(this,
								"That instruction can assume only two arguments"
							);
						}
						EnvironmentManager.getInstance().get(arguments[0]).getUserSessionManager().load();
					}
				});
			}

			@Override
			public String getName() {
				return "session";
			}

			@Override
			public String getTag() {
				return "-s";
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

package Server;

import Core.*;
import Core.InternalError;
import Terminal.*;
import Terminal.Error;

import java.util.Vector;

/**
 * Created by Savonin on 2014-11-15
 */
public class ServerTerminal extends Station {

	/**
	 * Construct controller with instructions
	 */
	public ServerTerminal() throws InternalError {
		super(
			new Instruction("help", "-h", new Instruction.Event() {
				@Override
				public String getDescription() {
					return "It will show information about all instructions, it's" +
							"usage and description";
				}
				@Override
				public String getUsage() {
					return "help [instruction]";
				}
				@Override
				public void make(String[] args) throws InternalError {
					if (args.length != 0) {
						if (args.length != 1) {
							throw new Error(getInstruction(),
									"That instruction can't assume more than one argument"
							);
						}
						about(getStation().find(args[0]));
					} else {
						for (Instruction i : getStation().getInstructions()) {
							about(i);
						}
					}
				}
			}),

			new Instruction("kill", "-k", new Instruction.Event() {
				@Override
				public String getDescription() {
					return "Exit from console (terminal's thread will be terminated)";
				}
				@Override
				public String getUsage() {
					return "exit";
				}
				@Override
				public void make(String[] args) throws InternalError, InterruptedException {
					if (args.length != 0) {
						throw new Terminal.Error(getInstruction(),
								"That instruction can't assume arguments"
						);
					} else {
						Station.getInternal().find("-terminal-terminate")
								.getEvent().make();
					}
				}
			}),

			new Instruction("log", "-l", new Instruction.Event() {
				@Override
				public String getDescription() {
					return "Exit from console (terminal's thread will be terminated)";
				}
				@Override
				public String getUsage() {
					return "log [date]";
				}
				@Override
				public void make(String[] args) throws InternalError, InterruptedException {
					Vector<String> stringVector;
					if (args.length != 0) {
						stringVector = Logger.loadLog(args[0]);
					} else {
						stringVector = Logger.loadLog();
					}
					for (String s : stringVector) {
						System.out.format(" + %s", s);
					}
				}
			})
		);
	}
}

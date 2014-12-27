package Terminal;

/**
 * Created by Savonin on 2014-11-15
 */
public class Error extends Exception {

	/**
	 * Constructs a new exception with the specified detail message.  The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause}.
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
	 * method.
	 */
	public Error(Instruction instruction, String message) {
		super(message); this.instruction = instruction;
	}

	/**
	 * @return - Error's instruction
	 */
	public Instruction getInstruction() {
		return instruction;
	}

	private Instruction instruction;
}

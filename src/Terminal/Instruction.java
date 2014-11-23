package Terminal;

/**
 * Created by Savonin on 2014-11-15
 */
public class Instruction {

	/**
	 * @param name - Instruction's name
	 * @param tag - Instruction's short tag
	 * @param event - Event hooker
	 */
	public Instruction(String name, String tag, Event event) {
		if (event != null) {
			this.description = event.getDescription();
			this.usage = event.getUsage();
			event.instruction = this;
		}
		this.name = name;
		this.event = event;
		this.tag = tag;
	}

	/**
	 * @return - Instruction's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return - Event hooker
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @return - Help description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return - Help usage
	 */
	public String getUsage() {
		return usage;
	}

	/**
	 * @return - Instruction's tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return - Instruction's controller
	 */
	public Station getStation() {
		return station;
	}

	/**
	 * @param station - Controller
	 */
	public void setStation(Station station) {
		if (this.event != null) {
			this.event.station = station;
		}
		this.station = station;
	}

	public static abstract class Event {

		/**
		 * @param i - Instruction
		 */
		public void about(Instruction i) {
			System.out.format(
				" - name : %s <%s>\n" +
				"   description : %s\n" +
				"   usage : %s\n", i.getName(), i.getTag(), i.getDescription(), i.getUsage());
		}

		/**
		 * @return - Instruction description
		 */
		public String getDescription() {
			return "No description";
		}

		/**
		 * @return - Instruction's usage
		 */
		abstract public String getUsage();

		/**
		 * @param args - List with arguments
		 */
		public abstract void make(String[] args) throws Core.InternalError, InterruptedException;

		/**
		 * @throws Core.InternalError
		 * @throws InterruptedException
		 */
		public void make() throws Core.InternalError, InterruptedException {
			make(new String[] {});
		}

		/**
		 * @return - Instruction
		 */
		public Instruction getInstruction() {
			return instruction;
		}

		/**
		 * @return - Instruction's controller
		 */
		public Station getStation() {
			return station;
		}

		private Instruction instruction;
		private Station station;
	}

	private String name = null;
	private Event event = null;
	private String description = null;
	private String usage = null;
	private String tag = null;
	private Station station = null;
}

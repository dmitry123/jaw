package Terminal;

import Core.*;
import Core.InternalError;

/**
 * Created by Savonin on 2014-11-15
 */
public abstract class Instruction implements Protocol {

	/**
	 * @param name - Instruction's name
	 * @param tag - Instruction's short tag
	 */
	public Instruction(Station station, String name, String tag) {
		this.station = station;
		this.name = name;
		this.tag = tag;
	}

	/**
	 * @return - Instruction's name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return - Instruction's controller
	 */
	@Override
	public Station getStation() {
		return station;
	}

	/**
	 * @return - Instruction's tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return - Instruction description
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * @return - Instruction's usage
	 */
	public String getUsage() {
		return null;
	}

	/**
	 * @param i - Instruction
	 */
	public void getAbout(Protocol i) throws Core.InternalError {
		System.out.format(
			" - \"%s\" <%s>\n" +
			"   description : %s\n" +
			"   usage : %s\n", i.getName(), i.getTag(), i.getDescription(), i.getUsage());
	}

	private String name = null;
	private String tag = null;
	private Station station = null;
}

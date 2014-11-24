package Terminal;

import Core.InternalError;

/**
 * Created by Savonin on 2014-11-23
 */
public class Adapter extends Station {

	/**
	 * Construct controller with instructions
	 * @param machine - Station's machine
	 * @param parent - Station's parent
	 * @param name - Station's name
	 * @param tag - Station's tag
	 */
	public Adapter(Machine machine, Station parent, String name, String tag) throws InternalError {
		super(machine, parent, name, tag);
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
		if (super.getName() != null) {
			return super.getName();
		}
		try {
			return getMachine().getKeyByInstance(this);
		} catch (InternalError internalError) {
			return "{Unknown-Name}";
		}
	}

	/**
	 * Short name for every station's element
	 * @return - Instruction short tag
	 */
	@Override
	public String getTag() {
		return super.getTag();
	}

	/**
	 * Description about instruction or station
	 * @return - Description
	 */
	@Override
	public String getDescription() {
		return super.getDescription();
	}

	/**
	 * Get usage about element
	 * @return - Usage
	 */
	@Override
	public String getUsage() {
		return super.getUsage();
	}
}

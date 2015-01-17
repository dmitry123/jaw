package jaw.Terminal;



/**
 * Created by Savonin on 2014-11-23
 */
public interface Protocol {

	/**
	 * Every instruction must have it's owner (station)
	 * @return - Instruction's owner
	 */
	public Station getStation();

	/**
	 * Every instruction must have name
	 * @return - Instruction or station name
	 */
	public String getName();

	/**
	 * Short name for every station's element
	 * @return - Instruction short tag
	 */
	public String getTag();

	/**
	 * Description about instruction or station
	 * @return - Description
	 */
	public String getDescription();

	/**
	 * Get usage about element
	 * @return - Usage
	 */
	public String getUsage();

	/**
	 * Execute instruction or station
	 * @param arguments - Instruction arguments
	 */
	public void run(String[] arguments) throws Exception, InterruptedException;
}

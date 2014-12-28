package jaw.Sql;



/**
 * Created by dmitry on 18.11.14
 */
public class CortegeKey implements CortegeProtocol {

	/**
	 * @param key Key
	 */
	public CortegeKey(String key) {
		this.key = key;
	}

	/**
	 * Every collage must have
	 * own identifier
	 *
	 * @return row's key
	 */
	@Override
	public int getID() throws Exception {
		throw new Exception("CortegeKey/getID() : \"Table hasn't primary integer identifier\"");
	}

	/**
	 * @return - String key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Identifier
	 */
	private String key;
}

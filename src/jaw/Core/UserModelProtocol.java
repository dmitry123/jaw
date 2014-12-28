package jaw.Core;

/**
 * Created by Savonin on 2014-11-22
 */
public interface UserModelProtocol {

	/**
	 * Implement this function to return user by it's
	 * primary key
	 *
	 * @param id - User's unique identifier
	 * @return - Found user adapted to UserProtocol
	 * @throws Exception
	 */
	public UserProtocol fetchUserByPk(int id) throws Exception;

	/**
	 * Also you must implement method to fetch user by it's
	 * unique login name
	 *
	 * @param login - User's unique login
	 * @return - Found user adapted to UserProtocol
	 * @throws Exception
	 */
	public UserProtocol fetchUserByLogin(String login) throws Exception;

	/**
	 *
	 * @param userProtocol - Protocol with basic user information
	 * @throws Exception
	 */
	public void registerUser(UserProtocol userProtocol) throws Exception;
}

package Core;

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
	 * @throws InternalError
	 */
	public UserProtocol fetchUserByPk(int id) throws InternalError;

	/**
	 * Also you must implement method to fetch user by it's
	 * unique login name
	 *
	 * @param login - User's unique login
	 * @return - Found user adapted to UserProtocol
	 * @throws InternalError
	 */
	public UserProtocol fetchUserByLogin(String login) throws InternalError;

	/**
	 *
	 * @param userProtocol - Protocol with basic user information
	 * @throws InternalError
	 */
	public void registerUser(UserProtocol userProtocol) throws InternalError;
}

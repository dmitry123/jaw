package Core;

/**
 * Created by Savonin on 2014-11-22
 */
public class UserAdapter extends User {

	/**
	 * User's constructor, which will store basic information getAbout user (identifier, login, hash) - only for affected rows.
	 * You can extend that class and put more features if you want more functionality or simply implement UserProtocol
	 * methods
	 * @param id - User's unique identifier
	 * @param login - User's unique login name
	 * @param hash - Hash of user's password (crypted by PasswordEncryptor)
	 */
	public UserAdapter(int id, String login, String hash) {
		super(id, login, hash);
	}
}

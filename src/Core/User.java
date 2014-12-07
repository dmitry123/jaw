package Core;

import Sql.CortegeRow;

import java.io.Serializable;

/**
 * Created by Savonin on 2014-11-08
 */
public abstract class User extends CortegeRow implements UserProtocol, Serializable {

	/**
	 * User's constructor, which will store basic information
	 * getAbout user (identifier, login, hash) - only for affected rows.
	 * You can extend that class and put more features if you
	 * want more functionality or simply implement UserProtocol methods
	 *
	 * @param id - User's unique identifier
	 * @param login - User's unique login name
	 * @param hash - Hash of user's password (crypted by PasswordEncryptor)
	 */
	public User(int id, String login, String hash) {
		super(id); this.login = login; this.hash = hash;
	}

	/**
	 * You can't store user without login (string type)
	 * @return - User's unique name
	 */
	@Override
	public String getLogin() {
		return login;
	}

	/**
	 * You must encrypt user password with PasswordEncryptor
	 * @return - User's encrypted password
	 */
	@Override
	public String getHash() {
		return hash;
	}

	private String login;
	private String hash;
}

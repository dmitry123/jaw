package jaw.Core;

import jaw.Sql.CortegeProtocol;

import java.io.Serializable;

/**
 * Created by Savonin on 2014-11-22
 */
public interface UserProtocol extends CortegeProtocol, Serializable {

	/**
	 * Every information system must have identity system, that mean
	 * that you must implement UserProtocol and it's methods for user's
	 * model and every user must have integer identifier
	 *
	 * @return - User's identifier
	 */
	public int getID();

	/**
	 * You can't store user without login (string type)
	 *
	 * @return - User's unique name
	 */
	public String getLogin();

	/**
	 * You must encrypt user password with PasswordEncryptor
	 *
	 * @return - User's encrypted password
	 */
	public String getHash();
}

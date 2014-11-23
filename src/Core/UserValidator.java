package Core;

import Core.InternalError;
import Core.User;

/**
 * Created by Savonin on 2014-11-08
 */
public class UserValidator extends Extension {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public UserValidator(Environment environment) {
		super(environment);
	}

	/**
	 * Register new user in system
	 *
	 * @param login - User's login
	 * @param password - User's password (not crypted)
	 * @return - Just declared user in database
	 * @throws Core.InternalError
	 */
	public User register(String login, String password) throws InternalError {

//		Jaw.models.User userModel = ((Jaw.models.User) getEnvironment().getModelManager().get("users"));

//		if (userModel == null) {
//			throw new InternalError(
//				"UserValidator/register() : \"Can't resolve 'user' model in database\""
//			);
//		}

//		if (userModel.exists(login)) {
//			throw new ExternalError(ExternalError.Code.UserAlreadyRegistered);
//		}
//
//		User user = new UserAdapter(userModel, new Jaw.models.User.Row(
//			login, PasswordEncryptor.crypt(login, password)
//		));
//
//		userModel.add(user.getCortege());

		return null;
//		return user;
	}

	/**
	 * Validate user by it's login and password, if
	 * user doesn't exist or password invalid then
	 * we will throw runtime exception with error
	 *
	 * @param login - User's login
	 * @param password - User's password
	 * @return - Found user in system with cortege
	 * 		bind to it's model
	 * @throws Exception
	 */
	public User validate(String login, String password) throws Exception {

//		Jaw.models.User userModel = ((Jaw.models.User) getEnvironment().getModelManager().get("users"));

//		if (userModel == null) {
//			throw new InternalError(
//				"UserValidator/register() : \"Can't resolve 'user' model in database\""
//			);
//		}

//		if (!userModel.exists(login)) {
//			throw new ExternalError(ExternalError.Code.InvalidLoginOrPassword);
//		}

//		Jaw.models.User.Row row = userModel.fetchByLogin(login);

//		if (!row.getHash().equals(PasswordEncryptor.crypt(login, password))) {
//			throw new ExternalError(ExternalError.Code.InvalidLoginOrPassword);
//		}

		return null;
//		return new User(userModel, row);
	}
}

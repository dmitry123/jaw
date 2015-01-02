package controllers;

import jaw.Core.*;

import jaw.Sql.CortegeProtocol;
import org.json.JSONObject;

/**
 * Created by Savonin on 2014-12-04
 */
public class User extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public User(Environment environment) {
		super(environment);
	}

	public void actionLogin() throws Exception {

		final String login = POST("login");
		final String password = POST("password");

		JSONObject jsonResponse = new JSONObject();
		Model userModel = getModel("User");

		CortegeProtocol cortegeProtocol = userModel.fetchRow("fetchByLoginAndHash", login,
			PasswordEncryptor.crypt(login, password)
		);

		if (cortegeProtocol != null) {
			jaw.Core.User user = (jaw.Core.User) cortegeProtocol;
			getEnvironment().getUserSessionManager().put(user);
			jsonResponse.put("status", true);
			getEnvironment().getMustacheDefiner().put("User.Login", user.getLogin());
			getEnvironment().getMustacheDefiner().put("User.ID", Integer.toString(user.getID()));
		} else {
			jsonResponse.put("status", false);
			jsonResponse.put("message", "Неверный пароль или логин пользователя");
		}

		setAjaxResponse(jsonResponse.toString());
	}

	public void actionRegister() throws Exception {

		final String login = POST("login");
		final String password = POST("password");
		final String email = POST("email");

		JSONObject jsonResponse = new JSONObject();
		Model userModel = getModel("User");

		if (userModel.fetchRow("fetchByLogin", login) == null) {
			if (userModel.fetchRow("fetchByMail", email) != null) {
				jsonResponse.put("message", "Пользователь с таким ящиком уже зарегистрирован в системе");
				jsonResponse.put("status", false);
			} else {
				userModel.fetchRow("register", login, PasswordEncryptor.crypt(login, password), email);
			}
		}  else {
			jsonResponse.put("message", "Пользователь с таким логином уже существует");
			jsonResponse.put("status", false);
		}

		setAjaxResponse(jsonResponse.toString());
	}

	public void actionLogout() throws Exception {

		getSession().getCookies().delete("JAW_SESSION_ID");

		if (getEnvironment().getUserSessionManager().has()) {
			getEnvironment().getUserSessionManager().remove();
		}

		getEnvironment().getMustacheDefiner().remove("User.Login");
		getEnvironment().getMustacheDefiner().remove("User.ID");

		redirect("Index", "View");
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}
}

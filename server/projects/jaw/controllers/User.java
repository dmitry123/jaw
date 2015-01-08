package jaw.controllers;

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
			Session session = (Session) cortegeProtocol;
			getEnvironment().getSessionManager().put(session);
			jsonResponse.put("status", true);
			getEnvironment().getMustacheDefiner().put("User.Login", session.getLogin());
			getEnvironment().getMustacheDefiner().put("User.ID", Integer.toString(session.getID()));
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

		if (getEnvironment().getSessionManager().has()) {
			getEnvironment().getSessionManager().remove();
		}

		getEnvironment().getMustacheDefiner().remove("User.Login");
		getEnvironment().getMustacheDefiner().remove("User.ID");

		JSONObject json = new JSONObject();
		json.put("status", true);
		setAjaxResponse(json.toString());
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}
}

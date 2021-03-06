package jaw.controllers;

import jaw.Core.*;

import org.json.JSONObject;

import java.lang.Exception;
import java.sql.ResultSet;

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

		JSONObject json = new JSONObject();
		Model userModel = getModel("User");
		String hash;

		try {
			hash = PasswordEncryptor.crypt(login, password);
		} catch (Exception ignored) {
			postErrorMessage("Неверный пароль или логин пользователя");
			return;
		}

		ResultSet resultSet = userModel.fetchSet("fetchByLoginAndHash", login, hash);

		if (resultSet.next()) {
			Session session = new Session(
				resultSet.getInt("id"),
				resultSet.getString("login"),
				resultSet.getString("hash")
			);
			getEnvironment().getSessionManager().put(session);
			json.put("status", true);
			session.getMustacheDefiner().put("USER_LOGIN", session.getLogin());
			session.getMustacheDefiner().put("USER_ID", Integer.toString(session.getID()));
		} else {
			json.put("status", false);
			json.put("message", "Неверный пароль или логин пользователя");
		}

		setAjaxResponse(json.toString());
	}

	public void actionRegister() throws Exception {

		final String login = POST("login");
		final String password = POST("password");
		final String email = POST("email");

		JSONObject json = new JSONObject();
		Model userModel = getModel("User");

		if (userModel.fetchRow("fetchByLogin", login) == null) {
			if (userModel.fetchRow("fetchByMail", email) != null) {
				json.put("message", "Пользователь с таким ящиком уже зарегистрирован в системе");
				json.put("status", false);
			} else {
				userModel.fetchRow("register", login, PasswordEncryptor.crypt(login, password), email);
			}
		}  else {
			json.put("message", "Пользователь с таким логином уже существует");
			json.put("status", false);
		}

		setAjaxResponse(json.toString());
	}

	public void actionLogout() throws Exception {

		if (getEnvironment().getSessionManager().has()) {
			getEnvironment().getSessionManager().remove();
			getEnvironment().getSessionManager().save();
		}

		getSession().getCookies().delete("JAW_SESSION_ID");

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

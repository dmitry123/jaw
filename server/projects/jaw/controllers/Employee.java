package controllers;

import jaw.Core.*;
import jaw.Core.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.Exception;
import java.sql.ResultSet;

public class Employee extends Controller {

	/**
	 * @param environment - Every core's extension must have environment
	 * with predeclared extensions
	 */
	public Employee(Environment environment) {
		super(environment);
	}

	public void actionLogin() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		User user = getEnvironment().getUserSessionManager().get();
		user.put("employee", GET("id"));

		JSONObject json = new JSONObject();
		json.put("status", true);
		setAjaxResponse(json.toString());
	}

	public void actionLogout() throws Exception {
	}

	public void actionGetUserEmployees() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		int userID = getEnvironment().getUserSessionManager().get().getID();

		ResultSet resultSet = getModel().fetchSet("fetchUserEmployee", userID);

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		while (resultSet.next()) {
			JSONObject node = new JSONObject();
			node.put("id", resultSet.getInt("id"));
			node.put("name",
				resultSet.getString("surname") + " " +
				resultSet.getString("name") + " " +
				resultSet.getString("patronymic")
			);
			array.put(node);
		}

		json.put("status", true);
		json.put("employees", array);

		setAjaxResponse(json.toString());
	}

	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}
}

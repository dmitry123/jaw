package controllers;

import jaw.Core.*;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class Request extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Request(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}

	public void actionJoinCompany() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		int userID = getEnvironment().getUserSessionManager().get().getID();
		int receiverID = Integer.parseInt(GET("receiverID"));
		int companyID = getModel("Company").fetchRow("fetchByEmployee", receiverID).getID();

		int senderID = getModel("Employee").fetchRow("register", GET("name"), GET("surname"),
			GET("patronymic"), userID, 0, 0, 0).getID();

		registerJoin(receiverID, senderID, 0, GET("message"), "company/join");
	}

	public void actionJoinProduct() throws Exception {

		if (!checkAccessWithResponse()) {
			return;
		}

		registerJoin(Integer.parseInt(GET("receiverID")), Integer.parseInt(GET("senderID")),
			Integer.parseInt(GET("productID")), GET("message"), "product/join"
		);
	}

	public void actionGetInfo() throws Exception {

		if (!checkAccessWithResponse() || !getEnvironment().getUserSessionManager().get().containsKey("employee")) {
			return;
		}

		ResultSet resultSet = getModel().fetchSet("fetchInfo",
			Integer.parseInt(GET("id"))
		);

		JSONObject json = new JSONObject();

		if (resultSet.next()) {
			json.put("request", Model.buildMap(resultSet));
		}

		json.put("status", true);

		setAjaxResponse(json.toString());
	}

	public void actionCancel() throws Exception {

		jaw.Core.User user = getEnvironment().getUserSessionManager().get();

		if (!checkAccessWithResponse() || !user.containsKey("employee")) {
			return;
		}

		final int requestID = Integer.parseInt(GET("id"));
		int employeeID = Integer.parseInt(user.get("employee").toString());

		final ResultSet requestSet = getModel().fetchByID(requestID);

		if (!requestSet.next()) {
			postErrorMessage("Запрос не найден");
			return;
		}

		if (requestSet.getInt("receiver_id") != employeeID) {
			postErrorMessage("Доступ запрещен");
			return;
		}

		getModel().deleteByID(requestID);

		if (requestSet.getInt("product_id") == 0) {

			if (!checkAccessWithResponse("product/join")) {
				return;
			}

			getModel("Employee").deleteByID(requestSet.getInt("sender_id"));

		} else {
			if (!checkAccessWithResponse("company/join")) {
				return;
			}
		}

		JSONObject json = new JSONObject();
		json.put("status", true);
		setAjaxResponse(json.toString());
	}

	public void actionAccept() throws Exception {

		jaw.Core.User user = getEnvironment().getUserSessionManager().get();

		if (!checkAccessWithResponse() || !user.containsKey("employee")) {
			return;
		}

		final int requestID = Integer.parseInt(GET("id"));
		int employeeID = Integer.parseInt(user.get("employee").toString());

		ResultSet requestSet = getModel().fetchSet("fetchInfo", requestID);

		if (!requestSet.next()) {
			postErrorMessage("Запрос не найден");
			return;
		}

		final Map<String, String> map = Model.buildMap(requestSet);

		if (Integer.parseInt(map.get("request.receiver_id")) != employeeID) {
			postErrorMessage("Доступ запрещен");
			return;
		}

		if (Integer.parseInt(map.get("request.product_id")) != 0) {

			if (!checkAccessWithResponse("product/join")) {
				return;
			}

			getModel("ProductEmployee").insert(new LinkedHashMap<String, Object>() {{
				put("product_id", map.get("request.product_id"));
				put("employee_id", map.get("request.sender_id"));
			}});

			getModel("EmployeeGroup").insert(new LinkedHashMap<String, String>() {{
				put("employee_id", map.get("request.sender_id"));
				put("group_id", "" + getModel("Group").fetchRow("fetchByName", "employee").getID());
			}});

		} else {

			if (!checkAccessWithResponse("company/join")) {
				return;
			}

			getModel("Employee").updateByID(map.get("request.sender_id"),
				new LinkedHashMap<String, Object>() {{
					put("company_id", map.get("company.id"));
				}}
			);
		}

		getModel().deleteByID(requestID);

		JSONObject json = new JSONObject();
		json.put("status", true);
		setAjaxResponse(json.toString());
	}

	private void registerJoin(int receiverID, int senderID, int productID, String message, String privilegeID) throws Exception {

		getModel().fetchInsert("register",
			receiverID, senderID, productID, privilegeID, message
		);

		JSONObject jsonResponse = new JSONObject();

		jsonResponse.put("status", true);
		jsonResponse.put("message", "Запрос на регисрацию отправлен");

		setAjaxResponse(jsonResponse.toString());
	}
}

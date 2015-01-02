package controllers;

import jaw.Core.Controller;
import jaw.Core.Environment;
import org.json.JSONObject;

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

		if (!checkAccessWithResponse("company/join")) {
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

		if (!checkAccessWithResponse("product/join")) {
			return;
		}

		registerJoin(Integer.parseInt(GET("receiverID")), Integer.parseInt(GET("senderID")),
			Integer.parseInt(GET("productID")), GET("message"), "product/join"
		);
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

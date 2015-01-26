package jaw.controllers;

import jaw.Core.Controller;
import jaw.Core.Environment;
import jaw.Core.Form;
import org.json.JSONObject;

import java.lang.Exception;

public class Ticket extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Ticket(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {
		redirect("Index", "Denied");
	}

	public void actionRegister() throws Exception {

		JSONObject json = new JSONObject();
		Form form = getEnvironment().getFormManager().get("Ticket");

		if (!form.validate(POST("model"))) {
			setAjaxResponse(form.getJsonResponse());
			return;
		}

		form.getForm().put("creator_id", getEnvironment().getSession().get("employee").toString());
		getModel("Ticket").insert(form.getForm());

		json.put("message", "Задача была успешно добавлена");
		json.put("status", true);
		json.put("validation", true);

		setAjaxResponse(json.toString());
	}
}
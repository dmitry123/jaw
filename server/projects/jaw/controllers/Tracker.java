package jaw.controllers;

import jaw.Core.Controller;
import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Html.Html;

import java.lang.Exception;
import java.sql.ResultSet;
import java.util.Map;

import java.io.StringWriter;

public class Tracker extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Tracker(Environment environment) {
		super(environment);
	}

	@Override
	public void actionView() throws Exception {

		if (!checkAccessWithResponse() || !getEnvironment().getSession().containsKey("employee")) {
			redirect("Index", "Denied"); return;
		}

		render("View");
	}

	public void actionCurrent() throws Exception {

		if (!checkAccessWithResponse() || !getEnvironment().getSession().containsKey("employee")) {
			redirect("Index", "Denied"); return;
		}

		renderTickets("SYSTEM_CURRENT_TICKET_TABLE", getModel("Ticket").fetchSet(
			"fetchByEmployee", Integer.parseInt(
				getEnvironment().getSessionManager().get().get("employee").toString()
			)
		));

		render("Current");
	}

	public void actionCompany() throws Exception {

		if (!checkAccessWithResponse() || !getEnvironment().getSession().containsKey("employee")) {
			redirect("Index", "Denied"); return;
		}

		renderTickets("SYSTEM_TICKET_COMPANY_TABLE", getModel("Ticket").fetchSet(
			"fetchCompanyByEmployee", Integer.parseInt(
				getEnvironment().getSessionManager().get().get("employee").toString()
			)
		));

		render("Company");
	}

	public void actionProject() throws Exception {

		if (!checkAccessWithResponse() || !getEnvironment().getSession().containsKey("employee")) {
			redirect("Index", "Denied"); return;
		}

		renderTickets("SYSTEM_TICKET_PROJECT_TABLE", getModel("Ticket").fetchSet(
			"fetchProjectByEmployee", Integer.parseInt(
				getEnvironment().getSessionManager().get().get("employee").toString()
			)
		));

		render("Project");
	}

	private void renderTickets(String alias, final ResultSet tickets) throws Exception {

		StringWriter writer = new StringWriter();

		new Html(writer) {{
			int total = 0;
			while (tickets.next()) {
				Map<String, String> map = Model.buildMap(tickets);
				tr();
					td().text(map.get("ticket.id")); end();
					td().text(map.get("ticket.name")); end();
					td().text(map.get("project.name")); end();
					td().text(map.get("ticket.precedence")); end();
					td().text(map.get("ticket.creation_date")); end();
				end();
				++total;
			}
			if (total == 0) {
				tr();
					td().colspan("5").style("text-align: center; padding-bottom: 15px;");
						h3().text("Нет активных задач"); end();
					end();
				end();
			}
		}};

		getEnvironment().getMustacheDefiner().put(alias,
			writer.toString().replaceAll("[\n\r\t]+", "")
		);

		String button = "";

		if (checkAccess("ticket/register")) {

			writer = new StringWriter();

			new Html(writer) {{
				button().classAttr("btn btn-primary").attr(
					"data-toggle", "modal", "data-target", "#modal-register-ticket"
				).id("create-ticket").text("Создать задачу"); end();
			}};

			button = writer.toString().replaceAll("[\n\r\t]+", "");
		}

		getEnvironment().getMustacheDefiner().put("SYSTEM_TICKET_CREATE_BUTTON", button);
	}
}

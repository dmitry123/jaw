package jaw.controllers;

import jaw.Core.*;
import jaw.Core.Session;
import jaw.Html.Html;

import java.io.StringWriter;
import java.lang.Exception;
import java.sql.ResultSet;


/**
 * Created by Savonin on 2014-12-04
 */
public class System extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public System(Environment environment) {
		super(environment);
	}

	public int getRequestCount() throws Exception {
		return getModel("Request").fetchSize("receiver_id = ?",
			getEnvironment().getSession().get("employee")
		);
	}

	public int getNotificationCount() throws Exception {
		return getModel("Message").fetchSize("receiver_id = ?",
			getEnvironment().getSession().get("employee")
		);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionView() throws Exception {

		Session session = getEnvironment().getSession();

		if (session == null || !session.containsKey("employee")) {
			redirect("Index", "View"); return;
		}

		renderVm("View", "SystemMenu", "ShowRequest");
	}

	public void actionTracker() throws Exception {

		Session session = getEnvironment().getSession();

		if (session == null || !session.containsKey("employee")) {
			redirect("Index", "View"); return;
		}

		renderVm("Tracker",
			"SystemMenu",
			"ShowRequest",
			"TrackerCurrent"
		);
	}
}

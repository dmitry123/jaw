package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Core.Widget;

import java.lang.Object;
import java.lang.String;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class SystemMenu extends Widget {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public SystemMenu(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to run your widget
	 * @param data - Data to run widget
	 */
	@Override
	public void run(HashMap<String, Object> data) throws Exception {

		render(new HashMap<String, Object>() {{

			Collection<LinkedHashMap<String, String>> requests = new Vector<LinkedHashMap<String, String>>();
			Collection<LinkedHashMap<String, String>> messages = new Vector<LinkedHashMap<String, String>>();

			ResultSet requestSet = getModel("Request").fetchSet("fetchByEmployeeID",
				Integer.parseInt(getEnvironment().getSession().get("employee").toString())
			);

			ResultSet messageSet = getModel("Message").fetchSet("fetchByEmployeeID",
				Integer.parseInt(getEnvironment().getSession().get("employee").toString())
			);

			while (requestSet.next()) {
				requests.add(Model.buildMap(requestSet));
			}

			while (messageSet.next()) {
				LinkedHashMap<String, String> map = Model.buildMap(messageSet);
				if (map.get("message.message").length() > 100) {
					map.put("message.message", map.get("message.message").substring(0, 100) + "...");
				}
				messages.add(map);
			}

			put("requests", requests);
			put("messages", messages);
		}});
	}

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	@Override
	public String getAlias() {
		return "SYSTEM_MENU";
	}
}

package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Core.Widget;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class TrackerCurrent extends Widget {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public TrackerCurrent(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to run your widget
	 * @param data - Data to run widget
	 */
	@Override
	public void run(final HashMap<String, Object> data) throws Exception {
		render("TrackerTable", new HashMap<String, Object>() {{
			put("tickets", new Vector<LinkedHashMap<String, String>>() {{
				ResultSet set = getModel("Ticket").fetchSet("fetchByEmployee",
					getEnvironment().getSession().get("employee")
				);
				while (set.next()) {
					add(Model.buildPrefixMap(set));
				}
			}});
		}});
	}

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	@Override
	public String getAlias() {
		return "TRACKER_CURRENT";
	}
}

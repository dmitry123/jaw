package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Core.Widget;

import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Form extends Widget {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Form(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to run your widget
	 * @param data - Data to run widget
	 */
	@Override
	public void run(final HashMap<String, Object> data) throws Exception {
		if (!data.containsKey("form")) {
			throw new Exception("Form widget must contains 'form' field with form name");
		}
		if (getEnvironment().getFormManager().get(data.get("form").toString()) == null) {
			throw new Exception("Unresolved form path \"" + data.get("form") + "\"");
		}
		final Map<String, Object> config = getEnvironment().getFormManager().get(data.get("form").toString()).getConfig();
		for (Object value : config.values()) {
			HashMap<String, Object> map = ((HashMap<String, Object>) value);
			if (!map.containsKey("type")) {
				map.put("type", "text");
			}
			if (!map.containsKey("data")) {
				continue;
			}
			Object configData = map.get("data");
			if (!(configData instanceof ResultSet)) {
				continue;
			}
			ResultSet set = ((ResultSet) configData);
			Vector<LinkedHashMap<String, String>> map2 = new Vector<LinkedHashMap<String, String>>();
			while (set.next()) {
				map2.add(Model.buildMap(set));
			}
			if (map.containsKey("format")) {
				String format = map.get("format").toString();
				Vector<String> vector = new Vector<String>(map2.size());
				for (LinkedHashMap<String, String> field : map2) {
					String result = format;
					Matcher matcher = Pattern.compile("%\\{(.*?)\\}").matcher(format);
					while (matcher.find()) {
						String id = matcher.group(1);
						if (field.get(id) == null) {
							continue;
						}
						result = result.replaceAll("%\\{(" + id + ")\\}", field.get(id));
					}
					vector.add(result);
				}
				map.put("data", vector);
			} else {
				map.put("data", map2);
			}
		}
		render(new HashMap<String, Object>() {{
			put("config", config);
			put("id", data.get("id"));
			put("title", data.get("title"));
		}});
	}

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	@Override
	public String getAlias() {
		return "FORM";
	}
}

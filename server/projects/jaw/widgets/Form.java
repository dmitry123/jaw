package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Model;
import jaw.Core.Widget;

import java.lang.Exception;
import java.lang.Object;
import java.lang.String;
import java.sql.ResultSet;
import java.util.*;
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
			if (map.containsKey("validate") && map.get("validate").toString().toLowerCase().contains("required")) {
				map.put("required", "required");
			}
			if (!map.containsKey("data")) {
				continue;
			}
			Object configData = map.get("data");
			if (!(configData instanceof ResultSet)) {
				Vector<Map<String, String>> maps = new Vector<Map<String, String>>(((Collection<Vector>) configData).size());
				int index = 1;
				for (Object val : ((Collection<Vector>) configData)) {
					HashMap<String, String> field = new HashMap<String, String>(2);
					field.put("value", val.toString());
					field.put("id", Integer.toString(index++));
					maps.add(field);
				}
				map.put("data", maps);
				continue;
			}
			ResultSet set = ((ResultSet) configData);
			Vector<LinkedHashMap<String, String>> map2 = new Vector<LinkedHashMap<String, String>>();
			while (set.next()) {
				map2.add(Model.buildMap(set));
			}
			if (map.containsKey("format")) {
				Vector<String> result = format(map.get("format").toString(), map2);
				Vector<String> identifications;
				if (map.containsKey("id")) {
					identifications = format(map.get("id").toString(), map2);
				} else {
					identifications = null;
				}
				Vector<Map<String, String>> maps = new Vector<Map<String, String>>(result.size());
				if (identifications != null && result.size() == identifications.size()) {
					for (int i = 0; i < result.size(); i++) {
						HashMap<String, String> field = new HashMap<String, String>(2);
						field.put("value", result.get(i));
						field.put("id", identifications.get(i));
						maps.add(field);
					}
				} else {
					for (int i = 0; i < result.size(); i++) {
						HashMap<String, String> field = new HashMap<String, String>(2);
						field.put("value", result.get(i));
						field.put("id", Integer.toString(i + 1));
						maps.add(field);
					}
				}
				map.put("data", maps);
			} else {
				map.put("data", map2);
			}
		}
		if (data.containsKey("alias")) {
			alias = data.get("alias").toString();
		}
		render(new HashMap<String, Object>() {{
			put("config", config);
			put("id", data.get("id"));
			put("title", data.get("title"));
		}});
	}

	private Vector<String> format(String format, Vector<LinkedHashMap<String, String>> map) {
		Vector<String> vector = new Vector<String>(map.size());
		for (LinkedHashMap<String, String> field : map) {
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
		return vector;
	}

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	@Override
	public String getAlias() {
		return alias;
	}

	private String alias = "FORM";
}

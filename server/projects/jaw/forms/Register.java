package jaw.forms;

import jaw.Core.Environment;
import jaw.Core.Form;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Register extends Form {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Register(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to return form configuration, form should return map in next format: Every map's key is name
	 * of form's identification value, likes name of table in database or something else. Every row must contains: +
	 * text - Translated text that will be displayed + type - Type of value (number, text, select, date ... etc)
	 * @return - Form configuration
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> getConfig() throws Exception {
		return new HashMap<String, Object>() {{
			put("login", new LinkedHashMap<String, Object>() {{
				put("name", "Логин");
				put("type", "text");
			}});
			put("password", new LinkedHashMap<String, Object>() {{
				put("name", "Пароль");
				put("type", "password");
			}});
			put("repeat", new LinkedHashMap<String, Object>() {{
				put("name", "Пароль");
				put("type", "password");
			}});
			put("email", new LinkedHashMap<String, Object>() {{
				put("name", "Почта");
				put("type", "email");
			}});
		}};
	}
}

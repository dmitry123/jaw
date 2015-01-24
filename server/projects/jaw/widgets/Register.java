package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Widget;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Savonin on 2015-01-16
 */
public class Register extends Widget {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Register(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to run your widget
	 * @param data - Data to run widget
	 */
	@Override
	public void run(HashMap<String, Object> data) throws Exception {
		render(new LinkedHashMap<String, Object>() {{
			put("body", "Hello, World");
		}});
	}

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	@Override
	public String getAlias() {
		return "REGISTER";
	}
}

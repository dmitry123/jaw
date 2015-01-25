package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Widget;

import java.lang.Exception;
import java.lang.Object;
import java.util.HashMap;

/**
 * Created by Savonin on 2015-01-16
 */
public class Modal extends Widget {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Modal(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to run your widget
	 * @param data - Data to run widget
	 */
	@Override
	public void run(HashMap<String, Object> data) throws Exception {
		if (data.containsKey("alias")) {
			alias = data.get("alias").toString();
		}
		if (data.containsKey("form")) {
			Widget form = getEnvironment().getWidgetManager().get("Form");
			if (form == null) {
				throw new Exception("Widget 'Form' hasn't been declared in widget scope");
			}
			getEnvironment().getMustacheDefiner().remove("FORM");
			form.run(((HashMap<String, Object>) data.get("form")));
			data.put("body", getEnvironment().getMustacheDefiner().get("FORM"));
		}
		render(data);
	}

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	@Override
	public String getAlias() {
		return alias;
	}

	private String alias = "MODAL";
}

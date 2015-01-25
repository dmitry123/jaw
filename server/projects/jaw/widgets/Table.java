package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Widget;

import java.util.HashMap;

public class Table extends Widget {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Table(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to run your widget
	 * @param data - Data to run widget
	 */
	@Override
	public void run(HashMap<String, Object> data) throws Exception {
		render();
	}

	/**
	 * Override that method to return widget's alias for mustache definer
	 * @return - Name of widget's alias
	 */
	@Override
	public String getAlias() {
		return "TABLE";
	}
}

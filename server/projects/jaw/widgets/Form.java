package jaw.widgets;

import jaw.Core.Environment;
import jaw.Core.Widget;

import java.util.HashMap;

/**
 * Created by Savonin on 2015-01-16
 */
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
	public void run(HashMap<String, Object> data) throws Exception {
		
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

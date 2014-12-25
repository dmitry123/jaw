package views;

import Core.*;
import Core.InternalError;

import java.util.Map;

/**
 * Created by Savonin on 2014-12-04
 */
public class System extends View {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public System(Environment environment) {
		super(environment);
	}

	/**
	 * Every view must implements default render method to display basic information about page or widget
	 * @param hashData
	 */
	@Override
	public void renderView(Map<String, Object> hashData) throws Core.InternalError {
		template("common/header");
		template("system/menu");
		template("system/view");
		template("modal/register");
		template("common/footer");
	}
}

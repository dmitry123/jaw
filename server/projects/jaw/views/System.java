package jaw.views;

import jaw.Core.*;


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
	public void renderView(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("system/menu");
		template("system/view");
		template("modal/show-request");
		template("common/footer");
	}
}

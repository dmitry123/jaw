package jaw.views;

import jaw.Core.*;


import java.util.Map;

/**
 * Created by dmitry on 28.11.14
 */
public class Tracker extends View {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Tracker(Environment environment) {
		super(environment);
	}

	@Override
	public void renderView(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("common/footer");
	}

	public void renderCurrent(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("system/menu");
		template("modal/register-ticket");
		template("tracker/current");
		template("common/footer");
	}

	public void renderCompany(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("system/menu");
		template("modal/register-ticket");
		template("tracker/company");
		template("common/footer");
	}

	public void renderProject(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("system/menu");
		template("modal/register-ticket");
		template("tracker/project");
		template("common/footer");
	}
}

package views;

import jaw.Core.*;


import java.util.Map;

/**
 * Created by dmitry on 28.11.14
 */
public class Index extends View {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Index(Environment environment) {
		super(environment);
	}

	/**
	 * Every view must implements default render
	 * method to display basic information about
	 * page or widget
	 * @param hashData
	 */
	@Override
	public void renderView(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("index/view");
		template("modal/register");
		template("common/footer");
	}

	/**
	 *
	 * @param hashData
	 * @throws Exception
	 */
	public void renderDenied(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("index/denied");
		template("common/footer");
	}

	/**
	 *
	 * @param hashData
	 * @throws Exception
	 */
	public void renderProject(Map<String, Object> hashData) throws Exception {
		template("common/header");
		template("index/project");
		template("modal/create-company");
		template("modal/join-company");
		template("modal/create-project");
		template("modal/join-project");
		template("common/footer");
	}
}

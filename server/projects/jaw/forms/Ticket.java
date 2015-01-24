package jaw.forms;

import jaw.Core.Environment;
import jaw.Core.Form;

import java.lang.String;
import java.util.*;

/**
 * Created by Savonin on 2015-01-08
 */
public class Ticket extends Form {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Ticket(Environment environment) {
		super(environment);
	}

	@Override
	public Map<String, Object> getConfig() throws Exception {

		return new HashMap<String, Object>() {{

			// Name
			put("name", new HashMap<String, Object>() {{
				put("text", "Название задачи");
				put("type", "text");
			}});

			// Owner
			put("owner_id", new HashMap<String, Object>() {{
				put("text", "Владелец задачи");
				put("type", "text");
				put("data", getModel("Employee").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
			}});

			// Precedence
			put("precedence", new HashMap<String, Object>() {{
				put("text", "Приоритет");
				put("type", "select");
				put("data", new Vector<Integer>() {{
					for (int i = 0; i < 5; i++) {
						add(i + 1);
					}
				}});
			}});

			// Product
			put("product_id", new HashMap<String, Object>() {{
				put("text", "Продукт, к которой будет привязана задача");
				put("type", "select");
				put("data", getModel("Product").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
			}});

			// Project
			put("project_id", new HashMap<String, Object>() {{
				put("text", "Проект, в которой находится задача");
				put("type", "select");
				put("data", getModel("Project").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
			}});

			// Parent
			put("parent_id", new HashMap<String, Object>() {{
				put("text", "Родительская задача");
				put("type", "select");
				put("data", getModel("Ticket").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
			}});
		}};
	}
}
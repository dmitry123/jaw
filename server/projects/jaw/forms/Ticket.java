package jaw.forms;

import jaw.Core.Environment;
import jaw.Core.Form;

import java.lang.Object;
import java.lang.String;
import java.util.*;
import java.util.LinkedHashMap;

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

		return new LinkedHashMap<String, Object>() {{

			// Name
			put("name", new HashMap<String, Object>() {{
				put("text", "Название задачи");
				put("type", "text");
				put("validate", "required");
			}});

			// Owner
			put("owner_id", new HashMap<String, Object>() {{
				put("text", "Владелец задачи");
				put("type", "select");
				put("data", getModel("Employee").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
				put("format", "%{employee.surname} %{employee.name}");
				put("id", "%{employee.id}");
				put("validate", "required");
			}});

			// Precedence
			put("precedence", new HashMap<String, Object>() {{
				put("text", "Приоритет");
				put("type", "select");
				put("data", new Vector<Object>() {{
					for (int i = 0; i < 5; i++) {
						add(i + 1);
					}
				}});
				put("validate", "required");
			}});

			// Product
			put("product_id", new HashMap<String, Object>() {{
				put("text", "Продукт, к которому будет привязана задача");
				put("type", "select");
				put("data", getModel("Product").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
				put("format", "%{product.name}, %{product.created}");
				put("id", "%{product.id}");
				put("validate", "required");
			}});

			// Project
			put("project_id", new HashMap<String, Object>() {{
				put("text", "Текущий проект");
				put("type", "select");
				put("data", getModel("Project").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
				put("format", "%{product.name}, %{product.created}");
				put("id", "%{project.id}");
			}});

			// Parent
			put("parent_id", new HashMap<String, Object>() {{
				put("text", "Родительская задача");
				put("type", "select");
				put("data", getModel("Ticket").fetchSet("fetchByCompany",
					getEnvironment().getSession().get("company")
				));
				put("format", "%{ticket.name}");
				put("id", "%{ticket.id}");
			}});
		}};
	}
}
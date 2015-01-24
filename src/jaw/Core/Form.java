package jaw.Core;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Form extends Component {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Form(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to return form configuration, form should return
	 * map in next format: Every map's key is name of form's identification value, likes
	 * name of table in database or something else. Every row must contains:
	 *  + text - Translated text that will be displayed
	 *  + type - Type of value (number, text, select, date ... etc)
	 * @return - Form configuration
	 * @throws Exception
	 */
	public abstract Map<String, Object> getConfig() throws Exception;

	/**
	 * Set form's model
	 * @param model - Model
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * Get form's model
	 * @return - Model with same name as form's
	 * @throws Exception
	 */
	public Model getModel() throws Exception {
		return getModel(null);
	}

	/**
	 * Get any model via path
	 * @return - Model
	 */
	public Model getModel(String path) throws Exception {
		if (path != null) {
			return getEnvironment().getModelManager().get(path);
		} else {
			return model;
		}
	}

	private Model model;
}

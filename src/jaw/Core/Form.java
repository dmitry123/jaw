package jaw.Core;

import java.sql.ResultSet;
import java.util.LinkedHashMap;

public abstract class Form extends Component {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Form(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to return form's model
	 * @return - Linked hash map associated with collections or object values
	 */
	public abstract LinkedHashMap<String, ResultSet> getForm() throws Exception;

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

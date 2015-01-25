package jaw.Core;

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
	 * Validate model with specific form
	 * @param model - Map with model fields (static map for single table)
	 * @throws Exception
	 */
	public final void validate(Map<String, Object> model) throws Exception {
		Map<String, Object> config = getConfig();
		for (Map.Entry<String, Object> entry : config.entrySet()) {
			if (!(entry.getValue() instanceof Map)) {
				continue;
			}
			Map<String, Object> map = ((Map) entry.getValue());
			if (!map.containsKey("validate")) {
				continue;
			}
			String[] validators = map.get("validate").toString().split("\\s*,\\s*");
			for (String validatorName : validators) {
				Validator validator = getEnvironment().getValidatorManager().get(validatorName);
				validator.setForm(this);
				validator.setField(entry.getKey());
				validator.setValue(model.get(entry.getKey()));
				validator.setModel(model);
				validator.setConfig(map);
				validator.validate();
			}
		}
	}
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

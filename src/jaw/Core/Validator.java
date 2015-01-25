package jaw.Core;

import java.util.Map;

public abstract class Validator extends Component {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Validator(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to validate field from some form model
	 * @return - True if validation finished with success or false on failure
	 */
	public abstract boolean validate();

	/**
	 * Override that method to return error message
	 * @return - Message with error on validation failure
	 */
	public abstract String getMessage();

	/**
	 * Set validator form
	 * @param form - Form to validate
	 */
	public void setForm(Form form) {
		this.form = form;
	}

	/**
	 * Get validator form
	 * @return - Form to validate
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * Set name of field, which you're going to validate
	 * @param field - Name of field to validate
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * Get name of validating field
	 * @return - Field's name
	 */
	public String getField() {
		return field;
	}

	/**
	 * Set field's value to validate
	 * @param value - Value to validate
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Get field's value to validate
	 * @return - Value to validate
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Set map with model where field's value and key contains
	 * @param model - Map with all model's fields
	 */
	public void setModel(Map<String, Object> model) {
		this.model = model;
	}

	/**
	 * Get map with model where field's value and key contains
	 * @return - Map with all model's fields
	 */
	public Map<String, Object> getModel() {
		return model;
	}

	/**
	 * Set field's configuration from config map
	 * @param config - Map with field's configuration
	 */
	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	/**
	 * Get field's configuration from config map
	 * @return - Map with field's configuration
	 */
	public Map<String, Object> getConfig() {
		return config;
	}

	private String field = null;
	private Form form = null;
	private Object value = null;
	private Map<String, Object> model = null;
	private Map<String, Object> config = null;
}

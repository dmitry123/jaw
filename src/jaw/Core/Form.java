package jaw.Core;

import javafx.util.Pair;
import org.json.JSONObject;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
	 * @return - Validation status
	 * @throws Exception
	 */
	public final boolean validate(Map<String, Object> model) throws Exception {
		Map<String, Object> config = getConfig();
		boolean success = true;
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
				Validator validator = createValidator(validatorName, entry.getKey(), model, map);
				if (!validator.validate()) {
					errorMessages.add(new Pair<String, String>(entry.getKey(), validator.getMessage()));
					success = false;
				}
			}
		}
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			form.put(entry.getKey(), entry.getValue().toString());
		}
		return success;
	}

	/**
	 * Create validator for field validation with model and field's configuration
	 * @param name - Name of validator
	 * @param key - Name of field to validate
	 * @param model - Model with values to validate
	 * @param config - Configuration of validation field
	 * @return - Validator
	 * @throws Exception
	 */
	private Validator createValidator(String name, String key, Map<String, Object> model, Map<String, Object> config) throws Exception {
		Validator validator = getEnvironment().getValidatorManager().get(name);
		if (validator == null) {
			throw new Exception("Can't resolve validator \"" + name + "\"");
		}
		validator.setForm(this);
		validator.setField(key);
		validator.setValue(model.get(key));
		validator.setModel(model);
		validator.setConfig(config);
		return validator;
	}

	/**
	 * Validate model via it's url. It will decode url and cast it to
	 * map model
	 * @param url - Decode url string with model
	 * @return - Validation status
	 * @throws Exception
	 */
	public final boolean validate(String url) throws Exception {
		return validate(buildFromUrl(url));
	}

	/**
	 * Build form's model from encode url
	 * @param url - Encoded url
	 * @return - Map with decode model
	 * @throws Exception
	 */
	public static Map<String, Object> buildFromUrl(String url) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		url = URLDecoder.decode(url, "UTF-8");
		for (String s : url.split("&")) {
			int index = s.indexOf('=');
			map.put(s.substring(0, index), s.substring(index + 1));
		}
		return map;
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

	/**
	 * Get form's model (form model), cuz getModel method in used
	 * @return - Map with form's model
	 */
	public Map<String, String> getForm() {
		return form;
	}

	/**
	 * Generate json response with error messages
	 * @return - Encode json string with error messages
	 */
	public String getJsonResponse() {

		JSONObject json = new JSONObject();
		Vector<HashMap<String, String>> array = new Vector<HashMap<String, String>>();

		for (Pair<String, String> message : getErrorMessages()) {
			HashMap<String, String> error = new HashMap<String, String>();
			error.put("id", message.getKey());
			error.put("message", message.getValue());
			array.add(error);
		}

		json.put("status", false);
		json.put("validation", false);
		json.put("errors", array);
		json.put("message", "Произошли ошибки во время валидации формы");

		return json.toString();
	}

	/**
	 * Get collection with all messages
	 * @return - Vector with error messages
	 */
	public Collection<Pair<String, String>> getErrorMessages() {
		return errorMessages;
	}

	private Collection<Pair<String, String>> errorMessages = new Vector<Pair<String, String>>();
	private Map<String, String> form = new HashMap<String, String>();
	private Model model;
}

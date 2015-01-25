package jaw.validators;

import jaw.Core.Environment;
import jaw.Core.Validator;

public class Required extends Validator {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Required(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to validate field from some form model
	 */
	@Override
	public boolean validate() {
		if (getValue() == null) {
			return false;
		}
		if (getConfig().containsKey("type") && getConfig().get("type").toString().equals("select")) {
			if (getValue() instanceof Integer) {
				return ((Integer) getValue()) != -1;
			}
		}
		return true;
	}

	/**
	 * Override that method to return error message
	 * @return - Message with error on validation failure
	 */
	@Override
	public String getMessage() {
		return "Поле \"" + getConfig().get("text") + "\" обязательно для заполнения";
	}
}

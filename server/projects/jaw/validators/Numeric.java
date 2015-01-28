package jaw.validators;

import jaw.Core.Environment;
import jaw.Core.Validator;

public class Numeric extends Validator {

    /**
     * @param environment - Every core's extension must have environment with predeclared extensions
     */
    public Numeric(Environment environment) {
        super(environment);
    }

    /**
     * Override that method to validate field from some form model
     *
     * @return - True if validation finished with success or false on failure
     */
    @Override
    public boolean validate() {
        if (getValue() == null) {
            return false;
        }
        if (getValue() instanceof Integer ||
            getValue() instanceof Float ||
            getValue() instanceof Long ||
            getValue() instanceof Double
        ) {
            return true;
        } else if (getValue() instanceof String) {
            return ((String) getValue()).matches("[0-9+-]+");
        }
        return false;
    }

    /**
     * Override that method to return error message
     *
     * @return - Message with error on validation failure
     */
    @Override
    public String getMessage() {
        return "Поле \"" + getConfig().get("text") + "\" не является числом";
    }
}

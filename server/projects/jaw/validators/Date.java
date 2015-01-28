package jaw.validators;

import jaw.Core.Environment;
import jaw.Core.Validator;

public class Date extends Validator {

    /**
     * @param environment - Every core's extension must have environment with predeclared extensions
     */
    public Date(Environment environment) {
        super(environment);
    }

    /**
     * Override that method to validate field from some form model
     *
     * @return - True if validation finished with success or false on failure
     */
    @Override
    public boolean validate() {
        return !(getValue() == null || !(getValue() instanceof String))
            && getValue().toString().matches("[0-9]{4}-[0-9]{2}-[0-9]]{2}");
    }

    /**
     * Override that method to return error message
     *
     * @return - Message with error on validation failure
     */
    @Override
    public String getMessage() {
        return "Поле \"" + getConfig().get("text") + "\" не является датой";
    }
}

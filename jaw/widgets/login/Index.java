package widgets.login;

import Core.Controller;
import Core.Environment;

/**
 * Created by Savonin on 2014-11-02
 */
@Annotation.Controller
public class Index extends Controller {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public Index(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	@Override
	public void actionIndex() {

	}
}

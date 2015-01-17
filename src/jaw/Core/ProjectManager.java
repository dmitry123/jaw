package jaw.Core;

/**
 * Created by Savonin on 2014-11-23
 */
public class ProjectManager extends Extension {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public ProjectManager(Environment environment) {
		super(environment);
	}

	/**
	 * @return - Project's compiler
	 */
	public ProjectCompiler getCompiler() {
		return new ProjectCompiler(this);
	}
}

package Core;

/**
 * Created by Savonin on 2014-11-02
 */
public abstract class Controller extends Extension {

	/**
	 * @param environment - Every core's extension must have environment
	 * 		with predeclared extensions
	 */
	public Controller(Environment environment) {
		super(environment);
	}

	/**
	 * Default index action
	 */
	public abstract void actionIndex();

	/**
	 *
	 * @param action  - Name of action to which controller have
	 * 		to be redirected
	 */
	public void redirect(String action) {
	}

	/**
	 * Render view via it's path
	 * @param path - Path tp view
	 */
	public void render(String path) {
	}

	/**
	 * @param model - Model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return Current controller's model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param view - View to set
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 *
	 * @return Current controller's view
	 */
	public View getView() {
		return view;
	}

	private Model model;
	private View view;
}

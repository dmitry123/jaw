package Core;

/**
 * Created by Savonin on 2014-11-02
 */
public abstract class Controller extends Extension {

	/**
	 * @param model - Controller's model, have to
	 * 		be bind by child class via super constructor
	 * @param view - Controller's view, have to
	 * 		be bind by child class via super constructor
	 */
	public Controller(Model model, View view) {

		super(model.getEnvironment());

		this.model = model;
		this.view = view;
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
	 * @return Current controller's model
	 */
	public Model getModel() {
		return model;
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

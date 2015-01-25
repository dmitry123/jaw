package jaw.Core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ManagerCollection extends Extension implements Serializable {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public ManagerCollection(Environment environment) {

		super(environment);

		register(new AbstractManager<Controller>(environment, ComponentType.CONTROLLER));
		register(new AbstractManager<View>(environment, ComponentType.VIEW));
		register(new AbstractManager<Model>(environment, ComponentType.MODEL));
		register(new AbstractManager<Form>(environment, ComponentType.FORM));
		register(new AbstractManager<Form>(environment, ComponentType.MODULE));
		register(new AbstractManager<Form>(environment, ComponentType.WIDGET));
		register(new AbstractManager<Validator>(environment, ComponentType.VALIDATOR));
	}

	public void cleanup() throws Exception {
		for (AbstractManager manager : map.values()) {
			try {
				manager.cleanup();
			} catch (Exception ignored) {
			}
		}
	}

	public AbstractManager<Controller> getControllerManager() throws Exception {
		return map.get(ComponentType.CONTROLLER.getName());
	}

	public AbstractManager<View> getViewManager() throws Exception {
		return map.get(ComponentType.VIEW.getName());
	}

	public AbstractManager<Model> getModelManager() throws Exception {
		return map.get(ComponentType.MODEL.getName());
	}

	public AbstractManager<Form> getFormManager() throws Exception {
		return map.get(ComponentType.FORM.getName());
	}

	public AbstractManager<Module> getModuleManager() throws Exception {
		return map.get(ComponentType.MODULE.getName());
	}

	public AbstractManager<Widget> getWidgetManager() throws Exception {
		return map.get(ComponentType.WIDGET.getName());
	}

	public AbstractManager<Validator> getValidatorManager() throws Exception {
		return map.get(ComponentType.VALIDATOR.getName());
	}

	private <T extends Component> void register(AbstractManager<T> manager) {
		map.put(manager.getType().getName(), manager);
	}

	private Map<String, AbstractManager> map
		= new HashMap<String, AbstractManager>();
}

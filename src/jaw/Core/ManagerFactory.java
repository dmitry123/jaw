package jaw.Core;

public class ManagerFactory {

	/**
	 * Get manager factory
	 * @return - Manager factory
	 */
	public static ManagerFactory getManager() {
		return managerFactory;
	}

	/**
	 * Locked constructor
	 */
	private ManagerFactory() {
		/* Locked */
	}

	public AbstractManager<Controller> createControllerManager(Environment environment) throws Exception {
		return new AbstractManager<Controller>(environment, ComponentType.CONTROLLER);
	}

	public AbstractManager<View> createViewManager(Environment environment) throws Exception {
		return new AbstractManager<View>(environment, ComponentType.VIEW);
	}

	public AbstractManager<Model> createModelManager(Environment environment) throws Exception {
		return new AbstractManager<Model>(environment, ComponentType.MODEL);
	}

	public AbstractManager<Form> createFormManager(Environment environment) throws Exception {
		return new AbstractManager<Form>(environment, ComponentType.FORM);
	}

	public AbstractManager<Module> createModuleManager(Environment environment) throws Exception {
		return new AbstractManager<Module>(environment, ComponentType.MODULE);
	}

	private static ManagerFactory managerFactory
			= new ManagerFactory();
}

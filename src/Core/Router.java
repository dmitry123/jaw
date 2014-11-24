package Core;

import Core.*;

import java.io.File;

/**
 * Created by Savonin on 2014-11-23
 */
public class Router {

	/**
	 * Compute absolute path for component by it's project directory. It
	 * will find element in filesystem, which for existence and build it's
	 * absolute path to file with java extension
	 * @param projectPath - Path to project
	 * @param componentPath - Name of component, which you'd like to find
	 * @param componentFolder - Name of component type's folder
	 * @see Core.Config
	 * @return - Absolute path to file (about project path)
	 * @throws Core.InternalError
	 */
	public static String getAbsolutePath(String projectPath, String componentPath, String componentFolder) throws Core.InternalError {

		File handle = new File(
			projectPath
		);

		if (!handle.exists()) {
			return null;
		}

		String[] listPath = componentPath.split("/");
		String componentName = listPath[listPath.length - 1];
		String absolutePath = projectPath;

		if (listPath.length > 1) {
			absolutePath += Config.MODULE_PATH;
		}

		for (String s : listPath) {
			if (s == listPath[listPath.length - 1]) {
				break;
			}
			absolutePath += s + "/";
		}
		absolutePath += componentFolder;

		File directoryHandle = new File(
			absolutePath
		);

		if (!directoryHandle.exists()) {
			return null;
		}

		return absolutePath + componentName;
	}
}

package Core;

import Core.*;
import Core.InternalError;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by Savonin on 2014-11-02
 */
public class ProjectCompiler {

	/**
	 * Construct project compiler with project manager
	 * @param projectManager - Reference to project's manager
	 */
	public ProjectCompiler(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	/**
	 * Compile
	 * @throws InternalError
	 */
	public void compile() throws InternalError {

		String projectName = getProjectManager().getEnvironment()
				.getProjectName();

		String projectPath = getProjectManager().getEnvironment()
				.getProjectPath();

		File projectHandle = new File(
			projectPath
		);

		Vector<String> files = new Vector<String>();

		if (!projectHandle.exists()) {
			throw new InternalError("ProjectCompiler/compile() : \"Unable to open project dir\"");
		}

		findFiles(files, projectHandle.getPath());

		for (String s : files) {

			String newFilePath = s.replace(projectName + File.separator,
					projectName + File.separator + Config.BINARY_PATH);

			newFilePath = newFilePath.substring(0,
				newFilePath.lastIndexOf(File.separator));

			if (!new File(newFilePath).mkdirs()) {
				/* Ignore */
			}

			compile(s, newFilePath.substring(0, newFilePath.lastIndexOf(File.separator)));
		}
	}

	/**
	 * Find files at path with depth and store it in collection
	 * @param collection - Collection to store elements
	 * @param path - Path to directory with files
	 * @throws InternalError
	 */
	private void findFiles(Collection<String> collection, String path) throws InternalError {

		File handle = new File(path);

		if (!handle.exists()) {
			if (!handle.mkdir()) {
				throw new InternalError(
					"ClassSeeker/findFiles() : \"Unable to create directory (" + handle.getPath() + ")\""
				);
			}
			return ;
		}

		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}

		File[] files = handle.listFiles();

		if (files == null) {
			return ;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				findFiles(collection, f.getPath());
			} else {
				if (!f.getName().endsWith(".java")) {
					continue;
				}
				collection.add(f.getAbsolutePath());
			}
		}
	}

	/**
	 * Compile file and move to binary folder
	 * @param path - Path to "*.java" file
	 * @param folder - Folder to store file (in binary)
	 * @throws InternalError
	 */
	public void compile(String path, String folder) throws InternalError {

		Vector<File> handles = new Vector<File>();
		Vector <String> options = new Vector<String>();

		handles.add(new File(path));

		options.add("-d");
		options.add(folder);

		Iterable<? extends JavaFileObject> compilationUnit
				= fileManager.getJavaFileObjectsFromFiles(handles);

		JavaCompiler.CompilationTask task = javaCompiler.getTask(
				null, fileManager, diagnosticCollector, options, null, compilationUnit);

		if (!task.call() && diagnosticCollector.getDiagnostics().size() > 0) {

			Diagnostic<? extends JavaFileObject> d = diagnosticCollector
				.getDiagnostics().get(0);

			throw new Core.InternalError("ProjectCompiler/compile() : Syntax error on line [" + d.getLineNumber()
				+ "] in " + d.getSource().toUri() + " : \"" + d.getMessage(Locale.getDefault()) + "\"");
		}

		try {
			fileManager.close();
		} catch (IOException e) {
			throw new InternalError("ProjectCompiler/compile() : \"Unable to close file manager\"");
		}
	}

	/**
	 * @return - Compiler's project manager
	 */
	public ProjectManager getProjectManager() {
		return projectManager;
	}

	private DiagnosticCollector<JavaFileObject> diagnosticCollector
			= new DiagnosticCollector<JavaFileObject>();

	private JavaCompiler javaCompiler
			= ToolProvider.getSystemJavaCompiler();

	private StandardJavaFileManager fileManager
			= javaCompiler.getStandardFileManager(diagnosticCollector, null, null);

	private ProjectManager projectManager;
}

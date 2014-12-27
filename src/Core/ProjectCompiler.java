package Core;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
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
	 * @throws Exception
	 */
	public void compile() throws Exception {

		cleanup();

		String projectName = getProjectManager().getEnvironment()
				.getProjectName();

		String projectPath = getProjectManager().getEnvironment()
				.getProjectPath();

		File projectHandle = new File(
			projectPath
		);

		Vector<String> files = new Vector<String>();

		if (!projectHandle.exists()) {
			throw new Exception("ProjectCompiler/compile() : \"Unable to open project dir\"");
		}

		findFiles(files, projectHandle.getPath());

		files.sort(new Comparator<String>() {
			@Override
			public int compare(String left, String right) {
				return left.compareToIgnoreCase(right);
			}
		});

		for (String s : files) {

			String newFilePath = s.replace(projectPath,
				projectPath + Config.BINARY_PATH
			);

			newFilePath = newFilePath.substring(0,
				newFilePath.lastIndexOf(File.separator));

			if (!new File(newFilePath).mkdirs()) {
				/* Ignore */
			}

			newFilePath = newFilePath.substring(0,
					newFilePath.lastIndexOf(File.separator));

			compile(s, newFilePath);
		}
	}

	public void cleanup() throws Exception {

		String projectName = getProjectManager().getEnvironment()
				.getProjectName();

		String projectPath = getProjectManager().getEnvironment()
				.getProjectPath();

		File projectHandle = new File(
			projectPath + Config.BINARY_PATH
		);

		Vector<String> files = new Vector<String>(100);

		findAllFiles(files, projectHandle.getAbsolutePath());

		for (String s : files) {
			new File(s).delete();
		}
	}

	/**
	 * Find files at path with depth and store it in collection
	 * @param collection - Collection to store elements
	 * @param path - Path to directory with files
	 * @throws Exception
	 */
	private void findAllFiles(Collection<String> collection, String path) throws Exception {
		File handle = new File(path);
		if (!handle.exists()) {
			if (!handle.mkdirs()) {
				throw new Exception(
					"ProjectCompiler/findAllFiles() : \"Unable to create directory (" + handle.getPath() + ")\""
				);
			}
			return ;
		}
		File[] files = handle.listFiles();
		if (files == null) {
			return ;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				findAllFiles(collection, f.getPath());
			} else {
				collection.add(f.getAbsolutePath());
			}
		}
	}

	/**
	 * Find files at path with depth and store it in collection
	 * @param collection - Collection to store elements
	 * @param path - Path to directory with files
	 * @throws Exception
	 */
	private void findFiles(Collection<String> collection, String path) throws Exception {
		File handle = new File(path);
		if (!handle.exists()) {
			if (!handle.mkdir()) {
				throw new Exception(
					"ClassSeeker/findFiles() : \"Unable to create directory (" + handle.getPath() + ")\""
				);
			}
			return ;
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
	 * @throws Exception
	 */
	private void compile(String path, String folder) throws Exception {

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

			throw new Exception("ProjectCompiler/compile() : Syntax error on line [" + d.getLineNumber()
				+ "] in " + d.getSource().toUri() + " : \"" + d.getMessage(Locale.getDefault()) + "\"");
		}

		try {
			fileManager.close();
		} catch (IOException e) {
			throw new Exception("ProjectCompiler/compile() : \"Unable to close file manager\"");
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

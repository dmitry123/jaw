package core;

import Core.*;

import javax.tools.*;
import java.io.File;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by Savonin on 2014-11-02
 */
public class ProjectCompiler {

	/**
	 */
	public ProjectCompiler() {
	}

	/**
	 * @param fileName - Path to "*.java" file
	 * @throws Exception
	 */
	public void compile(String fileName) throws Exception {

		Vector<File> fileHandles = new Vector<File>();
		Vector <String> compilerOptions = new Vector<String>();

		fileHandles.add(new File(fileName));

		Iterable<? extends JavaFileObject> compilationUnit
				= fileManager.getJavaFileObjectsFromFiles(fileHandles);

//		compilerOptions.add("-d");
//		compilerOptions.add(projectManager.getProjectPath());

		JavaCompiler.CompilationTask task = javaCompiler.getTask(
				null, fileManager, diagnosticCollector, compilerOptions, null, compilationUnit);

		if (!task.call()) {
			for (Diagnostic<? extends JavaFileObject> d : diagnosticCollector.getDiagnostics()) {
				throw new Core.InternalError("Error on line " + d.getLineNumber() + " in " + d.getSource().toUri() +
					" : \"" + d.getMessage(Locale.getDefault()) + "\"");
			}
		}

		fileManager.close();
	}

	private DiagnosticCollector<JavaFileObject> diagnosticCollector
			= new DiagnosticCollector<JavaFileObject>();

	private JavaCompiler javaCompiler
			= ToolProvider.getSystemJavaCompiler();

	private StandardJavaFileManager fileManager
			= javaCompiler.getStandardFileManager(diagnosticCollector, null, null);
}

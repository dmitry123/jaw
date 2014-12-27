package Server;

import Core.*;


import java.io.*;

/**
 * HtmlReader
 */
public class FileLoader {

	/**
	 * Load html file from server's path
	 *
	 * @param fileName Html file's name
	 */
	public String load(String fileName) throws Exception {

		File fileHandle = new File(fileName);

		if (!fileHandle.exists()) {
			throw new Exception("HtmlReader/load : 'Unable to load file (" + fileName + ")'");
		}

		FileInputStream fileInputStream =
			new FileInputStream(fileHandle);

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(fileInputStream)
		);

		String html = "";
		String line;

		while ((line = bufferedReader.readLine()) != null) {
			html += line + File.separatorChar;
		}

		return html;
	}

	/**
	 * Load html file from view path
	 *
	 * @param fileName File's name
	 */
	public String loadView(String fileName) throws Exception {
		return load(Config.VIEW_PATH + fileName);
	}

	/**
	 * Load index.html
	 */
	public String loadIndex() throws Exception {
		return load(Config.INDEX_PATH);
	}

	/**
	 * @return HtmlReader's global instance
	 */
	public static FileLoader getFileLoader() {
		return fileLoader;
	}

	/**
	 * Locked constructor
	 */
	private FileLoader() {
	}

	/**
	 * Singleton instance
	 */
	static private FileLoader fileLoader
		= new FileLoader();
}

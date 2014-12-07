package Core;

import Server.Mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Savonin on 2014-11-02
 */
public abstract class View extends Component {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public View(Environment environment) {
		super(environment);
	}

	/**
	 * Every view must implements default render
	 * method to display basic information about
	 * page or widget
	 */
	public abstract void renderView(Map<String, Object> hashData) throws InternalError;

	/**
	 * Every view must implements default 404
	 * render method to display basic error
	 */
	public void render404(Map<String, Object> hashData) throws InternalError {
	}

	/**
	 * Render template with specific path
	 * @param path - Path to template to render
	 */
	public void template(String path) throws InternalError {

		String absolutePath = Router.getStaticPath(
			getEnvironment().getProjectPath(), path, "templates" + File.separator
		);

		String htmlPath = absolutePath + ".html";

		inputStream = Mime.TEXT_HTML.getLoader().load(htmlPath);

		byte[] chunk = new byte[1024];
		int length;

		try {
			while ((length = inputStream.read(chunk)) > 0) {
				if (htmlContent == null) {
					htmlContent = new String(chunk);
				} else {
					htmlContent += new String(chunk, 0, length);
				}
			}
		} catch (IOException e) {
			throw new InternalError(
				"View/template() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * @return - View's input stream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 *
	 * @return
	 */
	public String getHtmlContent() {
		return htmlContent;
	}

	private String htmlContent = null;
	private InputStream inputStream = null;
}

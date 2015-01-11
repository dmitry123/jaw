package jaw.core;

import jaw.server.Mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Savonin on 2014-11-02
 */
public class View extends Component {

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
	public void renderView(Map<String, Object> hashData) throws Exception {
		/* Backward compatibility */
	}

	/**
	 * Every view must implements default 404
	 * render method to display basic error
	 */
	public void render404(Map<String, Object> hashData) throws Exception {
		/* Backward compatibility */
	}

	/**
	 * Render template with specific path
	 * @param path - Path to template to render
	 */
	public void template(String path) throws Exception {

		String absolutePath = Router.getStaticPath(
			getEnvironment().getProjectPath(), path, Config.TEMPLATE_PATH
		);

		String htmlPath = absolutePath + ".html";

		InputStream inputStream = Mime.TEXT_HTML.getLoader().load(htmlPath);

		byte[] chunk = new byte[16384];
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
			throw new Exception(
				"View/template() : \"" + e.getMessage() + "\""
			);
		}
	}

	/**
	 * Get view's html content to render
	 * @return - Content with HTML page
	 */
	public String getHtmlContent() {
		return htmlContent;
	}

	/**
	 * Set view's html content
	 * @param htmlContent - Content with HTML page
	 */
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	private String htmlContent = null;
}

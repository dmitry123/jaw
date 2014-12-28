package jaw.Server;

import jaw.Html.Html;

import java.io.StringWriter;

/**
 * Created by Savonin on 2014-11-15
 */
public class HtmlBuilder {

	/**
	 * Default
	 */
	public HtmlBuilder() {
		html = new Html(writer) {
			{
				html();
				  head().lang("en");
				    title().text("Hello, World"); end();
					meta().charset("UTF-8");
				  end();
				    script().type("type/javascript").src("scripts/jquery.js"); end();
				    script().type("type/javascript").src("scripts/jquery.js"); end();
				  body();
			}
		};
	}

	/**
	 * @return - Result html string
	 */
	public String getResult() {
		html.endAll(); return writer.toString();
	}

	/**
	 * @return - Builder's html
	 */
	public Html getHtml() {
		return html;
	}

	private StringWriter writer
			= new StringWriter();

	private Html html;
}

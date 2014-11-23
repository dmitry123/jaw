package Server;

import Html.Html;

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
				    h1().text("Hello, World");
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
	 * @return - Current instance
	 */
	private HtmlBuilder getThis() {
		return this;
	}

	private StringWriter writer
			= new StringWriter();

	private Html html;
}

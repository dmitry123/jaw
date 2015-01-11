package jaw.core;

import jaw.Mustache.Mustache;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Savonin on 2014-12-05
 */
public class MustacheDefiner extends HashMap<String, String> implements Serializable {

	/**
	 * Compile html code and execute mustache to apply macros
	 * @param htmlText - Text with html
	 * @return - Compiled html text
	 */
	public String execute(String htmlText) {
		try {
			return Mustache.compiler().compile(htmlText).execute(this);
		} catch (Exception ignored) {
		}
		return htmlText;
	}
}

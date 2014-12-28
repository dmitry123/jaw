package jaw.Core;

import jaw.Mustache.Mustache;
import java.util.HashMap;

/**
 * Created by Savonin on 2014-12-05
 */
public class MustacheDefiner extends Extension {

	/**
	 * @param environment - Every core's extension must have environment with predeclared extensions
	 */
	public MustacheDefiner(Environment environment) {
		super(environment);
	}

	/**
	 *
	 * @param htmlText
	 * @return
	 */
	public String execute(String htmlText) {
		try {
			return Mustache.compiler().compile(htmlText).execute(data);
		} catch (Exception ignored) {
		}
		return htmlText;
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		data.put(key, value);
	}

	/**
	 *
	 * @param key
	 */
	public void remove(String key) {
		data.remove(key);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return data.get(key);
	}

	HashMap<String, String> data =
			new HashMap<String, String>();
}

package jaw.server;

/**
 * Class with headers
 */
public class Headers {

	/**
	 * Get header for html response
	 *
	 * @param content Content with html page
	 * @return Header with html content
	 */
	public static String getHtmlHeader(String content) {
		return
			"HTTP/1.1 200 OK\r\n" +
			"Server: YarServer/2009-09-09\r\n" +
			"Content-Type: text/html\r\n" +
			"Content-Length: " + content.length() + "\r\n" +
			"Connection: close\r\n\r\n"
		+ content;
	}
}

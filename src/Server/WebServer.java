package Server;

import Core.*;
import Terminal.Machine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Savonin on 2014-11-08
 */
public class WebServer extends NanoHttpd {

	/**
	 * Constructs an HTTP server on given port.
	 */
	public WebServer() {
		super(Config.SERVER_PORT);
	}

	/**
	 * Override this to customize the server.
	 * <p/>
	 * <p/>
	 * (By default, this delegates to serveFile() and allows directory listing.)
	 * @param session The HTTP session
	 * @return HTTP response, see class Response for details
	 */
	@Override
	public Response serve(IHTTPSession session) throws Exception {

		Method method = session.getMethod();
		String uri = session.getUri();
		Map<String, String> parameters = session.getParms();
		FileLoader fileLoader = FileLoader.getFileLoader();
		InputStream uriData = null;
		String resultPath = Config.SERVER_PATH + uri.substring(1);
		Mime mime = Mime.findByExtension(getFileExtension(uri));

		logger.log(method + " '" + uri + "' ");

		try {
			if (uri.length() > 1 && uri.indexOf(0) != '/' && mime != null) {
				uriData = mime.getLoader().load(resultPath);
			}
		} catch (Exception ignored) {
			logger.log("Can't load file : \"" + uri + "\"");
		}

		if (uriData == null && mime == null) {
			return new Response(fileLoader.loadIndex());
		} else {
			return new Response(Response.Status.OK,
					mime.getName(), uriData);
		}
	}

	/**
	 * @throws Core.InternalError
	 */
	public static void run() throws Core.InternalError, IOException {

		WebServer server = new WebServer();
		Machine terminal = new Machine(null);

		try {
			server.start();
		} catch (IOException ioe) {
			logger.log("Couldn't start server: " + ioe.getMessage());
			System.exit(-1);
		}

		logger.log("Server started");
		System.out.println("Server Terminal:");

		terminal.register("terminal",
			new ServerTerminal(terminal)
		);
		terminal.start();

		server.stop();
		logger.log("Server stopped");
	}

	/**
	 * @param fileName - File's name
	 * @return - File's extension
	 */
	private String getFileExtension(String fileName) {
		int lastIndexOf;
		if ((lastIndexOf = fileName.lastIndexOf(".")) == -1) {
			return "";
		}
		return fileName.substring(lastIndexOf);
	}

	private static Logger logger
			= Logger.getLogger();
}

package jaw.server;

import jaw.core.*;
import jaw.terminal.Machine;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Savonin on 2014-11-08
 */
public class WebServer extends NanoHttpd {

	/**
	 * Constructs an HTTP server on given port.
	 */
	private WebServer() {
		super(Config.SERVER_PORT);
	}

	/**
	 * Get WebServer singleton instance
	 * @return - Web server
	 */
	public static WebServer getWebServer() {
		return webServer;
	}

	private static WebServer webServer
		= new WebServer();

	/**
	 * Override this to customize the server.
	 * <p/>
	 * <p/>
	 * (By default, this delegates to serveFile() and allows directory listing.)
	 * @param session The HTTP session
	 * @return HTTP response, see class Response for details
	 */
	@Override
	public Response serve(IHTTPSession session) throws java.lang.Exception {

		Method sessionMethod = session.getMethod();
		String uri = session.getUri();
		Map<String, String> postFiles = new HashMap<String, String>();
		View view = null;
		WebSocket webSocket = new WebSocket(session);

		// Parse body for post/put requests
		if (Method.PUT.equals(sessionMethod) || Method.POST.equals(sessionMethod)) {
			try {
				session.parseBody(postFiles);
			} catch (IOException e) {
				return new Response(Response.Status.INTERNAL_ERROR, Mime.TEXT_PLAIN.getName(), e.getMessage());
			} catch (ResponseException e) {
				return new Response(e.getStatus(), Mime.TEXT_PLAIN.getName(), e.getMessage());
			}
		}

		try {
			// Get mime's type
			Mime mime = Mime.findByExtension(getFileExtension(uri));

			// If we have mime type, then load file
			if (mime != null) {
				uri = uri.substring(1);
				try {
					return new Response(Response.Status.OK, mime.getName(), mime.getLoader().load(Config.PROJECT_PATH + uri));
				} catch (Exception ignored) {
					return new Response(Response.Status.NOT_FOUND, mime.getName(), "");
				}
			}

			// Split link into paths
			String[] pathLink = uri.substring(1).split("/");

			String projectName;
			String actionName;

			// Try to get project path or throw AccessDenied
			if (pathLink.length > 0 && pathLink[0].length() > 0) {
				projectName = pathLink[0];
			} else {
				return new Response("<html><body><h1>Access Denied</h1></body></html>");
			}

			String totalPath = "";

			// Build path to action's controller
			for (int i = 1; i < pathLink.length - 1; i++) {
				totalPath += pathLink[i];
				if (i != pathLink.length - 2) {
					totalPath += ".";
				}
			}

			// Last path should be action
			actionName = pathLink[pathLink.length - 1];

			// If we have unresolved symbols then set it to default
			if (pathLink.length == 1) {
				totalPath = "index";
				actionName = "view";
			}

			// Find or create environment by project's name
			Environment environment = EnvironmentManager.getInstance()
					.get(projectName);

			// Read session from browser's cookie
			String sessionID = session.getCookies().read("JAW_SESSION_ID");

			if (sessionID == null) {

				// Generate random session id
				sessionID = PasswordEncryptor.generateSessionID();

				// Put session identifier into browser's cookie
				session.getCookies().set(new Cookie(
					"JAW_SESSION_ID", sessionID, 30, "/"
				));
			}

			// Set environment's session ID
			environment.setSessionID(sessionID);

			Session user = environment.getSessionManager().get();

			if (user != null) {
				environment.getMustacheDefiner().put("USER_LOGIN", user.getLogin());
				environment.getMustacheDefiner().put("USER_ID", Integer.toString(user.getID()));
			}

			Controller controller;

			environment.getControllerManager().get("Index").actionFilter(
					totalPath, actionName
			);

			// Set router session and redirect to our controller's action
			synchronized (this) {
				environment.getRouter().setSession(session);
				environment.getRouter().redirect(totalPath, actionName);
			}

			// Get current controller after invocation
			controller = environment.getRouter().getController();

			if (controller == null) {

				// If we haven't loaded controller, then load index controller
				controller = environment.getControllerManager().get("Index");

				if (controller == null) {
					return new Response(Response.Status.NOT_FOUND, Mime.TEXT_HTML.getName(),
						"<html><body><h1>404</h1></body></html>"
					);
				}

				// Invoke action 404
				controller.action404();

			} else {

				// Load controller's view
				view = environment.getRouter().getController().getView();
			}

			Response response;

			if (view != null && view.getHtmlContent() != null) {
				String content = view.getHtmlContent();
				if (environment.getMustacheDefiner() != null) {
					content = environment.getMustacheDefiner().execute(view.getHtmlContent());
				}
				response = new Response(Response.Status.OK, Mime.TEXT_HTML.getName(), content);
			} else {
				if (controller.getAjaxResponse() != null) {
					response = new Response(controller.getAjaxResponse());
				} else {
					controller = environment.getControllerManager().get("Index");
					controller.action404();
					response = new Response(Response.Status.NOT_FOUND, Mime.TEXT_PLAIN.getName(),
						controller.getAjaxResponse()
					);
				}
			}

			String sitePath = projectName + "/" + totalPath + "/" + actionName;

			if (controller.getAjaxResponse() == null) {
				response.addHeader("Location", "0; url=/" + sitePath);
			}

			return webSocket.setHeaders(response);

		} catch (Exception e) {

			StringWriter stringWriter = new StringWriter();

			PrintWriter writer = new PrintWriter(
				stringWriter
			);

			Logger.getLogger().log(
				e.getMessage()
			);

			e.printStackTrace(writer);

			JSONObject errorResponse = new JSONObject();

			errorResponse.put("status", false);
			errorResponse.put("message", e.getMessage() == null ? "null" : e.getMessage());
			errorResponse.put("trace", "<pre>" + stringWriter.toString() + "</pre>");

			if (e instanceof SQLException) {
				return new Response(Response.Status.OK, Mime.TEXT_HTML.getName(),
					errorResponse.toString()
				);
			} else {
				return new Response(Response.Status.OK, Mime.TEXT_HTML.getName(),
					"<body style=\"width:100%; height:100%;background-color:lightgray;\"><pre style=\"color: #7b1010;\">" +
							"<b>" + e.getMessage() + "</b><br>" + stringWriter.toString() +
					"</pre></body>"
				);
			}
		}
	}

	/**
	 * @throws Exception
	 */
	public static void run() throws Exception {

		WebServer server = getWebServer();

		Machine terminal = new Machine(
			EnvironmentManager.getInstance().get("jaw")
		);

		try {
			server.start();
		} catch (IOException ioe) {
			logger.log("couldn't start server: " + ioe.getMessage());
			System.exit(-1);
		}

		logger.log("server started");
		System.out.println("Server Terminal:");

		terminal.register("terminal",
			new ServerTerminal(terminal)
		);
		terminal.start();

		server.stop();
		logger.log("server stopped");
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

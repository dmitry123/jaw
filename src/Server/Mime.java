package Server;

/**
 * Created by Savonin on 2014-11-09
 */
public enum Mime {

	IMAGE_GIF       (Type.IMAGE, "image/gif", ".gif"),
	IMAGE_JPEG      (Type.IMAGE, "image/jpeg", ".jpeg", ".jpg"),
	IMAGE_PJPEG     (Type.IMAGE, "image/pjpeg"),
	IMAGE_PNG       (Type.IMAGE, "image/png", ".png"),
	IMAGE_SVG_XML   (Type.IMAGE, "image/svg+xml"),
	IMAGE_TIFF      (Type.IMAGE, "image/tiff", ".tiff"),
	IMAGE_ICO       (Type.IMAGE, "image/vnd.microsoft.icon", ".ico"),
	IMAGE_WBMP      (Type.IMAGE, "image/vnd.wap.wbmp"),

	TEXT_CMD        (Type.TEXT,  "text/cmd"),
	TEXT_CSS        (Type.TEXT,  "text/css", ".css"),
	TEXT_CSV        (Type.TEXT,  "text/csv", ".csv"),
	TEXT_HTML       (Type.TEXT,  "text/html", ".html"),
	TEXT_JAVASCRIPT (Type.TEXT,  "text/javascript", ".js"),
	TEXT_PLAIN      (Type.TEXT,  "text/plain"),
	TEXT_PHP        (Type.TEXT,  "text/php", ".php"),
	TEXT_XML        (Type.TEXT,  "text/xml", ".xml"),

	APPLICATION_OCTET_STREAM (Type.APPLICATION, "application/octet-stream",
		".map", ".ttf", ".eot", ".woff"
	),
	APPLICATION_JSON         (Type.APPLICATION, "application/json ", ".json");

	public static enum Type {
		UNKNOWN,
		IMAGE,
		TEXT,
		APPLICATION
	}

	/**
	 * @param type - Mime's type enumeration
	 * @param mime - Mime name's name
	 * @param extensions - Extensions arguments
	 */
	private Mime(Type type, String mime, String... extensions) {
		this.type = type; this.name = mime; this.extensions = extensions;
	}

	/**
	 * Find name enumeration by it's extension
	 *
	 * @param extension - Extension format
	 * @return - Found name or null
	 */
	static public Mime findByExtension(String extension) {
		for (Mime m : Mime.values()) {
			for (String s : m.getExtensions()) {
				if (s.equals(extension)) {
					return m;
				}
			}
		}
		return null;
	}

	public boolean isImage() {
		return type == Type.IMAGE;
	}

	public boolean isText() {
		return type == Type.TEXT;
	}

	public boolean isApplication() {
		return type == Type.APPLICATION;
	}

	public boolean isUnknown() {
		return type == Type.UNKNOWN;
	}

	/**
	 * @return - Type's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return - Type's extensions
	 */
	public String[] getExtensions() {
		return extensions;
	}

	/**
	 * @return - Mime's type enumeration
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return - Mime's loader
	 */
	public MimeLoader getLoader() {
		if (loader == null) {
			loader = new MimeLoader(this);
		}
		return loader;
	}

	private String name = null;
	private String[] extensions = null;
	private Type type = null;
	private MimeLoader loader = null;
}

package Core;

/**
 * Created by Savonin on 2014-11-09
 */
public class ExternalError extends Exception {

	/**
	 * Enumeration with error codes
	 */
	public enum Code {

		Unknown                (0, "Unknown error"),
		InvalidLoginOrPassword (1, "Invalid login or password"),
		InvalidLogin           (2, "Invalid login"),
		InvalidPassword        (3, "Invalid password"),
		InvalidCompanyBundle   (4, "Invalid company bundle"),
		UserAlreadyRegistered  (5, "User with that login already registered"),
		UnknownMimeType        (6, "Unknown mime type"),
		InvalidPrimaryKey      (7, "Unable to find row in table");

		/**
		 * @param code - Error code
		 * @param message - Error message
		 */
		private Code(int code, String message) {
			this.code = code; this.message = message;
		}

		/**
		 * @return - Get error's code
		 */
		public int getCode() {
			return code;
		}

		/**
		 * @return - Get error's message
		 */
		public String getMessage() {
			return message;
		}

		private String message;
		private int code;
	}

	/**
	 * @param code - Error code
	 */
	public ExternalError(Code code) {
		this.code = code;
	}

	/**
	 * Returns the detail message string of this throwable.
	 * @return - the detail message string of
	 * 		this {@code Throwable} instance (which
	 * 		may be {@code null}).
	 */
	@Override
	public String getMessage() {
		return code.getMessage();
	}

	/**
	 * @return - Get error code from
	 * 		code object
	 */
	public int getCode() {
		return code.getCode();
	}

	private Code code;
}

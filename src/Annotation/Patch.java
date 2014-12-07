package Annotation;

/**
 * Created by Savonin on 2014-11-22
 */
public @interface Patch {

	/**
	 * @return - Date when patch has been applied
	 */
	Date date();

	/**
	 * @return - Whom applied that patch
	 */
	Author author();
}

package Annotation;

/**
 * Created by Savonin on 2014-11-22
 */
public @interface Action {

	/**
	 * @return - Action's request method
	 */
	Method method() default @Method(
		type = "INDEX"
	);

	/**
	 * @return - Information getAbout author
	 */
	Author author() default @Author(
		surname = "Savonin",
		name    = "Dmitry"
	);

	/**
	 *
	 * @return - Modification date
	 */
	Date date() default @Date(
		year = 2014,
		month = 11,
		day = 22
	);
}
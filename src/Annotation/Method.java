package Annotation;

/**
 * Created by Savonin on 2014-11-22
 */
public @interface Method {

	/**
	 * @return - Method's type name {GET/POST/REQUEST/...}
	 */
	String type() default "INDEX";
}

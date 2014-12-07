package Annotation;

/**
 * Created by Savonin on 2014-11-22
 */
public @interface Version {

	/**
	 * @return - Major version
	 */
	int major() default 0;

	/**
	 * @return - Minor version
	 */
	int minor() default 0;
}

package org.springframework.profile;

import org.springframework.core.env.Environment;

/**
 * Created by elvis on 07/05/17.
 */
public class SpringEnvSingleton {

	private static Environment instance;

	/**
	 * Load spring properties based on active profiles
	 * @param args like spring boot params
	 * @return true if create a new environment, false when not
	 */
	public static boolean prepareEnv(String[] args){
		if (instance == null) {
			instance = new SpringEnv(args).getEnv();
			return true;
		}
		return false;
	}

	public static Environment getEnv() {
		return instance;
	}
}

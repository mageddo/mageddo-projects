package com.mageddo.featureswitch;

import com.mageddo.featureswitch.spring.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.ServiceLoader;

public class FeatureContext {
	public static FeatureManager getFeatureManager(){
		try {
			Class.forName(ApplicationContext.class.getName());
			final ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
			if(ctx != null){
				final FeatureManager featureManager = ctx.getBean(FeatureManager.class);
				if(featureManager != null){
					return featureManager;
				}
			}
		} catch (ClassNotFoundException ignored) {}
		final Iterator<FeatureManager> it = ServiceLoader.load(FeatureManager.class).iterator();
		if(it.hasNext()){
			return it.next();
		}
		throw new UnsupportedOperationException("No repository impl found");
	}
}

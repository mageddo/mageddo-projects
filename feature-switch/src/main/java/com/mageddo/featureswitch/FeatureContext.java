package com.mageddo.featureswitch;

import com.mageddo.featureswitch.repository.InMemoryFeatureRepository;
import com.mageddo.featureswitch.spring.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.ServiceLoader;

public class FeatureContext {
	public static FeatureManager getFeatureManager(){
		if(existsOnClasspath("org.springframework.context.ApplicationContext")){
			final ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
			if(ctx != null){
				final FeatureManager featureManager = ctx.getBean(FeatureManager.class);
				if(featureManager != null){
					return featureManager;
				}
			}
		}
		final Iterator<FeatureManager> it = ServiceLoader.load(FeatureManager.class).iterator();
		if(it.hasNext()){
			return it.next();
		}
		return new DefaultFeatureManager()
		.featureRepository(new InMemoryFeatureRepository())
		.featureMetadataProvider(new EnumFeatureMetadataProvider())
		;
	}

	public static boolean existsOnClasspath(String name){
		try {
			Class.forName(name);
			return true;
		} catch (ClassNotFoundException ignored) {
			return false;
		}
	}
}

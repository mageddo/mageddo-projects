#### [Gradle 5+](https://docs.gradle.org/5.0/userguide/java_plugin.html#sec:java_compile_avoidance)

On gradle 5+ use `annotationProcessor` 


	provided('com.mageddo:raw-string-literals:1.0.0')
	annotationProcessor('com.mageddo:raw-string-literals:1.0.0')
	
On gradle 4-

	provided('com.mageddo:raw-string-literals:1.0.0')

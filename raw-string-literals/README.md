String multiline support for Java without concatenation overhead 

* [Play the demo project](https://github.com/mageddo/mageddo-projects/tree/master/raw-string-literals-demo)
* Java 7+ support
* Tested on gradle and intellij

Getting Started

Main.java
```java
@Rsl // indicates RSL must scan this class for inject multiline strings
public class Stuff {
	public void doSomething(){
		/*
		Lorem Ipsum is simply dummy text of 
		the printing and typesetting industry
		 */
		@RawString // indicates this is a multiline string and the compiler must inject it
		final String text = RawStrings.lateInit(); // this method throws an exception if called  then it ensures RSL is working
		System.out.println(text);
	}
}
```

build.gradle

```groovy
apply plugin 'java'

dependencies {

	compileOnly('com.mageddo:rsl:1.0.0')
	annotationProcessor('com.mageddo:rsl-compiler:1.0.1')
	
	testCompileOnly('com.mageddo:rsl:1.0.0')
	testAnnotationProcessor('com.mageddo:rsl-compiler:1.0.1')
}

```

Licensed under Apache License Version 2.0

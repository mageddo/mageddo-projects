package com.mageddo.rawstringliterals;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.util.Set;

@SupportedAnnotationTypes(References.RAW_STRING_REF)
public final class JavacMultilineProcessor extends AbstractProcessor {

	private TreeMaker maker;
	private Trees trees;

	@Override
	public void init(final ProcessingEnvironment procEnv) {
		super.init(procEnv);
		JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) procEnv;
		this.maker = TreeMaker.instance(javacProcessingEnv.getContext());
		trees = Trees.instance(processingEnv);
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		for (final Element element : roundEnv.getElementsAnnotatedWith(getAnnotation())) {
			if (element instanceof ClassSymbol) {
				final ClassSymbol classSymbol = (ClassSymbol) element;
				if (classSymbol.sourcefile.getKind() == JavaFileObject.Kind.SOURCE) {
					final MyTreePathScanner scanner = new MyTreePathScanner(maker, trees, classSymbol);
					scanner.scan();
				}
			}
		}
		return true;

	}

	private Class<RawString> getAnnotation() {
		return RawString.class;
	}


}

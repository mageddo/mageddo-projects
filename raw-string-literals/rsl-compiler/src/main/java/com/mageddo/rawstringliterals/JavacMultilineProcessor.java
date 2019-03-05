package com.mageddo.rawstringliterals;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.util.Set;

public final class JavacMultilineProcessor extends AbstractProcessor {

	private ClassScanner classScanner;
	private TreeMaker maker;
	private Trees trees;

	JavacMultilineProcessor(ClassScanner classScanner) {
		this.classScanner = classScanner;
	}

	@Override
	public void init(ProcessingEnvironment procEnv) {
		super.init(procEnv);
	 final JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) procEnv;
	 this.maker = TreeMaker.instance(javacProcessingEnv.getContext());
	 this.trees = Trees.instance(processingEnv);

	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		try {
			for (final Element element : roundEnv.getElementsAnnotatedWith(References.RSL_CLASS)) {
				if (element instanceof ClassSymbol) {
					final ClassSymbol classSymbol = (ClassSymbol) element;
					if (classSymbol.sourcefile.getKind() == JavaFileObject.Kind.SOURCE) {
						new MultilineTreePathScanner(maker, trees, classSymbol, classScanner).scan();
//						try (
//							final Reader in = classSymbol.sourcefile.openReader(true);
//							final Writer out = new OutputStreamWriter(classSymbol.classfile.openOutputStream())
//						) {
//							classScanner.processMultilineVars(in, out);
//						}
					}
				}
			}
		} catch (Throwable e){
			e.printStackTrace();
			throw e;
		}
		return true;
	}

}

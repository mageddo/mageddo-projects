package com.mageddo.rawstringliterals;

import com.mageddo.rawstringliterals.javac.ClassScannerJava;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

public final class References {

	public static final String RAW_STRING_REF = "com.mageddo.rawstringliterals.Rsl";
	public static final Class<? extends Annotation> MULTILINE_ANNOTATION = RawString.class;
	public static final Class<? extends Annotation> RSL_CLASS = Rsl.class;

	private References() {
	}

	@SupportedAnnotationTypes(RAW_STRING_REF)
	public static final class MultilineProcessor extends AbstractProcessor {

		private Processor delegate;

		@Override
		public void init(final ProcessingEnvironment procEnv) {
			super.init(procEnv);
			delegate = new JavacMultilineProcessor(new ClassScannerJava());
			delegate.init(procEnv);
		}

		@Override
		public SourceVersion getSupportedSourceVersion() {
			return SourceVersion.latest();
		}

		@Override
		public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
			if (this.delegate == null) {
				return true;
			}
			return delegate.process(annotations, roundEnv);
		}
	}
}

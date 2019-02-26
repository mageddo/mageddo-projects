package com.mageddo.rawstringliterals;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes(References.RAW_STRING_REF)
public final class MultilineProcessor extends AbstractProcessor {

	private Processor delegate;

	@Override
	public void init(final ProcessingEnvironment procEnv) {
		super.init(procEnv);
		delegate = new JavacMultilineProcessor();
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

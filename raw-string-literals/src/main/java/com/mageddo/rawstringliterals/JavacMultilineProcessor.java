package com.mageddo.rawstringliterals;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes(References.RAW_STRING_REF)
public final class JavacMultilineProcessor extends AbstractProcessor {

	private JavacElements elementUtils;
	private TreeMaker maker;

	@Override
	public void init(final ProcessingEnvironment procEnv) {
		super.init(procEnv);
		JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) procEnv;
		this.elementUtils = javacProcessingEnv.getElementUtils();
		this.maker = TreeMaker.instance(javacProcessingEnv.getContext());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
               return SourceVersion.latest();
        }

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		Set<? extends Element> fields = roundEnv.getElementsAnnotatedWith(RawString.class);
		for (Element field : fields) {
			String docComment = elementUtils.getDocComment(field);
			if (null != docComment) {
				JCVariableDecl fieldNode = (JCVariableDecl) elementUtils.getTree(field);
				fieldNode.init = maker.Literal(StringProcessor.toString(docComment,field.getAnnotation(RawString.class)));
			}
		}
		return true;
	}
}

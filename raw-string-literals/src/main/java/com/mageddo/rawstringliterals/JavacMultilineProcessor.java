package com.mageddo.rawstringliterals;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.model.JavacElements;
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

	private JavacElements elementUtils;
	private TreeMaker maker;
	private Trees trees;

	@Override
	public void init(final ProcessingEnvironment procEnv) {
		super.init(procEnv);
		JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) procEnv;
		this.elementUtils = javacProcessingEnv.getElementUtils();
		this.maker = TreeMaker.instance(javacProcessingEnv.getContext());
		trees = Trees.instance(processingEnv);
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

//		if (!roundEnv.getElementsAnnotatedWith(getAnnotation()).isEmpty()) {
////			final TreePath path = trees.getPath((Element) roundEnv.getRootElements().toArray()[0]);
//			for (Element rootElement : roundEnv.getRootElements()) {
//				rootElement.getKind()
//			}
//			TreePath path = trees.getPath(roundEnv.getRootElements().iterator().next().getEnclosedElements().get(2));
//			scanner.scan(path, path.getCompilationUnit());
////			scanner.scan(path, path.getCompilationUnit());
//		}


		for (final Element element : roundEnv.getElementsAnnotatedWith(getAnnotation())) {

			if (element instanceof ClassSymbol) {
				final ClassSymbol classSymbol = (ClassSymbol) element;
				if (classSymbol.sourcefile.getKind() == JavaFileObject.Kind.SOURCE) {

					final MyTreePathScanner scanner = new MyTreePathScanner(maker, trees, classSymbol);
					scanner.scan();

//					for (final Element enclosedElement : element.getEnclosedElements()) {
//						if(enclosedElement instanceof MethodSymbol){
//							final TreePath path = trees.getPath(enclosedElement);
//							scanner.scan(path, path.getCompilationUnit());
//	//					String value = findVarValue(element);
//	//					System.out.println(value);
//
//						}
//					}
				}
			}


		}

//		new JavaParser(new ParserConfiguration().)

//		JavaParserBuild.

//		roundEnv.getElementsAnnotatedWith(null).iterator().next().get


//
//		Set<? extends Element> fields = roundEnv.getElementsAnnotatedWith(getAnnotation());
//		for (Element field : fields) {
//			String docComment = elementUtils.getDocComment(field);
//			if (docComment != null) {
//				JCVariableDecl fieldNode = (JCVariableDecl) elementUtils.getTree(field);
//				fieldNode.init = maker.Literal(StringProcessor.toString(docComment, field.getAnnotation(getAnnotation())));
//			}
//		}

//		if (!roundEnv.getElementsAnnotatedWith(getAnnotation()).isEmpty()) {
			final TreePath path = trees.getPath((Element) roundEnv.getRootElements().toArray()[0]);
//			TreePath path = trees.getPath(roundEnv.getRootElements().iterator().next().getEnclosedElements().get(2));
//			scanner.scan(path, path.getCompilationUnit());
//			scanner.scan(path, path.getCompilationUnit());
//		}
		return true;

	}

	private Class<RawString> getAnnotation() {
		return RawString.class;
	}


}

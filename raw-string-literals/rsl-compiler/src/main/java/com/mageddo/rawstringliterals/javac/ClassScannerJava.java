package com.mageddo.rawstringliterals.javac;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.mageddo.rawstringliterals.ClassScanner;
import com.mageddo.rawstringliterals.exception.ElementNotFound;
import com.mageddo.rawstringliterals.exception.UncheckedIOException;
import com.mageddo.rawstringliterals.exception.UnchekedParseException;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mageddo.rawstringliterals.References.MULTILINE_ANNOTATION;

public final class ClassScannerJava implements ClassScanner {

	@Override
	public String findMultilineVar(ClassSymbol classSymbol, Method method, String varName, String annotationName) {
		try (final Reader r = classSymbol.sourcefile.openReader(true)) {
			return findMultilineVar(r, method, varName, annotationName);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public String findMultilineVar(Reader r, Method method, String varName, String annotationName) throws IOException {
		try {
			final CompilationUnit cu = JavaParser.parse(r, true);
			for (final TypeDeclaration type : cu.getTypes()) {
				final MethodDeclaration methodDecl = findMethod(type, method);
				if (methodDecl != null) {
					for (Statement statement : getStatements(methodDecl)) {
						final LocalVariable localVariable = findVar(statement, varName);
						if(localVariable != null && localVariable.containsAnnotation(annotationName)){
							return localVariable.getComment();
						}
					}
				}
			}
			throw new ElementNotFound("Variable not found " + method + "." + varName);
		} catch (ParseException e) {
			throw new UnchekedParseException(e);
		}
	}

	@Override
	public void processMultilineVars(Reader reader, Writer writer) {

		try {
			final CompilationUnit cu = JavaParser.parse(reader, true);
			for (final TypeDeclaration type : cu.getTypes()) {
				for (final MethodDeclaration method : getMethods(type)) {
					for (final Statement statement : getStatements(method)) {
						for (final LocalVariable multilineVar : getMultilineVars(statement)) {
							final StringLiteralExpr expr = new StringLiteralExpr();
							expr.setValue(multilineVar.getComment().replaceAll("\n", "\\\\n"));
							multilineVar.getVariable().setInit(expr);
						}
					}
				}
			}
			writer.write(cu.toString());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
//		try {
//			while (true) {
//
//				int read = reader.read();
//				if(read == -1){
//					break;
//				}
//				writer.write(read);
//
//			}
//		} catch (IOException e){
//			throw new UncheckedIOException(e);
//		}
	}

	/**
	 *
	 * @param method
	 * @return a flatten list of all method declared statements
	 */
	private static List<Statement> getStatements(MethodDeclaration method) {
		final BlockStmt methodDeclarationBody = method.getBody();
		final List<Statement> statements = new ArrayList<>();
		findStatementsRecursive(methodDeclarationBody, statements);
		return statements;
	}

	static void findStatementsRecursive(final Statement parent, final List<Statement> children){
		for (final Statement stm : getStatements(parent)) {
			children.add(stm);
			findStatementsRecursive(stm, children);
		}
	}

	static LocalVariable findVar(Statement stmt, String varName) {
		if(!(stmt instanceof ExpressionStmt)){
			return null;
		}
		final ExpressionStmt expressionStmt = (ExpressionStmt) stmt;
		for (Node node : stmt.getChildrenNodes()) {
			if (node instanceof VariableDeclarationExpr) {
				final VariableDeclarationExpr varDeclar = (VariableDeclarationExpr) node;
				for (final VariableDeclarator var : varDeclar.getVars()) {
					if(var.getId().getName().equals(varName)){
						return new LocalVariable()
							.setComment(expressionStmt.getComment().getContent())
							.setName(var.getId().getName())
							.setVariableDeclarationExpr(varDeclar)
							.setAnnotationExprs(varDeclar.getAnnotations())
						;
					}
				}
			}
		}
		return null;
	}

	static List<MethodDeclaration> getMethods(TypeDeclaration type) {
		final List<MethodDeclaration> methods = new ArrayList<>();
		for (final BodyDeclaration member : type.getMembers()) {
			if (member instanceof MethodDeclaration) {
				methods.add((MethodDeclaration) member);
			}
		}
		return methods;
	}

	List<LocalVariable> getMultilineVars(Statement stmt) {
		if(!(stmt instanceof ExpressionStmt)){
			return Collections.emptyList();
		}
		final ExpressionStmt expressionStmt = (ExpressionStmt) stmt;
		final List<LocalVariable> vars = new ArrayList<>();
		for (Node node : stmt.getChildrenNodes()) {
			if (node instanceof VariableDeclarationExpr) {
				final VariableDeclarationExpr varDeclar = (VariableDeclarationExpr) node;
				for (final VariableDeclarator var : varDeclar.getVars()) {
					final LocalVariable localVariable = new LocalVariable()
						.setComment(expressionStmt.getComment().getContent())
						.setName(var.getId().getName())
						.setVariable(var)
						.setVariableDeclarationExpr(varDeclar)
						.setAnnotationExprs(varDeclar.getAnnotations());
					if(localVariable.containsAnnotation(MULTILINE_ANNOTATION.getSimpleName())) {
						vars.add(localVariable);
					}
				}
			}
		}
		return vars;
	}

	static MethodDeclaration findMethod(TypeDeclaration type, Method method) {
		for (final BodyDeclaration member : type.getMembers()) {
			if (member instanceof MethodDeclaration) {
				final MethodDeclaration methodDeclaration = (MethodDeclaration) member;
				if (toMethod(methodDeclaration).equals(method)) {
					return methodDeclaration;
				}
			}
		}
		return null;
	}

	private static Method toMethod(MethodDeclaration methodDeclaration) {
		final StringBuilder parameters = new StringBuilder();
		for (Parameter parameter : methodDeclaration.getParameters()) {
			parameters
				.append(parameter.getType())
				.append(',')
			;
		}
		return new Method(String.format("%s(%s)", methodDeclaration.getName(), parameters));
	}

	private static List<Statement> getStatements(Statement stmt) {
		if (stmt instanceof BlockStmt) {
			return ((BlockStmt) stmt).getStmts();
		}
		if (stmt instanceof TryStmt) {
			return ((TryStmt) stmt).getTryBlock().getStmts();
		}
		return Collections.emptyList();
	}

}

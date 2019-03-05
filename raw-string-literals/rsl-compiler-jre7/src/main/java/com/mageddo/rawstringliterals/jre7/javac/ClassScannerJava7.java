package com.mageddo.rawstringliterals.jre7.javac;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.mageddo.rawstringliterals.ClassScanner;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ClassScannerJava7 implements ClassScanner {

	@Override
	public String findMultilineVar(ClassSymbol classSymbol, String methodName, String varName, String annotationName) {
		try {
			final Reader r = classSymbol.sourcefile.openReader(true);
			return findMultilineVar(r, methodName, varName, annotationName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String findMultilineVar(Reader r, String methodName, String varName, String annotationName) throws IOException {
		try {
			final CompilationUnit cu = JavaParser.parse(r, true);
			for (final TypeDeclaration type : cu.getTypes()) {
				final MethodDeclaration method = findMethod(type, methodName);
				if (method != null) {
					for (Statement statement : getStatements(method)) {
							final LocalVariable localVariable = findVar(statement, varName);
							if(localVariable != null && localVariable.containsAnnotation(annotationName)){
								return localVariable.getComment();
							}
					}
				}
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} finally {
			r.close();
		}
		return null;
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

	static MethodDeclaration findMethod(TypeDeclaration type, String methodName) {
		for (final BodyDeclaration member : type.getMembers()) {
			if (member instanceof MethodDeclaration) {
				final MethodDeclaration methodDeclaration = (MethodDeclaration) member;
				if (methodDeclaration.getName().equals(methodName)) {
					return methodDeclaration;
				}
			}
		}
		return null;
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

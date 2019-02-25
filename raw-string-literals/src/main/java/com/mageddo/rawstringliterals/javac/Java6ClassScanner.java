package com.mageddo.rawstringliterals.javac;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public final class Java6ClassScanner {

	public static String findVarValue(ClassSymbol classSymbol, String methodName, String varName, String annotationName) {
		try {
			final Reader r = classSymbol.sourcefile.openReader(true);
			return findVarValue(r, methodName, varName, annotationName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String findVarValue(Reader r, String methodName, String varName, String annotationName) throws IOException {
		try {
			final CompilationUnit cu = JavaParser.parse(r, true);
			for (final TypeDeclaration type : cu.getTypes()) {
				for (final BodyDeclaration member : type.getMembers()) {
					if (member instanceof MethodDeclaration) {
						final MethodDeclaration methodDeclaration = (MethodDeclaration) member;
						if (methodDeclaration.getName().equals(methodName)) {
							final BlockStmt methodDeclarationBody = methodDeclaration.getBody();
							for (final Statement stm : methodDeclarationBody.getStmts()) {
								if (stm instanceof ExpressionStmt) {
									final ExpressionStmt expressionStmt = (ExpressionStmt) stm;
									for (final Node stmNodes : expressionStmt.getChildrenNodes()) {
										if (stmNodes instanceof VariableDeclarationExpr) {
											final VariableDeclarationExpr varDeclar = (VariableDeclarationExpr) stmNodes;
											if (containsVar(varDeclar.getVars(), varName)) {
												for (final AnnotationExpr annotation : varDeclar.getAnnotations()) {
													if (annotation.getName().getName().equals(annotationName)) {
														return expressionStmt.getComment().getContent();
													}
												}
											}
										}
									}
								}
							}
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

	private static boolean containsVar(List<VariableDeclarator> vars, String varName) {
		for (VariableDeclarator var : vars) {
			if (var.getId().getName().equals(varName)) {
				return true;
			}
		}
		return false;
	}
}

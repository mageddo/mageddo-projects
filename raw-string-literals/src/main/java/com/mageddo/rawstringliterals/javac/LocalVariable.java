package com.mageddo.rawstringliterals.javac;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import java.util.List;

class LocalVariable {

	private String name;
	private VariableDeclarationExpr variableDeclarationExpr;
	private String comment;
	private List<AnnotationExpr> annotationExprs;

	public boolean containsAnnotation(String name){
		for (AnnotationExpr annotationExpr : annotationExprs) {
			if(annotationExpr.getName().getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public LocalVariable setName(String name) {
		this.name = name;
		return this;
	}

	public VariableDeclarationExpr getVariableDeclarationExpr() {
		return variableDeclarationExpr;
	}

	public LocalVariable setVariableDeclarationExpr(VariableDeclarationExpr variableDeclarationExpr) {
		this.variableDeclarationExpr = variableDeclarationExpr;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public LocalVariable setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public List<AnnotationExpr> getAnnotationExprs() {
		return annotationExprs;
	}

	public LocalVariable setAnnotationExprs(List<AnnotationExpr> annotationExprs) {
		this.annotationExprs = annotationExprs;
		return this;
	}
}

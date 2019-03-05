package com.mageddo.rawstringliterals.javac;

import com.sun.tools.javac.tree.JCTree;

import java.util.Objects;

public class Method {

	private String signature;

	public Method(String signature) {
		this.signature = signature;
	}

	public Method(JCTree.JCMethodDecl methodDecl) {
		final StringBuilder sb = new StringBuilder(methodDecl.getName())
			.append('(')
			;

		for (final JCTree.JCVariableDecl parameter : methodDecl.getParameters()) {
			sb
			.append(parameter.vartype)
			.append(',')
			;
		}
		sb.append(')');
		this.signature = sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Method method = (Method) o;
		return signature.equals(method.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(signature);
	}

	@Override
	public String toString() {
		return signature;
	}
}

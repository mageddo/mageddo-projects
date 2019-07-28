package com.mageddo.common.graalvm;

import java.util.ArrayList;
import java.util.List;

public class SubstrateVMProcessBuilder {

	private List<Class> classes;
	private boolean methods;
	private boolean constructors;
	private boolean fields;

	SubstrateVMProcessBuilder() {
		this.classes = new ArrayList<>();
	}

	/**
	 * Add a clazz to be registered on the substratevm for reflection
	 */
	public SubstrateVMProcessBuilder clazz(Class classes) {
		this.classes.add(classes);
		return this;
	}

	/**
	 * Register methods for later reflection access
	 */
	public SubstrateVMProcessBuilder methods() {
		this.methods = true;
		return this;
	}

	/**
	 * Register constructors for later reflection access
	 */
	public SubstrateVMProcessBuilder constructors() {
		this.constructors = true;
		return this;
	}

	/**
	 * Register fields for later reflection access
	 */
	public SubstrateVMProcessBuilder fields() {
		this.fields = true;
		return this;
	}

	public void build(){
		for (Class clazz : classes) {
			SubstrateVM.process(clazz, constructors, methods, fields);
		}
	}
}

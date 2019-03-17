package com.mageddo.common.os;

import java.nio.file.Path;

public class WinProcess implements Process {

	private final int pid;

	public WinProcess(int pid) {
		this.pid = pid;
	}

	@Override
	public Path getPath() {
		return null;
	}
}

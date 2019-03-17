package com.mageddo.common.os;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UnixProcess implements Process {

	private final int pid;

	public UnixProcess(int pid) {
		this.pid = pid;
	}

	@Override
	public Path getPath() {
		try {
			final String cmd = String.format("ps -n -o args= -p %d", pid);
			java.lang.Process p = Runtime.getRuntime().exec(cmd);
			final int exitCode = p.waitFor();
			Validate.isTrue(exitCode == 0, "Got an error when getting process path " + exitCode);
			final String cmdLine = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
			return Paths.get(CommandLine.parse(cmdLine).getExecutable());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}

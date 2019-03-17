package com.mageddo.common.os;

import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.*;

public class ProcessUtilsTest {

	@Test
	public void getCurrentProcessPath(){
		final int pid = ProcessUtils.getPid();
		final Path processPath = ProcessUtils.getProcessPath(pid);

		System.out.println(">>>>>>>>>>" + processPath);

	}

}

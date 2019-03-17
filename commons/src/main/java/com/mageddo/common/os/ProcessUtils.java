package com.mageddo.common.os;

import org.apache.commons.exec.OS;

import java.nio.file.Path;

import static java.lang.management.ManagementFactory.*;

public final class ProcessUtils {

	private ProcessUtils() {
	}

	public static Path getProcessPath(int pid){
		if(OS.isFamilyWindows()){
			return new WinProcess(pid).getPath();
		} else if(OS.isFamilyUnix()){
			return new UnixProcess(pid).getPath();
		}
		throw new UnsupportedOperationException();
	}

	public static int getPid(){
		return Integer.valueOf(getRuntimeMXBean().getName().split("@")[0]);
	}
}

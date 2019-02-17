package com.mageddo.common.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CompressUtils {

	private static final int BUFFER_SIZE = 2048;

	/**
	 * No need to pass a buffered stream
	 * @param zipStream
	 * @param outdir
	 */
	public static void extractZip(InputStream zipStream, Path outdir) {
		try (final ZipInputStream is = new ZipInputStream(new BufferedInputStream(zipStream))) {
			ZipEntry entry;
			while ((entry = is.getNextEntry()) != null) {
				final String name = entry.getName();
				if (entry.isDirectory()) {
					mkDirs(outdir, name);
				} else {
					final String dir = directoryPart(name);
					if (dir != null) {
						mkDirs(outdir, dir);
					}
					extractFile(is, outdir, name);
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Extract g zip.
	 *
	 * @param tgzFile the tgz file
	 * @param outDir  the out dir
	 */
	public static void extractGZip(File tgzFile, Path outDir) {
		try {
			TarArchiveInputStream tarIs = new TarArchiveInputStream(new GzipCompressorInputStream(
				new BufferedInputStream(new FileInputStream(tgzFile))));
			TarArchiveEntry entry;
			while ((entry = (TarArchiveEntry) tarIs.getNextEntry()) != null) {
				String name = entry.getName();
				if (entry.isDirectory()) {
					mkDirs(outDir, name);
				} else {
					String dir = directoryPart(name);
					if (dir != null) {
						mkDirs(outDir, dir);
					}
					extractFile(tarIs, outDir, name);
				}
			}
			tarIs.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Extract file.
	 *
	 * @param inputStream the input stream
	 * @param outDir      the out dir
	 * @param name        the name
	 * @throws IOException the io exception
	 */
	private static void extractFile(InputStream inputStream, Path outDir, String name) throws IOException {
		final byte[] buffer = new byte[BUFFER_SIZE];
		int count;
		try (final BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(outDir.resolve(name)), BUFFER_SIZE)) {
			while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
				out.write(buffer, 0, count);
			}
		}
	}

	/**
	 * Mk dirs.
	 *  @param outdir the outdir
	 * @param path   the path
	 */
	private static void mkDirs(Path outdir, String path) {
		final Path d = outdir.resolve(path);
		if (!Files.exists(d)) {
			try {
				Files.createDirectories(d);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * Directory part string.
	 *
	 * @param name the name
	 * @return the string
	 */
	private static String directoryPart(String name) {
		int s = name.lastIndexOf(File.separatorChar);
		return s == -1 ? null : name.substring(0, s);
	}

}

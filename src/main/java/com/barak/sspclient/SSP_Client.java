package com.barak.sspclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.barak.sspclient.controller.ClientController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class SSP_Client {
	private static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final Random rnd = new Random();

	@Getter
	@Setter
	@AllArgsConstructor
	private static class TestFileInfo {
		private String fileName;
		private String dirPath;
		private String filePath;
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Provide path for a test file");
			System.out.println("Provide path to for file generation");
			System.out.println("Provide path for downloads");
			System.exit(0);
		}

		String testFilePath = args[0];
		String filesDir = args[1];
		String downloadPath = args[2];
		ClientController client = new ClientController("http://localhost:8080");

		File file = new File(testFilePath);
		byte[] fileContent;
		fileContent = Files.readAllBytes(file.toPath());

		/* Create files on mountpoint */
		String fileName;
		String filePath;
		String dirPath = File.separator;
		LinkedList<TestFileInfo> testFileInfoList = new LinkedList<TestFileInfo>();
		for (int i = 0; i < 10; ++i) {
			fileName = randomString(10);
			filePath = filesDir + fileName;

			file = new File(filePath);
			Path fullPath = Paths.get(filePath);
			Files.write(fullPath, fileContent, StandardOpenOption.CREATE_NEW);

			client.create(filePath, dirPath);
			testFileInfoList.add(new TestFileInfo(fileName, dirPath, filePath));
		}

		/* Download all uploaded files for verification. Delete downloaded files. */
		for (TestFileInfo testFileInfo : testFileInfoList) {
			if (client.download(testFileInfo.getDirPath() + testFileInfo.getFileName(),
					downloadPath + testFileInfo.getFileName()) != 0) {
				System.out.println("Failed downloading " + testFileInfo.getFileName());
			}
			file = new File(downloadPath + testFileInfo.getFileName());
			file.delete();
		}

		/* List all created file and verify each exist */
		LinkedList<String> files = client.ls(dirPath);
		Iterator<String> filesIt = files.iterator();

		/* Delete files on mountpoint */
		for (TestFileInfo testFileInfo : testFileInfoList) {
			if (filesIt.hasNext()) {
				String curFileName = filesIt.next();
				if (curFileName.equals(testFileInfo.getFileName())) {
					client.rm(testFileInfo.getDirPath(), testFileInfo.getFileName());
				} else {
					System.out.println("Error no match found for " + testFileInfo.getFileName());
				}
			} else {
				System.out.println("Error no match found for " + testFileInfo.getFileName());
			}
		}

		/* Create files on a dir under mountpoint */
		dirPath = File.separator + randomString(10) + File.separator;
		client.mkdir(dirPath);
		for (TestFileInfo testFileInfo : testFileInfoList) {
			testFileInfo.setDirPath(dirPath);
			client.create(testFileInfo.getFilePath(), dirPath);
		}

		/* Download all uploaded files for verification. Delete downloaded files. */
		for (TestFileInfo testFileInfo : testFileInfoList) {
			if (client.download(testFileInfo.getDirPath() + testFileInfo.getFileName(),
					downloadPath + testFileInfo.getFileName()) != 0) {
				System.out.println("Failed downloading " + testFileInfo.getFileName());
			}
			file = new File(downloadPath + testFileInfo.getFileName());
			file.delete();
		}

		/* List all created file and verify each exist */
		files = client.ls(dirPath);
		filesIt = files.iterator();

		/* Delete files from a dir under mountpoint */
		for (TestFileInfo testFileInfo : testFileInfoList) {
			if (filesIt.hasNext()) {
				String curFileName = filesIt.next();
				if (curFileName.equals(testFileInfo.getFileName())) {
					client.rm(testFileInfo.getDirPath(), testFileInfo.getFileName());
				} else {
					System.out.println("Error no match found for " + testFileInfo.getFileName());
				}
			} else {
				System.out.println("Error no match found for " + testFileInfo.getFileName());
			}
		}

		/* Delete dir under mountpoint */
		client.rm(dirPath, "");

		/* List all files (for debug) */
		client.listall();

		/* Delete test files */
		Iterator<TestFileInfo> it = testFileInfoList.iterator();
		while (it.hasNext()) {
			file = new File(it.next().getFilePath());
			file.delete();
		}
	}

	private static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}

		return sb.toString();
	}
}

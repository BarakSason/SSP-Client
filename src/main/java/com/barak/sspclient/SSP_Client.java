package com.barak.sspclient;

import com.barak.sspclient.controller.ClientController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedList;

public class SSP_Client {
	static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	@Getter
	@Setter
	@AllArgsConstructor
	private static class TestFileInfo {
		private String fileName;
		private String dirPath;
	}

	public static void main(String[] args) throws IOException {
		ClientController client = new ClientController("http://localhost:8080");

		File file = new File("/home/barak/aaa.txt");
		byte[] fileContent;
		try {
			fileContent = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			throw e;
		}

		/* Create files on mountpoint */
		String fileName;
		String filePath;
		String dirPath = File.separator;
		LinkedList<TestFileInfo> testFileInfoList = new LinkedList<TestFileInfo>();
		for (int i = 0; i < 10; ++i) {
			fileName = randomString(10);
			filePath = "/home/barak/files/" + fileName;

			file = new File(filePath);
			Path fullPath = Paths.get(filePath);
			Files.write(fullPath, fileContent, StandardOpenOption.CREATE_NEW);

			client.create(filePath, dirPath);
			testFileInfoList.add(new TestFileInfo(fileName, dirPath));
		}

		/* List all created file and verify each exist */
		// TODO: fix return value of ls
		client.ls(dirPath);

		/* Delete files on mountpoint */
		Iterator<TestFileInfo> it = testFileInfoList.iterator();
		while (it.hasNext()) {
			TestFileInfo testFileInfo = it.next();
			client.rm(testFileInfo.getDirPath(), testFileInfo.getFileName());
			it.remove();
		}

		/* Create files on a dir under mountpoint */
		dirPath = File.separator + randomString(10) + File.separator;
		client.mkdir(dirPath);
		for (int i = 0; i < 10; ++i) {
			fileName = randomString(10);
			filePath = "/home/barak/files/" + fileName;

			file = new File(filePath);
			Path fullPath = Paths.get(filePath);
			Files.write(fullPath, fileContent, StandardOpenOption.CREATE_NEW);

			client.create(filePath, dirPath);
			testFileInfoList.add(new TestFileInfo(fileName, dirPath));
		}

		/* List all created file and verify each exist */
		// TODO: fix return value of ls
		client.ls(dirPath);

		/* Delete files from a dir under mountpoint */
		it = testFileInfoList.iterator();
		while (it.hasNext()) {
			TestFileInfo testFileInfo = it.next();
			client.rm(testFileInfo.getDirPath(), testFileInfo.getFileName());
			it.remove();
		}

		/* Delete dir under mountpoint */
		client.rm(dirPath, "");

		client.listall();
	}

	private static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		return sb.toString();
	}
}

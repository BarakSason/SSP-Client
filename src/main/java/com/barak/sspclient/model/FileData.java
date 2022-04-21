package com.barak.sspclient.model;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileData {
	private String fileName;
	private String dirPath;
	private byte[] fileContent = null;

	public FileData(String filePath, String fileName, String dirPath) throws IOException {
		this.fileName = fileName;
		this.dirPath = dirPath;

		File file = new File(filePath);
		try {
			fileContent = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			throw e;
		}
	}
}

package com.barak.sspclient.controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.barak.sspclient.model.FileData;

public class ClientController {
	WebClient webClient = WebClient.create();

	public ClientController(String url) {
		webClient = WebClient.builder().baseUrl(url).build();
	}

	public int create(String filePath, String dirPath) {
		File file = new File(filePath);
		FileData fileData = null;
		try {
			fileData = new FileData(filePath, file.getName(), dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String response = null;
		try {
			response = webClient.post().uri("create").bodyValue(fileData).retrieve().bodyToMono(String.class).block();
		} catch (WebClientResponseException e) {
			System.out.println("Error " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			return -1;
		}

		System.out.println(response);
		return 0;
	}

	// TODO: for debugging only
	@SuppressWarnings("unchecked")
	public int listall() {
		LinkedList<String> files = webClient.get().uri("http://localhost:8080/listall").retrieve()
				.bodyToMono(LinkedList.class).block();

		StringBuilder sb = new StringBuilder();
		sb.append("Files:\n");

		for (Object file : files) {
			sb.append(file + "\n");
		}

		System.out.println(sb.toString());
		return 0;

	}

	public int mkdir(String dirPath) {
		String response = null;
		MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
		bodyValues.add("dirPath", dirPath);

		try {
			response = webClient.post()
					.uri(uriBuilder -> uriBuilder.path("/mkdir").queryParam("dirPath", dirPath).build()).retrieve()
					.bodyToMono(String.class).block();
		} catch (WebClientResponseException e) {
			System.out.println("Error " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			return -1;
		}

		System.out.println(response);
		return 0;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<String> ls(String dirPath) {
		LinkedList<String> files = null;

		try {
			files = webClient.get()
					.uri(uriBuilder -> uriBuilder.path("/ls").queryParam("dirPath", dirPath).build()).retrieve()
					.bodyToMono(LinkedList.class).block();

		} catch (WebClientResponseException e) {
			System.out.println("Error " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			return null;
		}

		// TODO: for debug only
		StringBuilder sb = new StringBuilder();
		sb.append("Files:\n");
		for (String file : files) {
			sb.append(dirPath + file + "\n");
		}
		System.out.println(sb.toString());

		return files;
	}

	public int rm(String dirPath, String fileName) {
		String response = null;

		try {
			response = webClient.delete().uri(uriBuilder -> uriBuilder.path("/rm").queryParam("dirPath", dirPath)
					.queryParam("fileName", fileName).build()).retrieve().bodyToMono(String.class).block();

		} catch (WebClientResponseException e) {
			System.out.println("Error " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			return -1;
		}

		System.out.println(response);
		return 0;
	}
}

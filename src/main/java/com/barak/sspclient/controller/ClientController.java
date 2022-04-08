package com.barak.sspclient.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ClientController {
	WebClient webClient = WebClient.create();

	public ClientController(String url) {
		webClient = WebClient.builder().baseUrl(url).build();
	}

	public int create(String filePath, String remotePath) {
		File file = new File(filePath);
		byte[] fileContent = null;
		try {
			fileContent = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			return -1;
		}

		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		String header = "form-data; name=file; filename=" + file.getName() + ";";
		builder.part("uploadfile", new ByteArrayResource(fileContent)).header("Content-Disposition", header);
		builder.part("dirPath", remotePath);

		String response = null;
		try {
			response = webClient.post().uri("http://localhost:8080/create").contentType(MediaType.MULTIPART_FORM_DATA)
					.body(BodyInserters.fromMultipartData(builder.build())).retrieve().bodyToMono(String.class).block();
		} catch (WebClientResponseException e) {
			System.out.println("Error " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			return -1;
		}

		System.out.println(response);
		return 0;
	}

	// TODO: for debugging only
	public int listall() {
		LinkedList<String> entries = webClient.get().uri("http://localhost:8080/listall").retrieve()
				.bodyToMono(LinkedList.class).block();

		StringBuilder sb = new StringBuilder();
		sb.append("Files:\n");
		for (String entry : entries) {
			sb.append(entry + "\n");
		}

		System.out.println(sb.toString());
		return 0;

	}

	public int mkdir(String dirPath) {
		String response = null;
		MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
		bodyValues.add("dirPath", dirPath);

		try {
			response = webClient.post().uri("http://localhost:8080/mkdir").contentType(MediaType.TEXT_PLAIN)
					.body(BodyInserters.fromFormData(bodyValues)).retrieve().bodyToMono(String.class).block();
		} catch (WebClientResponseException e) {
			System.out.println("Error " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			return -1;
		}

		System.out.println(response);
		return 0;
	}

	@SuppressWarnings("unchecked")
	public int ls(String dirPath) {
		LinkedList<String> entries = null;

		try {
			entries = webClient.get().uri(uriBuilder -> uriBuilder.path("/ls").queryParam("dirPath", dirPath).build())
					.retrieve().bodyToMono(LinkedList.class).block();

		} catch (WebClientResponseException e) {
			System.out.println("Error " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			return -1;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Files:\n");
		for (String entry : entries) {
			sb.append(entry + "\n");
		}

		System.out.println(sb.toString());
		return 0;
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

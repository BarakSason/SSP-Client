package com.barak.sspclient;

import com.barak.sspclient.controller.ClientController;

import java.io.IOException;

public class SSP_Client {
	public static void main(String[] args) throws IOException {
		ClientController client = new ClientController("http://localhost:8080");
		client.create("/home/barak/aaa.txt", "/");
		client.mkdir("/dir2");
		client.ls("/");
		client.rm("/", "aaa.txt");
		client.rm("/dir2", "");

		client.listall();
	}
}

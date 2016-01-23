package com.gethightower.api.examples;

import java.io.*;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;

public class UploadFile {

	public void uploadFile(String assetId, String fileDescription, File file) throws UnirestException {

		String url = String.format("https://%s/api/external/v1/assets/%s/attachments", Configuration.HOST, assetId);

		HttpResponse<JsonNode> response = Unirest.post(url)
			.header("accept", "application/json")
			.field("file_description", fileDescription)
			.field("file", file)
			.basicAuth(Configuration.API_KEY, Configuration.API_SECRET)
			.asJson();

		JsonNode body = response.getBody();

		if (response.getStatus() != 201) {
			System.err.println(String.format("Attachment create failed (HTTP %s)", response.getStatus()));
			System.err.println(body);
		} else {
			System.err.println(String.format("Attachment create succeeded (ID %s)", body.getObject().getInt("id")));
		}
	}


	public static void main(String[] args) throws UnirestException {

		UploadFile uploadFile = new UploadFile();

		if (args.length != 3) {
			System.err.println("usage: UploadFile <asset-id> <file-description> <file-path>");
			System.exit(1);
		}

		uploadFile.uploadFile(args[0], args[1], new File(args[2]));

	}

}

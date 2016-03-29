package com.gethightower;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONObject;

public class CreateAssetAttachment extends APIExample {

    public static void main(String[] args) throws UnirestException {
        new CreateAssetAttachment().run(args);
    }

    @Override
    public void doRun(String[] args) throws APIException, UnirestException {
        if (args.length != 3) {
            System.err.println("Three arguments required: assetId fileDescription filepath");
            System.exit(1);
        }

        String assetId = args[0];
        String fileDescription = args[1];
        String filepath = args[2];

        JSONObject attachment = createAssetAttachment(assetId, fileDescription, new File(filepath));

        System.out.println(String.format("Created asset attachment '%d'", attachment.getInt("id")));
    }

	private JSONObject createAssetAttachment(String assetId, String fileDescription, File file) throws UnirestException, APIException {
        String path = String.format("assets/%s/attachments", assetId);

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("file_description", fileDescription);
        fields.put("file", file);

        return executeHttpPost(path, fields);
	}
}

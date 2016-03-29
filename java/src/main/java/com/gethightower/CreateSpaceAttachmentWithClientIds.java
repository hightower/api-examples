package com.gethightower;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreateSpaceAttachmentWithClientIds {

    public static void main(String[] args) throws UnirestException {
        try {
            new CreateSpaceAttachmentWithClientIds().run(args);
        } catch (APIException ex) {
            System.err.println(String.format("The Hightower API returned an error (%d): %s", ex.getHttpStatusCode(), ex.getMessage()));
            System.exit(1);
        }
    }

    public void run(String[] args) throws APIException, UnirestException {
        if (args.length != 4) {
            System.err.println("Four arguments required: clientAssetId, clientSpaceId, fileDescription, filepath");
            System.exit(1);
        }

        String clientAssetId = args[0];
        String clientSpaceId = args[1];
        String fileDescription = args[2];
        File file = new File(args[3]);

        JSONObject asset = findAsset(clientAssetId);

        if (asset == null) {
            System.out.println(String.format("No asset '%s'", clientAssetId));
            return;
        }

        JSONObject space = findSpace(asset.getInt("id"), clientSpaceId);

        if (space == null) {
            System.out.println(String.format("No space '%s' in asset '%s'", clientSpaceId, clientAssetId));
            return;
        }

        JSONObject attachment = createSpaceAttachment(space.getInt("id"), file, fileDescription);

        String message = String.format("Created attachment '%d' on space '%s' in asset '%s'", attachment.getInt("id"), clientSpaceId, clientAssetId);
        System.out.println(message);
    }

    private JSONObject findAsset(String clientAssetId) throws UnirestException, APIException {
        int currentPage = 1;
        int totalPages;

        do {
            String path = "assets";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("page", currentPage);

            JSONObject root = executeHttpGet(path, parameters);
            JSONArray assets = root.getJSONArray("elements");

            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);

                if (!asset.isNull("client_asset_id") && asset.getString("client_asset_id").equals(clientAssetId)) {
                    return asset;
                }
            }

            currentPage += 1;
            totalPages = root.getJSONObject("pagination").getInt("total_pages");
        }
        while(currentPage <= totalPages);

        return null;
    }

    private JSONObject findSpace(int assetId, String clientSpaceId) throws UnirestException, APIException {
        int currentPage = 1;
        int totalPages;

        do {
            String path = String.format("assets/%d/spaces", assetId);

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("page", currentPage);

            JSONObject root = executeHttpGet(path, parameters);
            JSONArray spaces = root.getJSONArray("elements");

            for (int i = 0; i < spaces.length(); i++) {
                JSONObject space = spaces.getJSONObject(i);

                if (!space.isNull("client_space_id") && space.getString("client_space_id").equals(clientSpaceId)) {
                    return space;
                }
            }

            currentPage += 1;
            totalPages = root.getJSONObject("pagination").getInt("total_pages");
        }
        while(currentPage <= totalPages);

        return null;
    }

    private JSONObject createSpaceAttachment(int spaceId, File file, String fileDescription) throws UnirestException, APIException {
        String path = String.format("spaces/%d/attachments", spaceId);

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("file_description", fileDescription);
        fields.put("file", file);

        return executeHttpPost(path, fields);
    }

    private String buildUrl(String path) {
        return String.format("http://%s/v1/%s", Configuration.HOST, path);
    }

    private JSONObject executeHttpGet(String path, Map<String, Object> parameters) throws UnirestException, APIException {
        HttpRequest request = Unirest.get(buildUrl(path));
        request.queryString(parameters);
        return executeHttp(request);
    }

    private JSONObject executeHttpPost(String path, Map<String, Object> fields) throws UnirestException, APIException {
        HttpRequestWithBody request = Unirest.post(buildUrl(path));
        request.fields(fields);
        return executeHttp(request);
    }

    private JSONObject executeHttp(HttpRequest request) throws UnirestException, APIException {
        HttpResponse<JsonNode> response = request.header("Accept", "application/json")
            .basicAuth(Configuration.API_KEY, Configuration.API_SECRET)
            .asJson();

        JSONObject rootJSONObject = response.getBody().getObject();

        if (rootJSONObject == null) {
            throw new RuntimeException("Server returned a malformatted JSON object");
        }

        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            throw new APIException(response.getStatus(), rootJSONObject.getString("error"));
        }

        return rootJSONObject;
    }
}

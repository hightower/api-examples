package com.gethightower;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreateSpaceAttachmentWithClientIds extends APIExample {

    public static void main(String[] args) throws UnirestException {
        new CreateSpaceAttachmentWithClientIds().run(args);
    }

    @Override
    public void doRun(String[] args) throws APIException, UnirestException {
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
}

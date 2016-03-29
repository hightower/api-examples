package com.gethightower;

import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import org.json.JSONObject;

public abstract class APIExample {
    public void run(String[] args) throws UnirestException {
        try {
            doRun(args);
        } catch (APIException ex) {
            System.err.println(String.format("The Hightower API returned an error (%d): %s", ex.getHttpStatusCode(), ex.getMessage()));
            System.exit(1);
        }
    }

    protected abstract void doRun(String[] args) throws APIException, UnirestException;

    protected String buildUrl(String path) {
        return String.format("http://%s/v1/%s", Configuration.HOST, path);
    }

    protected JSONObject executeHttpGet(String path, Map<String, Object> parameters) throws UnirestException, APIException {
        HttpRequest request = Unirest.get(buildUrl(path));
        request.queryString(parameters);
        return executeHttp(request);
    }

    protected JSONObject executeHttpPost(String path, Map<String, Object> fields) throws UnirestException, APIException {
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

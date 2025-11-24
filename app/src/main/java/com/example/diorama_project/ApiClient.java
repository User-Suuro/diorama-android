package com.example.diorama_project;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A reusable HTTP client helper for GET and POST requests.
 * - Uses generics to parse responses into any model class.
 * - Main thread safety for callback invocation.
 * - Does NOT handle date/time; that logic belongs in the main program.
 */
public class ApiClient {

    private static final String BASE_URL = "https://diorama-endpoint.vercel.app";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    // ---- CALLBACK INTERFACE ----
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // ---- GENERIC GET REQUEST ----
    public <T> void get(String endpoint, Class<T> responseType, ApiCallback<T> callback) {
        String url = BASE_URL + endpoint;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postError(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response, responseType, callback);
            }
        });
    }

    // ---- GENERIC POST REQUEST ----
    public <T> void post(String endpoint, Object requestBody, Class<T> responseType, ApiCallback<T> callback) {
        String url = BASE_URL + endpoint;
        String jsonBody = gson.toJson(requestBody);

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postError(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response, responseType, callback);
            }
        });
    }

    // ---- PRIVATE UTILITY METHODS ----
    private <T> void handleResponse(Response response, Class<T> responseType, ApiCallback<T> callback) throws IOException {
        String body = response.body() != null ? response.body().string() : "";

        if (response.isSuccessful()) {
            try {
                T result = gson.fromJson(body, responseType);
                postSuccess(callback, result);
            } catch (JsonSyntaxException e) {
                postError(callback, e);
            }
        } else {
            postError(callback, new Exception("HTTP " + response.code() + ": " + body));
        }
    }

    private <T> void postSuccess(ApiCallback<T> callback, T result) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
    }

    private <T> void postError(ApiCallback<T> callback, Exception e) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
    }
}

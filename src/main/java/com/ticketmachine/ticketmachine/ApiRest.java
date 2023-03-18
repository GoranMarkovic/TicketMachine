package com.ticketmachine.ticketmachine;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.concurrent.Future;

public class ApiRest {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AccessTokenInterceptor())
            .build();

    public static Future<String> get(String url){



        Request request = new Request.Builder()
                .get()
                .url(url)
                .header("Accept", "application/json")
                .build();

        Call call = client.newCall(request);
        HttpResponseCallback callback = new HttpResponseCallback();
        call.enqueue(callback);
        return callback.future.thenApply(response -> {
            try {
                if(response.body() != null) {
                    return response.body().string();
                }
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

//    public static Future<String> post(String url, String requestBody){
//        Request request = new Request.Builder()
//                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
//                .url(url)
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .build();
//
//        Call call = client.newCall(request);
//        HttpResponseCallback callback = new HttpResponseCallback();
//        call.enqueue(callback);
//        return callback.future.thenApply(response -> {
//            try {
//                if(response.body() != null) {
//                    return response.body().string();
//                }
//                return null;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//    public static Future<String> delete(String url){
//        Request request = new Request.Builder()
//                .delete()
//                .url(url)
//                .header("Accept", "application/json")
//                .build();
//
//        Call call = client.newCall(request);
//        HttpResponseCallback callback = new HttpResponseCallback();
//        call.enqueue(callback);
//        return callback.future.thenApply(response -> {
//            try {
//                if(response.body() != null) {
//                    return response.body().string();
//                }
//                return null;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
}


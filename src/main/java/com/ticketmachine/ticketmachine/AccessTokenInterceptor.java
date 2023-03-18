package com.ticketmachine.ticketmachine;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AccessTokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Request newRequest = request.newBuilder()
                .addHeader("Accept", "application/json")
                //.addHeader("Authorization", "Bearer "+ SessionRest.loadAccessToken())
                .addHeader("Authorization", "Bearer eyJraWQiOiJrMSIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJxbV9zZXJ2ZXIiLCJleHAiOjE2NzkyODc0MDYsImp0aSI6IlJHbGxBTFVrVnBIcXB4cDdXd0hyZVEiLCJpYXQiOjE2NzkxMTQ2MDYsIm5iZiI6MTY3OTExNDQ4Niwic3ViIjoidGlja2V0TWFjaGluZSIsInR5cGUiOiJUSUNLRVRfTUFDSElORSJ9.b0BJtx8Oh8fR9grzIBndzlNkF-y92q_pHscJFCxnms11BfxNM0NLbFrtNcI8OtmrNrhMe8j7Ja1W8dl0pG1wd1qtGf3rOjb001bPQQA34sArdN87g3EK73z07tByq5JQSZFb2uwMft0xrd_VbTw4-qeej4pU7OOCBN9AFJmEKUkchaibO2TQF77OE_etsC7dM-uZkUYja_9CVZlk9rQdxKkHKwy8gl1jEhLyuO5-Teqcnp8YwbTRE4bi2_yUN5TDCiC5NAwFG1Y7y_xrMcsTXnXxipe7keOEPh8w1xk8PiYCPRM4HJmcKyHQwF34TQDt_SnVt9HWm4gY71o1zmxEkQ")
                .build();

        return chain.proceed(newRequest);
    }
}


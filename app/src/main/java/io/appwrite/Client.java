package io.appwrite;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static java.util.Map.entry;

public class Client {

    private final OkHttpClient http;
    private final Map<String, String> headers;
    private final Map<String, String> config;

    private String endPoint;
    private boolean selfSigned;
    //private CookieJar cookieJar = CookieJar.NO_COOKIES;
    private PersistentCookieJar cookieJar ;


    @RequiresApi(api = Build.VERSION_CODES.R)
    public Client(Context ctx) {
        this("https://appwrite.io/v1", false, new OkHttpClient(),ctx);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Client(String endPoint, boolean selfSigned, OkHttpClient http, Context ctx) {
        this.endPoint = endPoint;
        this.selfSigned = selfSigned;
        this.headers = new HashMap<>(Map.ofEntries(
                entry("content-type", "application/json"),
                entry("x-sdk-version", "appwrite:java:0.1.0")
        ));

        this.config = new HashMap<>();
        cookieJar =
                new PersistentCookieJar(ctx);

        this.http = http.newBuilder()
                .cookieJar(cookieJar)
                .build();
    }

    public String getEndPoint(){
        return endPoint;
    }

    public   Map<String, String> getConfig(){
        return config;
    }

//    private Future<Directory> getCookiePath() {
//        final directory = getApplicationDocumentsDirectory();
//        final path = directory.path;
//        final Directory dir = new Directory("$path/cookies");
//        dir.create();
//        return dir;
//    }

    /// Your project ID
    public Client setProject(String value) {
        config.put("project", value);
        addHeader("X-Appwrite-Project", value);
        return this;
    }

    public Client setLocale(String value) {
        config.put("locale", value);
        addHeader("X-Appwrite-Locale", value);
        return this;
    }

    public Client setSelfSigned(boolean status) {
        selfSigned = status;
        return this;
    }

    public Client setEndpoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public Client addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Call call(String method, String path, Map<String, String> headers, Map<String, Object> params) {
        if(selfSigned) {
            // Allow self signed requests

        }

        Headers requestHeaders = Headers.of(this.headers).newBuilder()
                .addAll(Headers.of(headers))
                .build();

        HttpUrl.Builder httpBuilder = HttpUrl.get(endPoint + path).newBuilder();
        if("GET".equals(method)) {
            params.forEach((k, v) -> {
                if(v instanceof List){
                    httpBuilder.addQueryParameter(k+"[]", v.toString());
                }else{
                    httpBuilder.addQueryParameter(k, v.toString());
                }
            });
            Request request = new Request.Builder()
                    .url(httpBuilder.build())
                    .headers(requestHeaders)
                    .get()
                    .build();

            return http.newCall(request);
        }

        RequestBody body;
        if("multipart/form-data".equals(headers.get("content-type"))) {
            FormBody.Builder builder = new FormBody.Builder();
            params.forEach((k, v) -> builder.add(k, v.toString()));
            body = builder.build();
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(params);
            body = RequestBody.create(json, MediaType.get("application/json"));
        }

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .headers(requestHeaders)
                .method(method, body)
                .build();

        return http.newCall(request);
    }

}

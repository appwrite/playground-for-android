package io.appwrite;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PersistentCookieJar implements CookieJar {

    private final SharedPreferences sharedPreferences;

    public PersistentCookieJar(Context context) {
        this(context.getSharedPreferences("CookiePersistence", Context.MODE_PRIVATE));
    }

    public PersistentCookieJar(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    synchronized public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        saveAll(filterPersistentCookies(cookies));
    }

    private void saveAll(Collection<Cookie> cookies) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String encodedCookie = null;
        for (Cookie cookie : cookies) {
                Log.d("AppWrite Cookie", cookie.toString());
                encodedCookie = new SerializableCookie().encode(cookie);
                editor.putString(cookie.name(), encodedCookie);
                Log.d("AppWrite Cookie after Serialized", encodedCookie);
            
        }
        editor.apply();
    }

    public List<Cookie> loadAll() {
        List<Cookie> cookies = new ArrayList<>(sharedPreferences.getAll().size());

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String serializedCookie = (String) entry.getValue();
            Cookie cookie = new SerializableCookie().decode(serializedCookie);
            if (cookie != null) {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    private static String createCookieKey(Cookie cookie) {
        return (cookie.secure() ? "https" : "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name();
    }

    private static List<Cookie> filterPersistentCookies(List<Cookie> cookies) {
        List<Cookie> persistentCookies = new ArrayList<>();

        for (Cookie cookie : cookies) {
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            }
        }
        return persistentCookies;
    }

    @NotNull
    @Override
    synchronized public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        List<Cookie> cookieList = loadAll();
        Log.d("AppWrite Cookie Read from sharedPreference ", cookieList.toString());

        return cookieList;
    }
}
package io.appwrite.services;


import okhttp3.HttpUrl;
import io.appwrite.Client;

import java.util.Map;

import static java.util.Map.entry;

public class Avatars extends Service {
    public Avatars(Client client){
        super(client);
    }

    /// Get Browser Icon
    /*
     * You can use this endpoint to show different browser icons to your users.
     * The code argument receives the browser code as it appears in your user
     * /account/sessions endpoint. Use width, height and quality arguments to
     * change the output settings.
     */
    public String getBrowser(String code, int width, int height, int quality) {
        final String path = "/avatars/browsers/{code}".replace("{code}", code);

        final Map<String, Object> params = Map.ofEntries(
                entry("width", width),
                entry("height", height),
                entry("quality", quality),
                entry("project", client.getConfig().get("project"))
        );



        HttpUrl.Builder httpBuilder = new HttpUrl.Builder().build().newBuilder(client.getEndPoint() + path);
        params.forEach((k, v) -> httpBuilder.addQueryParameter(k, v.toString()));

        return httpBuilder.build().toString();
    }

    /// Get Credit Card Icon
    /*
     * Need to display your users with your billing method or their payment
     * methods? The credit card endpoint will return you the icon of the credit
     * card provider you need. Use width, height and quality arguments to change
     * the output settings.
     */
    public String getCreditCard(String code, int width, int height, int quality) {
        final String path = "/avatars/credit-cards/{code}".replace("{code}", code);

        final Map<String, Object> params = Map.ofEntries(
                entry("width", width),
                entry("height", height),
                entry("quality", quality),
                entry("project", client.getConfig().get("project"))
        );



        HttpUrl.Builder httpBuilder = new HttpUrl.Builder().build().newBuilder(client.getEndPoint() + path);
        params.forEach((k, v) -> httpBuilder.addQueryParameter(k, v.toString()));

        return httpBuilder.build().toString();
    }

    /// Get Favicon
    /*
     * Use this endpoint to fetch the favorite icon (AKA favicon) of a  any remote
     * website URL.
     */
    public String getFavicon(String url) {
        final String path = "/avatars/favicon";

        final Map<String, Object> params = Map.ofEntries(
                entry("url", url),
                entry("project", client.getConfig().get("project"))
        );



        HttpUrl.Builder httpBuilder = new HttpUrl.Builder().build().newBuilder(client.getEndPoint() + path);
        params.forEach((k, v) -> httpBuilder.addQueryParameter(k, v.toString()));

        return httpBuilder.build().toString();
    }

    /// Get Country Flag
    /*
     * You can use this endpoint to show different country flags icons to your
     * users. The code argument receives the 2 letter country code. Use width,
     * height and quality arguments to change the output settings.
     */
    public String getFlag(String code, int width, int height, int quality) {
        final String path = "/avatars/flags/{code}".replace("{code}", code);

        final Map<String, Object> params = Map.ofEntries(
                entry("width", width),
                entry("height", height),
                entry("quality", quality),
                entry("project", client.getConfig().get("project"))
        );



        HttpUrl.Builder httpBuilder = new HttpUrl.Builder().build().newBuilder(client.getEndPoint() + path);
        params.forEach((k, v) -> httpBuilder.addQueryParameter(k, v.toString()));

        return httpBuilder.build().toString();
    }

    /// Get Image from URL
    /*
     * Use this endpoint to fetch a remote image URL and crop it to any image size
     * you want. This endpoint is very useful if you need to crop and display
     * remote images in your app or in case you want to make sure a 3rd party
     * image is properly served using a TLS protocol.
     */
    public String getImage(String url, int width, int height) {
        final String path = "/avatars/image";

        final Map<String, Object> params = Map.ofEntries(
                entry("url", url),
                entry("width", width),
                entry("height", height),
                entry("project", client.getConfig().get("project"))
        );



        HttpUrl.Builder httpBuilder = new HttpUrl.Builder().build().newBuilder(client.getEndPoint() + path);
        params.forEach((k, v) -> httpBuilder.addQueryParameter(k, v.toString()));

        return httpBuilder.build().toString();
    }

    /// Get User Initials
    /*
     * Use this endpoint to show your user initials avatar icon on your website or
     * app. By default, this route will try to print your logged-in user name or
     * email initials. You can also overwrite the user name if you pass the 'name'
     * parameter. If no name is given and no user is logged, an empty avatar will
     * be returned.
     *
     * You can use the color and background params to change the avatar colors. By
     * default, a random theme will be selected. The random theme will persist for
     * the user's initials when reloading the same theme will always return for
     * the same initials.
     */
    public String getInitials(String name, int width, int height, String color, String background) {
        final String path = "/avatars/initials";

        final Map<String, Object> params = Map.ofEntries(
                entry("name", name),
                entry("width", width),
                entry("height", height),
                entry("color", color),
                entry("background", background),
                entry("project", client.getConfig().get("project"))
        );



        HttpUrl.Builder httpBuilder = new HttpUrl.Builder().build().newBuilder(client.getEndPoint() + path);
        params.forEach((k, v) -> httpBuilder.addQueryParameter(k, v.toString()));

        return httpBuilder.build().toString();
    }

    /// Get QR Code
    /*
     * Converts a given plain text to a QR code image. You can use the query
     * parameters to change the size and style of the resulting image.
     */
    public String getQR(String text, int size, int margin, int download) {
        final String path = "/avatars/qr";

        final Map<String, Object> params = Map.ofEntries(
                entry("text", text),
                entry("size", size),
                entry("margin", margin),
                entry("download", download),
                entry("project", client.getConfig().get("project"))
        );



        HttpUrl.Builder httpBuilder = new HttpUrl.Builder().build().newBuilder(client.getEndPoint() + path);
        params.forEach((k, v) -> httpBuilder.addQueryParameter(k, v.toString()));

        return httpBuilder.build().toString();
    }
}
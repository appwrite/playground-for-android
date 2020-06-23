package io.appwrite.services;

import io.appwrite.enums.OrderType;
import okhttp3.Call;
import io.appwrite.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Database  extends Service {
    Client client = null;
    public Database(Client client){
        super(client);
        this.client = client;

    }

    /// List Documents
    /*
     * Get a list of all the user documents. You can use the query params to
     * filter your results. On admin mode, this endpoint will return a list of all
     * of the project documents. [Learn more about different API
     * modes](/docs/admin).
     */
    public Call listDocuments(String collectionId, List filters, int offset, int limit, String orderField, OrderType orderType, String orderCast, String search, int first, int last) {
        final String path = "/database/collections/{collectionId}/documents".replace("{collectionId}", collectionId);
        final Map<String, Object> params = new HashMap<>();
//                params.put("filters", filters);
//                params.put("offset", offset);
//                params.put("limit", limit);
//                params.put("orderField", orderField);
//                params.put("orderType", orderType.name());
//                params.put("orderCast", orderCast);
//                params.put("search", search);
//                params.put("first", first);
//                params.put("last", last);

//        final Map<String, Object> params = Map.ofEntries(
//                entry("filters", filters),
//                entry("offset", offset),
//                entry("limit", limit),
//                entry("orderField", orderField),
//                entry("orderType", orderType.name()),
//                entry("orderCast", orderCast),
//                entry("search", search),
//                entry("first", first),
//                entry("last", last)
//        );


        final Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");

//        final Map<String, String> headers = Map.ofEntries(
//                entry("content-type", "application/json")
//        );

        return client.call("GET", path, headers, params);

    }

    /// Create Document
    /*
     * Create a new Document. Before using this route, you should create a new
     * collection resource using either a [server
     * integration](/docs/server/database?sdk=nodejs#createCollection) API or
     * directly from your database console.
     */
    public Call createDocument(String collectionId, Object data, List read, List write, String parentDocument, String parentProperty, String parentPropertyType) {
        final String path = "/database/collections/{collectionId}/documents".replace("{collectionId}", collectionId);

        final Map<String, Object> params = Map.ofEntries(
                entry("data", data),
                entry("read", read),
                entry("write", write),
                entry("parentDocument", parentDocument),
                entry("parentProperty", parentProperty),
                entry("parentPropertyType", parentPropertyType)
        );



        final Map<String, String> headers = Map.ofEntries(
                entry("content-type", "application/json")
        );

        return client.call("POST", path, headers, params);
    }

    /// Get Document
    /*
     * Get document by its unique ID. This endpoint response returns a JSON object
     * with the document data.
     */
    public Call getDocument(String collectionId, String documentId) {
        final String path = "/database/collections/{collectionId}/documents/{documentId}".replace("{collectionId}", collectionId).replace("{documentId}", documentId);

        final Map<String, Object> params = Map.ofEntries(
        );



        final Map<String, String> headers = Map.ofEntries(
                entry("content-type", "application/json")
        );

        return client.call("GET", path, headers, params);
    }

    /// Update Document
    public Call updateDocument(String collectionId, String documentId, Object data, List read, List write) {
        final String path = "/database/collections/{collectionId}/documents/{documentId}".replace("{collectionId}", collectionId).replace("{documentId}", documentId);

        final Map<String, Object> params = Map.ofEntries(
                entry("data", data),
                entry("read", read),
                entry("write", write)
        );



        final Map<String, String> headers = Map.ofEntries(
                entry("content-type", "application/json")
        );

        return client.call("PATCH", path, headers, params);
    }

    /// Delete Document
    /*
     * Delete document by its unique ID. This endpoint deletes only the parent
     * documents, his attributes and relations to other documents. Child documents
     * **will not** be deleted.
     */
    public Call deleteDocument(String collectionId, String documentId) {
        final String path = "/database/collections/{collectionId}/documents/{documentId}".replace("{collectionId}", collectionId).replace("{documentId}", documentId);

        final Map<String, Object> params = Map.ofEntries(
        );



        final Map<String, String> headers = Map.ofEntries(
                entry("content-type", "application/json")
        );

        return client.call("DELETE", path, headers, params);
    }
}

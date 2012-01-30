package io.iron.ironmq;

import groovy.json.JsonSlurper;

public class Client {
    static final private String proto = "https";
    static final private String host = "mq-aws-us-east-1.iron.io";
    static final private int port = 443;
    static final private String apiVersion = "1";

    private String projectId;
    private String token;

    /**
     * Constructs a new Client using the specified project ID and token.
     * The network is not accessed during construction and this call will
     * succeed even if the credentials are invalid.
     *
     * @param projectId A 24-character project ID.
     * @param token An OAuth token.
     */
    public Client(String projectId, String token) {
        this.projectId = projectId;
        this.token = token;
    }

    /**
     * Returns a Queue using the given name.
     * The network is not accessed during this call.
     *
     * @param name The name of the Queue to create.
     */
    public Queue queue(String name) {
        return new Queue(this, name);
    }

    Map doDelete(String endpoint) throws IOException {
        return doRequest("DELETE", endpoint, null);
    }

    Map doGet(String endpoint) throws IOException {
        return doRequest("GET", endpoint, null);
    }

    Map doPost(String endpoint, String body) throws IOException {
        return doRequest("POST", endpoint, body);
    }

    private Map doRequest(String method, String endpoint, String body) throws IOException {
        String path = "/" + apiVersion + "/projects/" + projectId + "/" + endpoint;
        URL url = new URL(proto, host, port, path);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "OAuth " + token);
        conn.setRequestProperty("User-Agent", "IronMQ Java Client");

        if (body != null) {
            conn.setDoOutput(true);
        }

        conn.connect();

        if (body != null) {
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(body);
            out.flush();
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            String jsonString = conn.getInputStream().getText();
            Map jsonObj = (Map)((new JsonSlurper()).parseText(jsonString));
            String msg = (String)jsonObj.get("msg");
            throw new HTTPException(status, msg);
        }

        String jsonString = conn.getInputStream().getText();
        Map jsonObj = (Map)((new JsonSlurper()).parseText(jsonString));
        return jsonObj;
    }
}

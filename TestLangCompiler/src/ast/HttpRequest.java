package ast;

import java.util.*;

public class HttpRequest implements Statement {
    public String method; // GET, POST, PUT, DELETE
    public String path;
    public Map<String, String> headers;
    public String body;

    public HttpRequest(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers != null ? headers : new HashMap<>();
        this.body = body;
    }
}
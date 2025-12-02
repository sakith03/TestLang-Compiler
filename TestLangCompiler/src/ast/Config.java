package ast;

import java.util.*;

public class Config {
    public String baseUrl;
    public Map<String, String> headers;

    public Config(String baseUrl, Map<String, String> headers) {
        this.baseUrl = baseUrl;
        this.headers = headers != null ? headers : new HashMap<>();
    }
}

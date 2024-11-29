package org.ocklin.oidc;

import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.QueryStringDecoder;

// just testing the netty QueryStringDecoder
public class QueryDecoder {
    public static void main(String[] args) {
        String url = "http://localhost:8080/realms/hcd?client_id=myclient&redirect_uri=http://localhost:5443/&response_type=code&scope=openid";

        QueryStringDecoder decoder = new QueryStringDecoder(url);
        System.out.println("Path: " + decoder.path());
        Map<String, List<String>> parameters = decoder.parameters();

        // Print the decoded parameters
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequest {
    private Method method;
    private String uri;
    private HttpVersion version;
    private Map<String, String> headers = new HashMap<>();

    private HttpRequest() {}

    public static HttpRequest parse(String requestStr) {
        HttpRequest httpRequest = new HttpRequest();
        try (BufferedReader reader = new BufferedReader(new StringReader(requestStr))) {
            String line = reader.readLine();
            if (line == null) {
                httpRequest.setMethod(null);
                httpRequest.setVersion(HttpVersion.HTTP11);
                return httpRequest;
            }

            StringTokenizer tokenizer = new StringTokenizer(line);
            if (tokenizer.countTokens() >= 3) {
                httpRequest.setMethod(Method.getHttpMethod(tokenizer.nextToken()));
                httpRequest.setUri(URLDecoder.decode(tokenizer.nextToken(), "UTF-8").replace("+", "%2B"));
                httpRequest.setVersion(HttpVersion.getHttpVersion(tokenizer.nextToken()));
            } else {
                httpRequest.setMethod(null);
                httpRequest.setVersion(HttpVersion.HTTP11);
                return httpRequest;
            }

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                int colon = line.indexOf(":");
                if (colon != -1) {
                    String headerName = line.substring(0, colon).trim();
                    String headerValue = line.substring(colon + 1).trim();
                    httpRequest.headers.put(headerName, headerValue);
                } else {
                    httpRequest.setMethod(null);
                    httpRequest.setVersion(HttpVersion.HTTP11);
                    return httpRequest;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing HTTP request", e);
        }
        return httpRequest;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
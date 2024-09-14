import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Response {
    private final Map<String, String> headers;
    private StatusCode statusCode;
    private HttpVersion version;
    private String body;

    public Response(StatusCode statusCode, HttpVersion httpVersion) {
        this.statusCode = statusCode;
        this.version = httpVersion;
        headers = new HashMap<>();
//        setDefaultHeaders();
    }

    public static Response badRequest(HttpVersion httpVersion) {
        return buildResponse(StatusCode.BAD_REQUEST, httpVersion);
    }

    public static Response ok(HttpVersion httpVersion) {
        return buildResponse(StatusCode.OK, httpVersion);
    }

    public static Response fileNotFound(HttpVersion httpVersion) {
        return buildResponse(StatusCode.FILE_NOT_FOUND, httpVersion);
    }

    public static Response notModified(HttpVersion httpVersion) {
        return buildResponse(StatusCode.NOT_MODIFIED, httpVersion);
    }

    public static Response serverError(HttpVersion httpVersion) {
        return buildResponse(StatusCode.SERVER_ERROR, httpVersion);
    }

    public static Response buildResponse(StatusCode statusCode, HttpVersion httpVersion) {
        Response response = new Response(statusCode, httpVersion);
//        response.setDefaultHeaders();
        return response;
    }

    public void setDefaultHeaders() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        String httpDate = formatter.format(now);
        headers.put("Date", httpDate);

        headers.putIfAbsent("Content-Type", "text/plain");

        int contentLength = (body != null) ? body.getBytes().length : 0;
        headers.put("Content-Length", String.valueOf(contentLength));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s %s\r\n", version.version, statusCode.code, statusCode.msg));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(String.format("%s: %s\r\n", entry.getKey(), entry.getValue()));
        }
        sb.append("\r\n");
        if (body != null) {
            sb.append(body);
        }
        return sb.toString();
    }

    public void setBody(String body) {
        this.body = body;
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

    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}

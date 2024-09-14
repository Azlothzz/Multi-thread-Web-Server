public enum HttpVersion {
    HTTP10("HTTP/1"), HTTP11("HTTP/1.1");
    public final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion getHttpVersion(String version) {
        if (version.equals(HTTP10.version)) {
            return HTTP10;
        } else if (version.equals(HTTP11.version)) {
            return HTTP11;
        } else {
            return null;
        }
    }
}

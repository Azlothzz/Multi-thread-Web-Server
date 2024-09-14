public enum Method {
    GET("GET"), HEAD("HEAD");

    private final String method;

    Method(String method) {
        this.method = method;
    }

    public static Method getHttpMethod(String method) {
        for (Method Method : Method.values()) {
            if (Method.method.equalsIgnoreCase(method)) {
                return Method;
            }
        }
        return null;  // Return null if no match is found
    }

    @Override
    public String toString() {
        return this.method;
    }
}
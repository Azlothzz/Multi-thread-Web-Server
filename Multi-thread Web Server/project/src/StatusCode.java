public enum StatusCode {
    OK("200", "OK"),
    NOT_MODIFIED("304", "NOT MODIFIED"),
    BAD_REQUEST("400", "BAD REQUEST"),
    FILE_NOT_FOUND("404", "FILE NOT FOUND"),
    SERVER_ERROR("500", "SERVER ERROR");
    public final String code;
    public final String msg;

    StatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static StatusCode getStatusCode(String code) {
        if (code.equals(OK.code)) {
            return OK;
        } else if (code.equals(BAD_REQUEST.code)) {
            return BAD_REQUEST;
        } else if (code.equals(FILE_NOT_FOUND.code)) {
            return FILE_NOT_FOUND;
        } else if (code.equals(NOT_MODIFIED.code)) {
            return NOT_MODIFIED;
        } else {
            return null;
        }
    }
}

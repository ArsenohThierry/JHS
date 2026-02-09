package com.jhs.http;

public enum HttpStatusCode {

        /*Client errors */
        CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
        CLIENT_ERROR_401_UNAUTHORIZED(401, "Unauthorized"),
        CLIENT_ERROR_414_URI_TOO_LONG(414, "URI Too Long"),
        
        /*Server errors */
        SERVER_ERROR_500_INTERNAL_ERROR(500, "Internal Server Error"),
        SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not Implemented"),
        SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported")
        ;

    public final int _STATUS_CODE;
    public final String _MESSAGE;

    HttpStatusCode(int _STATUS_CODE, String _MESSAGE) {
        this._STATUS_CODE = _STATUS_CODE;
        this._MESSAGE = _MESSAGE;
    }

    
}


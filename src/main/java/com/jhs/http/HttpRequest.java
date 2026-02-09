package com.jhs.http;

import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage {
    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion;
    private HttpVersion bestCompatibleHttpVersion;
    private HashMap<String, String> headers = new HashMap<>();

    HttpRequest() {
    }

    HttpMethod getMethod() {
        return method;
    }

    void setMethod(String method) throws HttpParsingException {
        for(HttpMethod m : HttpMethod.values()){
            if(m.name().equals(method)){
                this.method = m;
                return;
            }
            throw new HttpParsingException(
                    HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
            );
        }
        this.method = HttpMethod.valueOf(method);
    }

    void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.length() <= 0) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_ERROR);
        }
        this.requestTarget = requestTarget;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getOriginalHttpVersion() {
        return originalHttpVersion;
    }

    void setHttpVersion(String originalHttpVersion) throws BadHttpVersionException, HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (this.bestCompatibleHttpVersion == null) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }
    
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public String getHeader(String headerNames){
        return headers.get(headerNames.toLowerCase());
    }

    void addHeader(String headerName, String headerField) {
            headers.put(headerName.toLowerCase(), headerField);
        }

    public HttpVersion getBestCompatibleHttpVersion() {
        return bestCompatibleHttpVersion;
    }
   
}
 
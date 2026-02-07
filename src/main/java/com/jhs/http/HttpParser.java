package com.jhs.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);
    
    private static final int SP = 0x20; // 32
    private static final int CR = 0x0D; // 13 
    private static final int LF = 0x0A; // 10 

    public HttpRequest parseHttpRequest(InputStream inputStream) {
    InputStreamReader reader = new InputStreamReader(inputStream,StandardCharsets.US_ASCII);

        HttpRequest httpRequest = new HttpRequest();

    parseRequestLine(reader,httpRequest);
    parseHeaders(reader,httpRequest);
    parseBody(reader,httpRequest);

    return httpRequest;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest httpRequest) throws IOException {
        StringBuilder processingdataBuffer = new StringBuilder();
        
        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    return;
                }
            }

            if (_byte == SP) {
                // TODO process previous data
            }
        }
        
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest httpRequest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void parseBody(InputStreamReader reader, HttpRequest httpRequest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

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

    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
    InputStreamReader reader = new InputStreamReader(inputStream,StandardCharsets.US_ASCII);

        HttpRequest httpRequest = new HttpRequest();

    try {
        parseRequestLine(reader,httpRequest);
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    // parseHeaders(reader,httpRequest);
    // parseBody(reader,httpRequest);

    return httpRequest;
    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest httpRequest) throws IOException, HttpParsingException {
        StringBuilder processingdataBuffer = new StringBuilder();
        
        boolean methodParsed = false;
        boolean requestTargetParsed = false;

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    LOGGER.debug("Request VERSION parsed: {}", processingdataBuffer.toString());
                    return;
                }
            }

            if (_byte == SP) {
                if (!methodParsed) {
                LOGGER.debug("Request line METHOD parsed: {}", processingdataBuffer.toString());
                httpRequest.setMethod(processingdataBuffer.toString());
                methodParsed = true;    
                }

                else if (!requestTargetParsed) {
                    LOGGER.debug("Request line REQUEST TARGET parsed: {}", processingdataBuffer.toString());
                    requestTargetParsed = true;
                }
                processingdataBuffer.delete(0, processingdataBuffer.length());
            }
            else {
                processingdataBuffer.append((char)_byte);
                if (!methodParsed) {
                    if (processingdataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(
                            HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
                        ); 
                    }
                }
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

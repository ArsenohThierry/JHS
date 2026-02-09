package com.jhs.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    try {
        parseHeaders(reader, httpRequest);
    } catch (IOException e) {
        e.printStackTrace();
    }
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
                    if(!methodParsed || !requestTargetParsed){
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    try {
                        httpRequest.setHttpVersion(processingdataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }

                    return;
                }
                else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
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
                    httpRequest.setRequestTarget(processingdataBuffer.toString());
                    requestTargetParsed = true;
                }
                else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingdataBuffer.delete(0, processingdataBuffer.length());
            }
            else {
                processingdataBuffer.append((char)_byte);
                if (!methodParsed) {
                    if (processingdataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
        
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean lineEndPreviouslySeen = false; // indicates previous CRLF or LF seen

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                int next = reader.read();
                if (next == LF) {
                    if (!lineEndPreviouslySeen) {
                        // process header line
                        processSingleHeaderField(processingDataBuffer, request);
                        processingDataBuffer.setLength(0);
                        lineEndPreviouslySeen = true;
                    } else {
                        // double CRLF -> end of headers
                        return;
                    }
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } else if (_byte == LF) {
                // accept lone LF as line terminator (tolerant parsing)
                if (!lineEndPreviouslySeen) {
                    processSingleHeaderField(processingDataBuffer, request);
                    processingDataBuffer.setLength(0);
                    lineEndPreviouslySeen = true;
                } else {
                    return;
                }
            } else {
                lineEndPreviouslySeen = false;
                processingDataBuffer.append((char) _byte);
            }
        }

        // EOF reached: if there's leftover data, try to process it as a header line
        if (processingDataBuffer.length() > 0) {
            processSingleHeaderField(processingDataBuffer, request);
        }
    }

    private void processSingleHeaderField(StringBuilder processingDataBuffer, HttpRequest request) throws HttpParsingException {
        String rawHeaderField = processingDataBuffer.toString();
        Pattern pattern = Pattern.compile("^(?<fieldName>[!#$%&’*+\\-./^_‘|˜\\dA-Za-z]+):\\s?(?<fieldValue>[!#$%&’*+\\-./^_‘|˜(),:;<=>?@[\\\\]{}\" \\dA-Za-z]+)\\s?$");

        Matcher matcher = pattern.matcher(rawHeaderField);
        if (matcher.matches()) {
            // We found a proper header
            String fieldName = matcher.group("fieldName");
            String fieldValue = matcher.group("fieldValue");
            request.addHeader(fieldName, fieldValue);
        } else{
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    private void parseBody(InputStreamReader reader, HttpRequest httpRequest) {
            // TODO : parse body if Content-Length or Transfer-Encoding headers are present
    }
}

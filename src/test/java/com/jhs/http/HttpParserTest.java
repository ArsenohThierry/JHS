package com.jhs.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HttpParserTest {
    private HttpParser httpParser;

    @BeforeEach
    public void beforeClass() {
        httpParser = new HttpParser();
    }

    @Test
    public void testParseHttpRequest() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidGETtestCase());
        } catch (HttpParsingException e) {
            fail(e);
        }
        assertNotNull(httpRequest);
        assertEquals(httpRequest.getMethod(), HttpMethod.GET);
        assertEquals(httpRequest.getRequestTarget(), "/");
        assertEquals(httpRequest.getOriginalHttpVersion(), "HTTP/1.1");
        assertEquals(httpRequest.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
    }

    @Test
    public void testParseBadHttpRequest() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateBadTestCaseMethodName());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }

    }

    @Test
    public void testParseBadLongMethod() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateBadTestCaseMethodLength());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }

    }

    @Test
    public void testParseBadLongRequest() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateBadTestCaseRequestLength());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

    }

    @Test
    public void testParseBadEmptyRequest() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateBadTestEmptyRequest());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

    }

    @Test
    public void testParseBadRequestOnlyCRnoLF() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(
                    generateBadTestRequestOnlyCRnoLF());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

    }

    @Test
    public void testParseBadHttpVersion() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(
                    generateBadHttpVersionTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

    }

    @Test
    public void testParseUnsupportedHttpVersion() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(
                    generateUnsupportedHttpVersionTestCase());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }

    }

    @Test
    public void testParseSupportedHttpVersion() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(
                    generateSupportedHttpVersionTestCase());

            assertNotNull(httpRequest);
            assertEquals(httpRequest.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
            assertEquals(httpRequest.getOriginalHttpVersion(), "HTTP/1.2");

        } catch (HttpParsingException e) {
            fail();
        }

    }

    private InputStream generateValidGETtestCase() {
        String rawData = "GET / HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Connection: keep-alive\r\n" + //
                "Cache-Control: max-age=0\r\n" + //
                "sec-ch-ua: \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Brave\";v=\"144\"\r\n" + //
                "sec-ch-ua-mobile: ?0\r\n" + //
                "sec-ch-ua-platform: \"Linux\"\r\n" + //
                "Upgrade-Insecure-Requests: 1\r\n" + //
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36\r\n"
                + //
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\r\n"
                + //
                "Sec-GPC: 1\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "Sec-Fetch-Site: none\r\n" + //
                "Sec-Fetch-Mode: navigate\r\n" + //
                "Sec-Fetch-User: ?1\r\n" + //
                "Sec-Fetch-Dest: document\r\n" + //
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" + //
                "Cookie: tracy-session=fbfc783321" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseMethodName() {
        String rawData = "GeT / HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseMethodLength() {
        String rawData = "GETTTTTTT / HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestCaseRequestLength() {
        String rawData = "GET / AAAAA HTTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestEmptyRequest() {
        String rawData = "\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadTestRequestOnlyCRnoLF() {
        String rawData = "GET / AAAAA HTTP/1.1\r" + // pas de \n
                "Host: localhost:8080\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadHttpVersionTestCase() {
        String rawData = "GET / HTP/1.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Connection: keep-alive\r\n" + //
                "Cache-Control: max-age=0\r\n" + //
                "sec-ch-ua: \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Brave\";v=\"144\"\r\n" + //
                "sec-ch-ua-mobile: ?0\r\n" + //
                "sec-ch-ua-platform: \"Linux\"\r\n" + //
                "Upgrade-Insecure-Requests: 1\r\n" + //
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36\r\n"
                + //
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\r\n"
                + //
                "Sec-GPC: 1\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "Sec-Fetch-Site: none\r\n" + //
                "Sec-Fetch-Mode: navigate\r\n" + //
                "Sec-Fetch-User: ?1\r\n" + //
                "Sec-Fetch-Dest: document\r\n" + //
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" + //
                "Cookie: tracy-session=fbfc783321" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateUnsupportedHttpVersionTestCase() {
        String rawData = "GET / HTTP/2.1\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Connection: keep-alive\r\n" + //
                "Cache-Control: max-age=0\r\n" + //
                "sec-ch-ua: \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Brave\";v=\"144\"\r\n" + //
                "sec-ch-ua-mobile: ?0\r\n" + //
                "sec-ch-ua-platform: \"Linux\"\r\n" + //
                "Upgrade-Insecure-Requests: 1\r\n" + //
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36\r\n"
                + //
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\r\n"
                + //
                "Sec-GPC: 1\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "Sec-Fetch-Site: none\r\n" + //
                "Sec-Fetch-Mode: navigate\r\n" + //
                "Sec-Fetch-User: ?1\r\n" + //
                "Sec-Fetch-Dest: document\r\n" + //
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" + //
                "Cookie: tracy-session=fbfc783321" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateSupportedHttpVersionTestCase() {
        String rawData = "GET / HTTP/1.2\r\n" + //
                "Host: localhost:8080\r\n" + //
                "Connection: keep-alive\r\n" + //
                "Cache-Control: max-age=0\r\n" + //
                "sec-ch-ua: \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Brave\";v=\"144\"\r\n" + //
                "sec-ch-ua-mobile: ?0\r\n" + //
                "sec-ch-ua-platform: \"Linux\"\r\n" + //
                "Upgrade-Insecure-Requests: 1\r\n" + //
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36\r\n"
                + //
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\r\n"
                + //
                "Sec-GPC: 1\r\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\r\n" + //
                "Sec-Fetch-Site: none\r\n" + //
                "Sec-Fetch-Mode: navigate\r\n" + //
                "Sec-Fetch-User: ?1\r\n" + //
                "Sec-Fetch-Dest: document\r\n" + //
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" + //
                "Cookie: tracy-session=fbfc783321" + //
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII));
        return inputStream;
    }

}

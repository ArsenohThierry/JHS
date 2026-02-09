package com.jhs.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHeaderParserTest {
    private HttpParser httpParser;

    private Method parseHeadersMethod;

    @BeforeAll
    public void beforeClass() throws NoSuchMethodException {
        httpParser = new HttpParser();

        Class<HttpParser> cls = HttpParser.class;
        parseHeadersMethod = cls.getDeclaredMethod("parseHeaders", InputStreamReader.class, HttpRequest.class);
        parseHeadersMethod.setAccessible(true);
    }

    @Test
    public void testSimpleSingleHeader() throws IllegalAccessException, InvocationTargetException {
        HttpRequest httpRequest = new HttpRequest();
        parseHeadersMethod.invoke(
                httpParser,
                generateSimpleSingleHeaderMessage(),
                httpRequest);
        assertEquals(1, httpRequest.getHeaderNames().size());
        assertEquals("localhost:8080", httpRequest.getHeader("host"));
    }

    @Test
    public void testMultipleHeaders() throws IllegalAccessException, InvocationTargetException {
        HttpRequest httpRequest = new HttpRequest();
        parseHeadersMethod.invoke(
                httpParser,
                generateMultipleHeadersMessage(),
                httpRequest);
        assertEquals(17, httpRequest.getHeaderNames().size());
        assertEquals("localhost:8080", httpRequest.getHeader("host"));
    }

     @Test
    public void testErrorSpaceBeforeColonHeader() throws InvocationTargetException, IllegalAccessException {
        HttpRequest httprequest = new HttpRequest();

        try {
            parseHeadersMethod.invoke(
                    httpParser,
                    generateSpaceBeforeColonErrorHeaderMessage(),
                    httprequest);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof HttpParsingException) {
                assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, ((HttpParsingException)e.getCause()).getErrorCode());
            }
        }

    }


    private InputStreamReader generateSimpleSingleHeaderMessage() {
        String rawData = "Host: localhost:8080\r\n"; //

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        return inputStreamReader;
    }

    private InputStreamReader generateMultipleHeadersMessage() {
        String rawData = "Host: localhost:8080\n" + //
                "Connection: keep-alive\n" + //
                "Cache-Control: max-age=0\n" + //
                "sec-ch-ua: \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Brave\";v=\"144\"\n" + //
                "sec-ch-ua-mobile: ?0\n" + //
                "sec-ch-ua-platform: \"Linux\"\n" + //
                "Upgrade-Insecure-Requests: 1\n" + //
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36\n"
                + //
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\n"
                + //
                "Sec-GPC: 1\n" + //
                "Accept-Language: fr-FR,fr;q=0.5\n" + //
                "Sec-Fetch-Site: none\n" + //
                "Sec-Fetch-Mode: navigate\n" + //
                "Sec-Fetch-User: ?1\n" + //
                "Sec-Fetch-Dest: document\n" + //
                "Accept-Encoding: gzip, deflate, br, zstd\n" + //
                "Cookie: tracy-session=fbfc783321";

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        return inputStreamReader;
    }

    private InputStreamReader generateSpaceBeforeColonErrorHeaderMessage() {
        String rawData = "Host : localhost:8080\r\n\r\n" ;

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        return reader;
    }

}

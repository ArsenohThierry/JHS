package com.jhs.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpParserTest {
    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass(){
        httpParser = new HttpParser();
    }

    @Test
    public void testParseHttpRequest(){
        httpParser.parseHttpRequest(generateValidTestCase());
    }

    private InputStream generateValidTestCase(){
        String rawData = "GET / HTTP/1.1\r\n" + //
                        "Host: localhost:8080\r\n" + //
                        "Connection: keep-alive\r\n" + //
                        "Cache-Control: max-age=0\r\n" + //
                        "sec-ch-ua: \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"144\", \"Brave\";v=\"144\"\r\n" + //
                        "sec-ch-ua-mobile: ?0\r\n" + //
                        "sec-ch-ua-platform: \"Linux\"\r\n" + //
                        "Upgrade-Insecure-Requests: 1\r\n" + //
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36\r\n" + //
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\r\n" + //
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
                StandardCharsets.US_ASCII
            )
        );
        return inputStream;
                    }
}

package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private final HttpServer server = new HttpServer(0);

    public HttpServerTest() throws IOException {
    }

    @Test
    void shouldGetIndexHTMLFromDisk() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/index.html");
        assertEquals(200, client.getStatusCode());
    }
}

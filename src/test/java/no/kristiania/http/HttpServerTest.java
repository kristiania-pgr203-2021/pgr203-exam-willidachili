package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private final HttpServer server = new HttpServer(0);

    public HttpServerTest() throws IOException {
    }

    @Test
    void shouldGetIndexHTMLFromDisk() throws IOException {
        server.setRoot(Paths.get("src/main/resources"));
        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/index.html");
        assertEquals(200, client.getStatusCode());
    }


}

package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {


    public String startLine;
    public Map<String, String> headerFields = new HashMap<>();
    public String messageBody;

    public HttpMessage(Socket socket) throws IOException {
        startLine = HttpMessage.readLine(socket);
        readHeaders(socket);
        if (headerFields.containsKey("Content-Length")) {
            messageBody = HttpMessage.readBytes(socket, getContentLength());
        }
    }


    public HttpMessage(String startLine, String messageBody){
        this.startLine = startLine;
        this.messageBody = messageBody;
    }

    public static Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        if (query != null) {
            for (String queryParameter : query.split("&")) {
                int equalsPos = queryParameter.indexOf('=');
                String parameterName = queryParameter.substring(0, equalsPos);
                String parameterValue = queryParameter.substring(equalsPos+1);
                // URLDecoder for å decode ØÆÅs url converted tilbake til vanligskrift
                queryMap.put(parameterName, URLDecoder.decode(parameterValue, StandardCharsets.UTF_8));
            }
        }
        return queryMap;
    }


    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }


    static String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            buffer.append((char)socket.getInputStream().read());
        }
        return buffer.toString();
    }

    private void readHeaders(Socket socket) throws IOException {
        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos+1).trim();
            headerFields.put(headerField, headerValue);
        }
    }

    static String readLine(Socket socket) throws IOException {
        StringBuilder buffer = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            buffer.append((char)c);
        }
        int expectedNewline = socket.getInputStream().read();
        assert expectedNewline == '\n';
        return buffer.toString();
    }

    public void write(Socket socket) throws IOException {
        String response = startLine + "\r\n" +
                "Content-Length: " + messageBody.getBytes().length + "\r\n" +
                "Connection: close " + "\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(response.getBytes());

    }

    public void redirect(Socket socket, String location) throws IOException {
        String response = "HTTP/1.1 303 see other\r\n" +
                "Content-Length: " + messageBody.getBytes().length + "\r\n" +
                "Connection: close " + "\r\n" +
                "Location: " + location + "\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(response.getBytes());
    }

}

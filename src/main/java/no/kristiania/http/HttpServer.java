package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        try {
            while(true){
                handleClient();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleClient() throws IOException, SQLException {
        Socket clientSocket = serverSocket.accept();

        HttpMessage httpMessage = new HttpMessage(clientSocket);
        String[] requestLine = httpMessage.startLine.split(" ");
        String requestTarget = requestLine[1];

/*

        if (controllers.containsKey(requestTarget)){
            HttpMessage response = controllers.get(requestTarget).handle(httpMessage);
            response.write(clientSocket);
            return;
        }
 */


        if (rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1)))) {
            String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));

            String contentType = "text/plain";
            if (requestTarget.endsWith(".html")) {
                contentType = "text/html";
            } else if (requestTarget.endsWith(".css")){
                contentType = "text/css";
            }
            writeOKResponse(clientSocket, responseText, contentType);
            return;
        }

        String responseText = "File not found: " + requestTarget;

        String response = "HTTP/1.1 404 Not found\r\n" +
                "Content-Length: " + responseText.length() + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }




    private void writeOKResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close " + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }



    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(9080);
        httpServer.setRoot(Paths.get("src/main/resources"));
    }



    public void setRoot(Path path) {
        this.rootDirectory = path;
    }

    public int getActualPort() {
        return serverSocket.getLocalPort();
    }

    /*

    public void addController(String path, HttpController controller) {
        controllers.put(path, controller);

    }
     */





}

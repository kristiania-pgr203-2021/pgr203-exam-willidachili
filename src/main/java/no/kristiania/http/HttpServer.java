package no.kristiania.http;

import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class HttpServer {
    private final ServerSocket serverSocket;
    private Path rootDirectory;

    private final QuestionDao questionDao = new QuestionDao(createDataSource());



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
        //Will be replaced by controller
        if (requestTarget.equals("/api/newQuestions")) {

            Map<String, String> queryMap = parseRequestParameters(httpMessage.messageBody);
            Question question = new Question();
            question.setTitle(queryMap.get("title"));
            question.setText(queryMap.get("text"));

            questionDao.save(question);

            String responseText = "<script>alert(\"" + question.getTitle() + " har blitt lagt til i questions\"); window.location.href = \"/index.html\"</script>";

            writeOKResponse(clientSocket, responseText, "text/html");
        } else if (requestTarget.equals("/api/questions")){
            String responseText = "";

            for (Question question : questionDao.listAll()){
                responseText += "<h2>" + question.getTitle() + "</h2> <br> <p>" + question.getText() + "</p>";
            }

            writeOKResponse(clientSocket, responseText, "text/html");

        }


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



    private Map<String, String> parseRequestParameters(String query) {
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


    private void writeOKResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close " + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }


    private static DataSource createDataSource() throws IOException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("pgr203.properties")) {
            properties.load(reader);
        }


        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(properties.getProperty("dataSource.url", "jdbc:postgresql://localhost:5432/postgres"));
        dataSource.setUser(properties.getProperty("dataSource.user"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
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

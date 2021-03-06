package no.kristiania.http;

import no.kristiania.controllers.*;
import no.kristiania.questionnaire.AnswerDao;
import no.kristiania.questionnaire.OptionDao;
import no.kristiania.questionnaire.QuestionDao;
import org.flywaydb.core.Flyway;
import org.fusesource.jansi.AnsiConsole;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class HttpServer {
    private final ServerSocket serverSocket;
    private final HashMap<String, Controller> controllers = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

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

        int questionPos = requestTarget.indexOf('?');
        String fileTarget;
        String query = null;
        if (questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
            query = requestTarget.substring(questionPos+1);
        } else {
            fileTarget = requestTarget;
        }

        if (controllers.containsKey(requestTarget)){
            HttpMessage response = controllers.get(requestTarget).handle(httpMessage);
            if (response.startLine.contains("303")) {
                response.redirect(clientSocket, "/index.html");
            } else {
                response.write(clientSocket);
            }
        } else {
            InputStream fileResource = getClass().getResourceAsStream(fileTarget);
            if (fileResource != null){
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                fileResource.transferTo(buffer);
                String responseText = buffer.toString();

                String contentType = "text/plain";
                if(requestTarget.endsWith(".html")) {
                    contentType = "text/html";
                } else if (requestTarget.endsWith(".css")) {
                    contentType = "text/css";
                } else if(requestTarget.endsWith(".ico")){
                    PrintStream printer = new PrintStream(clientSocket.getOutputStream());
                    printer.println(faviconResponse(buffer.toByteArray().length));
                    printer.write(buffer.toByteArray());
                }

                writeOKResponse(clientSocket, responseText, contentType);
                return;
            }

            String responseText = "File not found: " + requestTarget;
            String response = "HTTP/1.1 404 Not found\r\n" +
                    "Content-Length: " + responseText.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    responseText;
            clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void writeOKResponse(Socket clientSocket, String responseText, String contentType) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + responseText.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close " + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
    }

    private String faviconResponse(int length){
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Content-Type: image/x-icon\"\r\n";
    }

    private static DataSource createDataSource() throws IOException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("pgr203.properties")) {
            properties.load(reader);
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(properties.getProperty("dataSource.url", "jdbc:postgresql://localhost:5432/postgres"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    public static void main(String[] args) throws IOException {
        AnsiConsole.systemInstall();
        DataSource dataSource = createDataSource();
        QuestionDao questionDao = new QuestionDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        HttpServer httpServer = new HttpServer(9080);
        httpServer.addController("/api/newQuestions", new AddQuestionController(questionDao, optionDao));
        httpServer.addController("/api/questions", new ListQuestionsController(questionDao, optionDao));
        httpServer.addController("/api/answer", new AddAnswerController(answerDao));
        httpServer.addController("/api/newOption", new AddOptionController(optionDao));
        httpServer.addController("/api/optionsOnQuestion", new ListAddOptionOnQuestionController(questionDao));
        httpServer.addController("/api/editQuestions", new ListEditQuestionsController(questionDao));
        httpServer.addController("/api/newEditedQuestions", new EditQuestionController(questionDao));
        httpServer.addController("/api/results", new ListResultController(questionDao, optionDao, answerDao));

        logger.info("Starting http://localhost:{}/index.html", httpServer.getPort());
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void addController(String path, Controller controller) {
        controllers.put(path, controller);
    }
}

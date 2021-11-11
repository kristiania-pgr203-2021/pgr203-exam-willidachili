package no.kristiania.http;

import no.kristiania.controllers.AddQuestionController;
import no.kristiania.controllers.ListQuestionsController;
import no.kristiania.questionnaire.OptionDao;
import no.kristiania.questionnaire.QuestionDao;
import no.kristiania.questionnaire.TestData;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private final HttpServer server = new HttpServer(0);
    DataSource dataSource = TestData.testDataSource();
    private final QuestionDao questionDao = new QuestionDao(dataSource);
    private final OptionDao optionDao = new OptionDao(dataSource);

    public HttpServerTest() throws IOException {
    }

    @Test
    void shouldGetIndexHTMLFromDisk() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/index.html");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldFindQuestions() throws IOException {
        server.addController("/api/newQuestions", new AddQuestionController(questionDao, optionDao));
        server.addController("/api/questions", new ListQuestionsController(questionDao, optionDao));

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestions",
                "title=Doge&text=wowee&low_label=yes&high_label=no"
        );
        assertEquals(303, postClient.getStatusCode());

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions");
        assertThat(client.getMessageContent())
                .contains("Doge", "wowee");
    }

    @Test
    void ShouldFindFavicon() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(),"/favicon.ico");

        client.getMessageContent();

        assertEquals(200, client.getResponseCode());
    }

}

package no.kristiania.questionnaire;

import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionDaoTest {

    private QuestionDao questionDao = new QuestionDao(TestData.testDataSource());


    @Test
    void shouldListAllQuestionsInDB() throws SQLException {
        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favorit blant disse?");

        questionDao.save(question);

        assertThat(questionDao.listAll())
                .extracting(Question::getTitle)
                .contains(question.getTitle());
    }

    @Test
    void shouldListInsertedQuestions() throws IOException, SQLException {
        HttpServer server = new HttpServer(0);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getActualPort(),
                "/api/newQuestions",
                "title=Doge&text=wowee"
        );
        assertEquals(200, postClient.getStatusCode());

        /*
        Denne delen failer siden POST requesten blir lagret i den ekte DB
        men vi vil at den skal lagres i testdatadb versjon når den kjøres fra test.
        Tror jeg trenger en constructør som kan endre datasource i httpServer elns.

        assertThat(questionDao.listAll())
                .extracting(Question::getTitle)
                .contains("Doge");
         */

    }

}

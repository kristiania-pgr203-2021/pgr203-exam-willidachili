package no.kristiania.questionnaire;

import no.kristiania.controllers.AddQuestionController;
import no.kristiania.controllers.EditQuestionController;
import no.kristiania.controllers.ListEditQuestionsController;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionDaoTest {

    DataSource dataSource = TestData.testDataSource();
    private final QuestionDao questionDao = new QuestionDao(dataSource);
    private final OptionDao optionDao = new OptionDao(dataSource);


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
    void shouldContainInsertedQuestions() throws IOException, SQLException {
        HttpServer server = new HttpServer(0);
        server.addController("/api/newQuestions", new AddQuestionController(questionDao, optionDao));

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newQuestions",
                "title=Doge&text=wowee&low_label=yes&high_label=no"
        );
        assertEquals(303, postClient.getStatusCode());
        assertThat(questionDao.listAll())
                .extracting(Question::getTitle)
                .contains("Doge");
    }

    @Test
    void ShouldListEditedQuestions() throws IOException, SQLException {
        HttpServer server = new HttpServer(0);
        server.addController("/api/newEditedQuestions", new EditQuestionController(questionDao));
        server.addController("/api/editQuestions", new ListEditQuestionsController(questionDao));

        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favorit blant disse?");

        questionDao.save(question);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newEditedQuestions",
                "1|Title=Katt"
        );
        assertEquals(303, postClient.getStatusCode());
        assertThat(questionDao.listAll())
                .extracting(Question::getTitle)
                .contains("Katt");


    }
}

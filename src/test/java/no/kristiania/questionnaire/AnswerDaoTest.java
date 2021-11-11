package no.kristiania.questionnaire;

import no.kristiania.controllers.AddAnswerController;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnswerDaoTest {

    HttpServer server = new HttpServer(0);
    DataSource dataSource = TestData.testDataSource();
    private final QuestionDao questionDao = new QuestionDao(dataSource);
    private final OptionDao optionDao = new OptionDao(dataSource);
    private final AnswerDao answerDao = new AnswerDao(dataSource);

    public AnswerDaoTest() throws IOException {
    }

    @Test
    void shouldReturnSavedAnswers() throws SQLException {
        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favoritt blant disse?");
        questionDao.save(question);

        Option option1 = new Option();
        option1.setLabel("Yes");
        option1.setQuestionId(question.getId());
        optionDao.save(option1);

        Answer answer = new Answer();
        answer.setOptionId(option1.getId());
        answerDao.save(answer);

        assertThat(answerDao.listAll())
                .extracting(Answer::getOptionId)
                .contains(option1.getId(), answer.getOptionId());
    }

    @Test
    void shouldListInsertedAnswers() throws IOException, SQLException {
        server.addController("/api/answer", new AddAnswerController(answerDao));

        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favoritt blant disse?");
        questionDao.save(question);

        Option option1 = new Option();
        option1.setLabel("Yes");
        option1.setQuestionId(question.getId());
        optionDao.save(option1);
        Option option2 = new Option();
        option2.setLabel("No");
        option2.setQuestionId(question.getId());
        optionDao.save(option2);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/answer",
                option1.getId() + "=on&" + option2.getId() + "=on"
        );
        assertEquals(303, postClient.getStatusCode());
        assertThat(answerDao.listAll())
                .extracting(Answer::getOptionId)
                .contains(option1.getId(), option2.getId());
    }
}

package no.kristiania.questionnaire;

import no.kristiania.controllers.AddAnswerController;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnswerDaoTest {

    HttpServer server = new HttpServer(0);
    DataSource dataSource = TestData.testDataSource();
    QuestionDao questionDao;
    OptionDao optionDao;
    AnswerDao answerDao;

    Question question;
    Option option;

    public AnswerDaoTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws IOException, SQLException {
        this.server = new HttpServer(0);
        this.questionDao = new QuestionDao(dataSource);
        this.optionDao = new OptionDao(dataSource);
        this.answerDao = new AnswerDao(dataSource);

        this.question = new Question();
        question.setTitle("Hund");
        question.setText("Din favoritt blant disse?");
        questionDao.save(question);

        this.option = new Option();
        option.setLabel("Yes");
        option.setQuestionId(question.getId());
        optionDao.save(option);
    }

    @Test
    void shouldReturnSavedAnswers() throws SQLException {

        Answer answer = new Answer();
        answer.setOptionId(option.getId());
        answerDao.save(answer);

        assertThat(answerDao.listAll())
                .extracting(Answer::getOptionId)
                .contains(option.getId(), answer.getOptionId());
    }

    @Test
    void shouldListInsertedAnswers() throws IOException, SQLException {
        server.addController("/api/answer", new AddAnswerController(answerDao));

        Option option2 = new Option();
        option2.setLabel("No");
        option2.setQuestionId(question.getId());
        optionDao.save(option2);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/answer",
                option.getId() + "=on&" + option2.getId() + "=on"
        );
        assertEquals(303, postClient.getStatusCode());
        assertThat(answerDao.listAll())
                .extracting(Answer::getOptionId)
                .contains(option.getId(), option2.getId());
    }

    @Test
    void shouldShowAmountOfTimeChoosen() throws SQLException {

        Answer answer = new Answer();
        answer.setOptionId(option.getId());
        answerDao.save(answer);

        Answer answer2 = new Answer();
        answer2.setOptionId(option.getId());
        answerDao.save(answer2);

        assertEquals(2, answerDao.numberOfTimesChosen(option.getId()));
    }
}

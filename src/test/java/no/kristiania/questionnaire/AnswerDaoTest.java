package no.kristiania.questionnaire;

import no.kristiania.controllers.AddAnswerController;
import no.kristiania.controllers.AddQuestionController;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnswerDaoTest {
    private final QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    private final OptionDao optionDao = new OptionDao(TestData.testDataSource());
    private final AnswerDao answerDao = new AnswerDao(TestData.testDataSource());

    @Test
    void shouldReturnSavedAnswers() throws SQLException {
        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favorit blant disse?");
        questionDao.save(question);

        Option option1 = new Option();
        option1.setLabel("Yes");
        option1.setQuestionId(1);
        optionDao.save(option1);

        Answer answer = new Answer();
        answer.setOptionId(option1.getQuestionId());
        answerDao.save(answer);


        assertThat(answerDao.listAll())
                .extracting(Answer::getOptionId)
                .contains(option1.getId(), answer.getOptionId());
    }

    @Test
    void shouldListInsertedAnswers() throws IOException, SQLException {
        HttpServer server = new HttpServer(0);
        server.addController("/api/answer", new AddAnswerController(answerDao));

        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favorit blant disse?");
        questionDao.save(question);

        Option option1 = new Option();
        option1.setLabel("Yes");
        option1.setQuestionId(1);
        optionDao.save(option1);
        Option option2 = new Option();
        option2.setLabel("No");
        option2.setQuestionId(1);
        optionDao.save(option2);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/answer",
                "1=on&2=on"
        );
        assertEquals(303, postClient.getStatusCode());
        assertThat(answerDao.listAll())
                .extracting(Answer::getOptionId)
                .contains(1L);
    }


}

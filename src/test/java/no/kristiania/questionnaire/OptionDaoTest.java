package no.kristiania.questionnaire;

import no.kristiania.controllers.AddOptionController;
import no.kristiania.http.HttpPostClient;
import no.kristiania.http.HttpServer;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionDaoTest {

    DataSource dataSource = TestData.testDataSource();
    private final QuestionDao questionDao = new QuestionDao(dataSource);
    private final OptionDao optionDao = new OptionDao(dataSource);

    @Test
    void shouldContainSavedOptions() throws SQLException {
        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favoritt blant disse?");

        questionDao.save(question);

        Option option1 = new Option();
        option1.setLabel("Yes");
        option1.setQuestionId(1);
        optionDao.save(option1);
        Option option2 = new Option();
        option2.setLabel("No");
        option2.setQuestionId(1);
        optionDao.save(option2);

        assertThat(optionDao.listAll(1L))
                .extracting(Option::getLabel)
                .contains(option1.getLabel(), option2.getLabel());
    }

    @Test
    void ShouldContainAddedOption() throws IOException, SQLException {
        HttpServer server = new HttpServer(0);
        server.addController("/api/newOption", new AddOptionController(optionDao));

        Question question = new Question();
        question.setTitle("Hund");
        question.setText("Din favoritt blant disse?");

        questionDao.save(question);

        HttpPostClient postClient = new HttpPostClient(
                "localhost",
                server.getPort(),
                "/api/newOption",
                "1=Goldenretriever"
        );
        assertEquals(303, postClient.getStatusCode());
        assertThat(optionDao.listAll(1L))
                .extracting(Option::getLabel)
                .contains("Goldenretriever");
    }
}

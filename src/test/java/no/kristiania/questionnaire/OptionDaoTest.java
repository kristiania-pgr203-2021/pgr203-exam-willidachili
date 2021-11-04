package no.kristiania.questionnaire;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionDaoTest {

    private final QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    private final OptionDao optionDao = new OptionDao(TestData.testDataSource());

    @Test
    void shouldReturnSavedOptions() throws SQLException {
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

        assertThat(optionDao.listAll(1L))
                .extracting(Option::getLabel)
                .contains(option1.getLabel(), option2.getLabel());
    }
}

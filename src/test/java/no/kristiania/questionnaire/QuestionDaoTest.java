package no.kristiania.questionnaire;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

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


}

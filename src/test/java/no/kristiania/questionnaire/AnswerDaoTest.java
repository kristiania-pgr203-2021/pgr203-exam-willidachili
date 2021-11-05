package no.kristiania.questionnaire;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

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
}

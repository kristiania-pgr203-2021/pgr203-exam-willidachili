package no.kristiania.controllers;

import no.kristiania.http.HttpClient;
import no.kristiania.http.HttpServer;
import no.kristiania.questionnaire.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListControllers {

    HttpServer server;
    QuestionDao questionDao;
    OptionDao optionDao;
    AnswerDao answerDao = new AnswerDao(TestData.testDataSource());

    Question question;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        this.server = new HttpServer(0);
        this.questionDao = new QuestionDao(TestData.testDataSource());
        this.optionDao = new OptionDao(TestData.testDataSource());

        this.question = new Question();
        question.setTitle("Dette er en test.");
        question.setText("Håper det blir grønt.");
        questionDao.save(question);
    }


    @Test
    void shouldWriteQuestionsPage() throws IOException {
        server.addController("/api/questions", new ListQuestionsController(questionDao, optionDao));

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/questions"
        );
        assertEquals(200, client.getStatusCode());

        assertThat(client.getMessageContent()
                .contains("<h2>" + question.getTitle() + "</h2> <p>" + question.getText() + "</p>"));
    }


    @Test
    void shouldWriteAddOptionOnQuestionsPage() throws IOException, SQLException {
        server.addController("/api/optionsOnQuestion", new ListAddOptionOnQuestionController(questionDao));

        Option option = new Option();
        option.setLabel("Denne teksten er en test");
        option.setQuestionId(1);
        optionDao.save(option);

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/optionsOnQuestion"
        );
        assertEquals(200, client.getStatusCode());

        assertThat(client.getMessageContent()
                .contains("<h2>" + question.getTitle() + "</h2> <p>" + question.getText() + "</p>"));
    }


    @Test
    void shouldWriteEditQuestionsPage() throws IOException {
        server.addController("/api/editQuestions", new ListEditQuestionsController(questionDao));

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/editQuestions"
        );
        assertEquals(200, client.getStatusCode());

        assertThat(client.getMessageContent()
                .contains("Current title:" + question.getTitle()));
    }

    @Test
    void shouldWriteResultPage() throws IOException, SQLException {
        server.addController("/api/results", new ListResultController(questionDao, optionDao, answerDao));

        Option option = new Option();
        option.setLabel("Denne teksten er en test");
        option.setQuestionId(1);
        optionDao.save(option);

        Answer answer = new Answer();
        answer.setOptionId(1);

        HttpClient client = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/results"
        );
        assertEquals(200, client.getStatusCode());

        assertThat(client.getMessageContent())
                .contains(option.getLabel() +" was picked this many times: "+ answerDao.numberOfTimesChosen(option.getId()));
    }
}

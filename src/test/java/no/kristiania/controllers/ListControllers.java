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
                .contains("Dette er en test"));
    }


    @Test
    void shouldWriteOptionsOnQuestions() throws IOException, SQLException {
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
                .contains("Denne teksten er en test"));
    }


    @Test
    void shouldWriteEditQuestionsPage() {
    }
}

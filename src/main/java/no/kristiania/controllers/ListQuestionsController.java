package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;

import java.sql.SQLException;

public class ListQuestionsController implements Controller{

    private final QuestionDao questionDao;

    public ListQuestionsController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "";

        for (Question question : questionDao.listAll()){
            responseText += "<h2>" + question.getTitle() + "</h2> <br> <p>" + question.getText() + "</p>";
        }

        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}

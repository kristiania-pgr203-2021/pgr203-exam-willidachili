package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;

import java.sql.SQLException;
import java.util.Map;

public class AddQuestionController implements Controller {

    private final QuestionDao questionDao;

    public AddQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        Question question = new Question();
        question.setTitle(queryMap.get("title"));
        question.setText(queryMap.get("text"));

        questionDao.save(question);


        HttpMessage response = new HttpMessage("HTTP/1.1 303 see other/r/nLocation: /index.html", "It is done");
        return response;
    }
}

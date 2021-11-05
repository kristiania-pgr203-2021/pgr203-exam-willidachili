package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.Option;
import no.kristiania.questionnaire.OptionDao;
import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;

import java.sql.SQLException;
import java.util.Map;

public class AddAnswerController implements Controller{

    private final QuestionDao questionDao;
    private final OptionDao optionDao;

    public AddAnswerController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);
        Question question = new Question();
        question.setTitle(queryMap.get("title"));
        question.setText(queryMap.get("text"));
        questionDao.save(question);

        Option lowLabel = new Option();
        Option highLabel = new Option();
        lowLabel.setLabel(queryMap.get("low_label"));
        lowLabel.setQuestionId(question.getId());
        highLabel.setLabel(queryMap.get("high_label"));
        highLabel.setQuestionId(question.getId());
        optionDao.save(lowLabel);
        optionDao.save(highLabel);

        HttpMessage response = new HttpMessage("HTTP/1.1 303 see other", "It is done");
        return response;
    }
}

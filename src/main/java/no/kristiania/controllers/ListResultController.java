package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.*;

import java.sql.SQLException;

public class ListResultController implements Controller{

    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    private final AnswerDao answerDao;

    public ListResultController(QuestionDao questionDao, OptionDao optionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "<div>";

        for (Question question : questionDao.listAll()){

            responseText += "<h2>" + question.getTitle() + "</h2> <p>" + question.getText() + "</p> \n";

            for (Option option : optionDao.listAll(question.getId())
            ) {

                responseText += option.getLabel() +" was picked this many times: "+ answerDao.numberOfTimesChosen(option.getId()) +". \n";
            }
            responseText += "</div>";
        }
        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}
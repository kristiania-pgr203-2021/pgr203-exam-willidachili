package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.Option;
import no.kristiania.questionnaire.OptionDao;
import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;

import java.sql.SQLException;

public class ListOptionToAddOnQuestionController implements Controller{

    private final QuestionDao questionDao;
    private final OptionDao optionDao;

    public ListOptionToAddOnQuestionController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "<div>";

        for (Question question : questionDao.listAll()){

            responseText += "<h2>" + question.getTitle() + "</h2> <p>" + question.getText() + "</p>"
                    + "<form method=\"POST\" action=\"/api/newOption\">\n <label> " ;


            responseText += "<div>New option:</div>  <input type=\"text\" id=\"newOptionName\" name=\""+ question.getId() +"\"> ";


            responseText += "</label><button>Submit</button>\n</form></div>";

        }



        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }

}

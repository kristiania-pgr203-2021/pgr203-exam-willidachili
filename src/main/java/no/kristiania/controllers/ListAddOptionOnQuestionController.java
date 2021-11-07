package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.Option;
import no.kristiania.questionnaire.OptionDao;
import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;

import java.sql.SQLException;

public class ListAddOptionOnQuestionController implements Controller{

    private final QuestionDao questionDao;

    public ListAddOptionOnQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "<div>";

        for (Question question : questionDao.listAll()){

            responseText += "<h2>" + question.getTitle() + "</h2> <p>" + question.getText() + "</p>"
                    + "<form method=\"POST\" action=\"/api/newOption\">\n <label> "
                    + "<div>New option:</div>  <input type=\"text\" id=\"newOptionName\" name=\""+ question.getId() +"\"> "
                    + "</label><button>Submit</button>\n</form></div>";

        }



        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }

}

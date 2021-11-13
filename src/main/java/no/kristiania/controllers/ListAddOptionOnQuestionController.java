package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
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
                    + "<form method=\"POST\" action=\"/api/newOption\">"
                    + "<label for=\"" + question.getId() + "\">New option: </label><input type=\"text\" id=\"" + question.getId() + "\" name=\"" + question.getId() + "\"> "
                    + "<button>Submit</button></form>";
        }
        responseText += "</div>";

        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}

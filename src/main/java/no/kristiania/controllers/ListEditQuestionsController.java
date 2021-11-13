package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;
import java.sql.SQLException;

public class ListEditQuestionsController implements Controller{

    private final QuestionDao questionDao;

    public ListEditQuestionsController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "<div>";

        for (Question question : questionDao.listAll()){

            responseText += "<h2>Current title: " + question.getTitle() + "</h2><p>Current text: " + question.getText() + "</p>"
                    + "<form method=\"POST\" action=\"/api/newEditedQuestions\">"
                    + "<label for=\"" + question.getId() + "xTitle" + "\">New title: </label><input type=\"text\" id=\"" + question.getId() + "xTitle\" name=\"" + question.getId() + "xTitle\">"
                    + "<button>Submit</button></form>"
                    + "<form method=\"POST\" action=\"/api/newEditedQuestions\">"
                    + "<label for=\"" + question.getId() +"xText" + "\">New text: </label><input type=\"text\" id=\"" + question.getId() + "xText\" name=\"" + question.getId() + "xText\"> "
                    + "<button>Submit</button></form>";
        }
        responseText += "</div>";

        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}

package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.Option;
import no.kristiania.questionnaire.OptionDao;
import no.kristiania.questionnaire.Question;
import no.kristiania.questionnaire.QuestionDao;
import java.sql.SQLException;

public class ListQuestionsController implements Controller{

    private final QuestionDao questionDao;
    private final OptionDao optionDao;

    public ListQuestionsController(QuestionDao questionDao, OptionDao optionDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        String responseText = "<div>";

        for (Question question : questionDao.listAll()){

            responseText += "<h2>" + question.getTitle() + "</h2> <p>" + question.getText() + "</p>"
                    + "<form method=\"POST\" action=\"/api/answer\">";

            for (Option option : optionDao.listAll(question.getId())
            ) {

                responseText += "<label for=\"" + option.getId() + "\">" + option.getLabel() +"</label>" +
                        "<input type=\"checkbox\" id=\"" + option.getId() + "\" name=\"" + option.getId() + "\" />";
            }
            responseText += "<button>Submit</button></form>";
        }
        responseText += "</div>";

        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}

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

            responseText += "<h2>Current title:" + question.getTitle() + "</h2> <p>Current text:" + question.getText() + "</p>"
                    + "<form method=\"POST\" action=\"/api/newEditedQuestions\">\n <label> "
                    + "<div>New title:</div>  <input type=\"text\" id=\"newTitleName\" name=\""+ question.getId() +"|Title\"> "
                    + "<div>New text:</div>  <input type=\"text\" id=\"newLabel\" name=\""+ question.getId() +"|Text\"> "
                    + "</label><button>Submit</button>\n</form></div>";

        }



        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }

}

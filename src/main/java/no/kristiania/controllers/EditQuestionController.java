package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.QuestionDao;

import java.sql.SQLException;
import java.util.Map;

public class EditQuestionController implements Controller{

    private final QuestionDao questionDao;

    public EditQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);

            for (String map: queryMap.keySet()) {

                int linePos = map.indexOf('x');

                String parameterQuestionId = map.substring(0, linePos);
                String parameterColumn = map.substring(linePos+1);

                if (parameterColumn.equals("Title")){
                    questionDao.updateTitle(Integer.valueOf(parameterQuestionId), queryMap.get(map));
                } else {
                    questionDao.updateText(Integer.valueOf(parameterQuestionId), queryMap.get(map));
                }
            }
        return new HttpMessage("HTTP/1.1 303 see other", "It is done");
    }
}

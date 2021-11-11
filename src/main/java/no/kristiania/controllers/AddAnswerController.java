package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.*;
import java.sql.SQLException;
import java.util.Map;

public class AddAnswerController implements Controller{

    private final AnswerDao answerDao;

    public AddAnswerController(AnswerDao answerDao) {

        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);

        //Henter Nøkkel siden hvis nøkkelen finnes så er det fordi
        //det svaralternativet ble valgt
        for (String map: queryMap.keySet()) {
            Answer answer = new Answer();
            answer.setOptionId(Long.parseLong(map));
            //FILLER FOR NÅR VI KAN HENTE BRUKER/COOKIE
            answer.setResponseId(420L);
            answerDao.save(answer);
        }
        return new HttpMessage("HTTP/1.1 303 see other", "It is done");
    }
}

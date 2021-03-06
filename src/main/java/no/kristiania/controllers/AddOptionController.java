package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import no.kristiania.questionnaire.*;
import java.sql.SQLException;
import java.util.Map;

public class AddOptionController implements Controller{

    private final OptionDao optionDao;

    public AddOptionController(OptionDao optionDao) {
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> queryMap = HttpMessage.parseRequestParameters(request.messageBody);

        for (String map: queryMap.keySet()) {
            Option option = new Option();
            option.setQuestionId(Long.parseLong(map));
            option.setLabel(queryMap.get(map));
            optionDao.save(option);
        }
        return new HttpMessage("HTTP/1.1 303 see other", "It is done");
    }
}

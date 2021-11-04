package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;

import java.sql.SQLException;

public interface Controller {
    HttpMessage handle(HttpMessage request) throws SQLException;
}

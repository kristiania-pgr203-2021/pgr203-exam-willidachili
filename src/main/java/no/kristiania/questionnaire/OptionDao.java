package no.kristiania.questionnaire;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDao {
    private final DataSource dataSource;

    public OptionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Option option) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into options (label, question_id) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, option.getLabel());
                statement.setLong(2, option.getQuestionId());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    option.setId(rs.getLong("option_id"));
                }
            }
        }
    }

    public List<Option> listAll(Long questionId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from options where question_id = ?")) {
                statement.setLong(1, questionId);
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Option> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(readFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }

    private Option readFromResultSet(ResultSet rs) throws SQLException {
        Option option = new Option();
        option.setId(rs.getLong("option_id"));
        option.setLabel(rs.getString("label"));
        option.setQuestionId(rs.getLong("question_id"));
        return option;
    }
}

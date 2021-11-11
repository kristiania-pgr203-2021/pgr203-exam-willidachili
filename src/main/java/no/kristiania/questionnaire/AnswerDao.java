package no.kristiania.questionnaire;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDao {
    private final DataSource dataSource;

    public AnswerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Answer answer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into answers (response_id, option_id) values (?, ?)"
            )) {
                statement.setLong(1, answer.getResponseId());
                statement.setLong(2, answer.getOptionId());
                statement.executeUpdate();
            }
        }
    }

    public List<Answer> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from answers"
            )) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Answer> result = new ArrayList<>();
                    while (rs.next()){
                        result.add(readFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }

    private Answer readFromResultSet(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setOptionId(rs.getLong("option_id"));
        answer.setResponseId(rs.getLong("response_id"));
        return answer;
    }
}

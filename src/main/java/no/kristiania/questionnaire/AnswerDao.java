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
                    "insert into answers (option_id) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setLong(1, answer.getOptionId());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    answer.setResponseId(rs.getLong("response_id"));
                }
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

    public Long numberOfTimesChosen(Long option_id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select count(*) as antallValgt from answers where option_id = (?)"
            )) {
                statement.setLong(1, option_id);

                try (ResultSet rs = statement.executeQuery()) {
                while (rs.next())
                    return readFromResultSet2(rs);
                }
                return 0L;
            }
        }
    }

    private Long readFromResultSet2(ResultSet rs) throws SQLException {
        return rs.getLong("antallValgt");
    }
}

package codesquad.global.dao;

import codesquad.common.db.connection.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlArticleQuery implements ArticleQuery {
    private ConnectionManager connectionManager;

    public MySqlArticleQuery(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Optional<ArticleResponse> findById(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();
            String sql = "SELECT a.id AS articleId, a.title, a.content, u.id AS writerId, u.user_id AS writer " +
                    "FROM articles a " +
                    "LEFT JOIN users u ON a.writer = u.user_id " +
                    "WHERE a.id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Long articleId = resultSet.getLong("articleId");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                Long writerId = resultSet.getLong("writerId");
                String writer = resultSet.getString("writer");
                return Optional.of(new ArticleResponse(articleId, title, content, writerId, writer));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionManager.close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<ArticleResponse> findAll(QueryRequest queryRequest) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionManager.getConnection();
            String sql = "SELECT a.id AS articleId, a.title, a.content, u.id AS writerId, u.user_id AS writer " +
                    "FROM articles a " +
                    "LEFT JOIN users u ON a.writer = u.user_id " +
                    "WHERE a.status = ? " +
                    "LIMIT ? OFFSET ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, queryRequest.getStatus().name());
            preparedStatement.setInt(2, queryRequest.getPageSize());
            preparedStatement.setInt(3, queryRequest.getOffset());
            resultSet = preparedStatement.executeQuery();
            List<ArticleResponse> articles = new ArrayList<>();
            while (resultSet.next()) {
                Long articleId = resultSet.getLong("articleId");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                Long writerId = resultSet.getLong("writerId");
                String writer = resultSet.getString("writer");
                if (writer == null) {
                    writer = "알수없는 사용자";
                }
                articles.add(new ArticleResponse(articleId, title, content, writerId, writer));
            }
            return articles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionManager.close(connection, preparedStatement, resultSet);
        }
    }
}
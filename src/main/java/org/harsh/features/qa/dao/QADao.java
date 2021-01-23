package org.harsh.features.qa.dao;

import org.harsh.domain.AuthorInfo;
import org.harsh.domain.QuestionDetails;
import org.harsh.utils.DBUtils;

import javax.ws.rs.WebApplicationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QADao {
    public QADao() {

    }

    public QuestionDetails postQuestion(QuestionDetails details, int userId) {
        String sql = "insert into questions(question) values('" + details.getQuestions() + "');";
        try (Connection dbConnection = DBUtils.getDBConnection();
             PreparedStatement statement = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int rows = statement.executeUpdate();
            if (rows == 1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    // insert this id and the user id to the authorinfo table
                    sql = "insert into authorinfo(authorid,questionid) values(" + userId + "," + id + ")";
                    System.out.println("Insert author details " + sql);
                    PreparedStatement statement1 = dbConnection.prepareStatement(sql);
                    int authrows = statement1.executeUpdate();
                    System.out.println("Num Rows: " + authrows);
                    details.setId(id);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return details;
    }

    public List<QuestionDetails> getQuestions() {
        String sql = "select q.id, q.question, q.upvotes, q.downvotes, q.answers, a.authorid, u.name from questions q" +
                " inner join authorinfo a on q.id=a.questionid" +
                " inner join users u on a.authorid=u.id;";
        System.out.println("Fetch Questions: " + sql);
        List<QuestionDetails> questions = new ArrayList<>();
        try (Connection dbConn = DBUtils.getDBConnection(); Statement statement = dbConn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                QuestionDetails details = new QuestionDetails();
                details.setId(rs.getInt("id"));
                details.setQuestions(rs.getString("question"));
                details.setNumUpVotes(rs.getLong("upvotes"));
                details.setNumDownVotes(rs.getLong("downvotes"));
                details.setNumAnswers(rs.getLong("answers"));
                AuthorInfo info = new AuthorInfo();
                info.setId(rs.getInt("authorid"));
                info.setName(rs.getString("name"));
                details.setAuthor(info);
                questions.add(details);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage());
        }
        return questions;
    }

    public QuestionDetails getQuestionById(int id) {
        String sql = "select q.id, q.question, q.upvotes, q.downvotes, q.answers, a.authorid, u.name from questions q" +
                " inner join authorinfo a on q.id=a.questionid" +
                " inner join users u on a.authorid=u.id where q.id = " + id + ";";
        System.out.println("Get QUESTION by id: " + sql);
        QuestionDetails details = null;
        try (Connection dbConn = DBUtils.getDBConnection(); Statement statement = dbConn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                details = new QuestionDetails();
                details.setId(rs.getInt("id"));
                details.setQuestions(rs.getString("question"));
                details.setNumUpVotes(rs.getLong("upvotes"));
                details.setNumDownVotes(rs.getLong("downvotes"));
                details.setNumAnswers(rs.getLong("answers"));
                AuthorInfo info = new AuthorInfo();
                info.setId(rs.getInt("authorid"));
                info.setName(rs.getString("name"));
                details.setAuthor(info);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage());
        }

        return details;
    }

    public List<QuestionDetails> getQuestionsByAuthor(int authorId) {
        String sql = "select q.id, q.question, q.upvotes, q.downvotes, q.answers, a.authorid, u.name from questions q" +
                " inner join authorinfo a on q.id=a.questionid" +
                " inner join users u on a.authorid=u.id where u.id=" + authorId + ";";
        System.out.println("Fetch Questions: " + sql);
        List<QuestionDetails> questions = new ArrayList<>();
        try (Connection dbConn = DBUtils.getDBConnection(); Statement statement = dbConn.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                QuestionDetails details = new QuestionDetails();
                details.setId(rs.getInt("id"));
                details.setQuestions(rs.getString("question"));
                details.setNumUpVotes(rs.getLong("upvotes"));
                details.setNumDownVotes(rs.getLong("downvotes"));
                details.setNumAnswers(rs.getLong("answers"));
                AuthorInfo info = new AuthorInfo();
                info.setId(rs.getInt("authorid"));
                info.setName(rs.getString("name"));
                details.setAuthor(info);
                questions.add(details);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage());
        }
        return questions;
    }

    public void updateUpVotes(int questionId) {
        String sql = "update questions set upvotes = upvotes + 1 where id=" + questionId + ";";
        System.out.println("Update upvotes sql : " + sql);
        try (Connection connection = DBUtils.getDBConnection(); Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(sql);
            System.out.println("No of rows updated: " + rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateDownVotes(int questionId) {
        String sql = "update questions set downvotes = downvotes + 1 where id=" + questionId + ";";
        System.out.println("Update down votes sql : " + sql);
        try (Connection connection = DBUtils.getDBConnection(); Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(sql);
            System.out.println("No of rows updated: " + rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

package org.harsh.features.daos;

import lombok.extern.slf4j.Slf4j;
import org.harsh.features.domain.Answer;
import org.harsh.features.domain.AuthorInfo;
import org.harsh.features.domain.QuestionDetails;
import org.harsh.features.domain.UserInfo;
import org.harsh.features.services.AuthService;
import org.harsh.utils.db.DBUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QADao {

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

    public QuestionDetails getQuestionById(long id) {
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

    public List<Answer> fetchAnswersForQuestion(long questionId, Integer start, Integer limit) {
        StringBuilder sql = new StringBuilder
                ("select answerId, answer, upvotes, downvotes, authorid from answers where questionid=" + questionId);
        if (start != null) {
            sql.append(" and answerId >= ").append(start);
        }

        if (limit != null && limit != 0) {
            sql.append(" limit ").append(limit);
        }
        log.debug("SQL to fetch answers: {}-{}", questionId, sql.toString());
        System.out.println("SQL to fetch Anwers for question id" + questionId + " :" + sql);
        List<Answer> answers = new ArrayList<>();
        try (Connection connection = DBUtils.getDBConnection(); Statement stmnt = connection.createStatement()) {
            ResultSet rs = stmnt.executeQuery(sql.toString());
            while (rs.next()) {
                Answer newAnswer = new Answer();
                newAnswer.setQuestionId(questionId);
                newAnswer.setAnswerId(rs.getInt("answerId"));
                newAnswer.setAnswer(rs.getString("answer"));
                newAnswer.setNumUpVotes(rs.getLong("upvotes"));
                newAnswer.setNumDownVotes(rs.getLong("downvotes"));
                AuthorInfo info = new AuthorInfo();
                info.setId(rs.getInt("authorid"));
                AuthService srvc = new AuthService();
                Response user = srvc.getUserById(info.getId());
                UserInfo userInfo = (UserInfo) user.getEntity();
                info.setName(userInfo.getName());
                answers.add(newAnswer);
            }

            return answers;
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), ex.getErrorCode());
        }
    }

    public Answer answerQuestion(Answer answer) {
        String sql = "insert into answers(answer, questionid,authorid) values ('" + answer.getAnswer() + "'," + answer.getQuestionId() + "," + answer.getAuthor().getId() + ");";
        System.out.println("SQL to insert answer: {}" + sql);
        try (Connection connection = DBUtils.getDBConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int rows = statement.executeUpdate();
            if (rows == 1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()){
                    int answerId = rs.getInt(1);
                    answer.setAnswerId(answerId);
                    answer.setNumUpVotes(0);
                    answer.setNumDownVotes(0);
                }else {
                    throw new WebApplicationException("Insert Failed");
                }
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), ex.getErrorCode());
        }

        return answer;
    }
}

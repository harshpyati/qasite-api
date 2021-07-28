package org.harsh.daos;

import lombok.extern.slf4j.Slf4j;
import org.harsh.domain.*;
import org.harsh.services.UserService;
import org.harsh.utils.ValidationUtils;
import org.harsh.utils.db.DBUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QADao {

    public QuestionDetails postQuestion(QuestionDetails details, long userId) {
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

    public List<QuestionDetails> getQuestions(String title, Long id, Integer authorId) {
        String sql = "select q.id, q.question, q.upvotes, q.downvotes, q.answers, a.authorid, u.name from questions q" +
                " inner join authorinfo a on q.id=a.questionid" +
                " inner join users u on a.authorid=u.id ";

        if (ValidationUtils.isNotNull(title) && !title.isEmpty()) {
            sql += " where to_tsvector('english',q.question) @@ to_tsquery('english','" + title + "')";
        }

        if (id != null){
            sql += " where q.id = " + id;
        }

        if (authorId != null){
            sql += " where u.id = " + authorId;
        }

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

    public QuestionDetails getQuestionById(Long id) {
        List<QuestionDetails> questions = getQuestions(null, id, null);
        if (questions == null || questions.isEmpty()){
            throw new WebApplicationException("Question doesn't exists", Response.Status.BAD_REQUEST);
        }
        return questions.get(0);
    }

    public List<QuestionDetails> getQuestionsByAuthor(int authorId) {
        List<QuestionDetails> questionDetails = getQuestions(null, null, authorId);
        if (questionDetails == null || questionDetails.isEmpty()){
            throw new WebApplicationException("This author hasn't asked any questions", Response.Status.BAD_REQUEST);
        }

        return questionDetails;
    }

    public List<Answer> getAnswersByAuthorId(int authorId) {
        List<Answer> answersByAuthor = fetchAnswersForQuestion(null, null, null, null, authorId);
        if (answersByAuthor == null || answersByAuthor.isEmpty()) {
            throw new WebApplicationException("This author hasn't written any answer", Response.Status.BAD_REQUEST);
        }
        return answersByAuthor;
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

    public void updateNumAnswers(long questionId) {
        String sql = "update questions set answers = answers + 1 where id=" + questionId + ";";
        try (Connection connection = DBUtils.getDBConnection(); Statement stmnt = connection.createStatement()) {
            int rows = stmnt.executeUpdate(sql);
            System.out.println("No of rows updated : " + rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Answer> fetchAnswersForQuestion(Long questionId, Integer start, Integer limit, Long answerId, Integer authorId) {
        StringBuilder fetchAnswersSql = new StringBuilder("select a.answerId, a.answer, a.upvotes, a.downvotes, u.id, u.name, q.id, q.question" +
                " from answers as a inner join users u on a.authorid = u.id" +
                " inner join questions q on a.questionid = q.id");

        if (questionId != null) {
            fetchAnswersSql.append(" where a.questionid = ").append(questionId);
        }

        if (authorId != null) {
            fetchAnswersSql.append(" where a.authorid = ").append(authorId);
        }

        if (start != null && start != 0) {
            fetchAnswersSql.append(" and answerId >= ").append(start);
        }

        if (answerId != null) {
            fetchAnswersSql.append(" and a.answerId = ").append(answerId);
        }

        if (limit != null && limit != 0) {
            fetchAnswersSql.append(" limit ").append(limit);
        }
        log.debug("SQL to fetch answers: {}-{}", questionId, fetchAnswersSql.toString());
        System.out.println("SQL to fetch Anwers for question id" + questionId + " :" + fetchAnswersSql);
        List<Answer> answers = new ArrayList<>();
        try (Connection connection = DBUtils.getDBConnection(); Statement stmnt = connection.createStatement()) {
            ResultSet rs = stmnt.executeQuery(fetchAnswersSql.toString());
            while (rs.next()) {
                Answer newAnswer = new Answer();

                newAnswer.setAnswerId(rs.getLong("answerId"));
                newAnswer.setAnswer(rs.getString("answer"));
                newAnswer.setNumUpVotes(rs.getLong("upvotes"));
                newAnswer.setNumDownVotes(rs.getLong("downvotes"));

                EntityRef user = new EntityRef();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                newAnswer.setAuthor(user);

                EntityRef question = new EntityRef();
                question.setId(rs.getInt("id"));
                question.setName(rs.getString("question"));
                newAnswer.setQuestion(question);

                answers.add(newAnswer);
            }

            return answers;
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), ex.getErrorCode());
        }
    }

    public Answer fetchAnswerById(long questionId, long answerId) {
        List<Answer> answers = fetchAnswersForQuestion(questionId, null, null, answerId, null);
        if (answers == null || answers.isEmpty()) {
            throw new WebApplicationException("Invalid answer id", Response.Status.BAD_REQUEST);
        }
        return answers.get(0);
    }

    public Answer answerQuestion(Answer answer) {
        String sql = "insert into answers(answer, questionid,authorid) values ('" + answer.getAnswer() + "'," + answer.getQuestion().getId() + "," + answer.getAuthor().getId() + ");";
        System.out.println("SQL to insert answer: {}" + sql);
        try (Connection connection = DBUtils.getDBConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int rows = statement.executeUpdate();
            if (rows == 1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int answerId = rs.getInt(1);
                    answer.setAnswerId(answerId);
                    answer.setNumUpVotes(0);
                    answer.setNumDownVotes(0);
                } else {
                    throw new WebApplicationException("Insert Failed");
                }
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), ex.getErrorCode());
        }
        // update num answers for the question by 1
        updateNumAnswers(answer.getQuestion().getId());
        return answer;
    }
}

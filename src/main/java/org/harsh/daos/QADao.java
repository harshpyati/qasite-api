package org.harsh.daos;

import lombok.extern.slf4j.Slf4j;
import org.harsh.domain.*;
import org.harsh.utils.ValidationUtils;
import org.harsh.utils.db.DBUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QADao extends CommonDao {

    public QuestionDetails postQuestion(QuestionDetails details, long userId) {
        String sql = "insert into questions(question) values('" + details.getQuestions() + "');";
        Long id = executeUpdateAndReturnId(sql, IdType.INTEGER);
        if (id != null) {
            sql = "insert into authorinfo(authorid,questionid) values(" + userId + "," + id + ")";
            executeUpdate(sql);
            details.setId(id.intValue());
            return details;
        }
        throw new WebApplicationException("failed to insert", Response.Status.INTERNAL_SERVER_ERROR);
    }

    public List<QuestionDetails> getQuestions(String title, Long id, Integer authorId) {
        String sql = "select q.id, q.question, q.upvotes, q.downvotes, q.answers, a.authorid, u.name from questions q" +
                " inner join authorinfo a on q.id=a.questionid" +
                " inner join users u on a.authorid=u.id ";

        if (ValidationUtils.isNotNull(title) && !title.isEmpty()) {
            sql += " where to_tsvector('english',q.question) @@ to_tsquery('english','" + title + "')";
        }

        if (id != null) {
            sql += " where q.id = " + id;
        }

        if (authorId != null) {
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
        if (questions == null || questions.isEmpty()) {
            throw new WebApplicationException("Question doesn't exists", Response.Status.BAD_REQUEST);
        }
        return questions.get(0);
    }

    public List<QuestionDetails> getQuestionsByAuthor(int authorId) {
        List<QuestionDetails> questionDetails = getQuestions(null, null, authorId);
        if (questionDetails == null || questionDetails.isEmpty()) {
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

    private boolean checkIfEntryExistsInQuestionCount(Long questionId, Long userId, VoteDirection voteDirection) {
        String countSql = "select count(userid) from question_counts where questionid = " + questionId + " and userid = " + userId + " and direction = " + voteDirection.getVal();
        return getCount(countSql) == 1;
    }

    private boolean checkIfEntryExistsInAnswerCount(Long questionId, Long answerId, Long userId, VoteDirection voteDirection) {
        String countSql = "select count(userid) from answer_vote_counts where questionid = " + questionId + " and userid = " + userId + " and answerid = " + answerId + " and direction = " + voteDirection.getVal();
        return getCount(countSql) == 1;
    }

    private int getCount(String sql) {
        int count = -1;
        try (Connection con = DBUtils.getDBConnection(); Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            throw new WebApplicationException("failed to fetch count", Response.Status.INTERNAL_SERVER_ERROR);
        }

        return count;
    }

    public void updateUpVotesForAnswers(Long questionId, Long answerId, Long userId) {
        boolean entryExists = checkIfEntryExistsInAnswerCount(questionId, answerId, userId, VoteDirection.UP);
        if (!entryExists) {
            String insertToMapperSql = "insert into answer_vote_counts(questionid, answerid, userid, direction) values(" + questionId + "," + answerId + "," + userId + "," + VoteDirection.UP.getVal() + ");";
            executeUpdate(insertToMapperSql);

            String insertToAnswersSql = "update answers set upvotes = upvotes + 1 where questionid=" + questionId + " and answerid=" + answerId;
            executeUpdate(insertToAnswersSql);
        }
    }

    public void updateDownVotesForAnswers(Long questionId, Long answerId, Long userId) {
        boolean entryExists = checkIfEntryExistsInAnswerCount(questionId, answerId, userId, VoteDirection.DOWN);
        if (!entryExists) {
            String insertToMapperSql = "insert into answer_vote_counts(questionid, answerid, userid, direction) values(" + questionId + "," + answerId + "," + userId + "," + VoteDirection.DOWN.getVal() + ");";
            executeUpdate(insertToMapperSql);

            String insertToAnswersSql = "update answers set downvotes = downvotes + 1 where questionid=" + questionId + " and answerid=" + answerId;
            executeUpdate(insertToAnswersSql);
        }
    }

    public void updateUpVotes(Long questionId, Long userId) {
        boolean entryExists = checkIfEntryExistsInQuestionCount(questionId, userId, VoteDirection.UP);
        if (!entryExists) {
            String insertToMapperSql = "insert into question_counts(questionid,userid,direction) values(" + questionId + "," + userId + "," + VoteDirection.UP.getVal() + ");";
            executeUpdate(insertToMapperSql);
            String insertToQuestionsSql = "update questions set upvotes = upvotes + 1 where id=" + questionId;
            executeUpdate(insertToQuestionsSql);
        }
    }

    public void updateDownVotes(Long questionId, Long userId) {
        // check if entry is in questions_count
        // if yes, remove it from there
        // update downvote count for the questions
        boolean entryExists = checkIfEntryExistsInQuestionCount(questionId, userId, VoteDirection.DOWN);
        if (!entryExists) {
            String insertToMapperSql = "insert into question_counts(questionid, userid, direction) values(" + questionId + "," + userId + "," + VoteDirection.DOWN.getVal() + ");";
            executeUpdate(insertToMapperSql);

            String insertToQuestions = "update questions set downvotes=downvotes+1 where id=" + questionId;
            executeUpdate(insertToQuestions);
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
        log.debug("SQL to fetch answers: {}-{}", questionId, fetchAnswersSql);
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
        Long id = executeUpdateAndReturnId(sql, IdType.LONG);
        if (id != null) {
            answer.setAnswerId(id);
            answer.setNumUpVotes(0);
            answer.setNumDownVotes(0);

            sql = "update questions set answers = answers + 1 where id=" + answer.getQuestion().getId() + ";";
            executeUpdate(sql);
            return answer;
        }
        throw new WebApplicationException("failed to answer", Response.Status.INTERNAL_SERVER_ERROR);
    }

    public void deleteAnswer(long questionId, long answerId, long userId) {
        // only the author can delete the answer
        Answer answerToBeDeleted = fetchAnswerById(questionId, answerId);
        if (userId == answerToBeDeleted.getAuthor().getId()) {
            String sql = "delete from answers where questionId = " + questionId + " and answerId = " + answerId + ";";
            executeUpdate(sql);

            // update answer count for the question
            sql = "update questions set answers = answers - 1 where id=" + questionId;
            executeUpdate(sql);
        } else {
            throw new WebApplicationException("you don't have the permissions to delete this answer", Response.Status.BAD_REQUEST);
        }
    }
}

package org.harsh.features.services;

import lombok.extern.slf4j.Slf4j;
import org.harsh.features.domain.Answer;
import org.harsh.features.domain.AuthorInfo;
import org.harsh.features.domain.QuestionDetails;
import org.harsh.features.domain.UserInfo;
import org.harsh.features.daos.QADao;
import org.harsh.utils.db.DBUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
public class QAService {
    QADao dao;

    public QAService() {
        dao = new QADao();
    }

    public void validateQuestion(QuestionDetails details) throws WebApplicationException {
        if (details.getQuestions() == null) {
            throw new WebApplicationException("Enter a valid Question", Response.Status.BAD_REQUEST);
        }
    }

    public QuestionDetails addQuestion(QuestionDetails details, String accessToken) {
        try {
            validateQuestion(details);
            details.setNumAnswers(0);
            details.setNumDownVotes(0);
            details.setNumUpVotes(0);
            UserInfo userInfo = DBUtils.getUserDetails(accessToken);
            return dao.postQuestion(details, userInfo.getId());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    public List<QuestionDetails> getQuestions() {
        try {
            return dao.getQuestions();
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public QuestionDetails getQuestionById(long id) {
        try {
            QuestionDetails details = dao.getQuestionById(id);
            if (details == null) {
                throw new Exception("Question with id: " + id + " not found");
            }
            return details;
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public List<QuestionDetails> getQuestionsByAuthor(int authorId) {
        try {
            return dao.getQuestionsByAuthor(authorId);
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public void updateUpVotes(int questionId) {
        try {
            // if already upvoted, then stay as it is
            // else upvote
            // use stored proc here
            dao.updateUpVotes(questionId);
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public void updateDownVotes(int questionId) {
        try {
            dao.updateDownVotes(questionId);
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public List<Answer> getAnswers(long questionId, int start, int limit) {
        try {
            return dao.fetchAnswersForQuestion(questionId, start, limit);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Answer answerQuestion(long questionId, Answer answer, String accessToken) {
        try {
            QuestionDetails question = getQuestionById(questionId);
            if (question == null) {
                throw new WebApplicationException("Question with Id = " + questionId + " doesn't exists", Response.Status.BAD_REQUEST);
            }
            answer.setQuestionId(questionId);
            UserInfo user = DBUtils.getUserDetails(accessToken);
            AuthorInfo info = new AuthorInfo();
            info.setId(user.getId());
            answer.setAuthor(info);
            return dao.answerQuestion(answer);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public void deleteAnswer(long questionId, long answerId) {
        String sql = "delete from answers where questionId = " + questionId + " and answerId = " + answerId + ";";
        try (Connection conn = DBUtils.getDBConnection(); Statement stmnt = conn.createStatement()) {
            int rows = stmnt.executeUpdate(sql);
            log.debug("Rows: {}",rows);
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }
}

package org.harsh.services;

import lombok.extern.slf4j.Slf4j;
import org.harsh.domain.Answer;
import org.harsh.domain.AuthorInfo;
import org.harsh.domain.QuestionDetails;
import org.harsh.domain.UserInfo;
import org.harsh.daos.QADao;
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

    public Response addQuestion(QuestionDetails details, String accessToken) {
        try {
            validateQuestion(details);
            details.setNumAnswers(0);
            details.setNumDownVotes(0);
            details.setNumUpVotes(0);
            UserInfo userInfo = DBUtils.getUserDetails(accessToken);
            QuestionDetails postedQuestion = dao.postQuestion(details, userInfo.getId());
            return Response.ok().entity(postedQuestion).build();
        } catch (Exception exception) {
            throw new WebApplicationException("Failed to add the question", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getQuestions(String title) {
        try {
            List<QuestionDetails> questions = dao.getQuestions(title);
            return Response.ok().entity(questions).build();
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response getQuestionById(long id) {
        try {
            QuestionDetails details = dao.getQuestionById(id);
            if (details == null) {
                throw new Exception("Question with id: " + id + " not found");
            }
            return Response.ok().entity(details).build();
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response getQuestionsByAuthor(int authorId) {
        try {
            List<QuestionDetails> details = dao.getQuestionsByAuthor(authorId);
            return Response.ok().entity(details).build();
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

    public Response getAnswers(long questionId, int start, int limit) {
        try {
            List<Answer> answers = dao.fetchAnswersForQuestion(questionId, start, limit);
            return Response.ok().entity(answers).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response getAnswerById(long questionId,long answerId) {
        try {
            Answer answer = dao.fetchAnswerById(questionId, answerId);
            return Response.ok().entity(answer).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response answerQuestion(long questionId, Answer answer, String accessToken) {
        try {
            QuestionDetails question = getQuestionById(questionId).readEntity(QuestionDetails.class);
            if (question == null) {
                throw new WebApplicationException("Question with Id = " + questionId + " doesn't exists", Response.Status.BAD_REQUEST);
            }
            answer.setQuestionId(questionId);
            UserInfo user = DBUtils.getUserDetails(accessToken);
            AuthorInfo info = new AuthorInfo();
            info.setId(user.getId());
            answer.setAuthor(info);
            Answer postedAnswer = dao.answerQuestion(answer);
            return Response.ok().entity(postedAnswer).build();
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

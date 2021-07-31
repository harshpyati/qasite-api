package org.harsh.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.harsh.domain.*;
import org.harsh.daos.QADao;
import org.harsh.utils.db.DBUtils;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
public class QAService {

    @Inject
    QADao dao;

    public void validateQuestion(QuestionDetails details) throws WebApplicationException {
        if (details.getQuestions() == null) {
            throw new WebApplicationException("Enter a valid Question", Response.Status.BAD_REQUEST);
        }
    }

    public Response addQuestion(QuestionDetails details, String accessToken) {
        try {
            validateQuestion(details);
            UserInfo userInfo = DBUtils.getUserDetails(accessToken);
            QuestionDetails postedQuestion = dao.postQuestion(details, userInfo.getId());
            return Response.ok().entity(postedQuestion).build();
        } catch (Exception exception) {
            throw new WebApplicationException("Failed to add the question", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response getQuestions(String title) {
        try {
            List<QuestionDetails> questions = dao.getQuestions(title, null, null);
            return Response.ok().entity(questions).build();
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response getQuestionById(long id) {
        try {
            QuestionDetails details = dao.getQuestionById(id);
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
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response getAnswersByAuthorId(int authorId) {
        try {
            List<Answer> details = dao.getAnswersByAuthorId(authorId);
            return Response.ok().entity(details).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public void updateUpVotes(Long questionId, String accessToken) {
        try {
            // if already upvoted, then stay as it is
            // else upvote
            // use stored proc here
            UserInfo user = DBUtils.getUserDetails(accessToken);
            dao.updateUpVotes(questionId, user.getId());
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public void updateDownVotes(Long questionId, String accessToken) {
        try {
            UserInfo user = DBUtils.getUserDetails(accessToken);
            dao.updateDownVotes(questionId, user.getId());
        } catch (Exception ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response getAnswers(long questionId, Integer start, Integer limit) {
        try {
            List<Answer> answers = dao.fetchAnswersForQuestion(questionId, start, limit, null, null);
            return Response.ok().entity(answers).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public Response getAnswerById(long questionId, long answerId) {
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
            QuestionDetails question = ((QuestionDetails) getQuestionById(questionId).getEntity());
            if (question == null) {
                throw new WebApplicationException("Question with Id = " + questionId + " doesn't exists", Response.Status.BAD_REQUEST);
            }
            answer.setQuestion(new EntityRef(questionId, ""));
            UserInfo user = DBUtils.getUserDetails(accessToken);
            EntityRef info = new EntityRef();
            info.setId(user.getId());
            answer.setAuthor(info);
            Answer postedAnswer = dao.answerQuestion(answer);
            return Response.ok().entity(postedAnswer).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public void deleteAnswer(long questionId, long answerId, String accessToken) {
        try {
            UserInfo user = DBUtils.getUserDetails(accessToken);
            dao.deleteAnswer(questionId, answerId, user.getId());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateUpVotesForAnswers(String accessToken, Long questionId, Long answerId) {
        try {
            UserInfo user = DBUtils.getUserDetails(accessToken);
            dao.updateUpVotesForAnswers(questionId, answerId, user.getId());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    public void updateDownVotesForAnswers(String accessToken, Long questionId, Long answerId) {
        try {
            UserInfo user = DBUtils.getUserDetails(accessToken);
            dao.updateDownVotesForAnswers(questionId, answerId, user.getId());
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.BAD_REQUEST);
        }
    }
}

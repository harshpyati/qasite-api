package org.harsh.features.qa.service;

import org.harsh.domain.QuestionDetails;
import org.harsh.domain.UserInfo;
import org.harsh.features.qa.dao.QADao;
import org.harsh.utils.DBUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

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

    public QuestionDetails getQuestionById(int id) {
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
}

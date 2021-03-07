package org.harsh.features.qa.resource;

import lombok.extern.slf4j.Slf4j;
import org.harsh.domain.Answer;
import org.harsh.domain.QuestionDetails;
import org.harsh.filters.annotations.Secured;
import org.harsh.features.qa.service.QAService;
import org.harsh.utils.DBUtils;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Slf4j
@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {
    QAService service = new QAService();

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    public QuestionDetails addQuestion(@Context ContainerRequestContext ctxt, QuestionDetails details) {
        String accessToken = DBUtils.getAccessToken(ctxt);
        return service.addQuestion(details, accessToken);
    }

    @GET
    @Secured
    public List<QuestionDetails> getQuestions() {
        log.debug("Fetching Questions:");
        List<QuestionDetails> details = service.getQuestions();
        log.debug("Details: {}", details);

        return details;
    }

    @GET
    @Secured
    @Path("/{id}")
    public QuestionDetails getQuestionById(@PathParam("id") long id) {
        return service.getQuestionById(id);
    }

    @GET
    @Secured
    @Path("/author/{authorId}")
    public List<QuestionDetails> getQuestionsByAuthorId(@PathParam("authorId") int authorId) {
        return service.getQuestionsByAuthor(authorId);
    }

    @PUT
    @Path("/{id}/upvote")
    @Secured
    public QuestionDetails updateUpVotes(@Context ContainerRequestContext ctxt, @PathParam("id") int questionId) {
        service.updateUpVotes(questionId);
        return getQuestionById(questionId);
    }

    @PUT
    @Path("/{id}/downvote")
    @Secured
    public QuestionDetails updateDownVotes(@Context ContainerRequestContext ctxt, @PathParam("id") int questionId) {
        service.updateDownVotes(questionId);
        return getQuestionById(questionId);
    }

    @GET
    @Path("/{questionId}/answers")
    public List<Answer> getAnswers(@PathParam("questionId") long questionId, @QueryParam("start") int start, @QueryParam("limit") int limit) {
        QAService qaService = new QAService();
        return qaService.getAnswers(questionId, start, limit);
    }

    @POST
    @Path("/{questionId}/answer")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Answer postAnswer(@Context ContainerRequestContext context, @PathParam("questionId") long questionId, Answer answer) {
        QAService qaService = new QAService();
        String accessToken = DBUtils.getAccessToken(context);
        return qaService.answerQuestion(questionId, answer, accessToken);
    }

    @DELETE
    @Path("{questionId}/answer/{answerId}")
    @Secured
    public Response deleteAnswer(@Context ContainerRequestContext context,
                                 @PathParam("questionId") long questionId,
                                 @PathParam("answerId") long answerId) {
        QAService qaService = new QAService();
        QuestionDetails questionDetails = getQuestionById(questionId);
        qaService.deleteAnswer(questionId, answerId);
        return Response.noContent().build();
    }
}

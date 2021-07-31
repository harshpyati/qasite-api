package org.harsh.resources;

import lombok.extern.slf4j.Slf4j;
import org.harsh.domain.Answer;
import org.harsh.domain.QuestionDetails;
import org.harsh.domain.VoteDirection;
import org.harsh.filters.annotations.Secured;
import org.harsh.services.QAService;
import org.harsh.utils.db.DBUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
public class QAResource {

    @Inject
    QAService service;

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addQuestion(@Context ContainerRequestContext ctxt, QuestionDetails details) {
        String accessToken = DBUtils.getAccessToken(ctxt);
        return service.addQuestion(details, accessToken);
    }

    @GET
    @Secured
    public Response getQuestions(@QueryParam("title") String title) {
        return service.getQuestions(title != null ? title : "");
    }

    @GET
    @Secured
    @Path("/{id}")
    public Response getQuestionById(@PathParam("id") long id) {
        return service.getQuestionById(id);
    }

    @GET
    @Secured
    @Path("/author/{authorId}")
    public Response getQuestionsByAuthorId(@PathParam("authorId") int authorId) {
        return service.getQuestionsByAuthor(authorId);
    }

    @GET
    @Secured
    @Path("/answer/author/{authorId}")
    public Response getAnswersByAuthorId(@PathParam("authorId") int authorId) {
        return service.getAnswersByAuthorId(authorId);
    }

    @PATCH
    @Path("/{id}/vote")
    @Secured
    public void updateUpVotes(
            @Context ContainerRequestContext ctxt,
            @PathParam("id") Long questionId,
            @QueryParam("direction") int direction) {
        String accessToken = DBUtils.getAccessToken(ctxt);
        if (VoteDirection.getByVal(direction).equals(VoteDirection.UP)) {
            service.updateUpVotes(questionId, accessToken);
        } else {
            service.updateDownVotes(questionId, accessToken);
        }
    }

    @PATCH
    @Path("/{id}/answer/{answerId}/vote")
    @Secured
    public void updateVotesForAnswers(@Context ContainerRequestContext ctxt,
                                      @PathParam("id") Long questionId,
                                      @PathParam("answerId") Long answerId,
                                      @QueryParam("direction") int voteDirection
    ) {
        String accessToken = DBUtils.getAccessToken(ctxt);
        try{
            if (VoteDirection.getByVal(voteDirection).equals(VoteDirection.UP)) {
                service.updateUpVotesForAnswers(accessToken, questionId, answerId);
            } else {
                service.updateDownVotesForAnswers(accessToken, questionId, answerId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @GET
    @Path("/{questionId}/answers")
    public Response getAnswers(@PathParam("questionId") long questionId, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit) {
        return service.getAnswers(questionId, start, limit);
    }

    @GET
    @Path("/{questionId}/answers/{answerId}")
    public Response getAnswerById(@PathParam("questionId") long questionId, @PathParam("answerId") long answerId) {
        return service.getAnswerById(questionId, answerId);
    }

    @POST
    @Path("/{questionId}/answer")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postAnswer(@Context ContainerRequestContext context, @PathParam("questionId") long questionId, Answer answer) {
        String accessToken = DBUtils.getAccessToken(context);
        return service.answerQuestion(questionId, answer, accessToken);
    }

    @DELETE
    @Path("{questionId}/answer/{answerId}")
    @Secured
    public Response deleteAnswer(@Context ContainerRequestContext context,
                                 @PathParam("questionId") long questionId,
                                 @PathParam("answerId") long answerId) {
        QuestionDetails questionDetails = getQuestionById(questionId).readEntity(QuestionDetails.class);
        if (questionDetails == null) {

        }
        service.deleteAnswer(questionId, answerId);
        return Response.noContent().build();
    }
}

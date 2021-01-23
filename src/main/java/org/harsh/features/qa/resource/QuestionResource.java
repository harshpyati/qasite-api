package org.harsh.features.qa.resource;

import org.harsh.domain.QuestionDetails;
import org.harsh.filters.annotations.Secured;
import org.harsh.features.qa.service.QAService;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {
    QAService service = new QAService();

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    public QuestionDetails addQuestion(@Context ContainerRequestContext ctxt, QuestionDetails details) {
        String accessToken = getAccessToken(ctxt);
        return service.addQuestion(details, accessToken);
    }

    @GET
    @Secured
    public List<QuestionDetails> getQuestions() {
        return service.getQuestions();
    }

    @GET
    @Secured
    @Path("/{id}")
    public QuestionDetails getQuestionById(@PathParam("id") int id) {
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

    private String getAccessToken(ContainerRequestContext context) {
        return context.getHeaderString(HttpHeaders.AUTHORIZATION).substring("Bearer".length()).trim();
    }
}

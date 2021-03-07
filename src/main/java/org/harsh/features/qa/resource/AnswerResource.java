package org.harsh.features.qa.resource;
//
//import lombok.extern.slf4j.Slf4j;
//import org.harsh.domain.Answer;
//import org.harsh.features.qa.service.QAService;
//import org.harsh.filters.annotations.Secured;
//import org.harsh.utils.DBUtils;
//
//import javax.ws.rs.*;
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.core.Context;
//import java.util.List;
//
//@Slf4j
//@Path("/answer")
//public class AnswerResource {
//    @GET
//    @Path("/{questionId}")
//    public List<Answer> getAnswers(@PathParam("questionId") long questionId, @QueryParam("start") int start, @QueryParam("limit") int limit) {
//        QAService qaService = new QAService();
//        return qaService.getAnswers(questionId, start, limit);
//    }
//
//    @POST
//    @Secured
//    @Path("/{questionId}")
//    public Answer postAnswer(@Context ContainerRequestContext context, @PathParam("questionId") long questionId, Answer answer) {
//        QAService qaService = new QAService();
//        String accessToken = DBUtils.getAccessToken(context);
//        return qaService.answerQuestion(questionId, answer, accessToken);
//    }
//}

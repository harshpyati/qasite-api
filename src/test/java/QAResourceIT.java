import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.internal.HttpUrlConnector;
import org.harsh.domain.*;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class QAResourceIT {
    final String BASE_URL = "http://localhost:8080/qasite/api";
    WebTarget target;
    UserInfo userInfo;
    Client client = ClientBuilder.newClient();
    final String BASE_URL_FOR_QA = "/question";
    QuestionDetails question;
    Answer answerDetails;

    @BeforeTest
    public void loginUser() {
        target = client.target(BASE_URL);
        LoginBody credentials = getLoginCredentials();
        Response loginResponse = target.path("/user/login").request().post(Entity.json(credentials));
        assert loginResponse.getStatus() == 200;
        userInfo = loginResponse.readEntity(UserInfo.class);
        System.out.println(userInfo.getAccessToken());
    }

    @Test
    public void getQuestions() {
        Response fetchQuestions = target.path(BASE_URL_FOR_QA)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .get();

        assert fetchQuestions.getStatus() == 200;
        System.out.println("Questions: " + fetchQuestions.readEntity(new GenericType<List<QuestionDetails>>() {
        }));
    }

    @Test
    public void getQuestionsByTags() {
        Response fetchQuestions = target.path(BASE_URL_FOR_QA)
                .queryParam("tags", "eth")
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .get();

        assert fetchQuestions.getStatus() == 200;
        System.out.println("Questions: " + fetchQuestions.readEntity(new GenericType<List<QuestionDetails>>() {
        }));
    }

    @Test(dependsOnMethods = "addQuestion")
    public void getQuestionById() {
        assert question != null;
        String url = BASE_URL_FOR_QA + "/" + question.getId();
        Response response = target
                .path(url)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .get();

        assert response.getStatus() == 200;
        question = response.readEntity(QuestionDetails.class);
        System.out.println("Question with id = " + question.getId() + " :- " + question.toString());
    }

    @Test(dataProvider = "getSampleQuestion")
    public void addQuestion(QuestionDetails questionDetails) {
        Response response = target
                .path(BASE_URL_FOR_QA)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .post(Entity.json(questionDetails));

        assert response.getStatus() == 200;
        question = response.readEntity(QuestionDetails.class);
        System.out.println("Question added: " + question.toString());
    }

    @Test(dataProvider = "getSampleAnswer", dependsOnMethods = "addQuestion")
    public void answerQuestion(Answer answer) {
        assert question != null;
        EntityRef questionInfo = new EntityRef(question.getId(), question.getQuestions());
        answer.setQuestion(questionInfo);
        String url = BASE_URL_FOR_QA + "/" + question.getId() + "/answer";
        Response response = target
                .path(url)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .post(Entity.json(answer));

        assert response.getStatus() == 200;
        answerDetails = response.readEntity(Answer.class);
    }

    @Test(dependsOnMethods = "answerQuestion")
    public void getAnswerByQuestionId() {
        assert question != null;
        String url = BASE_URL_FOR_QA + "/" + question.getId() + "/answers";
        Response response = target.path(url)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .get();

        assert response.getStatus() == 200;
        System.out.println(response.readEntity(new GenericType<List<Answer>>() {
        }));
    }

    @Test
    public void getAnswersByAuthorId() {
        String url = BASE_URL_FOR_QA + "/answer/author/" + userInfo.getId();

        Response response = target.path(url)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .get();

        assert response.getStatus() == 200;
        System.out.println(response.readEntity(new GenericType<List<Answer>>() {
        }));
    }

    @Test
    public void getQuestionsByAuthorId() {
        String url = BASE_URL_FOR_QA + "/author/" + userInfo.getId();

        Response response = target.path(url)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .get();

        assert response.getStatus() == 200;
        System.out.println(response.readEntity(new GenericType<List<QuestionDetails>>() {
        }));
    }

    // no functionality present to modify question
    @Test(dependsOnMethods = "addQuestion")
    public void modifyQuestion() {
        assert question != null;
        String url = BASE_URL_FOR_QA + "/" + question.getId();
        question.setQuestions("Can you explain Nuclear Physics in 2 mins?");

        Response response = target
                .path(url)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .put(Entity.json(question));

        assert response.getStatus() == 200;
        question = response.readEntity(QuestionDetails.class);
        System.out.println("Question Modified: " + question.toString());
    }

    // functionality not present yet
    //
    // have to soft delete the question
    @Test(dependsOnMethods = "addQuestion")
    public void deleteQuestion() {
        assert question != null;
        String url = BASE_URL_FOR_QA + "/" + question.getId();
        Response response = target
                .path(url)
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .delete();

        assert response.getStatus() == 204;
        System.out.println("Question Deleted: " + question.getId());
        question = null;
    }

    @Test()
    public void getQuestionsByTitle() {
        Response response = target
                .path(BASE_URL_FOR_QA)
                .queryParam("title", "crypto terms")
                .request()
                .header("Authorization", "Bearer " + userInfo.getAccessToken())
                .get();

        assert response.getStatus() == 200;
        System.out.println("Questions: " + response.readEntity(new GenericType<List<QuestionDetails>>() {
        }));

    }

    @AfterTest
    public void logoutUser() {
        // this can be done only on client side
    }

    @DataProvider
    public Object[][] getSampleQuestion() {
        QuestionDetails details = new QuestionDetails() {{
            setQuestions("crypto terms 2?");
            setAuthor(new AuthorInfo() {{
                setId(userInfo.getId());
            }});
            setTags(Arrays.asList("eth", "nft", "doge"));
        }};
        return new Object[][]{{details}};
    }

    @DataProvider
    public Object[][] getSampleAnswer() {
        Answer answer = new Answer() {{
            setAnswer("blah blah blah! this is the answer");
            setAuthor(new EntityRef() {{
                setId(userInfo.getId());
            }});
        }};

        return new Object[][]{{answer}};
    }

    private LoginBody getLoginCredentials() {
        return new LoginBody() {{
            setEmail("test@qasite.com");
            setPassword("admin");
        }};
    }
}

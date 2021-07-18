import org.harsh.domain.AuthInfo;
import org.harsh.domain.LoginBody;
import org.harsh.domain.UserInfo;
import org.testng.annotations.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.harsh.utils.ValidationUtils.generateRandomUserName;

public class UserResourceIT {
    final String BASE_URL = "http://localhost:8080/qasite/api";
    WebTarget target;
    UserInfo user;
    AuthInfo info;

    @BeforeTest
    public void authenticateUser() {
        Client client = ClientBuilder.newClient();
        target = client.target(BASE_URL);
    }

    @AfterTest
    public void logoutUser() {

    }

    @Test(dataProvider = "sampleUser")
    public void testLOGINUser(UserInfo sampleUser) {
        Response createdUser = target.path("/user/signup")
                .request().post(Entity.json(sampleUser));
        System.out.println("Response: " + createdUser.getStatus());
        assert createdUser.getStatus() == 200;
        user = createdUser.readEntity(UserInfo.class);
        System.out.println("User Created: " + user.toString());

        LoginBody loginBody = new LoginBody(sampleUser.getEmail(), sampleUser.getPwd());
        Response loggedInUser = target.path("/user/login")
                .request()
                .post(Entity.json(loginBody));
        System.out.println("Login Response: " + loggedInUser.getStatus());
        assert loggedInUser.getStatus() == 200;
        info = loggedInUser.readEntity(AuthInfo.class);
        System.out.println("Info: " + info.toString());
    }

    @Test(dependsOnMethods = "testLOGINUser")
    public void testGETUserById() {
        String url = "/user/" + user.getId();
        Response response = target
                .path(url)
                .request()
                .header("Authorization", "Bearer " + info.getAccessToken())
                .get();
        System.out.println("Response: " + response.getStatus());
        assert response.getStatus() == 200;
        user = response.readEntity(UserInfo.class);
    }

    @Test(dependsOnMethods = "testGETUserById")
    public void testUPDATEUserById() {
        String url = "/user/" + user.getId();
        System.out.println("user: "+ user.toString());
        user.setName(generateRandomUserName(12));
        user.setEmail("admin@" + generateRandomUserName(12) + ".com");
        user.setDob("01/01/1998");

        Response response = target.path(url)
                .request()
                .header("Authorization","Bearer " + info.getAccessToken())
                .put(Entity.json(user));

        user = response.readEntity(UserInfo.class);
        assert response.getStatus() == 200;
    }

    @Test(dependsOnMethods = "testUPDATEUserById")
    public void testDELETEUserById() {
        String url = "/user/" + user.getId();
        Response response = target.path(url).request()
                .header("Authorization", "Bearer " + info.getAccessToken())
                .delete();

        assert response.getStatus() == 204;
    }

    @DataProvider
    public Object[][] sampleUser() {
        UserInfo user = new UserInfo();
        user.setName(generateRandomUserName());
        user.setPwd("admin");
        String email = "admin@" + generateRandomUserName(8) + ".com";
        user.setEmail(email);
        user.setDob("25/11/1998");
        return new Object[][]{{user}};
    }
}

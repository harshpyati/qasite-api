package org.harsh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;


@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class UserInfo {
    private long id;
    private String name;
    private String pwd;
    private String encryptedPwd;
    private String email;
    private String dob;
    private String accessToken;
    private boolean verified;
    private long createdAt;

    @Override
    public String toString() {
        return "User:" + this.getName() + ", " + this.getEmail();
    }
}

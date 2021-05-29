package org.harsh.features.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class UserInfo {
    private int id;
    private String name;
    private String pwd;
    private String encryptedPwd;
    private String email;
    private String dob;
    private String gender;
    private String accessToken;
    private boolean isValidated;

    @Override
    public String toString() {
        return "User:" + this.getName() + ", " + this.getEmail();
    }
}

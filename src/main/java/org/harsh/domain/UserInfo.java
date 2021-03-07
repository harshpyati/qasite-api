package org.harsh.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

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

    @Override
    public String toString() {
        return "User:" + this.getName() + ", " + this.getEmail();
    }
}

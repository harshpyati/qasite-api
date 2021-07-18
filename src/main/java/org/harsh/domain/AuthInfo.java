package org.harsh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfo {
    int id;
    String email;
    String pwd;
    String accessToken;
    Long accessTokenTime;
}

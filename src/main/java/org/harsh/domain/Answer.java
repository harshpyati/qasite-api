package org.harsh.domain;
import lombok.Data;

@Data
public class Answer {
    long answerId;
    String answer;
    long numUpVotes;
    long numDownVotes;
    EntityRef author;
    EntityRef question;
}

package org.harsh.features.domain;
import lombok.Data;

@Data
public class Answer {
    long answerId;
    long questionId;
    String answer;
    long numUpVotes;
    long numDownVotes;
    AuthorInfo author;
}

package org.harsh.features.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDetails {
    int id;
    String questions;
    long numUpVotes;
    long numDownVotes;
    long numAnswers;
    AuthorInfo author;
}

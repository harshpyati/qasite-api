package org.harsh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    List<String> tags;
}

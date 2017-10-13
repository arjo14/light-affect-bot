package me.aboullaite.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Choice {

    private Integer id;

    private String name;
    private Integer questionId;
    private Integer lastSelected;
    private Integer nextQuestionId;
}

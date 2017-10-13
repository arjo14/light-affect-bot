package me.aboullaite.domain;

import com.github.messenger4j.send.QuickReply;

import java.util.List;

public interface LightAffectDao {

    List<Contact> getContacts();
    List<Question> getQuestions();
    List<Choice> getChoices();

    List<String> getChoicesByQuestionTopic(String topic);

    String getQuestionByTopic(String start);

    List<QuickReply> getListOfQuickRepliesForStart(String topic);

    Choice getChoiceFromChoiceId(Integer id);

    Question getQuestionByQuestionId(Integer questionId);

}

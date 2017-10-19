package me.aboullaite.domain;

import com.github.messenger4j.send.QuickReply;
import com.github.messenger4j.send.templates.GenericTemplate;

import java.util.List;

public interface LightAffectDao {

    List<Contact> getContacts();
    List<Question> getQuestions();
    List<Choice> getChoices();

    List<String> getChoicesByQuestionTopic(String topic);

    List<String> getChoicesByQuestionId(String questionId);

    String getQuestionByTopic(String start);

    List<QuickReply> getListOfQuickRepliesForStart(String topic);

    Choice getChoiceFromChoiceId(Integer id);

    Question getQuestionByQuestionId(Integer questionId);

    List<Product> getAllProductsFromChoiceId(Integer typeId);

    List<ProductPhoto> getProductPhotosByProductId(Integer id);

    ProductPhoto getOneProductPhotoByProductId(Integer id);

    GenericTemplate getQuickRepliesForProducts(List<Product> products);

    List<QuickReply> getQuickRepliesFromChoiceListAndQuestion(List<String> choiceList, Question question);

    Product getProductById(String productId);

    GenericTemplate getGenericTemplateForOneProduct(List<ProductPhoto> list);
}

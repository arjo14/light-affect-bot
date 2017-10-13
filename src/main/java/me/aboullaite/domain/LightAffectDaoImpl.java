package me.aboullaite.domain;

import com.github.messenger4j.send.QuickReply;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class LightAffectDaoImpl implements LightAffectDao {

    private JdbcTemplate jdbcTemplate;

    public LightAffectDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public List<Contact> getContacts() {
        List<Contact> contactList;
        String sql = "SELECT * FROM LIGHT_AFFECT.CONTACT ";
        contactList = jdbcTemplate.query(sql, (resultSet, i) -> {
            Contact contact = new Contact();
            contact.setId(resultSet.getInt(1));
            contact.setPlaceName(resultSet.getString(2));
            contact.setPhoneNumber(resultSet.getString(3));
            contact.setAddress(resultSet.getString(4));

            return contact;
        });

        return contactList;
    }

    @Override
    public List<Question> getQuestions() {
        return null;
    }

    @Override
    public List<Choice> getChoices() {
        return null;
    }

    @Override
    public List<String> getChoicesByQuestionTopic(String topic) {
        List<String> choices;
        String sql = "SELECT c.NAME FROM LIGHT_AFFECT.CHOICE c " +
                "INNER JOIN LIGHT_AFFECT.QUESTION q ON c.QUESTIONID = q.ID " +
                "WHERE q.TOPIC = ?";
        choices = jdbcTemplate.query(sql, ((resultSet, i) -> resultSet.getString(1)), topic);

        return choices;
    }

    @Override
    public String getQuestionByTopic(String topic) {
        String question;
        String sql = "SELECT NAME FROM LIGHT_AFFECT.QUESTION WHERE TOPIC = ?";
        question = jdbcTemplate.queryForObject(sql, ((resultSet, i) -> resultSet.getString(1)), topic);

        return question;
    }

    @Override
    public List<QuickReply> getListOfQuickRepliesForStart(String topic) {

        List<String> listForChoices = getChoicesByQuestionTopic("start");

        final QuickReply.ListBuilder quickRepliesForStartBldForBarev = QuickReply.newListBuilder();
        for (int i = 0; i < listForChoices.size(); i++) {
            String choice = listForChoices.get(i);
            quickRepliesForStartBldForBarev.addTextQuickReply(choice.substring(choice.indexOf("_") + 1), choice).toList();
        }

        return quickRepliesForStartBldForBarev.build();
    }

    @Override
    public Choice getChoiceFromChoiceId(Integer id) {

        Choice choice;
        String sql = "SELECT * FROM LIGHT_AFFECT.CHOICE WHERE ID = ?";
        choice = jdbcTemplate.queryForObject(sql, ((resultSet, i) -> {
            Choice newChoice = new Choice();
            newChoice.setId(resultSet.getInt(1));
            newChoice.setName(resultSet.getString(2));
            newChoice.setQuestionId(resultSet.getInt(3));
            newChoice.setLastSelected(resultSet.getInt(4));
            newChoice.setNextQuestionId(resultSet.getInt(5));
            return newChoice;
        }), id);

        return choice;
    }

    @Override
    public Question getQuestionByQuestionId(Integer questionId) {
        Question question;
        String sql = "SELECT * FROM LIGHT_AFFECT.QUESTION WHERE ID = ?";

        question = jdbcTemplate.queryForObject(sql, ((resultSet, i) -> {
            Question newQuestion=new Question();
            newQuestion.setId(resultSet.getInt(1));
            newQuestion.setName(resultSet.getString(2));
            newQuestion.setTopic(resultSet.getString(3));
            return newQuestion;
        }), questionId);

        return question;
    }
}

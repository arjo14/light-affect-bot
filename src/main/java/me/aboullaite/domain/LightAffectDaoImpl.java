package me.aboullaite.domain;

import com.github.messenger4j.send.QuickReply;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.templates.GenericTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

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
    public List<String> getChoicesByQuestionId(String questionId) {
        List<String> choices;
        String sql = "SELECT NAME FROM LIGHT_AFFECT.CHOICE  WHERE QUESTIONID = ?";
        choices = jdbcTemplate.query(sql, ((resultSet, i) -> resultSet.getString(1)), questionId);

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
        List<Question> question;
        String sql = "SELECT * FROM LIGHT_AFFECT.QUESTION WHERE ID = ?";

        question = jdbcTemplate.query(sql, ((resultSet, i) -> new Question(resultSet.getInt(1), resultSet.getString(2),
                resultSet.getString(3))), questionId);

        return question.isEmpty() ? null : question.get(0);
    }

    @Override
    public List<Product> getAllProductsFromChoiceId(Integer typeId) {
        List<Product> list;

        String sql = "SELECT * FROM LIGHT_AFFECT.PRODUCT WHERE CHOICE_ID = ? ";
        list = jdbcTemplate.query(sql, (resultSet, i) -> new Product(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)), typeId);
        return list;
    }

    @Override
    public List<ProductPhoto> getProductPhotosByProductId(Integer id) {
        List<ProductPhoto> list;

        String sql = "SELECT * FROM LIGHT_AFFECT.PRODUCT_PHOTO WHERE PRODUCT_ID = ?";
        list = jdbcTemplate.query(sql, (resultSet, i) -> new ProductPhoto(resultSet.getInt(1), resultSet.getString(2),
                resultSet.getBytes(3), resultSet.getInt(4), resultSet.getString(5)), id);

        return list;
    }

    @Override
    public ProductPhoto getOneProductPhotoByProductId(Integer id) {
        List<ProductPhoto> productPhoto;

        String sql = "SELECT * FROM LIGHT_AFFECT.PRODUCT_PHOTO WHERE PRODUCT_ID = ?";
        productPhoto = jdbcTemplate.query(sql, (resultSet, i) -> new ProductPhoto(resultSet.getInt(1), resultSet.getString(2),
                resultSet.getBytes(3), resultSet.getInt(4), resultSet.getString(5)), id);

        return productPhoto.isEmpty() ? null : productPhoto.get(0);
    }

    @Override
    public Product getProductById(String productId) {
        String sql = "SELECT * FROM LIGHT_AFFECT.PRODUCT WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, (resultSet, i) -> new Product(resultSet.getInt(1), resultSet.getString(2),
                resultSet.getInt(3)), Integer.parseInt(productId));
    }

    @Override
    public GenericTemplate getGenericTemplateForOneProduct(List<ProductPhoto> list) {

        GenericTemplate.Element.ListBuilder listBuilder = GenericTemplate.newBuilder().addElements();

        Product product = getProductById(String.valueOf(list.get(0).getProductId()));
        for (ProductPhoto productPhoto : list) {
            Button.ListBuilder buttonList = Button.newListBuilder().addPostbackButton("Կրկին որոնել", "0").toList();
            listBuilder
                    .addElement("Կոդ " + productPhoto.getProductId())
                    .subtitle(product.getPrice())
                    .buttons(buttonList.addPostbackButton("Կոնտակտներ", "contact").toList().build())
                    //todo change productPhoto.toString.. show photo
                    .imageUrl(productPhoto.getUrl())
                    .itemUrl(productPhoto.getTargetUrl())
                    .toList();
        }
        return listBuilder.done().build();
    }

    @Override
    public GenericTemplate getQuickRepliesForProducts(List<Product> products) {

        GenericTemplate.Element.ListBuilder listBuilder = GenericTemplate.newBuilder().addElements();
        boolean isAbleToBuild = false;

        for (Product product : products) {
            ProductPhoto productPhoto = getOneProductPhotoByProductId(product.getId());
            Button.ListBuilder list = Button.newListBuilder().addPostbackButton("Կրկին որոնել", "0_start").toList();
            if (productPhoto != null) {
                isAbleToBuild = true;
                listBuilder
                        .addElement("Կոդ " + product.getId())
                        .subtitle(product.getPrice())
                        .buttons(list.addPostbackButton("Տեսնել ավելին", "seeMore" + productPhoto.getId()).toList().build())
                        //todo change productPhoto.toString.. show photo
                        .imageUrl(productPhoto.getUrl())
                        .itemUrl(productPhoto.getTargetUrl())
                        .toList();
            }
        }
        return !isAbleToBuild ? null : listBuilder.done().build();
    }

    @Override
    public List<QuickReply> getListOfQuickRepliesForStart(String topic) {

        List<String> listForChoices = getChoicesByQuestionTopic("start");

        final QuickReply.ListBuilder quickRepliesForStartBldForBarev = QuickReply.newListBuilder();
        for (String choice : listForChoices) {
            quickRepliesForStartBldForBarev.addTextQuickReply(choice.substring(choice.indexOf("_") + 1), choice).toList();
        }

        return quickRepliesForStartBldForBarev.build();
    }

    @Override
    public List<QuickReply> getQuickRepliesFromChoiceListAndQuestion(List<String> choiceList, Question question) {
        final QuickReply.ListBuilder quickReplies = QuickReply.newListBuilder();
        for (String choice : choiceList) {
            quickReplies.addTextQuickReply(choice.substring(choice.indexOf("_") + 1), choice).toList();
        }
        return quickReplies.build();
    }
}

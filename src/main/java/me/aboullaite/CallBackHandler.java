package me.aboullaite;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.exceptions.MessengerVerificationException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.events.AccountLinkingEvent;
import com.github.messenger4j.receive.handlers.*;
import com.github.messenger4j.send.*;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.templates.ButtonTemplate;
import com.github.messenger4j.send.templates.GenericTemplate;
import com.github.messenger4j.user.UserProfile;
import com.github.messenger4j.user.UserProfileClientBuilder;
import me.aboullaite.domain.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by aboullaite on 2017-02-26.
 */

@RestController
@RequestMapping("/callback")
public class CallBackHandler {

    @Autowired
    private LightAffectDao lightAffectDao;

    private static final Logger logger = LoggerFactory.getLogger(CallBackHandler.class);

    private static final String RESOURCE_URL =
            "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public";
    private static final String Men = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION";
    private static final String MaykaMan = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_MAYKA";
    private static final String TrusikMan = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_TRUSIK";
    private static final String KoshikMan = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_KOSHIK";


    private static final String Women = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_ACTION";
    private static final String MaykaWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_MAYKA";
    private static final String TrusikWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_TRUSIK";
    private static final String BijuWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_BIJU";
    private static final String KoshikWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_KOSHIK";


    private final MessengerReceiveClient receiveClient;
    private final MessengerSendClient sendClient;

    /**
     * Constructs the {@code CallBackHandler} and initializes the {@code MessengerReceiveClient}.
     *
     * @param appSecret   the {@code Application Secret}
     * @param verifyToken the {@code Verification Token} that has been provided by you during the setup of the {@code
     *                    Webhook}
     * @param sendClient  the initialized {@code MessengerSendClient}
     */
    @Autowired
    public CallBackHandler(@Value("${messenger4j.appSecret}") final String appSecret,
                           @Value("${messenger4j.verifyToken}") final String verifyToken,
                           final MessengerSendClient sendClient) {

        logger.debug("Initializing MessengerReceiveClient - appSecret: {} | verifyToken: {}", appSecret, verifyToken);
        this.receiveClient = MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken)
                .onTextMessageEvent(newTextMessageEventHandler())
                .onQuickReplyMessageEvent(newQuickReplyMessageEventHandler())
                .onPostbackEvent(newPostbackEventHandler())
                .onAccountLinkingEvent(newAccountLinkingEventHandler())
                .onOptInEvent(newOptInEventHandler())
                .onEchoMessageEvent(newEchoMessageEventHandler())
                .onMessageDeliveredEvent(newMessageDeliveredEventHandler())
                .onMessageReadEvent(newMessageReadEventHandler())
                .fallbackEventHandler(newFallbackEventHandler())
                .build();
        this.sendClient = sendClient;
    }

    /**
     * Webhook verification endpoint.
     * <p>
     * The passed verification token (as query parameter) must match the configured verification token.
     * In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebhook(@RequestParam("hub.mode") final String mode,
                                                @RequestParam("hub.verify_token") final String verifyToken,
                                                @RequestParam("hub.challenge") final String challenge) {

        logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode,
                verifyToken, challenge);
        try {
            return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode, verifyToken, challenge));
        } catch (MessengerVerificationException e) {
            logger.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final String payload,
                                               @RequestHeader("X-Hub-Signature") final String signature) {

        logger.debug("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
            this.receiveClient.processCallbackPayload(payload, signature);
            logger.debug("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessengerVerificationException e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private TextMessageEventHandler newTextMessageEventHandler() {
        return event -> {
            logger.debug("Received TextMessageEvent: {}", event);

            final String senderId = event.getSender().getId();

            UserProfile userProfile = null;
            try {
                userProfile = new UserProfileClientBuilder("EAADySZAVy1wkBAJ530AorFPk5UW0rLK5B44sK66m6d68jAvXC0ByStZASAwXpNqAmadHLPnmRQbEINY7WF8BMZAPPqvezXLcdYNxo8jHPR9lhdgnriBaSMqfey752apZB2wlL7sS6JO2HUQKlyQ3YNyqtQ6s7zVcBIOZChw9xZBAZDZD").build().queryUserProfile(senderId);
            } catch (MessengerApiException | MessengerIOException e) {
                e.printStackTrace();
            }

            final String messageId = event.getMid();
            final String messageText = event.getText();
            final Date timestamp = event.getTimestamp();

            logger.info("Received message '{}' with text '{}' from user '{}' at '{}'",
                    userProfile == null ? messageId : userProfile.getFirstName(), messageText, senderId, timestamp);
            int count = 0;
            if (messageText.toLowerCase().startsWith("kode")) {
                count = 4;

            } else if (messageText.toLowerCase().startsWith("kod") || messageText.toLowerCase().startsWith("կոդ")) {
                count = 3;
            }
            if (count != 0) {
                String productId = messageText.substring(count).trim();
                ProductPhoto photo = lightAffectDao.getOneProductPhotoByProductId(Integer.valueOf(productId));
                Product product = lightAffectDao.getProductById(productId);
                if (photo != null) {
                    Button.ListBuilder list = Button.newListBuilder().addPostbackButton("Կրկին որոնել", "0_start").toList();
                    final GenericTemplate genericTemplate = GenericTemplate.newBuilder()
                            .addElements()
                            .addElement("կոդ" + productId)
                            .subtitle(product.getPrice())
                            .buttons(list.build())
                            .itemUrl(photo.getTargetUrl())
                            .imageUrl(photo.getUrl())
                            .toList()
                            .done()
                            .build();
                    try {
                        this.sendClient.sendTemplate(senderId, genericTemplate);
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }

            switch (messageText.toLowerCase()) {
                case "mersi":
                case"shnorhakalucyun":
                case"shnorhakalutyun": sendTextMessage(senderId,"Ձեզ շնորհակալություն հարգելի " +userProfile.getFirstName());break;
                case "start":
                case "go":
                    List<QuickReply> quickList = lightAffectDao.getListOfQuickRepliesForStart("topic");

                    String questionForBarev = lightAffectDao.getQuestionByTopic("start");

                    try {
                        this.sendClient.sendTextMessage(senderId, questionForBarev, quickList);
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "barev":
                case "hi":
                case "hello":
                case "barlus":
                case "barigun":
                case "barior":
                case "privet":
                case "zdrasci":
                case "bari or":
                case "բարիօր":
                case "բարի օր":
                case "բարև":
                    try {

                        this.sendClient.sendTextMessage(senderId, "Բարև հարգելի " + (userProfile == null ? "հաճախորդ" : userProfile.getFirstName()));
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }

                    quickList = lightAffectDao.getListOfQuickRepliesForStart("topic");

                    questionForBarev = lightAffectDao.getQuestionByTopic("start");

                    try {
                        this.sendClient.sendTextMessage(senderId, questionForBarev, quickList);
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "hajox":
                case "bye":
                case "poka":
                case "bari gisher":
                    sendTextMessage(senderId, "Հաջողություն հարգելի " + userProfile.getFirstName());
                    break;
                case "inch ka ?":
                case "inch ka?":
                case "inch ka":
                case "inchka":
                case "vonc es ?":
                case "vonc es?":
                case "vonc es":
                case "vonces":
                    sendTextMessage(senderId, "Նոռմալ հարգելի " + userProfile.getFirstName());
                    break;

                default:
                    sendTextMessage(senderId, "Չեմ հասկանում։ Խնդրում եմ գրեք 'help' օգնության համար");
                    break;
            }
        };
    }


    private QuickReplyMessageEventHandler newQuickReplyMessageEventHandler() {
        return event -> {
            logger.debug("Received QuickReplyMessageEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String messageId = event.getMid();
            final String quickReplyPayload = event.getQuickReply().getPayload();

            logger.info("Received quick reply for message '{}' with payload '{}'", messageId, quickReplyPayload);

            Integer choiceId = Integer.valueOf(quickReplyPayload.substring(0, quickReplyPayload.indexOf('_')));
            if (choiceId == 0) {
                List<QuickReply> quickList = lightAffectDao.getListOfQuickRepliesForStart("topic");

                String questionForBarev = lightAffectDao.getQuestionByTopic("start");

                try {
                    this.sendClient.sendTextMessage(senderId, questionForBarev, quickList);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }
                return;
            }

            Choice choice = lightAffectDao.getChoiceFromChoiceId(choiceId);
            Integer nextQuestionId = choice.getNextQuestionId();

            Question question = lightAffectDao.getQuestionByQuestionId(nextQuestionId);

            if (question != null) {
                List<String> choiceList = lightAffectDao.getChoicesByQuestionId(question.getId().toString());

                if (choiceList.isEmpty()) {
                    List<Product> products = lightAffectDao.getAllProductsFromChoiceId(choiceId);
                    try {
                        GenericTemplate reply = lightAffectDao.getQuickRepliesForProducts(products);
                        if (reply == null) {
                            List<QuickReply> quickReplies = QuickReply.newListBuilder().addTextQuickReply("Այստեղ", "0_Այստեղ").toList().build();
                            try {
                                this.sendClient.sendTextMessage(senderId, "Ցավոք տվյալ պահին ոչ մի տվյալներ չգտնվեցին\nԿրկին որոնելու համար սեղմեք այստեղ", quickReplies);
                            } catch (MessengerApiException | MessengerIOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            this.sendClient.sendTemplate(senderId, reply);
                        }

                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }


                } else {
                    List<QuickReply> quickReplies = lightAffectDao.getQuickRepliesFromChoiceListAndQuestion(choiceList, question);
                    try {
                        this.sendClient.sendTextMessage(senderId, question.getName(), quickReplies);
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (choiceId == 2) {
                List<Contact> contactList = lightAffectDao.getContacts();
                sendTextMessage(senderId, "Մեր կոնտակտային տվյալներն են․");
                for (Contact contact : contactList) {
                    sendTextMessage(senderId, contact.getPlaceName() + "\n"
                            + "Հասցե․ " + contact.getAddress() + "\n"
                            + "Հեռախոս․ " + contact.getPhoneNumber());
                }
                List<QuickReply> quickReplies = QuickReply.newListBuilder().addTextQuickReply("Այստեղ", "0_Այստեղ").toList().build();
                try {
                    this.sendClient.sendTextMessage(senderId, "Կրկին որոնելու համար սեղմեք այստեղ", quickReplies);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private PostbackEventHandler newPostbackEventHandler() {
        return event -> {
            logger.debug("Received PostbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String payload = event.getPayload();
            final Date timestamp = event.getTimestamp();
            if ("0" .equals(payload)) {

                List<QuickReply> quickList = lightAffectDao.getListOfQuickRepliesForStart("topic");

                String questionForBarev = lightAffectDao.getQuestionByTopic("start");

                try {
                    this.sendClient.sendTextMessage(senderId, questionForBarev, quickList);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }
            } else if ("contact" .equals(payload)) {
                List<Contact> contactList = lightAffectDao.getContacts();
                sendTextMessage(senderId, "ԳՆելու կամ չափսերի առկայության համար կապնվեք մեզ հետ։\nՄեր կոնտակտային տվյալներն են․");
                for (Contact contact : contactList) {
                    sendTextMessage(senderId, contact.getPlaceName() + "\n"
                            + "Հասցե․ " + contact.getAddress() + "\n"
                            + "Հեռախոս․ " + contact.getPhoneNumber());
                }
                List<QuickReply> quickReplies = QuickReply.newListBuilder().addTextQuickReply("Այստեղ", "0_Այստեղ").toList().build();
                try {
                    this.sendClient.sendTextMessage(senderId, "Կրկին որոնելու համար սեղմեք այստեղ", quickReplies);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }

            } else if (payload.contains("seeMore")) {
                String productId = payload.substring(7);
                List<ProductPhoto> productPhotosByProductId = lightAffectDao.getProductPhotosByProductId(Integer.valueOf(productId));
                final GenericTemplate genericTemplate = lightAffectDao.getGenericTemplateForOneProduct(productPhotosByProductId);
                try {
                    this.sendClient.sendTemplate(senderId, genericTemplate);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private AccountLinkingEventHandler newAccountLinkingEventHandler() {
        return event -> {
            logger.debug("Received AccountLinkingEvent: {}", event);

            final String senderId = event.getSender().getId();
            final AccountLinkingEvent.AccountLinkingStatus accountLinkingStatus = event.getStatus();
            final String authorizationCode = event.getAuthorizationCode();

            logger.info("Received account linking event for user '{}' with status '{}' and auth code '{}'",
                    senderId, accountLinkingStatus, authorizationCode);

        };
    }

    private OptInEventHandler newOptInEventHandler() {
        return event -> {
            logger.debug("Received OptInEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String passThroughParam = event.getRef();
            final Date timestamp = event.getTimestamp();

            logger.info("Received authentication for user '{}' and page '{}' with pass through param '{}' at '{}'",
                    senderId, recipientId, passThroughParam, timestamp);

            sendTextMessage(senderId, "Authentication successful");
        };
    }

    private EchoMessageEventHandler newEchoMessageEventHandler() {
        return event -> {
            logger.debug("Received EchoMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String recipientId = event.getRecipient().getId();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received echo for message '{}' that has been sent to recipient '{}' by sender '{}' at '{}'",
                    messageId, recipientId, senderId, timestamp);
        };
    }

    private MessageDeliveredEventHandler newMessageDeliveredEventHandler() {
        return event -> {
            logger.debug("Received MessageDeliveredEvent: {}", event);

            final List<String> messageIds = event.getMids();
            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            if (messageIds != null) {
                messageIds.forEach(messageId -> {
                    logger.info("Received delivery confirmation for message '{}'", messageId);
                });
            }

            logger.info("All messages beforksdsvlglae '{}' were delivered to user '{}'", watermark, senderId);
        };
    }

    private MessageReadEventHandler newMessageReadEventHandler() {
        return event -> {
            logger.debug("Received MessageReadEvent: {}", event);

            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            logger.info("All messages before '{}' were read by user '{}'", watermark, senderId);
        };
    }

    /**
     * This handler is called when either the message is unsupported or when the event handler for the actual event type
     * is not registered. In this showcase all event handlers are registered. Hence only in case of an
     * unsupported message the fallback event handler is called.
     */
    private FallbackEventHandler newFallbackEventHandler() {
        return event -> {
            logger.debug("Received FallbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            logger.info("Received unsupported message from user '{}'", senderId);
        };
    }

    private void sendTextMessage(String recipientId, String text) {
        try {
            final Recipient recipient = Recipient.newBuilder().recipientId(recipientId).build();
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            this.sendClient.sendTextMessage(recipient, notificationType, text, metadata);
        } catch (MessengerApiException | MessengerIOException e) {
            handleSendException(e);
        }
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }

    private void handleIOException(Exception e) {
        logger.error("Could not open Spring.io page. An unexpected error occurred.", e);
    }

    private void sendSpringDoc(String recipientId, String keyword) throws MessengerApiException, MessengerIOException, IOException {

        Document doc = Jsoup.connect(("https://spring.io/search?q=").concat(keyword)).get();
        String countResult = doc.select("div.search-results--count").first().ownText();
        Elements searchResult = doc.select("section.search-result");
        List<SearchResult> searchResults = searchResult.stream().map(element ->
                new SearchResult(element.select("a").first().ownText(),
                        element.select("a").first().absUrl("href"),
                        element.select("div.search-result--subtitle").first().ownText(),
                        element.select("div.search-result--summary").first().ownText())
        ).limit(3).collect(Collectors.toList());

        final List<Button> firstLink = Button.newListBuilder()
                .addUrlButton("Open Link", searchResults.get(0).getLink()).toList()
                .build();
        final List<Button> secondLink = Button.newListBuilder()
                .addUrlButton("Open Link", searchResults.get(1).getLink()).toList()
                .build();
        final List<Button> thirdtLink = Button.newListBuilder()
                .addUrlButton("Open Link", searchResults.get(2).getLink()).toList()
                .build();
        final List<Button> searchLink = Button.newListBuilder()
                .addUrlButton("Open Link", ("https://spring.io/search?q=").concat(keyword)).toList()
                .build();


        final GenericTemplate genericTemplate = GenericTemplate.newBuilder()
                .addElements()
                .addElement(searchResults.get(0).getTitle())
                .subtitle(searchResults.get(0).getSubtitle())
                .itemUrl(searchResults.get(0).getLink())
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(firstLink)
                .toList()
                .addElement(searchResults.get(1).getTitle())
                .subtitle(searchResults.get(1).getSubtitle())
                .itemUrl(searchResults.get(1).getLink())
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(secondLink)
                .toList()
                .addElement(searchResults.get(2).getTitle())
                .subtitle(searchResults.get(2).getSubtitle())
                .itemUrl(searchResults.get(2).getLink())
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(thirdtLink)
                .toList()
                .addElement("All results " + countResult)
                .subtitle("Spring Search Result")
                .itemUrl(("https://spring.io/search?q=").concat(keyword))
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(searchLink)
                .toList()
                .done()
                .build();

        this.sendClient.sendTemplate(recipientId, genericTemplate);
    }

    private void sendGifMessage(String recipientId, String gif) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendImageAttachment(recipientId, gif);
    }


    private void sendQuickReply(String recipientId) throws MessengerApiException, MessengerIOException {
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Looks good", Men).toList()
                .addTextQuickReply("Nope!", Women).toList()
                .build();

        this.sendClient.sendTextMessage(recipientId, "Was this helpful?!", quickReplies);
    }

    private void sendReadReceipt(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.MARK_SEEN);
    }

    private void sendTypingOn(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.TYPING_ON);
    }

    private void sendTypingOff(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.TYPING_OFF);
    }
}

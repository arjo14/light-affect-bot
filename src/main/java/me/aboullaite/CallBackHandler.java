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
import com.github.messenger4j.send.templates.GenericTemplate;
import com.github.messenger4j.user.UserProfile;
import com.github.messenger4j.user.UserProfileClient;
import com.github.messenger4j.user.UserProfileClientBuilder;
import me.aboullaite.domain.SearchResult;
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
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by aboullaite on 2017-02-26.
 */

@RestController
@RequestMapping("/callback")
public class CallBackHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallBackHandler.class);

    private static final String RESOURCE_URL =
            "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public";
    public static final String Men = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION";
    public static final String MaykaMan = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_MAYKA";
    public static final String TrusikMan = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_TRUSIK";
    public static final String KoshikMan = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_KOSHIK";


    public static final String Women = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_ACTION";
    public static final String MaykaWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_MAYKA";
    public static final String TrusikWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_TRUSIK";
    public static final String BijuWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_BIJU";
    public static final String KoshikWoman = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_KOSHIK";


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

            switch (messageText.toLowerCase()) {
                case "start":
                    final List<QuickReply> quickRepliesForStart = QuickReply.newListBuilder()
                            .addTextQuickReply("Տղամարդու", Men).toList()
                            .addTextQuickReply("Կանացի", Women).toList()
                            .build();

                    try {
                        this.sendClient.sendTextMessage(senderId, "Ընտրեք  որ բաժինն է հետաքրքրում?", quickRepliesForStart);
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "barev":
                case "hi":
                case "barlus":
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
                    final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                            .addTextQuickReply("Տղամարդու", Men).toList()
                            .addTextQuickReply("Կանացի", Women).toList()
                            .build();

                    try {
                        this.sendClient.sendTextMessage(senderId, "Ընտրեք  որ բաժինն է հետաքրքրում?", quickReplies);
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "hajox":
                case "bye":
                case "poka":
                case "bari gisher":
                    sendTextMessage(senderId, "Դավայ բռատ");
                    break;
                case "inch ka ?":
                case "inch ka?":
                case "inch ka":
                case "inchka":
                case "vonc es ?":
                case "vonc es?":
                case "vonc es":
                case "vonces":
                    sendTextMessage(senderId, "Նոռմալ բռատ,դու ասա");
                    break;

                default:
                    sendTextMessage(senderId, "Չեմ ջոգում։ ՈՒրիշ բան գրի!");
                    break;
            }
        };
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

    private QuickReplyMessageEventHandler newQuickReplyMessageEventHandler() {
        return event -> {
            logger.debug("Received QuickReplyMessageEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String messageId = event.getMid();
            final String quickReplyPayload = event.getQuickReply().getPayload();

            logger.info("Received quick reply for message '{}' with payload '{}'", messageId, quickReplyPayload);


            if (quickReplyPayload.equals(Men)) {
                final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                        .addTextQuickReply("Տռուսիկ", TrusikMan).toList()
                        .addTextQuickReply("Մայկա", MaykaMan).toList()
                        .addTextQuickReply("Կոշիկ", KoshikMan).toList()
                        .build();

                try {
                    this.sendClient.sendTextMessage(senderId, "Ընտրեք  ինչ է ձեզ հետաքրքում ?", quickReplies);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }
//                    sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
            } else if (quickReplyPayload.equals(Women)) {
                final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                        .addTextQuickReply("Տռուսիկ", TrusikWoman).toList()
                        .addTextQuickReply("Մայկա", MaykaWoman).toList()
                        .addTextQuickReply("Բիժու", BijuWoman).toList()
                        .addTextQuickReply("Կոշիկ", KoshikWoman).toList()
                        .build();

                try {
                    this.sendClient.sendTextMessage(senderId, "Ընտրեք  ինչ է ձեզ հետաքրքում ?", quickReplies);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }
            } else if (quickReplyPayload.equals(MaykaWoman)) {


                final GenericTemplate genericTemplate = GenericTemplate.newBuilder()
                        .addElements()
                        .addElement("Շորիկ")
                        .subtitle("N1")
                        .imageUrl("https://scontent.fevn1-1.fna.fbcdn.net/v/t31.0-8/18836668_1975739422660318_8841904638295923351_o.jpg?oh=316f99c55180fb1882ddc5b73daafb43&oe=5A4638F2")
                        .toList()
                        .addElement("Շորիկ")
                        .imageUrl("https://scontent.fevn1-1.fna.fbcdn.net/v/t31.0-8/19390634_1984278141806446_5720918993626411404_o.jpg?oh=9df18a8ed80fb94e92b0972055d705ce&oe=5A7D4A60")
                        .subtitle("N2")
                        .toList()
                        .addElement("Շորիկ")
                        .imageUrl("https://scontent.fevn1-1.fna.fbcdn.net/v/t1.0-9/21192570_2022213041346289_8292937856284510617_n.jpg?oh=55fb9dc481d827ac0cee5db7488d36e1&oe=5A3BA6B9")
                        .subtitle("N3")
                        .toList()
                        .addElement("Շորիկ")
                        .imageUrl("https://scontent.fevn1-1.fna.fbcdn.net/v/t31.0-8/21246504_2022214021346191_9126202906630822922_o.jpg?oh=a2da98218e012a1a7ca0441d8cd65400&oe=5A739372")
                        .subtitle("N4")
                        .toList()
                        .done()
                        .build();

                try {
                    this.sendClient.sendTemplate(senderId, genericTemplate);
                    final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                            .addTextQuickReply("N1", "N1").toList()
                            .addTextQuickReply("N2", "N2").toList()
                            .addTextQuickReply("N3", "N3").toList()
                            .addTextQuickReply("N4", "N4").toList()
                            .addTextQuickReply("Ոչ մեկ", "Ոչ մեկ").toList()
                            .build();

                    try {
                        this.sendClient.sendTextMessage(senderId, "Ընտրեք  որ ապրանքն է ձեզ հետաքրքրում?", quickReplies);
                    } catch (MessengerApiException | MessengerIOException e) {
                        e.printStackTrace();
                    }
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }

            } else if (quickReplyPayload.equals("Ոչ մեկ")) {
                sendTextMessage(senderId, "Ցավոք ձեզ դուր չեկավ մեր տեսականին․․․");

                final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                        .addTextQuickReply("Այո", "Այո").toList()
                        .addTextQuickReply("Ոչ", "Ոչ").toList()
                        .build();

                try {
                    this.sendClient.sendTextMessage(senderId, "Կցանկանաք շարունակել ?", quickReplies);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }

            } else if (quickReplyPayload.equals("Այո")) {
                final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                        .addTextQuickReply("Տղամարդու", Men).toList()
                        .addTextQuickReply("Կանացի", Women).toList()
                        .build();

                try {
                    this.sendClient.sendTextMessage(senderId, "Ընտրեք  որ բաժինն է հետաքրքրում?", quickReplies);
                } catch (MessengerApiException | MessengerIOException e) {
                    e.printStackTrace();
                }

            } else if (quickReplyPayload.equals("Ոչ")) {
                sendTextMessage(senderId, "Ցավոք ․․․ Նորից դիտելու համար դուք կարող եք պարզապես գրել 'start'");
            } else {
                getProductById(quickReplyPayload);
            }
//                    sendGifMessage(senderId, "https://media.giphy.com/media/26ybx7nkZXtBkEYko/giphy.gif");

/*
            sendTextMessage(senderId, "Let's try another one :D!");
*/
        };
    }

    public void getProductById(String productId) {

    }

    private PostbackEventHandler newPostbackEventHandler() {
        return event -> {
            logger.debug("Received PostbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String payload = event.getPayload();
            final Date timestamp = event.getTimestamp();

            logger.info("Received postback for user '{}' and page '{}' with payload '{}' at '{}'",
                    senderId, recipientId, payload, timestamp);

            sendTextMessage(senderId, "Postback called");
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
}

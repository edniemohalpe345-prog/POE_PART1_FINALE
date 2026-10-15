/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quickchatapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
/**
 *
 * @author Student
 */
public class Message {

    private static final int MESSAGE_LIMIT = 250;
    private static final Path JSON_FILE = Paths.get("messages.json");

    private static final List<Message> SENT_MESSAGES = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private final int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int selectedAction;

    public Message(int messageNumber, String recipient,
                   String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    // Constructor for testing valid and invalid message IDs.
    public Message(String messageID, int messageNumber,
                   String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    private static String generateMessageID() {
        long id = ThreadLocalRandom.current()
                .nextLong(1_000_000_000L, 10_000_000_000L);

        return Long.toString(id);
    }

    public boolean checkMessageID() {
        return messageID != null
                && messageID.matches("[0-9]{1,10}");
    }

    public String checkRecipientCell() {
        if (recipient != null
                && recipient.matches("\\+27[0-9]{9}")) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number "
                + "and try again.";
    }

    public String checkMessageLength() {
        if (messageText != null
                && messageText.length() <= MESSAGE_LIMIT) {
            return "Message ready to send.";
        }

        int length = messageText == null ? 0 : messageText.length();
        int extraCharacters = Math.max(0, length - MESSAGE_LIMIT);

        return "Message exceeds 250 characters by " + extraCharacters
                + " characters; please reduce message length.";
    }

    public String createMessageHash() {
        String firstTwoIDNumbers = "00";

        if (messageID != null && messageID.length() >= 2) {
            firstTwoIDNumbers = messageID.substring(0, 2);
        }

        String firstWord = "";
        String lastWord = "";

        if (messageText != null && !messageText.trim().isEmpty()) {
            String[] words = messageText.trim().split("\\s+");

            firstWord = cleanWord(words[0]);
            lastWord = cleanWord(words[words.length - 1]);
        }

        return firstTwoIDNumbers + ":"
                + messageNumber + ":"
                + (firstWord + lastWord).toUpperCase();
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    public String SentMessage(int action) {
        this.selectedAction = action;
        return SentMessage();
    }

    public String SentMessage() {

        if (!checkMessageID()) {
            return "Message ID is not correctly formatted.";
        }

        String recipientStatus = checkRecipientCell();

        if (!recipientStatus.equals(
                "Cell phone number successfully captured.")) {
            return recipientStatus;
        }

        String lengthStatus = checkMessageLength();

        if (!lengthStatus.equals("Message ready to send.")) {
            return lengthStatus;
        }

        if (selectedAction == 1) {
            SENT_MESSAGES.add(this);
            totalMessagesSent++;

            return "Message successfully sent.";
        }

        if (selectedAction == 2) {
            return "Press 0 to delete the message.";
        }

        if (selectedAction == 3) {
            return storeMessages();
        }

        return "Invalid message option.";
    }

    public String printMessage() {

        if (SENT_MESSAGES.isEmpty()) {
            return "No messages have been sent yet.";
        }

        StringBuilder output = new StringBuilder();

        for (Message sentMessage : SENT_MESSAGES) {
            output.append("Message ID: ")
                    .append(sentMessage.messageID)
                    .append("\nMessage Hash: ")
                    .append(sentMessage.messageHash)
                    .append("\nRecipient: ")
                    .append(sentMessage.recipient)
                    .append("\nMessage: ")
                    .append(sentMessage.messageText)
                    .append("\n\n");
        }

        return output.toString().trim();
    }

    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    public static int getTotalSentCount() {
        return totalMessagesSent;
    }

    public String storeMessages() {

        String existingJson = "";

        try {
            if (Files.exists(JSON_FILE)) {
                existingJson = new String(
                        Files.readAllBytes(JSON_FILE),
                        StandardCharsets.UTF_8
                ).trim();
            }

            String newObject = toJsonObject();
            String updatedJson;

            if (existingJson.isEmpty() || existingJson.equals("[]")) {
                updatedJson = "[\n  " + newObject + "\n]\n";

            } else if (existingJson.startsWith("[")
                    && existingJson.endsWith("]")) {

                String existingEntries = existingJson.substring(
                        1, existingJson.length() - 1
                ).trim();

                updatedJson = "[\n  "
                        + existingEntries
                        + (existingEntries.isEmpty() ? "" : ",")
                        + "\n  "
                        + newObject
                        + "\n]\n";

            } else {
                return "Could not store the message: messages.json is not "
                        + "a valid JSON array.";
            }

            Files.write(
                    JSON_FILE,
                    updatedJson.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            return "Message successfully stored.";

        } catch (IOException exception) {
            return "Could not store the message in messages.json.";
        }
    }

    private String toJsonObject() {
        return "{\n"
                + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(messageText) + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(messageHash) + "\"\n"
                + "  }";
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);

            switch (character) {
                case '"':
                    escaped.append("\\\"");
                    break;

                case '\\':
                    escaped.append("\\\\");
                    break;

                case '\n':
                    escaped.append("\\n");
                    break;

                case '\r':
                    escaped.append("\\r");
                    break;

                case '\t':
                    escaped.append("\\t");
                    break;

                case '\b':
                    escaped.append("\\b");
                    break;

                case '\f':
                    escaped.append("\\f");
                    break;

                default:
                    if (character < 0x20) {
                        escaped.append(String.format(
                                "\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
            }
        }

        return escaped.toString();
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }
}

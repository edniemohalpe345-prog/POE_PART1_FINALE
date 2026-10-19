/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package quickchatapp;

// Import classes for file handling, JSON storage, collections and random IDs.
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class Message {

    // Maximum number of characters allowed in a message.
    private static final int MESSAGE_LIMIT = 250;

    // Default JSON file used to store messages.
    private static final Path DEFAULT_JSON_FILE =
            Paths.get("messages.json");

    // Stores all messages successfully sent during this session.
    private static final List<Message> SENT_MESSAGES =
            new ArrayList<>();

    // Tracks the total number of messages successfully sent.
    private static int totalMessagesSent = 0;

    // Variables containing the details of an individual message.
     String messageID;
     int messageNumber;
     String recipient;
     String messageText;
     String messageHash;
     Path jsonFile;
     int selectedAction = 0;

    // Constructor that generates a random 10-digit message ID.
    public Message(int messageNumber, String recipient,
                   String messageText) {
        this(generateMessageID(), messageNumber, recipient,
                messageText, DEFAULT_JSON_FILE);
    }

    // Constructor that accepts an existing message ID.
    public Message(String messageID, int messageNumber,
                   String recipient, String messageText) {
        this(messageID, messageNumber, recipient,
                messageText, DEFAULT_JSON_FILE);
    }

    // Constructor used by tests to specify a temporary JSON file.
    Message(String messageID, int messageNumber,
            String recipient, String messageText, Path jsonFile) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.jsonFile = jsonFile;

        // Generate the message hash.
        this.messageHash = createMessageHash();
    }

    // Generates a random message ID containing exactly 10 digits.
    private static String generateMessageID() {
        long id = ThreadLocalRandom.current()
                .nextLong(1_000_000_000L, 10_000_000_000L);

        return Long.toString(id);
    }

    // Checks that the message ID contains exactly 10 digits.
    public boolean checkMessageID() {
        return messageID != null
                && messageID.matches("[0-9]{10}");
    }

    // Validates the South African cellphone number.
    public String checkRecipientCell() {
        if (recipient != null
                && recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number "
                + "and try again.";
    }

    // Checks whether the message contains no more than 250 characters.
    public String checkMessageLength() {
        if (messageText != null
                && messageText.length() <= MESSAGE_LIMIT) {
            return "Message ready to send.";
        }

        // Calculate how many characters exceed the limit.
        int length = messageText == null ? 0 : messageText.length();
        int extraCharacters = Math.max(0, length - MESSAGE_LIMIT);

        return "Message exceeds 250 characters by "
                + extraCharacters
                + " characters; please reduce message length.";
    }

    // Creates a hash using the first two ID digits, message number,
    // first word and last word of the message.
    public String createMessageHash() {
        String firstTwoIDNumbers = "00";

        if (messageID != null && messageID.length() >= 2) {
            firstTwoIDNumbers = messageID.substring(0, 2);
        }

        String firstWord = "";
        String lastWord = "";

        // Extract the first and last words when text is available.
        if (messageText != null
                && !messageText.trim().isEmpty()) {
            String[] words = messageText.trim().split("\\s+");

            firstWord = cleanWord(words[0]);
            lastWord = cleanWord(words[words.length - 1]);
        }

        // Return the hash in uppercase.
        return firstTwoIDNumbers + ":"
                + messageNumber + ":"
                + firstWord.toUpperCase(Locale.ROOT)
                + lastWord.toUpperCase(Locale.ROOT);
    }

    // Removes punctuation and symbols from a word.
    private String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // Updates the recipient cellphone number.
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    // Updates the message text and recalculates the hash.
    public void setMessageText(String messageText) {
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Accepts an action number and processes the selected action.
    public String SentMessage(int action) {
        if (action < 1 || action > 3) {
            return "Invalid message option.";
        }

        this.selectedAction = action;
        return SentMessage();
    }

    // Validates and processes the selected message action.
    public String SentMessage() {
        if (!checkMessageID()) {
            return "Message ID is not correctly formatted.";
        }

        if (!checkRecipientCell().equals(
                "Cell phone number successfully captured.")) {
            return checkRecipientCell();
        }

        if (!checkMessageLength().equals("Message ready to send.")) {
            return checkMessageLength();
        }

        // Action 1: Send the message.
        if (selectedAction == 1) {
            SENT_MESSAGES.add(this);
            totalMessagesSent++;
            return "Message successfully sent.";
        }

        // Action 2: Disregard the message.
        if (selectedAction == 2) {
            return "Press 0 to delete the message.";
        }

        // Action 3: Store the message in a JSON file.
        if (selectedAction == 3) {
            return storeMessages();
        }

        return "Invalid message option.";
    }

    // Displays all successfully sent messages.
    public String printMessage() {
        if (SENT_MESSAGES.isEmpty()) {
            return "No messages have been sent yet.";
        }

        StringBuilder output = new StringBuilder();

        for (Message sentMessage : SENT_MESSAGES) {
            output.append("Message Number: ")
                    .append(sentMessage.messageNumber)
                    .append("\nMessage ID: ")
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

    // Returns the total number of messages sent.
    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Returns the total sent-message count.
    public static int getTotalSentCount() {
        return totalMessagesSent;
    }

    // Saves the current message to a JSON file.
    public String storeMessages() {
        String existingJson = "";

        try {
            // Read existing JSON data if the file exists.
            if (Files.exists(jsonFile)) {
                existingJson = new String(
                        Files.readAllBytes(jsonFile),
                        StandardCharsets.UTF_8
                ).trim();
            }

            // Convert the message to a JSON object.
            String newObject = toJsonObject();
            String updatedJson;

            // Create a new JSON array if the file is empty.
            if (existingJson.isEmpty()
                    || existingJson.equals("[]")) {
                updatedJson = "[\n  " + newObject + "\n]\n";

            // Append the message if the existing file is a JSON array.
            } else if (existingJson.startsWith("[")
                    && existingJson.endsWith("]")) {

                String existingEntries = existingJson.substring(
                        1, existingJson.length() - 1
                ).trim();

                updatedJson = "[\n  "
                        + existingEntries
                        + (existingEntries.isEmpty() ? "" : ",")
                        + "\n  " + newObject + "\n]\n";

            } else {
                return "Could not store the message: messages.json is not "
                        + "a valid JSON array.";
            }

            // Write the updated JSON data to the file.
            Files.write(
                    jsonFile,
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

    // Converts the message details into a JSON object.
    private String toJsonObject() {
        return "{\n"
                + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(messageText) + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(messageHash) + "\"\n"
                + "  }";
    }

    // Escapes special characters so the stored JSON remains valid.
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);

            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");

                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format(
                                Locale.ROOT,
                                "\\u%04x",
                                (int) character
                        ));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }

    // Returns the message ID.
    public String getMessageID() {
        return messageID;
    }

    // Returns the message number.
    public int getMessageNumber() {
        return messageNumber;
    }

    // Returns the recipient cellphone number.
    public String getRecipient() {
        return recipient;
    }

    // Returns the message text.
    public String getMessageText() {
        return messageText;
    }

    // Returns the message hash.
    public String getMessageHash() {
        return messageHash;
    }
}

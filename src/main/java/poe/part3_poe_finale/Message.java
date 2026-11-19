package poe.part3_poe_finale;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class Message {
    private static final int MESSAGE_LIMIT = 250;
    private static final Path DEFAULT_JSON_FILE = Path.of("messages.json");
    private static final List<Message> SENT_MESSAGES = new ArrayList<>();
    private static int totalMessagesSent;

    String messageID;
    int messageNumber;
    String recipient;
    String messageText;
    String messageHash;
    Path jsonFile;
    int selectedAction;

    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText, DEFAULT_JSON_FILE);
    }

    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this(messageID, messageNumber, recipient, messageText, DEFAULT_JSON_FILE);
    }

    Message(String messageID, int messageNumber, String recipient, String messageText, Path jsonFile) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.jsonFile = jsonFile;
        this.messageHash = createMessageHash();
    }

    private static String generateMessageID() {
        return Long.toString(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("[0-9]{10}");
    }

    public String checkRecipientCell() {
        return recipient != null && recipient.matches("^\\+27[0-9]{9}$")
                ? "Cell phone number successfully captured."
                : "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {
        if (messageText != null && messageText.length() <= MESSAGE_LIMIT) return "Message ready to send.";
        int length = messageText == null ? 0 : messageText.length();
        return "Message exceeds 250 characters by " + Math.max(0, length - MESSAGE_LIMIT)
                + " characters; please reduce message length.";
    }

    public String createMessageHash() {
        String idPart = messageID != null && messageID.length() >= 2 ? messageID.substring(0, 2) : "00";
        String[] words = messageText == null || messageText.trim().isEmpty()
                ? new String[0] : messageText.trim().split("\\s+");
        String first = words.length == 0 ? "" : cleanWord(words[0]);
        String last = words.length == 0 ? "" : cleanWord(words[words.length - 1]);
        return idPart + ":" + messageNumber + ":" + first.toUpperCase(Locale.ROOT) + last.toUpperCase(Locale.ROOT);
    }

    private String cleanWord(String word) { return word.replaceAll("[^A-Za-z0-9]", ""); }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setMessageText(String messageText) { this.messageText = messageText; this.messageHash = createMessageHash(); }

    public String SentMessage(int action) {
        if (action < 1 || action > 3) return "Invalid message option.";
        selectedAction = action;
        return SentMessage();
    }

    public String SentMessage() {
        if (!checkMessageID()) return "Message ID is not correctly formatted.";
        if (!checkRecipientCell().equals("Cell phone number successfully captured.")) return checkRecipientCell();
        if (!checkMessageLength().equals("Message ready to send.")) return checkMessageLength();
        if (selectedAction == 1) {
            SENT_MESSAGES.add(this);
            totalMessagesSent++;
            return "Message successfully sent.";
        }
        if (selectedAction == 2) return "Press 0 to delete the message.";
        if (selectedAction == 3) return storeMessages();
        return "Invalid message option.";
    }

    public String printMessage() {
        if (SENT_MESSAGES.isEmpty()) return "No messages have been sent yet.";
        StringBuilder out = new StringBuilder();
        for (Message message : SENT_MESSAGES) {
            out.append("Message Number: ").append(message.messageNumber)
                    .append("\nMessage ID: ").append(message.messageID)
                    .append("\nMessage Hash: ").append(message.messageHash)
                    .append("\nRecipient: ").append(message.recipient)
                    .append("\nMessage: ").append(message.messageText).append("\n\n");
        }
        return out.toString().trim();
    }

    public int returnTotalMessages() { return totalMessagesSent; }
    public static int getTotalSentCount() { return totalMessagesSent; }

    public String storeMessages() {
        try {
            String existing = Files.exists(jsonFile) ? Files.readString(jsonFile, StandardCharsets.UTF_8).trim() : "";
            String object = "{\"messageID\":\"" + escape(messageID) + "\",\"messageNumber\":" + messageNumber
                    + ",\"recipient\":\"" + escape(recipient) + "\",\"message\":\"" + escape(messageText)
                    + "\",\"messageHash\":\"" + escape(messageHash) + "\"}";
            String updated;
            if (existing.isEmpty() || existing.equals("[]")) updated = "[\n" + object + "\n]\n";
            else if (existing.startsWith("[") && existing.endsWith("]"))
                updated = existing.substring(0, existing.length() - 1) + ",\n" + object + "\n]\n";
            else return "Could not store the message: messages.json is not a valid JSON array.";
            Files.writeString(jsonFile, updated, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return "Message successfully stored.";
        } catch (IOException ex) { return "Could not store the message in messages.json."; }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
}

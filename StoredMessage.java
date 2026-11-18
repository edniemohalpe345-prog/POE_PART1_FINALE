/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poe.part3_poe_finale;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Student
 */
public class StoredMessages {
    
    private static final Pattern JSON_OBJECT =
            Pattern.compile("\\{([^{}]*)\\}");
    private final Path jsonFile;
    private final List<MessageRecord> messages = new ArrayList<>();
    private String[] sentMessages = new String[0];
    private String[] disregardedMessages = new String[0];
    private String[] storedMessages = new String[0];
    private String[] messageHashes = new String[0];
    private String[] messageIDs = new String[0];
    private String[] recipients = new String[0];
    private String[] senders = new String[0];
    private String[] statuses = new String[0];
    public StoredMessages() throws IOException {
        this(Path.of("messages.json"));
    }
    public StoredMessages(Path jsonFile) throws IOException {
        this.jsonFile = jsonFile;
        loadStoredMessagesFromJSON();
        populateArrays();
    }
    private static class MessageRecord {
        private final Message message;
        private String status;
        private String sender;
        private MessageRecord(Message message, String status, String sender) {
            this.message = message;
            this.status = status;
            this.sender = sender;
        }
    }
    public void addMessage(Message message, int action) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        String status = statusForAction(action);
        MessageRecord existing = findRecordByID(message.getMessageID());
        if (existing == null) {
            messages.add(new MessageRecord(message, status, "Developer"));
        } else {
            existing.status = status;
        }
        saveMessagesToJSON();
        populateArrays();
    }
    private String statusForAction(int action) {
        return switch (action) {
            case 1 -> "SENT";
            case 2 -> "DISREGARDED";
            case 3 -> "STORED";
            default -> throw new IllegalArgumentException("Invalid message action.");
        };
    }
    private MessageRecord findRecordByID(String messageID) {
        for (MessageRecord record : messages) {
            if (record.message.getMessageID().equals(messageID)) {
                return record;
            }
        }
        return null;
    }
    private void loadStoredMessagesFromJSON() throws IOException {
        if (Files.notExists(jsonFile)) {
            return;
        }
        String json = Files.readString(jsonFile, StandardCharsets.UTF_8);
        Matcher objectMatcher = JSON_OBJECT.matcher(json);
        while (objectMatcher.find()) {
            String object = objectMatcher.group(1);
            String id = readStringField(object, "messageID");
            if (id == null) {
                id = readStringField(object, "id");
            }
            String recipient = readStringField(object, "recipient");
            String text = readStringField(object, "message");
            if (text == null) {
                text = readStringField(object, "messageText");
            }
            if (id == null || recipient == null || text == null) {
                continue;
            }
            String numberText = readNumberField(object, "messageNumber");
            int number = numberText == null
                    ? messages.size() + 1
                    : Integer.parseInt(numberText);
            Message message = new Message(id, number, recipient, text, jsonFile);
            String savedHash = readStringField(object, "messageHash");
            if (savedHash != null) {
                message.messageHash = savedHash;
            }
            String status = readStringField(object, "status");
            if (status == null) {
                status = "STORED";
            }
            String sender = readStringField(object, "sender");
            if (sender == null) {
                sender = "Developer";
            }
            messages.add(new MessageRecord(
                    message,
                    normalizeStatus(status),
                    sender
            ));
        }
    }
    private String readStringField(String object, String fieldName) {
        String expression = "\"" + Pattern.quote(fieldName)
                + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"";
        Matcher matcher = Pattern.compile(expression).matcher(object);
        if (!matcher.find()) {
            return null;
        }
        return unescapeJson(matcher.group(1));
    }
    private String readNumberField(String object, String fieldName) {
        String expression = "\"" + Pattern.quote(fieldName)
                + "\"\\s*:\\s*(\\d+)";
        Matcher matcher = Pattern.compile(expression).matcher(object);
        return matcher.find() ? matcher.group(1) : null;
    }
    private String unescapeJson(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character != '\\' || i + 1 >= value.length()) {
                result.append(character);
                continue;
            }
            char escaped = value.charAt(++i);
            switch (escaped) {
                case '"' -> result.append('"');
                case '\\' -> result.append('\\');
                case '/' -> result.append('/');
                case 'b' -> result.append('\b');
                case 'f' -> result.append('\f');
                case 'n' -> result.append('\n');
                case 'r' -> result.append('\r');
                case 't' -> result.append('\t');
                case 'u' -> {
                    if (i + 4 < value.length()) {
                        String hex = value.substring(i + 1, i + 5);
                        result.append((char) Integer.parseInt(hex, 16));
                        i += 4;
                    }
                }
                default -> result.append(escaped);
            }
        }
        return result.toString();
    }
    private String escapeJson(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> result.append(character);
            }
        }
        return result.toString();
    }
    private String normalizeStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("DISREGARD")) {
            return "DISREGARDED";
        }
        if (!normalized.equals("SENT")
                && !normalized.equals("STORED")
                && !normalized.equals("DISREGARDED")) {
            return "STORED";
        }
        return normalized;
    }
    private void saveMessagesToJSON() throws IOException {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            MessageRecord record = messages.get(i);
            Message message = record.message;
            json.append("  {\n")
                    .append("    \"messageID\": \"")
                    .append(escapeJson(message.getMessageID())).append("\",\n")
                    .append("    \"messageNumber\": ")
                    .append(message.getMessageNumber()).append(",\n")
                    .append("    \"recipient\": \"")
                    .append(escapeJson(message.getRecipient())).append("\",\n")
                    .append("    \"message\": \"")
                    .append(escapeJson(message.getMessageText())).append("\",\n")
                    .append("    \"messageHash\": \"")
                    .append(escapeJson(message.getMessageHash())).append("\",\n")
                    .append("    \"status\": \"")
                    .append(escapeJson(record.status)).append("\",\n")
                    .append("    \"sender\": \"")
                    .append(escapeJson(record.sender)).append("\"\n")
                    .append("  }");
            if (i < messages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");
        Files.writeString(
                jsonFile,
                json.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }
    public void populateArrays() {
        sentMessages = messagesWithStatus("SENT");
        disregardedMessages = messagesWithStatus("DISREGARDED");
        storedMessages = messagesWithStatus("STORED");
        messageHashes = messages.stream()
                .map(record -> record.message.getMessageHash())
                .toArray(String[]::new);
        messageIDs = messages.stream()
                .map(record -> record.message.getMessageID())
                .toArray(String[]::new);
        recipients = messages.stream()
                .map(record -> record.message.getRecipient())
                .toArray(String[]::new);
        senders = messages.stream()
                .map(record -> record.sender)
                .toArray(String[]::new);
        statuses = messages.stream()
                .map(record -> record.status)
                .toArray(String[]::new);
    }
    private String[] messagesWithStatus(String status) {
        return messages.stream()
                .filter(record -> record.status.equals(status))
                .map(record -> record.message.getMessageText())
                .toArray(String[]::new);
    }
    public String[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentMessages.length);
    }
    public String[] getDisregardedMessages() {
        return Arrays.copyOf(disregardedMessages, disregardedMessages.length);
    }
    public String[] getStoredMessages() {
        return Arrays.copyOf(storedMessages, storedMessages.length);
    }
    public String[] getMessageHashes() {
        return Arrays.copyOf(messageHashes, messageHashes.length);
    }
    public String[] getMessageIDs() {
        return Arrays.copyOf(messageIDs, messageIDs.length);
    }
    public String[] getRecipients() {
        return Arrays.copyOf(recipients, recipients.length);
    }
    public String[] getSenders() {
        return Arrays.copyOf(senders, senders.length);
    }
    public String[] getStatuses() {
        return Arrays.copyOf(statuses, statuses.length);
    }
    public String getLongestMessage() {
        return messages.stream()
                .map(record -> record.message.getMessageText())
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }
    public String searchByMessageID(String id) {
        for (MessageRecord record : messages) {
            if (record.message.getMessageID().equalsIgnoreCase(id.trim())) {
                return "Recipient: " + record.message.getRecipient()
                        + System.lineSeparator()
                        + "Message: " + record.message.getMessageText();
            }
        }
        return "No message was found with that ID.";
    }
    public String searchByRecipient(String recipient) {
        String searchTerm = recipient.trim().toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder();
        for (MessageRecord record : messages) {
            if (record.message.getRecipient().toLowerCase(Locale.ROOT)
                    .equals(searchTerm)) {
                result.append("ID: ")
                        .append(record.message.getMessageID())
                        .append(" | Status: ")
                        .append(record.status)
                        .append(" | Message: ")
                        .append(record.message.getMessageText())
                        .append(System.lineSeparator());
            }
        }
        return result.length() == 0
                ? "No messages were found for that recipient."
                : result.toString();
    }
    public boolean deleteMessageByHash(String hash) throws IOException {
        boolean removed = messages.removeIf(record ->
                record.message.getMessageHash().equalsIgnoreCase(hash.trim())
        );
        if (removed) {
            saveMessagesToJSON();
            populateArrays();
        }
        return removed;
    }
    public String createMessageReport() {
        if (messages.isEmpty()) {
            return "No messages are available.";
        }
        StringBuilder report = new StringBuilder("MESSAGE REPORT\n")
                .append("Total messages: ")
                .append(messages.size())
                .append("\n");
        for (MessageRecord record : messages) {
            Message message = record.message;
            report.append("\nMessage ID: ").append(message.getMessageID())
                    .append("\nMessage Hash: ").append(message.getMessageHash())
                    .append("\nSender: ").append(record.sender)
                    .append("\nRecipient: ").append(message.getRecipient())
                    .append("\nStatus: ").append(record.status)
                    .append("\nMessage: ").append(message.getMessageText())
                    .append("\n");
        }
        return report.toString();
    }
    public void showStoredMessagesMenu(Scanner input) {
        boolean running = true;
        while (running) {
            System.out.println("\nSTORED MESSAGES");
            System.out.println("1. Display sender and recipient");
            System.out.println("2. Display the longest message");
            System.out.println("3. Search by message ID");
            System.out.println("4. Search by recipient");
            System.out.println("5. Delete a message by hash");
            System.out.println("6. Display the full message report");
            System.out.println("0. Return to the main menu");
            System.out.print("Choose an option: ");
            String choice = input.nextLine().trim();
            try {
                switch (Integer.parseInt(choice)) {
                    case 1 -> displaySendersAndRecipients();
                    case 2 -> {
                        String longest = getLongestMessage();
                        System.out.println(longest == null
                                ? "There are no messages."
                                : "Longest message: " + longest);
                    }
                    case 3 -> {
                        System.out.print("Enter message ID: ");
                        System.out.println(searchByMessageID(input.nextLine()));
                    }
                    case 4 -> {
                        System.out.print("Enter recipient number: ");
                        System.out.println(searchByRecipient(input.nextLine()));
                    }
                    case 5 -> {
                        System.out.print("Enter message hash: ");
                        if (deleteMessageByHash(input.nextLine())) {
                            System.out.println("Message deleted.");
                        } else {
                            System.out.println("No message was found with that hash.");
                        }
                    }
                    case 6 -> System.out.println(createMessageReport());
                    case 0 -> running = false;
                    default -> System.out.println("Choose a number from 0 to 6.");
                }
            } catch (NumberFormatException exception) {
                System.out.println("Enter a number from 0 to 6.");
            } catch (IOException exception) {
                System.out.println("Could not update messages.json: " + exception.getMessage());
            }
        }
    }
    private void displaySendersAndRecipients() {
        if (messages.isEmpty()) {
            System.out.println("There are no messages.");
            return;
        }
        for (MessageRecord record : messages) {
            System.out.printf(
                    "ID: %s | Sender: %s | Recipient: %s%n",
                    record.message.getMessageID(),
                    record.sender,
                    record.message.getRecipient()
            );
        }
    }
}

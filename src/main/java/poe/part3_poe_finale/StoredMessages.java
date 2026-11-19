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

public class StoredMessages {
    private static final Pattern OBJECT = Pattern.compile("\\{([^{}]*)\\}");
    private final Path jsonFile;
    private final List<Record> messages = new ArrayList<>();
    private String[] sent = new String[0], disregarded = new String[0], stored = new String[0];
    private String[] hashes = new String[0], ids = new String[0], recipients = new String[0], senders = new String[0], statuses = new String[0];

    private static final class Record {
        final Message message;
        String status;
        final String sender;
        Record(Message message, String status, String sender) { this.message = message; this.status = status; this.sender = sender; }
    }

    public StoredMessages() throws IOException { this(Path.of("messages.json")); }
    public StoredMessages(Path jsonFile) throws IOException { this.jsonFile = jsonFile; load(); populateArrays(); }

    public void addMessage(Message message, int action) throws IOException {
        if (message == null) throw new IllegalArgumentException("Message cannot be null.");
        String status = switch (action) { case 1 -> "SENT"; case 2 -> "DISREGARDED"; case 3 -> "STORED"; default -> throw new IllegalArgumentException("Invalid message action."); };
        Record existing = find(message.getMessageID());
        if (existing == null) messages.add(new Record(message, status, "Developer")); else existing.status = status;
        save(); populateArrays();
    }

    private Record find(String id) { for (Record r : messages) if (r.message.getMessageID().equals(id)) return r; return null; }
    private void load() throws IOException {
        if (!Files.exists(jsonFile)) return;
        Matcher objects = OBJECT.matcher(Files.readString(jsonFile, StandardCharsets.UTF_8));
        while (objects.find()) {
            String object = objects.group(1);
            String id = stringField(object, "messageID");
            String recipient = stringField(object, "recipient");
            String text = stringField(object, "message");
            if (id == null || recipient == null || text == null) continue;
            String n = numberField(object, "messageNumber");
            Message m = new Message(id, n == null ? messages.size() + 1 : Integer.parseInt(n), recipient, text, jsonFile);
            String hash = stringField(object, "messageHash"); if (hash != null) m.messageHash = hash;
            String status = stringField(object, "status"); if (status == null) status = "STORED";
            String sender = stringField(object, "sender"); if (sender == null) sender = "Developer";
            messages.add(new Record(m, normalize(status), sender));
        }
    }

    private String stringField(String object, String key) {
        Matcher m = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"").matcher(object);
        return m.find() ? unescape(m.group(1)) : null;
    }
    private String numberField(String object, String key) {
        Matcher m = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*(\\d+)").matcher(object);
        return m.find() ? m.group(1) : null;
    }
    private String unescape(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char e = s.charAt(++i);
                switch (e) { case 'n' -> out.append('\n'); case 'r' -> out.append('\r'); case 't' -> out.append('\t'); case '"' -> out.append('"'); case '\\' -> out.append('\\'); case '/' -> out.append('/'); default -> out.append(e); }
            } else out.append(c);
        }
        return out.toString();
    }
    private String escape(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t"); }
    private String normalize(String s) { String v = s.trim().toUpperCase(Locale.ROOT); if (v.equals("DISREGARD")) v = "DISREGARDED"; return List.of("SENT", "STORED", "DISREGARDED").contains(v) ? v : "STORED"; }

    private void save() throws IOException {
        StringBuilder out = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            Record r = messages.get(i); Message m = r.message;
            out.append("  {\"messageID\":\"").append(escape(m.getMessageID())).append("\",\"messageNumber\":").append(m.getMessageNumber())
                    .append(",\"recipient\":\"").append(escape(m.getRecipient())).append("\",\"message\":\"").append(escape(m.getMessageText()))
                    .append("\",\"messageHash\":\"").append(escape(m.getMessageHash())).append("\",\"status\":\"").append(r.status)
                    .append("\",\"sender\":\"").append(escape(r.sender)).append("\"}");
            if (i + 1 < messages.size()) out.append(',');
            out.append('\n');
        }
        out.append("]\n");
        Files.writeString(jsonFile, out.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public void populateArrays() {
        sent = withStatus("SENT"); disregarded = withStatus("DISREGARDED"); stored = withStatus("STORED");
        hashes = messages.stream().map(r -> r.message.getMessageHash()).toArray(String[]::new);
        ids = messages.stream().map(r -> r.message.getMessageID()).toArray(String[]::new);
        recipients = messages.stream().map(r -> r.message.getRecipient()).toArray(String[]::new);
        senders = messages.stream().map(r -> r.sender).toArray(String[]::new);
        statuses = messages.stream().map(r -> r.status).toArray(String[]::new);
    }
    private String[] withStatus(String status) { return messages.stream().filter(r -> r.status.equals(status)).map(r -> r.message.getMessageText()).toArray(String[]::new); }
    public String[] getSentMessages() { return Arrays.copyOf(sent, sent.length); }
    public String[] getDisregardedMessages() { return Arrays.copyOf(disregarded, disregarded.length); }
    public String[] getStoredMessages() { return Arrays.copyOf(stored, stored.length); }
    public String[] getMessageHashes() { return Arrays.copyOf(hashes, hashes.length); }
    public String[] getMessageIDs() { return Arrays.copyOf(ids, ids.length); }
    public String[] getRecipients() { return Arrays.copyOf(recipients, recipients.length); }
    public String[] getSenders() { return Arrays.copyOf(senders, senders.length); }
    public String[] getStatuses() { return Arrays.copyOf(statuses, statuses.length); }
    public String getLongestMessage() { return messages.stream().map(r -> r.message.getMessageText()).max(Comparator.comparingInt(String::length)).orElse(null); }
    public String searchByMessageID(String id) { for (Record r : messages) if (r.message.getMessageID().equalsIgnoreCase(id.trim())) return "Recipient: " + r.message.getRecipient() + System.lineSeparator() + "Message: " + r.message.getMessageText(); return "No message was found with that ID."; }
    public String searchByRecipient(String recipient) {
        String term = recipient.trim().toLowerCase(Locale.ROOT); StringBuilder out = new StringBuilder();
        for (Record r : messages) if (r.message.getRecipient().toLowerCase(Locale.ROOT).equals(term)) out.append("ID: ").append(r.message.getMessageID()).append(" | Status: ").append(r.status).append(" | Message: ").append(r.message.getMessageText()).append(System.lineSeparator());
        return out.length() == 0 ? "No messages were found for that recipient." : out.toString();
    }
    public boolean deleteMessageByHash(String hash) throws IOException { boolean removed = messages.removeIf(r -> r.message.getMessageHash().equalsIgnoreCase(hash.trim())); if (removed) { save(); populateArrays(); } return removed; }
    public String createMessageReport() {
        if (messages.isEmpty()) return "No messages are available.";
        StringBuilder out = new StringBuilder("MESSAGE REPORT\nTotal messages: ").append(messages.size()).append('\n');
        for (Record r : messages) out.append("\nMessage ID: ").append(r.message.getMessageID()).append("\nMessage Hash: ").append(r.message.getMessageHash()).append("\nSender: ").append(r.sender).append("\nRecipient: ").append(r.message.getRecipient()).append("\nStatus: ").append(r.status).append("\nMessage: ").append(r.message.getMessageText()).append('\n');
        return out.toString();
    }
    public void showStoredMessagesMenu(Scanner input) {
        boolean running = true;
        while (running) {
            System.out.println("\nSTORED MESSAGES\n1. Display sender and recipient\n2. Display the longest message\n3. Search by message ID\n4. Search by recipient\n5. Delete a message by hash\n6. Display the full message report\n0. Return to the main menu");
            System.out.print("Choose an option: "); String choice = input.nextLine().trim();
            try {
                switch (Integer.parseInt(choice)) {
                    case 1 -> displaySendersAndRecipients();
                    case 2 -> System.out.println(getLongestMessage() == null ? "There are no messages." : "Longest message: " + getLongestMessage());
                    case 3 -> { System.out.print("Enter message ID: "); System.out.println(searchByMessageID(input.nextLine())); }
                    case 4 -> { System.out.print("Enter recipient number: "); System.out.println(searchByRecipient(input.nextLine())); }
                    case 5 -> { System.out.print("Enter message hash: "); System.out.println(deleteMessageByHash(input.nextLine()) ? "Message deleted." : "No message was found with that hash."); }
                    case 6 -> System.out.println(createMessageReport());
                    case 0 -> running = false;
                    default -> System.out.println("Choose a number from 0 to 6.");
                }
            } catch (NumberFormatException ex) { System.out.println("Enter a number from 0 to 6."); }
            catch (IOException ex) { System.out.println("Could not update messages.json: " + ex.getMessage()); }
        }
    }
    private void displaySendersAndRecipients() {
        if (messages.isEmpty()) { System.out.println("There are no messages."); return; }
        for (Record r : messages) System.out.printf("ID: %s | Sender: %s | Recipient: %s%n", r.message.getMessageID(), r.sender, r.message.getRecipient());
    }
}

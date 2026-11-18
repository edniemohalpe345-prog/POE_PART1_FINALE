/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poe.part3_poe_finale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Student
 */

class StoredMessagesTest {
    @TempDir
    Path temporaryDirectory;
    @Test
    void populatesTheRequiredArraysDynamically() throws IOException {
        StoredMessages stored = createMessages();
        assertArrayEquals(
                new String[]{"Did you get the cake?", "It's dinner time!"},
                stored.getSentMessages()
        );
        assertArrayEquals(
                new String[]{"Yohhoooo, I am at your gate."},
                stored.getDisregardedMessages()
        );
        assertArrayEquals(
                new String[]{
                    "Where are you? You are late! I have asked you to be on time.",
                    "Ok, I am leaving without you."
                },
                stored.getStoredMessages()
        );
        assertEquals(5, stored.getMessageIDs().length);
        assertEquals(5, stored.getMessageHashes().length);
        assertEquals(5, stored.getRecipients().length);
        assertEquals(5, stored.getSenders().length);
        assertEquals(5, stored.getStatuses().length);
    }
    @Test
    void returnsTheLongestMessage() throws IOException {
        StoredMessages stored = createMessages();
        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                stored.getLongestMessage()
        );
    }
    @Test
    void searchesByMessageID() throws IOException {
        StoredMessages stored = createMessages();
        String result = stored.searchByMessageID("4234567890");
        assertTrue(result.contains("0838884567"));
        assertTrue(result.contains("It's dinner time!"));
    }
    @Test
    void searchesMessagesByRecipient() throws IOException {
        StoredMessages stored = createMessages();
        String result = stored.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you?"));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }
    @Test
    void deletesAMessageByHashAndPersistsTheChange() throws IOException {
        Path jsonFile = temporaryDirectory.resolve("messages.json");
        StoredMessages stored = createMessages(jsonFile);
        String hash = stored.getMessageHashes()[1];
        assertTrue(stored.deleteMessageByHash(hash));
        assertFalse(stored.deleteMessageByHash(hash));
        StoredMessages reloaded = new StoredMessages(jsonFile);
        assertTrue(
                reloaded.searchByMessageID("2234567890")
                        .contains("No message was found")
        );
    }
    @Test
    void reportIncludesAllMessageDetails() throws IOException {
        StoredMessages stored = createMessages();
        String report = stored.createMessageReport();
        assertTrue(report.contains("Message ID: 1234567890"));
        assertTrue(report.contains("Message Hash:"));
        assertTrue(report.contains("Sender: Developer"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Status: SENT"));
        assertTrue(report.contains("Message: Did you get the cake?"));
    }
    @Test
    void loadsTheFiveMessagesFromTheProjectJSONFile() throws IOException {
        StoredMessages stored = new StoredMessages(Path.of("messages.json"));
        assertEquals(5, stored.getMessageIDs().length);
    }
    private StoredMessages createMessages() throws IOException {
        return createMessages(
                temporaryDirectory.resolve("test-messages.json")
        );
    }
    private StoredMessages createMessages(Path jsonFile) throws IOException {
        StoredMessages stored = new StoredMessages(jsonFile);
        stored.addMessage(new Message(
                "1234567890", 1, "+27834557896",
                "Did you get the cake?", jsonFile
        ), 1);
        stored.addMessage(new Message(
                "2234567890", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.",
                jsonFile
        ), 3);
        stored.addMessage(new Message(
                "3234567890", 3, "+27834484567",
                "Yohhoooo, I am at your gate.", jsonFile
        ), 2);
        stored.addMessage(new Message(
                "4234567890", 4, "0838884567",
                "It's dinner time!", jsonFile
        ), 1);
        stored.addMessage(new Message(
                "5234567890", 5, "+27838884567",
                "Ok, I am leaving without you.", jsonFile
        ), 3);
        return stored;
    }
}

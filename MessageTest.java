/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quickchatapp;

/**
 *
 * @author Student
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MessageTest {
    @Test
    public void messageIdIsTenDigits() {
        Message message = new Message(
                0, "+27838968976", "Hi Mike, can you join us for dinner tonight?"
        );
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
    }
    @Test
    public void messageIdLongerThanTenDigitsIsRejected() {
        Message message = new Message(
                "12345678901", 0, "+27838968976", "Hello"
        );
        assertFalse(message.checkMessageID());
    }
    @Test
    public void messageAtTheLimitIsAccepted() {
        String text = repeat('a', 250);
        Message message = new Message(0, "+27838968976", text);
        assertEquals("Message ready to send.", message.checkMessageLength());
    }
    @Test
    public void messageLongerThanLimitReportsExtraCharacters() {
        String text = repeat('a', 253);
        Message message = new Message(0, "+27838968976", text);
        assertEquals(
                "Message exceeds 250 characters by 3 characters; "
                        + "please reduce message length.",
                message.checkMessageLength()
        );
    }
    @Test
    public void southAfricanInternationalRecipientIsAccepted() {
        Message message = new Message(
                0, "+27838968976", "Hi Mike, can you join us for dinner tonight?"
        );
        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell()
        );
    }
    @Test
    public void localNumberWithoutCountryCodeIsRejected() {
        Message message = new Message(
                0, "0838968976", "Hi Mike, can you join us for dinner tonight?"
        );
        assertEquals(
                "Cell phone number is incorrectly formatted or does not "
                        + "contain an international code. Please correct the "
                        + "number and try again.",
                message.checkRecipientCell()
        );
    }
    @Test
    public void messageHashUsesIdNumberAndFirstAndLastWords() {
        Message message = new Message(
                "1234567890",
                0,
                "+27838968976",
                "Hi Mike, can you join us for dinner tonight?"
        );
        assertEquals("12:0:HITONIGHT", message.createMessageHash());
    }
    @Test
    public void sendActionIncreasesTotalSent() {
        Message message = new Message(
                0, "+27838968976", "Hi Mike, can you join us for dinner tonight?"
        );
        int beforeSending = message.returnTotalMessages();
        assertEquals("Message successfully sent.", message.SentMessage(1));
        assertEquals(beforeSending + 1, message.returnTotalMessages());
    }
    @Test
    public void disregardActionDoesNotIncreaseTotalSent() {
        Message message = new Message(
                0, "+27838968976", "Hi Mike, can you join us for dinner tonight?"
        );
        int beforeSending = message.returnTotalMessages();
        assertEquals(
                "Press 0 to delete the message.",
                message.SentMessage(2)
        );
        assertEquals(beforeSending, message.returnTotalMessages());
    }
    private String repeat(char character, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(character);
        }
        return result.toString();
    }
}
}

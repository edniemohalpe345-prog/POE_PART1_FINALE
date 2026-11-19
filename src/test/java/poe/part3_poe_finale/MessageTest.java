package poe.part3_poe_finale;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    @Test void generatesTenDigitId() { assertTrue(new Message(1, "+27838968976", "Hello there").checkMessageID()); }
    @Test void rejectsInvalidId() { assertFalse(new Message("12345678901", 1, "+27838968976", "Hello").checkMessageID()); }
    @Test void validatesMessageLength() {
        assertEquals("Message ready to send.", new Message(1, "+27838968976", "a".repeat(250)).checkMessageLength());
        assertTrue(new Message(1, "+27838968976", "a".repeat(253)).checkMessageLength().contains("by 3 characters"));
    }
    @Test void validatesInternationalRecipient() {
        assertEquals("Cell phone number successfully captured.", new Message(1, "+27838968976", "Hello").checkRecipientCell());
        assertTrue(new Message(1, "0838968976", "Hello").checkRecipientCell().contains("incorrectly formatted"));
    }
    @Test void createsExpectedHash() { assertEquals("12:0:HITONIGHT", new Message("1234567890", 0, "+27838968976", "Hi Mike, can you join us for dinner tonight?").createMessageHash()); }
}

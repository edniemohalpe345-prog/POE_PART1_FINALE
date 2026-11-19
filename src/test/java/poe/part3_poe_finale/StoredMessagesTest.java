package poe.part3_poe_finale;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class StoredMessagesTest {
    @TempDir Path temp;
    @Test void storesSearchesAndDeletesMessages() throws IOException {
        Path file = temp.resolve("messages.json");
        StoredMessages stored = new StoredMessages(file);
        Message message = new Message("1234567890", 1, "+27834557896", "Dinner tonight", file);
        stored.addMessage(message, 1);
        assertArrayEquals(new String[]{"Dinner tonight"}, stored.getSentMessages());
        assertTrue(stored.searchByMessageID("1234567890").contains("Dinner tonight"));
        StoredMessages reloaded = new StoredMessages(file);
        assertEquals(1, reloaded.getMessageIDs().length);
        assertTrue(reloaded.deleteMessageByHash(message.getMessageHash()));
        assertEquals(0, reloaded.getMessageIDs().length);
    }
}

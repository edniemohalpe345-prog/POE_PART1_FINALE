package poe.part3_poe_finale;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class QuickChatApp {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final Path JSON_FILE = Path.of("messages.json");

    public static void main(String[] args) {
        System.out.println("REGISTRATION");
        System.out.print("FirstName: "); String first = INPUT.nextLine().trim();
        System.out.print("LastName: "); String last = INPUT.nextLine().trim();
        System.out.print("userName: "); String username = INPUT.nextLine().trim();
        System.out.print("password: "); String password = INPUT.nextLine();
        System.out.print("Cellphone (+27...): "); String phone = INPUT.nextLine().trim();
        Login user = new Login(first, last, username, password, phone);
        String registration = user.registerUser();
        System.out.println(registration);
        if (!registration.equals("User has been registered successfully.")) return;

        System.out.println("\nLOGIN");
        System.out.print("userName: "); String loginName = INPUT.nextLine().trim();
        System.out.print("password: "); String loginPassword = INPUT.nextLine();
        user.setLoginDetails(loginName, loginPassword);
        String loginStatus = user.returnLoginStatus();
        System.out.println(loginStatus);
        if (!loginStatus.startsWith("Welcome ")) return;

        try {
            StoredMessages messages = new StoredMessages(JSON_FILE);
            runMenu(messages);
        } catch (IOException ex) {
            System.out.println("Could not load messages.json: " + ex.getMessage());
        }
    }

    private static void runMenu(StoredMessages messages) {
        int limit = readPositiveNumber("How many messages would you like to enter? ");
        int entered = 0;
        boolean running = true;
        while (running) {
            System.out.println("\nQUICKCHAT MENU\n1) Send Messages\n2) Show recently sent messages\n3) Quit\n4) Stored Messages");
            switch (readOption("Choose an option: ", 1, 4)) {
                case 1 -> {
                    if (entered >= limit) System.out.println("You have entered the maximum number of messages for this session.");
                    else if (createAndProcessMessage(entered + 1, messages)) entered++;
                }
                case 2 -> {
                    String[] sent = messages.getSentMessages();
                    if (sent.length == 0) System.out.println("No messages have been sent yet.");
                    else for (int i = 0; i < sent.length; i++) System.out.println((i + 1) + ". " + sent[i]);
                }
                case 3 -> running = false;
                case 4 -> messages.showStoredMessagesMenu(INPUT);
                default -> throw new IllegalStateException("Unexpected menu option");
            }
        }
        System.out.println("Total messages sent: " + Message.getTotalSentCount());
        System.out.println("Goodbye.");
    }

    private static boolean createAndProcessMessage(int number, StoredMessages stored) {
        Message message = new Message(number, "", "First message");
        while (true) {
            System.out.print("Recipient cellphone number (+27...): ");
            message.setRecipient(INPUT.nextLine().trim());
            String result = message.checkRecipientCell();
            System.out.println(result);
            if (result.equals("Cell phone number successfully captured.")) break;
        }
        while (true) {
            System.out.print("Enter your message (maximum 250 characters): ");
            message.setMessageText(INPUT.nextLine());
            String result = message.checkMessageLength();
            System.out.println(result);
            if (result.equals("Message ready to send.")) break;
        }
        System.out.println("Message ID: " + message.getMessageID());
        System.out.println("Message Hash: " + message.getMessageHash());
        System.out.println("Choose action: 1) Send  2) Disregard  3) Store");
        int action = readOption("Choose an option: ", 1, 3);
        String result = message.SentMessage(action);
        System.out.println(result);
        try {
            stored.addMessage(message, action);
        } catch (IOException | IllegalArgumentException ex) {
            System.out.println("Could not update messages.json: " + ex.getMessage());
            return false;
        }
        if (action == 1 && result.equals("Message successfully sent.")) System.out.println(message.printMessage());
        return true;
    }

    private static int readPositiveNumber(String prompt) {
        while (true) { int value = readNumber(prompt); if (value > 0) return value; System.out.println("Please enter a number greater than zero."); }
    }
    private static int readOption(String prompt, int min, int max) {
        while (true) { int value = readNumber(prompt); if (value >= min && value <= max) return value; System.out.println("Please choose a number from " + min + " to " + max + "."); }
    }
    private static int readNumber(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(INPUT.nextLine().trim()); }
            catch (NumberFormatException ex) { System.out.println("Please enter a whole number."); }
        }
    }
}

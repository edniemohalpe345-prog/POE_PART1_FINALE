/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package poe.part3_poe_finale;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;


/**
 *
 * @author Student
 */
public class QuickChatApp {

    private static final Scanner INPUT = new Scanner(System.in);
    private static final Path jsonFile = Path.of("messages.json");

    public static void main(String[] args) {

        // REGISTRATION
        System.out.println("REGISTRATION");

        System.out.print("FirstName: ");
        String firstName = INPUT.nextLine().trim();

        System.out.print("LastName: ");
        String lastName = INPUT.nextLine().trim();

        System.out.print("userName: ");
        String username = INPUT.nextLine().trim();

        System.out.print("password: ");
        String password = INPUT.nextLine();

        System.out.print("Cellphone (+27...): ");
        String cellphone = INPUT.nextLine().trim();

        Login user = new Login(
                firstName,
                lastName,
                username,
                password,
                cellphone
        );

        String registrationResult = user.registerUser();
        System.out.println(registrationResult);

        if (!registrationResult.equals(
                "User has been registered successfully.")) {

            System.out.println(
                    "Registration failed. The chat application "
                    + "cannot continue."
            );
            return;
        }

        // LOGIN
        System.out.println("\nLOGIN");

        System.out.print("userName: ");
        String loginUsername = INPUT.nextLine().trim();

        System.out.print("password: ");
        String loginPassword = INPUT.nextLine();

        user.setLoginDetails(loginUsername, loginPassword);

        String loginStatus = user.returnLoginStatus();

        if (!loginStatus.startsWith("Welcome ")) {
            System.out.println(loginStatus);
            return;
        }

        System.out.println(loginStatus);
        System.out.println("Welcome to QuickChat.");

        // LOAD STORED MESSAGES
        StoredMessages storedMessages;

        try {
            storedMessages = new StoredMessages(jsonFile);

        } catch (IOException exception) {

            System.out.println(
                    "Could not load messages.json: "
                    + exception.getMessage()
            );
            return;
        }

        // ASK HOW MANY MESSAGES THE USER WANTS TO ENTER
        int messageLimit = readPositiveNumber(
                "How many messages would you like to enter? "
        );

        int messagesEntered = 0;
        boolean running = true;

        // MAIN MENU
        while (running) {

            showMenu();

            int option = readOption(
                    "Choose an option: ",
                    1,
                    4
            );

            switch (option) {

                case 1 -> {

                    if (messagesEntered >= messageLimit) {

                        System.out.println(
                                "You have entered the maximum number "
                                + "of messages for this session."
                        );

                    } else {

                        // Message numbering starts at 1.
                        createAndProcessMessage(
                                messagesEntered + 1,
                                storedMessages
                        );

                        messagesEntered++;
                    }
                }

                case 2 -> {

                    System.out.println("\nRECENTLY SENT MESSAGES");

                    // Display messages that were successfully sent.
                    Message recentMessages =
                            new Message(1, "", "First message");

                    System.out.println(recentMessages.printMessage());
                }

                case 3 -> running = false;

                case 4 -> storedMessages.showStoredMessagesMenu(INPUT);

                default -> System.out.println("Invalid option.");
            }
        }

        // DISPLAY TOTAL SENT MESSAGES
        System.out.println(
                "Total messages sent: "
                + Message.getTotalSentCount()
        );

        System.out.println("Goodbye.");
    }

    // DISPLAY MAIN MENU
    private static void showMenu() {

        System.out.println("\nQUICKCHAT MENU");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
        System.out.println("4) Stored Messages");
    }

    // CREATE AND PROCESS A MESSAGE
    private static void createAndProcessMessage(
            int messageNumber,
            StoredMessages storedMessages) {

        // Automatically generate a 10-digit message ID.
        Message message = new Message(
                messageNumber,
                "",
                "First message"
        );

        // VALIDATE RECIPIENT CELLPHONE NUMBER
        while (true) {

            System.out.print(
                    "Recipient cellphone number (+27...): "
            );

            message.setRecipient(INPUT.nextLine().trim());

            String result = message.checkRecipientCell();

            System.out.println(result);

            if (result.equals(
                    "Cell phone number successfully captured.")) {

                break;
            }
        }

        // VALIDATE MESSAGE LENGTH
        while (true) {

            System.out.print(
                    "Enter your message (maximum 250 characters): "
            );

            message.setMessageText(INPUT.nextLine());

            String result = message.checkMessageLength();

            System.out.println(result);

            if (result.equals("Message ready to send.")) {

                break;
            }
        }

        // DISPLAY MESSAGE INFORMATION
        System.out.println("\nMessage ID: "
                + message.getMessageID());

        System.out.println("Message Hash: "
                + message.getMessageHash());

        System.out.println("Recipient: "
                + message.getRecipient());

        System.out.println("Message: "
                + message.getMessageText());

        // MESSAGE OPTIONS
        System.out.println("\nChoose what to do with this message:");

        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message in JSON file");

        int action = readOption(
                "Choose an option: ",
                1,
                3
        );

        String result = message.SentMessage(action);

        System.out.println(result);

        // UPDATE STORED MESSAGES
        try {

            storedMessages.addMessage(message, action);

        } catch (IOException exception) {

            System.out.println(
                    "Could not update messages.json: "
                    + exception.getMessage()
            );
        }

        // DISPLAY SENT MESSAGES
        if (action == 1 && result.equals(
                "Message successfully sent.")) {

            System.out.println("\nSent messages:");

            System.out.println(message.printMessage());
        }

        // DISPLAY TOTAL SENT MESSAGES
        System.out.println(
                "Total messages sent: "
                + Message.getTotalSentCount()
        );
    }

    // READ A POSITIVE NUMBER
    private static int readPositiveNumber(String prompt) {

        while (true) {

            int number = readNumber(prompt);

            if (number > 0) {
                return number;
            }

            System.out.println(
                    "Please enter a number greater than zero."
            );
        }
    }

    // VALIDATE MENU OPTIONS
    private static int readOption(
            String prompt,
            int minimum,
            int maximum) {

        while (true) {

            int option = readNumber(prompt);

            if (option >= minimum && option <= maximum) {
                return option;
            }

            System.out.println(
                    "Please choose a number from "
                    + minimum + " to " + maximum + "."
            );
        }
    }

    // READ AN INTEGER
    private static int readNumber(String prompt) {

        while (true) {

            System.out.print(prompt);

            String entered = INPUT.nextLine();

            try {

                return Integer.parseInt(entered.trim());

            } catch (NumberFormatException exception) {

                System.out.println(
                        "Please enter a whole number."
                );
            }
        }
    }
}

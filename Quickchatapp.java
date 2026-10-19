/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quickchatapp;

import java.nio.file.Path;
import java.util.Scanner;


/**
 *
 * @author Student
 */


public class QuickChatApp {

    private static final Scanner INPUT = new Scanner(System.in);
    private static Path jsonFile;

    public static void main(String[] args) {

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
                firstName, lastName, username, password, cellphone
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

        System.out.println("\nLOGIN");

        System.out.print("userName: ");
        String loginUsername = INPUT.nextLine().trim();

        System.out.print("password: ");
        String loginPassword = INPUT.nextLine();

        user.setLoginDetails(loginUsername, loginPassword);

        if (!user.loginUser()) {
            System.out.println(user.returnLoginStatus());
            return;
        }

        System.out.println("Welcome to QuickChat.");

        int messageLimit = readPositiveNumber(
                "How many messages would you like to enter? "
        );

        int messagesEntered = 0;
        boolean running = true;

        while (running) {

            showMenu();

            int option = readOption("Choose an option: ", 1, 3);

            switch (option) {

                case 1 -> {
                    if (messagesEntered >= messageLimit) {
                        System.out.println(
                                "You have entered the maximum number "
                                        + "of messages for this session."
                        );
                    } else {
                        createAndProcessMessage(messagesEntered);
                        messagesEntered++;
                    }
                }

                case 2 -> {
                    Message recentMessages =
                            new Message("", 0, "", "First message", jsonFile);

                    System.out.println("\nRECENTLY SENT MESSAGES");
                    System.out.println(
                            recentMessages.printMessage()
                    );
                }

                case 3 -> running = false;

                default -> System.out.println("Invalid option.");
            }
        }

        System.out.println(
                "Total messages sent: "
                + Message.getTotalSentCount()
        );

        System.out.println("Goodbye.");
    }

    private static void showMenu() {
        System.out.println("\nQUICKCHAT MENU");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }

    private static void createAndProcessMessage(int messageNumber) {

        Message message = new Message("", messageNumber, "", "First message", jsonFile);

        // Validate recipient cellphone number.
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

        // Validate message length.
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

        System.out.println("\nMessage ID: "
                + message.getMessageID());

        System.out.println("Message Hash: "
                + message.getMessageHash());

        System.out.println("Recipient: "
                + message.getRecipient());

        System.out.println("Message: "
                + message.getMessageText());

        System.out.println("\nChoose what to do with this message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message in JSON file");

        int action = readOption("Choose an option: ", 1, 3);

        String result = message.SentMessage(action);
        System.out.println(result);

        if (action == 1 && result.equals(
                "Message successfully sent.")) {
            System.out.println("\nSent messages:");
            System.out.println(message.printMessage());
        }

        System.out.println("Total messages sent: "
                + Message.getTotalSentCount());
    }

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

    private static int readOption(
            String prompt, int minimum, int maximum) {

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


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.Scanner;

package quickchatapp;

/**
 *
 * @author Student
 */
public class Quickchatapp {
     private static final Scanner INPUT = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("REGISTRATION");
        System.out.print("FirstName: ");
        String firstName = INPUT.nextLine();
        System.out.print("LastName: ");
        String lastName = INPUT.nextLine();
        System.out.print("userName: ");
        String username = INPUT.nextLine();
        System.out.print("password: ");
        String password = INPUT.nextLine();
        System.out.print("Cellphone: ");
        String cellphone = INPUT.nextLine();
        Login user = new Login(
                firstName, lastName, username, password, cellphone
        );
        String registrationResult = user.registerUser();
        System.out.println(registrationResult);
        if (!registrationResult.equals(
                "User has been registered successfully.")) {
            System.out.println("Registration failed. The chat application "
                    + "cannot continue.");
            return;
        }
        System.out.println("\nLOGIN");
        System.out.print("userName: ");
        String loginUsername = INPUT.nextLine();
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
        int messagesCreated = 0;
        boolean running = true;
        while (running) {
            showMenu();
            int option = readNumber("Choose an option: ");
            if (option == 1) {
                if (messagesCreated >= messageLimit) {
                    System.out.println("You have entered the maximum number "
                            + "of messages for this session.");
                    continue;
                }
                createAndProcessMessage(messagesCreated);
                messagesCreated++;
            } else if (option == 2) {
                System.out.println("Coming Soon.");
            } else if (option == 3) {
                running = false;
            } else {
                System.out.println("Please choose 1, 2, or 3.");
            }
        }
        System.out.println("Total messages sent: "
                + Message.getTotalSentCount());
        System.out.println("Goodbye.");
    }
    private static void showMenu() {
        System.out.println("\nQUICKCHAT MENU");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }
    private static void createAndProcessMessage(int messageNumber) {
        Message message = new Message(messageNumber, "", "");
        while (true) {
            System.out.print("Recipient cellphone number (+27...): ");
            message.setRecipient(INPUT.nextLine());
            String recipientResult = message.checkRecipientCell();
            if (recipientResult.equals(
                    "Cell phone number successfully captured.")) {
                System.out.println(recipientResult);
                break;
            }
            System.out.println(recipientResult);
        }
        while (true) {
            System.out.print("Enter your message (maximum 250 characters): ");
            message.setMessageText(INPUT.nextLine());
            String lengthResult = message.checkMessageLength();
            if (lengthResult.equals("Message ready to send.")) {
                System.out.println(lengthResult);
                break;
            }
            System.out.println(lengthResult);
        }
        System.out.println("\nMessage ID: " + message.getMessageID());
        System.out.println("Message Hash: " + message.getMessageHash());
        System.out.println("Recipient: " + message.getRecipient());
        System.out.println("Message: " + message.getMessageText());
        System.out.println("\nChoose what to do with this message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message in JSON file");
        int action = readNumber("Choose an option: ");
        System.out.println(message.SentMessage(action));
        System.out.println("\nSent messages:");
        System.out.println(message.printMessage());
        System.out.println("Total messages sent: "
                + message.returnTotalMessages());
    }
    private static int readPositiveNumber(String prompt) {
        while (true) {
            int number = readNumber(prompt);
            if (number > 0) {
                return number;
            }
            System.out.println("Please enter a number greater than zero.");
        }
    }
    private static int readNumber(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entered = INPUT.nextLine();
            try {
                return Integer.parseInt(entered.trim());
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
}
}

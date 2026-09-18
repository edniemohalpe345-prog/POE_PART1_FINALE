/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Login;
import java.util.Scanner;

/**
 *
 * @author Student
 */
public class Login {
  
    // Variables
     String firstName;
     String lastName;
     String username;
     String password;
     String cellphone;

    private String loginUsername;
    private String loginPassword;

    // Constructor
    public Login(String firstName, String lastName, String username,
                 String password, String cellphone) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
    }

    // Register user
    public String registerUser() {

        if (username == null || username.isEmpty()) {
            return "Username cannot be empty.";
        }

        if (password == null || password.isEmpty()) {
            return "Password cannot be empty.";
        }

        if (cellphone == null || cellphone.isEmpty()) {
            return "Cellphone cannot be empty.";
        }

        return "User has been registered successfully.";
    }

    // Set login details
    public void setLoginDetails(String loginUsername, String loginPassword) {
        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
    }

    // Check login status
    public String returnLoginStatus() {

        if (loginUsername != null
                && loginPassword != null
                && loginUsername.equals(username)
                && loginPassword.equals(password)) {

            return "Welcome " + firstName + " " + lastName
                    + ", it is great to see you again.";
        }

        return "Username or password is incorrect, please try again.";
    }

    // Main method
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("REGISTRATION");

        System.out.print("FirstName: ");
        String firstName = input.nextLine();

        System.out.print("LastName: ");
        String lastName = input.nextLine();

        System.out.print("userName: ");
        String username = input.nextLine();

        System.out.print("password: ");
        String password = input.nextLine();

        System.out.print("Cellphone: ");
        String cellphone = input.nextLine();

        Login user = new Login(
                firstName,
                lastName,
                username,
                password,
                cellphone
        );

        String registrationMessage = user.registerUser();

        System.out.println();
        System.out.println(registrationMessage);

        if (registrationMessage.equals(
                "User has been registered successfully.")) {

            System.out.println();
            System.out.println("LOGIN");

            System.out.print("userName: ");
            String loginUsername = input.nextLine();

            System.out.print("password: ");
            String loginPassword = input.nextLine();

            user.setLoginDetails(loginUsername, loginPassword);

            System.out.println(user.returnLoginStatus());
        }

        input.close();
    }
}


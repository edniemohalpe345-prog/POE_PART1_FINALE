/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poe.part3_poe_finale;

/**
 *
 * @author Student
 */

public class Login {

    String firstName;
    String lastName;
    String username;
    String password;
    String cellphone;

    private String loginUsername;
    private String loginPassword;

    private boolean registered;

    public Login(String firstName, String lastName,
                 String username, String password,
                 String cellphone) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;

        this.registered = false;
    }

    public boolean checkUserName() {
        return username != null
                && username.length() <= 5
                && username.contains("_");
    }

    public boolean checkPasswordComplexity() {

        if (password == null) {
            return false;
        }

        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    public boolean checkCellPhoneNumber() {
        return cellphone != null
                && cellphone.matches("^\\+27[0-9]{9}$");
    }

    public String registerUser() {

        if (!checkUserName()) {
            return "Username is not correctly formatted, please "
                    + "ensure that your username contains an underscore "
                    + "and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure "
                    + "that the password contains at least eight "
                    + "characters, a capital letter, a number "
                    + "and a special character.";
        }

        if (!checkCellPhoneNumber()) {
            return "Cell phone number is incorrectly formatted "
                    + "or does not contain the international code.";
        }

        registered = true;

        return "User has been registered successfully.";
    }

    public void setLoginDetails(String username, String password) {
        this.loginUsername = username;
        this.loginPassword = password;
    }

    public boolean loginUser() {
        return registered
                && username != null
                && password != null
                && loginUsername != null
                && loginPassword != null
                && username.equals(loginUsername)
                && password.equals(loginPassword);
    }

    public String returnLoginStatus() {

        if (loginUser()) {
            return "Welcome " + firstName + " " + lastName
                    + ", it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package poe.login.java;

/**
 *
 * @author Student
 */
public class Main {

    public static void main(String[] args) {
        
    }
        public class Login {

     String firstName;
     String lastName;
     String username;
     String password;
     String cellphone;

     String loginUsername;
     String loginPassword;

    private boolean registered;

    public Login(String firstName, String lastName, String username,
                 String password, String cellphone) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
        this.registered = false;
    }

    public boolean checkUserName() {

        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    public boolean checkPasswordComplexity() {

        return password != null
                && password.length() >= 8
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
            return "Username is not correctly formatted; "
                    + "please ensure that your username contains an underscore "
                    + "and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; "
                    + "please ensure that the password contains at least eight "
                    + "characters, a capital letter, a number, "
                    + "and a special character.";
        }

        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain "
                    + "an international code, please correct the number "
                    + "and try again.";
        }

        registered = true;

        return "User has been registered successfully.";
    }

    public void setLoginDetails(String loginUsername, String loginPassword) {

        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
    }

    public boolean loginUser() {

        return registered
                && username.equals(loginUsername)
                && password.equals(loginPassword);
    }

    public boolean loginUser(String loginUsername, String loginPassword) {

        setLoginDetails(loginUsername, loginPassword);
        return loginUser();
    }

    public String returnLoginStatus() {

        if (loginUser()) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}
    }


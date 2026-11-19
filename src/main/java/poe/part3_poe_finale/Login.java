package poe.part3_poe_finale;

import java.util.Scanner;

public class Login {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final String cellphone;
    private String loginUsername;
    private String loginPassword;
    private boolean registered;

    public Login(String firstName, String lastName, String username, String password, String cellphone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
    }

    public boolean checkUserName() { return username != null && username.contains("_") && username.length() <= 5; }
    public boolean checkPasswordComplexity() {
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*") && password.matches(".*[^a-zA-Z0-9].*");
    }
    public boolean checkCellPhoneNumber() { return cellphone != null && cellphone.matches("^\\+27[0-9]{9}$"); }

    public String registerUser() {
        if (!checkUserName()) return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        if (!checkPasswordComplexity()) return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        if (!checkCellPhoneNumber()) return "Cell phone number incorrectly formatted or does not contain an international code, please correct the number and try again.";
        registered = true;
        return "User has been registered successfully.";
    }
    public void setLoginDetails(String username, String password) { loginUsername = username; loginPassword = password; }
    public boolean loginUser() { return registered && username.equals(loginUsername) && password.equals(loginPassword); }
    public String returnLoginStatus() {
        return loginUser() ? "Welcome " + firstName + ", " + lastName + " it is great to see you again."
                : "Username or password incorrect, please try again.";
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("REGISTRATION");
        System.out.print("FirstName: "); String first = input.nextLine();
        System.out.print("LastName: "); String last = input.nextLine();
        System.out.print("userName: "); String user = input.nextLine();
        System.out.print("password: "); String pass = input.nextLine();
        System.out.print("Cellphone (+27...): "); String phone = input.nextLine();
        Login login = new Login(first, last, user, pass, phone);
        System.out.println(login.registerUser());
        System.out.print("userName: "); String loginUser = input.nextLine();
        System.out.print("password: "); String loginPass = input.nextLine();
        login.setLoginDetails(loginUser, loginPass);
        System.out.println(login.returnLoginStatus());
    }
}

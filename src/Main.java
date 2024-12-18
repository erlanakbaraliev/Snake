import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        String dbURL = "jdbc:mysql://127.0.0.1:3306/highscores";
        String dbUser = "root";
        String dbPwd = "MySQL3306";

        new GameFrame(dbURL, dbUser, dbPwd);
    }
}
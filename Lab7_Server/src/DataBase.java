import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Служебный класс
 */

public class DataBase {
    private DataBase() {}

    private static Connection connection = null;

    public static void createConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/Testbd", "postgres", "2319");
                System.out.println("-- Connection with database is established");
            } catch (Exception e) {

                System.out.println("-- Не удалось установить соединение с базой данных");
            }
        } else {
            System.out.println("-- Соединение с базой данных уже установлено");
        }
    }

    public static synchronized ResultSet makeRequest(String request) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        DataBase.connection = connection;
    }
}

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
                System.out.println("Connection with database is established");
            } catch (Exception e) {

                System.err.println("Could not establish database connection");
            }
        } else {
            System.out.println("Database connection already established");
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

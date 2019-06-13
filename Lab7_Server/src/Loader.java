import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Loader {
    private Loader() {}

    public static Vector<Person> loadFromDB() {
        ResultSet resultSet = DataBase.makeRequest("SELECT * FROM collection");
        Vector<Person> humans = new Vector<Person>();
        try {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String skill = resultSet.getString("skill");
                int coord = resultSet.getInt("coord");
                int height = resultSet.getInt("height");
                boolean beauty = resultSet.getBoolean("beauty");
                String time = resultSet.getString("timeID");
                String holder = resultSet.getString("holder");
                Person person = new Person(name, skill, coord, height, beauty);
                person.setHolder(holder);
                person.setTimeID(LocalDateTime.parse(time));

                humans.add(person);
            }
        } catch (Exception e) {
            System.err.println("Something go wrong while loading Database");
        }
        return humans;
    }

    public static Set<User> loadUsersFromDB() {
        ResultSet resultSet = DataBase.makeRequest("SELECT * FROM users");
        Set<User> users = new HashSet<User>();
        try {
            while (resultSet.next()) {
                try {
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");

                    User user = new User(email,login, password);

                    users.add(user);
                } catch (SQLException e) {

                }
            }
        } catch (Exception e) {

        }
        return users;
    }
}
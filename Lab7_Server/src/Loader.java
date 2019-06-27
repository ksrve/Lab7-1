import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class Loader {
    private Loader() {}

    public static Vector<Person> loadFromDB() {
        ResultSet resultSet = DataBase.makeRequest("SELECT * FROM collection");
        Vector<Person> humans = new Vector<Person>();
        try {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int coord = resultSet.getInt("coord");
                String time = resultSet.getString("timeid");
                String holder = resultSet.getString("holder");
                Person person = new Person(name,coord);
                person.setHolder(holder);
                person.setTimeID(LocalDateTime.parse(time));

                humans.add(person);
                System.out.println("Element: " + person);

            }
        } catch (Exception e) {
            System.err.println("Something go wrong while loading Database");
        }
        return humans;
    }

    public static Set<User> loadUsersFromDB() {
        ResultSet resultSet = DataBase.makeRequest("SELECT * FROM public.users");
        HashSet<User> users = new HashSet<User>();
        try {
            while (resultSet.next()) {
                try {
                    String login = resultSet.getString("login");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");

                    User user = new User(login,email, password);

                    users.add(user);


                } catch (SQLException e) {

                }
            }
        } catch (Exception e) {
        }
        for (User i : users) {
            System.out.println("User: " + i.getLogin());

        }
        return users;
    }
}
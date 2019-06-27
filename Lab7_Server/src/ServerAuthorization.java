import java.math.BigInteger;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class ServerAuthorization {


    /**
     * @param data
     * @param authorizationTokens
     * @param allUsers
     * @return
     */
    public static Response registerUser(String data, Map<String, LocalDateTime> authorizationTokens, Set<User> allUsers) {
        String[] dataArr = data.split(" ");
        String email        = dataArr[0];
        String login        = dataArr[1];
        String password     = dataArr[2];
        ResponseType type = ResponseType.PLANNED;
        Status status = Status.OK;
        for (User user : allUsers) {
            if (user.getLogin().equals(login)) { status = Status.USER_EXIST; break;}
            if (user.getEmail().equals(email)) { status = Status.USER_EXIST; break;}
        }
        RandomString rnd = new RandomString();
        String rndStr = rnd.nextString();
        while (authorizationTokens.containsKey(rndStr)) {
            rndStr = rnd.nextString();
        }

        if (status != Status.USER_EXIST) {
            status = SendEmail.send(email, rndStr);
        }

        if (status == Status.OK) {
            authorizationTokens.put(rndStr, LocalDateTime.now().plusMinutes(3).plusSeconds(30));
        }

        return new Response(status, type, rndStr);

    }

    /**
     * @param data
     * @param activeUsers
     * @param allUsers
     * @param saddr
     * @return
     */
    public static Response loginUser(String data, Set<User> activeUsers, Set<User> allUsers, SocketAddress saddr) {
        String[] dataArr = data.split(" ");
        String login =      dataArr[0];
        String password =   generateStrongPasswordHash(dataArr[1]);
        ResponseType type = ResponseType.PLANNED;
        Status status = Status.USER_NOT_FOUND;
        User thisUser = null;

        for (User user : allUsers) {
            if (user.getLogin().toLowerCase().equals(login.toLowerCase())) {
                status = Status.OK; thisUser = user;
                break;
            }
        }

        for (User user : activeUsers) {
            if (user.getLogin().toLowerCase().equals(login.toLowerCase())) {
                status = Status.USER_IN_SYSTEM; break;
            } else {
                System.out.println(user.getLogin());
            }
        }
        if (status == Status.OK && !password.equals(thisUser.getPassword())) {
            System.out.println(password + ":" + thisUser.getPassword());
            status = Status.WRONG_PASSWORD;
        }
        if (status == Status.OK && password.equals(thisUser.getPassword())) {
            RandomString gen = new RandomString();
            thisUser.setLastRequest(LocalDateTime.now().plusMinutes(2));
            String genToken = gen.nextString();
            thisUser.setToken(genToken);
            thisUser.setSaddr(saddr);
            activeUsers.add(thisUser);
            return new Response(status, type, genToken);
        }
        return new Response(status, type, "");
    }

    /**
     * @param data
     * @param authorizationTokens
     * @param allUsers
     * @return
     */
    public static Response completeRegistration(String data, Map<String, LocalDateTime> authorizationTokens, Set<User> allUsers) {
        String[] dataArr = data.split(" ");
        String email =      dataArr[0];
        String login =      dataArr[1];
        String password =   generateStrongPasswordHash(dataArr[2]);
        String token =      dataArr[3];
        ResponseType type = ResponseType.PLANNED;
        Status status = Status.OK;

        if (!authorizationTokens.containsKey(token)) { status = Status.WRONG_TOKEN; }
        if (authorizationTokens.get(token).compareTo(LocalDateTime.now()) < 0) { status = Status.EXPIRED_TOKEN; }

        if (status == Status.OK) {
            User user = new User(login, email, password);
            allUsers.add(user);
            String sql = "INSERT INTO users (login, email, password) VALUES (?, ?, ?)";
            try {
                PreparedStatement statement = DataBase.getConnection().prepareStatement(sql);
                statement.setString(1, login);
                statement.setString(2, email);
                statement.setString(3, password);
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                System.err.println("ERROR: an error occurred while executing the SQL query");
                e.getErrorCode();
                e.getMessage();
                e.printStackTrace();
            }
        }
        return new Response(status, type, "");
    }


    /**
     * @param password
     * @return
     */
    private static String generateStrongPasswordHash(final String password) {
        try {
            // getInstance() method is called with algorithm SHA-224
            MessageDigest md = MessageDigest.getInstance("SHA-224");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(password.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}

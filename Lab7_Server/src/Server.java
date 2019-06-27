import java.io.*;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    private Vector<Person> storage;
    // Tokens of authorization: key - token, value - time of deactivation
    private Map<String, LocalDateTime> authorizationTokens = new ConcurrentHashMap<>();
    // Clients, that are on server now
    private Set<User> activeUsers = new HashSet<User>();
    // All clients
    private Set<User> allUsers = new HashSet<User>();
    private Set<SocketAddress> allAddresses = new HashSet<>();

    private ClientListener listener;
    private ClientResponder responder;

    public static void main(String[] args) throws Exception {

        int input_port = -1;
        if (args.length != 1) {
            showUsage();
        }

        try {
            input_port = Integer.parseInt(args[0]);
        } catch (IllegalArgumentException e) {
            showUsage();
        }

        Date date = new Date();

        System.out.println(date);
        System.out.println("---------------------------------------------");
        System.out.println("Hi, dear User! This is 'help me, i wanna die' program! ");
        System.out.println("-Running UDP Server at " + InetAddress.getLocalHost());
        System.out.println("-UDP port " + input_port);
        System.out.println("-Server started");
        System.out.println("---------------------------------------------");

        Server server = new Server(input_port);
        server.loadCollection();
        server.loadUsers();
        server.listen();
    }

    public Server(int port) throws IOException {
        DataBase.createConnection();
        DatagramChannel channel = DatagramChannel.open().bind(new InetSocketAddress(port));
        responder = new ClientResponder(channel);
        listener = new ClientListener(channel);
    }

    public void loadCollection() {
        System.out.println("Initializing collection...");
        storage = Loader.loadFromDB();
    }

    public void loadUsers() {
        System.out.println("Initializing users...");
        allUsers = Loader.loadUsersFromDB();
    }

    private User getUserByToken(String token) {
        for (User client: activeUsers) {
            try {
                if (client.getToken().equals(token)) {
                    return client;
                } else {
                }
            } catch (NullPointerException e) {

            }
        }
        return null;
    }

    private void notifyAllActiveUsersExcept(User user) {
        Response response = new Response(Status.OK, ResponseType.INFO, String.format("User %s logged off from server", user.getLogin()));
        for (User active : activeUsers) {
            responder.sendResponse(active.getInfoSocket(), response);
        }
    }

    private void notifySpecificUser(User user) {
        Response response = new Response(Status.OK, ResponseType.INFO, "Timed out");
        responder.sendResponse(user.getInfoSocket(), response);
    }

    private void finish() {
        Response response = new Response(Status.OK, ResponseType.INFO, "Server is off, come back later");
        for (SocketAddress address : allAddresses)
            responder.sendResponse(address, response);
    }

    private void listen() throws Exception {
        CommandHandler handler;

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            for (User user : activeUsers) {
                if (user.getLastRequest().compareTo(LocalDateTime.now()) < 0) {
                    System.out.println("User " + user.getLogin() + " logged off");
                    notifySpecificUser(user);
                    activeUsers.remove(user);
                    notifyAllActiveUsersExcept(user);
                } else {
                }
            }
        };
        service.scheduleWithFixedDelay(task, 0, 5000, TimeUnit.MILLISECONDS);
        Command command;
        while (true) {
            while (!listener.isReady()) {
                Thread.sleep(10);
            }
            SocketAddress saddr = listener.getSaddr();
            command = listener.getCommand();
            Object data = listener.getDataFromLastCommand();
            handler = new CommandHandler();
            try {
                User user = getUserByToken(command.getToken());
                Response response = null;
                System.out.println("================");
                System.out.println("Client input: " + command.getCommand());
                if (!command.getCommand().equals("unlogin") && !command.getCommand().equals("infosocket")) {
                    response = handler.handleCommand(command, storage, user);
                    responder.sendResponse(user.getSaddr(), response);
                    System.out.println("Reply sent");
                } else if (command.getCommand().equals("infosocket")) {
                    user.setInfoSocket(saddr);
                    //System.out.println(user.getInfoSocket());
                    allAddresses.add(saddr);
                } else {
                    System.out.println("Removing: " + activeUsers.remove(user));
                    notifyAllActiveUsersExcept(user);
                }
            } catch (NullPointerException e) {
                String strData = (String) data;
                Response response = null;
                switch (command.getCommand().toLowerCase().trim()) {
                    case "register":
                        response = ServerAuthorization.registerUser(strData, authorizationTokens, allUsers);
                        break;
                    case "login":
                        response = ServerAuthorization.loginUser(strData, activeUsers, allUsers, saddr);
                        break;
                    case "acceptregister":
                        response = ServerAuthorization.completeRegistration(strData, authorizationTokens, allUsers);
                        break;
                    case "connecting":
                        response = new Response(Status.OK, ResponseType.CONNECTION, "");
                        break;
                }
                responder.sendResponse(saddr, response);
                System.out.println("Reply sent");
            }
        }
    }

    public static void showUsage() {
        System.out.println("To run server properly follow this syntax");
        System.out.println("java -jar Server.jar <port>");
        System.exit(1);
    }


}
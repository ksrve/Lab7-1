import java.io.Console;
import java.net.*;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

public class Client {
    private static InetAddress clientAddr;
    private static int port;

    private static Scanner scanner = new Scanner(System.in);
    private String token;
    private MessageSender sender;
    private MessageReceiver receiver;
    private PlannedReceiver plannedReceiver;
    private DatagramSocket socket;
    private DatagramSocket plannedSocket;
    private static String input_command;
    private static String input;
    private static String data;
    public static String input_login;
    public static String input_email;
    public static String input_password;
    Console console = System.console();
    public static char[] passwordArray;
    public static String email;
    public static String login;
    public static String password;


    private Client(InetAddress serverAddress, int port) {
        try {
            socket = new DatagramSocket();
            plannedSocket = new DatagramSocket();
        } catch (SocketException e) {

        }
        this.sender = new MessageSender(serverAddress, port, plannedSocket);
        this.receiver = new MessageReceiver(socket);
        this.plannedReceiver = new PlannedReceiver(plannedSocket, sender);
    }

    public static void showUsage() {
        System.out.println("To run client properly you need to follow this syntax");
        System.out.println("java Client <host> <port>");
        System.exit(1);
    }

    public void connectToServer() {
        sender.sendCommand("connecting", "");
        plannedReceiver.listenServer();
    }

    public boolean authorize() {
        System.out.println();
        System.out.println("Enter 'register' to authorize on server");
        System.out.println("---------------------------------------------");
        System.out.println("Enter 'authorize' to log in on server");
        System.out.println("---------------------------------------------");
        System.out.println("Enter 'exit' to exit");
        System.out.println("---------------------------------------------");
        System.out.print(">> ");
        input_command = scanner.nextLine();
        String command = input_command.split(" ")[0];
        System.out.println(command);
        ///////////////////////////////////////////////
        /*
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }
        */
        String token;
        switch (command) {
            case "register":
                System.out.print("Please enter your e-mail: ");
                //email = console.readLine();
                input_email = scanner.nextLine();
                //if (email != null && email.split(" ")[0] == null)
                if (input_email != null) {
                }else{System.err.println("You made a mistake!");
                    return false;}
                //=====================================================
                System.out.print("Please enter your login: ");
                //login = console.readLine();
                input_login = scanner.nextLine();
                //if (login != null && login.split(" ")[0] == null)
                if (input_login != null) {
                }else{System.err.println("You made a mistake!");
                    return false;}
                //=====================================================
                System.out.print("Please enter your password: ");
                //passwordArray = console.readPassword();
                //password = new String(passwordArray);
                input_password = scanner.nextLine();
                //if (password != null && password.split(" ")[0] == null){
                if(input_password != null ){
                }else{System.err.println("You made a mistake!");
                    return false;}
                data = input_email + " " + input_login + " " + input_password;
                token = register(data);
                if (token != null) {
                    completeRegistration(data + " " + token);
                    return false;
                } else {
                    return false;
                }
            case "authorize":
                //=====================================================
                System.out.print("Please enter your login: ");
                //login = console.readLine();
                input_login = scanner.nextLine();
                //if (login != null && login.split(" ")[0] == null)
                if (input_login != null) {
                }else{System.err.println("You made a mistake!");
                    return false;}
                //=====================================================
                System.out.print("Please enter your password: ");
                //passwordArray = console.readPassword();
                //password = new String(passwordArray);
                input_password = scanner.nextLine();
                //if (password != null && password.split(" ")[0] == null){
                if(input_password != null){
                }else{System.err.println("You made a mistake!");
                    return false;}
                //======================================================
                data = input_login + " " + input_password;

                token = login(data);
                if (token != null) {
                    this.token = token;
                    return true;
                } else {
                    return false;
                }
            case "exit":
                System.out.println("GoodBye!!! Hope I'll see you later, leather bastard");
                System.exit(0);
            default:
                System.err.println("Вы ввели неверную команду, попробуйте еще раз");
                System.out.println("=====");
                return false;
        }
    }

    private String register(String data) {
        String[] dataArr = data.split(" ");
        if (dataArr.length != 3) {
            System.err.println("Вы использовали неверный синтаксис или пробелы, попробуйте снова");
            return null;
        }
        sender.sendCommand("register", data);
        Response response = plannedReceiver.listenServer();
        if (response == null) {
            startAgain();
        }
        System.out.println(response.getStatus());
        if (response.getStatus() == Status.OK) {
            String token = Response.getStringFromResponse(response.getResponse());
            System.out.println("Вам на почту пришел 32-х значный токен, скопируйте его в строку ниже, чтобы подтвердить регистрацию\nБудьте внимательны, у вас всего 3 попытки");
            int attemptCounter = 0;
            while (attemptCounter < 3) {
                System.out.print("Токен > ");
                if (scanner.nextLine().trim().equals(token)) {
                    return token;
                }
                attemptCounter++;
            }
        } else if (response.getStatus() == Status.USER_EXIST) {
            System.err.println("Пользователь с таким email или login уже существует\nПопробуйте еще раз\n");
        } else if (response.getStatus() == Status.NO_MAIL) {
            System.err.println("Некорректная почта, письмо для завершения регистрации не было доставлено\nПопробуйте еще раз\n");
        }
        return null;
    }

    private boolean completeRegistration(String data) {
        sender.sendCommand("AcceptRegister", data);
        Response response = plannedReceiver.listenServer();
        if (response == null) {
            startAgain();
        }
        if (response.getStatus() == Status.OK) {
            System.out.println("Вы зарегистрированы на сервере");
            return true;
        } else if (response.getStatus() == Status.WRONG_TOKEN) {
            System.err.println("Неверный токен\n\n");
            return false;
        } else if (response.getStatus() == Status.EXPIRED_TOKEN) {
            System.err.println("Токен просрочен, поторопитесь в следующий раз, он действует всего 2.5 минуты\n\n");
            return false;
        }
        return false;
    }

    private String login(String data) {
        String[] dataArr = data.split(" ");
        if (dataArr.length != 2) {
            System.err.println("Вы использовали неверный синтаксис или пробелы, попробуйте снова");
            return null;
        }
        // Получаем и обрабатываем запрос к серверу
        sender.sendCommand("login", data);
        Response response = plannedReceiver.listenServer();
        if (response == null) {
            startAgain();
        }
        if (response.getStatus() == Status.OK) {
            return Response.getStringFromResponse(response.getResponse());
        } else if (response.getStatus() == Status.USER_IN_SYSTEM) {
            System.err.println("Пользователь с таким login уже в системе\n\n");
        } else if (response.getStatus() == Status.WRONG_PASSWORD) {
            System.err.println("Пароль неверен (Как и все, кто не верит в Аллаха)\n\n");
        } else if (response.getStatus() == Status.USER_NOT_FOUND) {
            System.err.println("Пользователь с таким login не найден\n\n");
        }

        return null;
    }

    private void start() {
        sender.sendCommandFromSpecificSocket(socket, token, "infosocket", "");
        System.out.println("Если не знаете команд программы, введите help");
        System.out.print("Введите команду > ");
        String lastCommand = "";
        String addStr = "";
        boolean commandEnd = true;
        boolean correctCommand = false;
        int nestingJSON = 0;

        try {
            input = scanner.nextLine();
            while (!input.equals("exit")) {
                if (!input.equals("")) {
                    String command = input.split(" ")[0].toLowerCase();
                    if (nestingJSON < 0) {
                        nestingJSON = 0;
                        lastCommand = "";
                        addStr = "";
                        commandEnd = true;
                    }

                    if (!commandEnd && (lastCommand.equals("add") || lastCommand.equals("add_if_min"))) {

                        nestingJSON += charCounter(input, '{');
                        nestingJSON -= charCounter(input, '}');
                        correctCommand = true;
                        addStr += input;
                        if (nestingJSON == 0) {
                            commandEnd = true;

                        }

                    } else if (command.equals("add_if_min") && commandEnd) {

                        lastCommand = "add_if_min";
                        commandEnd = false;
                        correctCommand = true;
                        addStr = input.substring(10).trim();
                        nestingJSON += charCounter(addStr, '{');
                        nestingJSON -= charCounter(addStr, '}');
                        if (nestingJSON == 0) {
                            commandEnd = true;
                        }

                    } else if (command.equals("add") && commandEnd) {

                        lastCommand = "add";
                        commandEnd = false;
                        correctCommand = true;
                        addStr = input.substring(3).trim();
                        nestingJSON += charCounter(addStr, '{');
                        nestingJSON -= charCounter(addStr, '}');
                        if (nestingJSON == 0) {
                            commandEnd = true;
                        }

                    } else if (command.equals("show") && commandEnd) {
                        lastCommand = "show";
                        correctCommand = true;
                        sender.sendCommand(token, "show", "");
                    } else if (command.equals("save") && commandEnd) {
                        lastCommand = "save";
                        correctCommand = true;
                        sender.sendCommand(token, "save", "");
                    } else if (command.equals("import") && commandEnd) {
                        lastCommand = "import";
                        correctCommand = true;
                        if (!input.substring(6).trim().startsWith("/dev/random") && !input.substring(6).trim().startsWith("/dev/urandom")) {
                            Vector<Person> humans = Toodles._import(input.substring(6).trim());
                            if (humans == null) {
                                System.err.println("Произошла ошибка при чтении файла");
                                System.out.print("> ");
                                input = scanner.nextLine();
                                continue;
                            } else {
                                sender.sendCommand(token, "import", humans);
                            }
                        } else {
                            System.err.println("Плохой файл, а-та-та");
                            System.out.print("> ");
                            input = scanner.nextLine();
                            continue;
                        }
                    } else if (command.equals("info") && commandEnd) {
                        lastCommand = "info";
                        correctCommand = true;
                        sender.sendCommand(token, "info", "");
                    } else if (command.equals("remove") && commandEnd) {
                        lastCommand = "remove";
                        correctCommand = true;
                        sender.sendCommand(token, "remove", input.substring(6).trim());
                    } else if (command.equals("help") && commandEnd) {
                        lastCommand = "help";
                        correctCommand = true;
                        sender.sendCommand(token, "help", "");
                    } else {
                        correctCommand = false;
                        commandEnd = true;
                    }

                    if (lastCommand.equals("add") && commandEnd && correctCommand) {
                        Person human = Toodles.fromJson(addStr);
                        if (human != null) {
                            sender.sendCommand(token, "add", Toodles.fromJson(addStr));
                            addStr = "";
                            correctCommand = true;
                        } else {
                            System.err.println("Произошла ошибка при чтении JSON строки");
                            correctCommand = false;
                        }
                    } else if (lastCommand.equals("add_if_min") && commandEnd && correctCommand) {
                        Person human = Toodles.fromJson(addStr);
                        if (human != null) {
                            sender.sendCommand(token, "add_if_min", Toodles.fromJson(addStr));
                            addStr = "";
                            correctCommand = true;
                        } else {
                            System.err.println("Произошла ошибка при чтении JSON строки");
                            correctCommand = false;
                        }

                    }
                } else {
                    if (commandEnd)
                        correctCommand = false;
                }

                if (commandEnd && correctCommand) {
                    Response response = plannedReceiver.listenServer();
                    if (response == null) {
                        System.err.println("Произошло отключение от сервера, между выполнениями команд");
                        startAgain();
                        return;
                    } else {
                    }
                    if (response.getStatus() == Status.OK) {
                        String serverResponse = Response.getStringFromResponse(response.getResponse());
                        System.out.println(serverResponse);
                    }
                    System.out.print("> ");
                } else if (commandEnd) {
                    if (!input.trim().equals("")) {
                        System.err.println("Неизвестная команда");
                    }
                    System.out.print("> ");
                }
                input = scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            startAgain();
        }

        System.exit(0);
    }

    public void finish() {
        sender.sendCommand(token,"unlogin", "");
    }

    /**
     * <p>Считает количество символов в строке</p>
     *
     * @param in - Исходная строка
     * @param c - Символ, который мы ищем
     * @return Количество символов в строке
     */
    private int charCounter(String in, char c) {
        int count = 0;
        int k = 0; // считает количество "
        for (char current: in.toCharArray()) {
            if (current == c && k % 2 == 0) {
                count++;
            }
            if (current == '"') {
                k++;
            }
        }
        return count;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            showUsage();
        }
        Date date = new Date();
        System.out.println(date);

        try {
            clientAddr = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
            InetAddress addr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            Client client = new Client(addr, port);

            System.out.println("---------------------------------------------");
            System.out.println("Hi, dear User! This is Server-Client program!");
            System.out.println("-Running UDP Client at " + InetAddress.getLocalHost());
            System.out.println("-UDP client settings --");
            System.out.println("-UDP connection to " + addr + " host");
            System.out.println("-UDP port " + port);
            System.out.println("-Client started");
            System.out.println("---------------------------------------------");
            /*
            0 - подключение к серверу
            1 - авторизация
            2 - команды
            3 - конец программы (отправляем на сервер то, что мы отключаемся)
             */
            client.connectToServer();
            client.setToken(null);
            boolean authorized = client.authorize();
            while (!authorized && client.getToken() == null) {
                authorized = client.authorize();
            }
            Thread f = new Thread(() -> {
                client.finish();
                System.out.println("Пока-пока");
            });
            Runtime.getRuntime().addShutdownHook(f);
            client.start();

        } catch (Exception e) {
            e.printStackTrace();
            showUsage();
        }
    }

    public static void startAgain() {
        Client client = new Client(clientAddr, port);
        client.setToken(null);
        client.connectToServer();
        boolean authorized = client.authorize();
        while (!authorized && client.getToken() == null) {
            authorized = client.authorize();
        }
        Thread f = new Thread(() -> {
            client.finish();
        });
        Runtime.getRuntime().addShutdownHook(f);
        client.start();
    }



    public Scanner getScanner() {
        return scanner;
    }
}
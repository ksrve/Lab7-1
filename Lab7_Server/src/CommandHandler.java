import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class CommandHandler extends Thread {

    public User lastUser;

    public CommandHandler() {

    }

    @Override
    public void run() {
    }

    private boolean isTokenValid(LocalDateTime expirationTime) {
        return expirationTime.compareTo(LocalDateTime.now()) > 0;
    }

    /**
     * <p>Ищет исполняемую команду и исполняет её</p>
     * @param com - команда
     * @param storage - ссылка на коллекцию с объектами
     */
    //public DatagramPacket handleCommand(String command, Vector<Human> storage, String data) {
    public Response handleCommand(Command com, Vector<Person> storage, User user) {
        synchronized (CommandHandler.class) {
            String command = com.getCommand();
            byte[] data = com.getData();
            String token = com.getToken();
            if (isTokenValid(user.getLastRequest())) {
                user.setLastRequest(LocalDateTime.now().plusMinutes(2));
                String buffer = null;
                switch (command.toLowerCase()) {
                    case "info":
                        buffer = info(storage);
                        break;
                    case "show":
                        buffer = show(storage);
                        break;
                    case "add_if_min":
                        buffer = add_if_min(storage, getPersonFromCommand(data), user);
                        break;
                    case "add":
                        buffer = add(storage, getPersonFromCommand(data), user);
                        break;
                    case "import":
                        buffer = _import(storage, getPersonVectorFromCommand(data), user);
                        break;
                    case "save":
                        buffer = "Collection saved";
                        break;
                    case "remove":
                        buffer = remove(storage, getPersonFromCommand(data), user);
                        break;
                    case "help":
                        buffer = help();
                        break;
                    default:
                        buffer = "Unknown command!Try again\nTo see all commands try to insert'help'!";

                }

                return new Response(Status.OK, ResponseType.PLANNED, buffer);
            } else {
                return new Response(Status.EXPIRED_TOKEN, ResponseType.PLANNED, "");
            }
        }
    }

    private Person getPersonFromCommand(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Person) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something wrong while getting 'Person' object");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Vector <Person> getPersonVectorFromCommand(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Vector <Person>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something wrong while getting 'Person' object");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method that adds items to the collection
     *
     * @return
     */
    public static String add(Vector <Person> storage, Person personal, User user) {

        boolean exist = false;


        for (Person current: storage) {
            if (current.equals(personal)) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            try {
                personal.setHolder(user.getLogin());
                storage.add(personal);
                sort(storage);
                String sql;
                sql = "INSERT INTO collection ( name, coord, timeid, holder) VALUES (?, ?, ?, ? )";
                PreparedStatement statement = DataBase.getConnection().prepareStatement(sql);
                statement.setString(1, personal.getName());
                statement.setInt(2, personal.getCoord());
                statement.setString(3, personal.getTimeID().toString());
                statement.setString(4, personal.getHolder());
                statement.execute();
                statement.close();
            } catch (NullPointerException e) {
            } catch (SQLException ex) {
                return "Database mistake";
            }
            sort(storage);
            return "Element was successfully added";
        }
        else {
            sort(storage);
            return "Collection contains such element";
        }

    }

    public String _import(Vector<Person> storage, Vector<Person> importing, User user) {
        for (Person person: importing) {
            add(storage, person, user);
        }

        return "Import completed";
    }

    /**
     * @param storage
     * @return
     */
    public String show(Vector <Person> storage) {
        String out = "";
        if (storage.size() > 0) {
            for (int i = 0; i < storage.size(); i++)
                out += storage.get(i).toString() + "\n";
        }
        if (storage.size() < 0) {
            return ("Collection is empty!!!");
        }
        return out;
    }

    /**
     * @param storage
     * @return
     */
    public static String info(Vector <Person> storage) {
        return("Info about collection: \n" +
                "\nType of collection: " + storage.getClass() +
                "\nAmount of elements: " + storage.size()+
                "\n----------------------------\n");
    }

    //========================================================================
    /**
     * method showing command description
     * @return
     */
    public static String help() {
        return("List of available commands: " +
                "\n show - show you the current content of the collection " +
                "\n exit - end the program " +
                "\n save - save current content of the collection " +
                "\n info - show you info about collection " +
                "\n add { name: ???; coord: ???; } - add the element in collection " +
                "\n remove { name: ???; coord: ???; } - remove the collection's element with given name " +
                "\n add_if_min { name: ???; coord: ???; } - add the element in collection if it's value is less than the smallest element of this collection " +
                "\n----------------------------");
    }
    //========================================================================
    /**
     * @param storage
     * @param personal
     * @param user
     * @return
     */
    public static String remove (Vector <Person> storage, Person personal, User user){

        for (Person person: storage) {
            if (person.getName().toLowerCase().equals(personal.getName().toLowerCase())) {
                if (user.getLogin().equals(person.getHolder())) {
                    String sql = "DELETE FROM collection WHERE name = ?";
                    try {
                        PreparedStatement statement = DataBase.getConnection().prepareStatement(sql);
                        statement.setString(1, person.getName());
                        statement.execute();
                        statement.close();
                    } catch (SQLException e) {
                        return "Error while removing. SQLException";
                    }
                    storage.remove(person);
                    return ("Person was removed with name  \"" + person.getName() + "\"");
                } else {
                    return "You can't delete this element. You are not a holder";
                }
            }
        }

        return"There is no such element";

    }
    //========================================================================
    /**
     * @param storage
     */
    private static void sort (Vector <Person> storage) {
        storage.stream().sorted();
    }
    //========================================================================
    /**
     * @param storage
     * @param personal
     * @param user
     * @return
     */
    protected  String add_if_min (Vector <Person> storage, Person personal, User user){

        if (storage.size() > 0) {
            Person min = storage.stream().min(Comparator.comparing(Person::getName)).get();
            if (personal.compareTo(min) < 0) {
                return add(storage,personal, user);
            } else {
                return "This element is not minimal";
            }
        } else {
            return add(storage,personal, user);
        }
    }

}
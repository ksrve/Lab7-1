

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Toodles {
    public static String name;
    public static boolean[] ifFull = new boolean[5];
    public static String skill;
    public static String coord;
    public static String DateOfBirth;
    public static String height;
    static boolean g;
    static boolean  isKateSleeping = true;

    public static Vector<Person> _import(String path) {
        File file = new File(path);
        Vector<Person> collection;
        collection = parseStrings(readStrings(file));
        return collection;
    }


    public static Person fromJson(String n) {

        try {

            String s = n;
            s = s.substring(s.indexOf("{") + 1, s.indexOf(" }"));
            String name = s.substring(s.indexOf("name: ") + 6, s.indexOf(";"));
            s = s.substring(s.indexOf("; ") + 2);
            String skill = s.substring(s.indexOf("skill: ") + 7, s.indexOf("; "));
            s = s.substring(s.indexOf("; ") + 2);
            int coord = Integer.parseInt(s.substring(s.indexOf("coord: ") + 13, s.indexOf("; ")));
            s = s.substring(s.indexOf("; ") + 2);
            int height = Integer.parseInt(s.substring(s.indexOf("height: ") + 8, s.indexOf(";")));
            s = s.substring(s.indexOf(";") + 2);
            boolean beauty = Boolean.parseBoolean(s.substring(s.indexOf("beauty: ") + 15, s.indexOf("; ")));
            s = s.substring(s.indexOf("; ") + 1);

            for (int i = 0; i < 5; i++) {
                ifFull[i] = true;
            }
            return new Person(name, skill, coord, height, beauty);

        } catch (StringIndexOutOfBoundsException ex) {
            System.err.println("Error while insert");
            return null;
        } catch (NumberFormatException ex) {
            System.err.println("Error while loading collection from file");
            return null;
        }
    }


    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }
    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    /**<p>Метод, читающий файл с элементами коллекции</p>
     *
     * @param file - файла загрузки
     * @return - возвращает массив строк, содержащих параметры элементов в формате csv
     */
    public static ArrayList<String> readStrings(File file) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            if (file.exists()) {
                ArrayList<String> str = new ArrayList<String>();
                while ((line = reader.readLine()) != null) {
                    str.add(line);
                }
                reader.close();

                System.out.println("File was loaded!");
                return str;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File wasn't found or not available to read/write\n");

        } catch (IOException e) {
            System.err.println("Something go wrong");
        }
        return null;
    }

    /**
     * <p>Метод, создающий объекты из строк формата csv</p>
     *
     * @param string - массив строк
     * @return - коллекция объектов
     */
    public static Vector<Person> parseStrings(ArrayList<String> string) {

        try {
            ArrayList<Person> collect = new ArrayList<Person>();
            List<Person> collection= new CopyOnWriteArrayList<Person>(collect);
            if (string != null) {
                for (int j = 0; j < string.size(); j++) {
                    String currentString = string.get(j);
                    ArrayDeque<Integer> punctuation = new ArrayDeque<Integer>();
                    boolean[] ifFull = new boolean[5];
                    Arrays.fill(ifFull, Boolean.FALSE);

                    for (int i = 0; i < currentString.length(); i++) {
                        if (currentString.charAt(i) == ',') {
                            punctuation.add(i);
                        }
                    }
                    String[] parameters = new String[punctuation.size() + 1];
                    int number = punctuation.size();
                    for (int i = 0; i < number + 1; i++) {
                        switch (i) {
                            case (0):
                                parameters[i] = currentString.substring(0, punctuation.getFirst().intValue());
                                break;
                            case (4):
                                if (currentString.length() - 1 > punctuation.getFirst())
                                    parameters[i] = currentString.substring(punctuation.pollFirst().intValue() + 1, currentString.length());
                                break;
                            default:
                                parameters[i] = currentString.substring(punctuation.pollFirst().intValue() + 1, punctuation.getFirst().intValue());
                                break;
                        }
                        if (parameters[i] != null)
                            if (!parameters[i].equals("")) {
                                ifFull[i] = true;
                            }
                    }
                    try {
                        String name = parameters[0];
                        String skill = parameters[1];
                        int coord = Integer.parseInt(parameters[2]);
                        int height = Integer.parseInt(parameters[3]);
                        boolean beauty = Boolean.parseBoolean(parameters[4]);

                        collection.add(Integer.valueOf(j), new Person(name,skill,coord,height,beauty));

                    } catch (NumberFormatException ex) {
                        System.err.println("Incorrect paramaters in csv file");
                    }
                }
                return (Vector)collection;

            } else {
                return null;
            }

        } catch (Exception e) {
            System.out.println("Incorrect paramaters in csv file");
            return null;
        }
    }

}


public class PersonFactory {

    public static Person personFactory(boolean[] ifFull, String name,
                                       int coord)
    {
        Person person = new Person();
        if(ifFull[0])
            person.setName(name);
        if(ifFull[1])
            person.setCoord(coord);
        return person;
    }


}


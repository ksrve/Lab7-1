
public class PersonFactory {

    public static Person personFactory(boolean[] ifFull, String name,
                                       String skill, int coord,
                                       int height, boolean beauty)
    {
        Person person = new Person();
        if(ifFull[0])
            person.setName(name);
        if(ifFull[1])
            person.setSkill(skill);
        if(ifFull[2])
            person.setCoord(coord);
        if(ifFull[3])
            person.setHeight(height);
        if(ifFull[4])
            person.setBeauty(beauty);
        return person;
    }


}


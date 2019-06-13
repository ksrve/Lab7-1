import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * Just a person
 */
public class Person implements Comparable <Person>, Serializable {
	/**
	 * Object identifiers
	 */
	private String name;
	private String skill;
	private int coord;
	private int height;
	private boolean beauty;
	private LocalDateTime time;
	private String holder;
	/**
	 * Constructor that creates an object with the specified parameters
	 */
	public Person(String name, String skill, int coord, int height, boolean beauty){
		this.name = name;
		this.skill = skill;
		this.coord = coord;
		this.height = height;
		this.beauty = beauty;
		this.time = LocalDateTime.now();
	}
	public Person(){
		this.name = name;
		this.skill = skill;
		this.coord = coord;
		this.beauty = beauty;
		this.height = height;
		this.time = LocalDateTime.now();
	}
	/**
	 * method by which a person makes a sound
	 */

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
	}

	public int getCoord() {
		return coord;
	}
	public void setCoord(int coord) {
		this.coord = coord;
	}

	public boolean getBeauty() {
		return beauty;
	}
	public void setBeauty(boolean beauty) {
		this.beauty = beauty;
	}

	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public LocalDateTime getTimeID() { return time; }
	public void setTimeID(LocalDateTime timeID) { this.time = timeID; }

	public String getHolder() { return holder; }

	public void setHolder(String holder) { this.holder = holder; }


	@Override
	public String toString() {
		return "{ owner:" + this.holder
				+ "; name: " + this.name
				+ "; skill: " + this.skill
				+ "; coord: " + this.coord
				+ "; height: " + this.height
				+ "; beauty: " + this.beauty
				+ "; timeOfCreation: "  + (time.getDayOfMonth()
				+ "/" + time.getMonth()
				+ "/" + time.getYear()
				+ " " + time.getHour()
				+ ":" + time.getMinute()
				+ ":" + time.getSecond())
				+ "; }";
	}

	public static int compareTo(Person o1, Person o2) {
		return o1.getName().compareTo(o2.getName());
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = result + coord + height ;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		if (!name.equals(person.name)) return false;
		if (!skill.equals(person.skill)) return false;
		if (coord != person.coord) return false;
		if (beauty!= person.beauty) return false;
		if (height!= person.height) return false;
		if (!time.equals(person.time)) return false;
		return true;
	}


	@Override
	public int compareTo(Person o) {
		return name.compareTo(o.name);
	}
}
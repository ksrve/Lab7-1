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
	private int coord;
	private LocalDateTime timeid;
	private String holder;
	/**
	 * Constructor that creates an object with the specified parameters
	 */
	public Person(String name, int coord){
		this.name = name;
		this.coord = coord;
		this.timeid = LocalDateTime.now();
	}
	public Person(){
		this.name = name;
		this.coord = coord;
		this.timeid = LocalDateTime.now();
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


	public int getCoord() {
		return coord;
	}
	public void setCoord(int coord) {
		this.coord = coord;
	}

	public LocalDateTime getTimeID() { return timeid; }
	public void setTimeID(LocalDateTime timeID) { this.timeid = timeID; }

	public String getHolder() { return holder; }

	public void setHolder(String holder) { this.holder = holder; }


	@Override
	public String toString() {
		return "{ owner: " + this.holder
				+ "; name: " + this.name
				+ "; coord: " + this.coord
				+ "; timeID: "  + (timeid.getDayOfMonth()
				+ "/" + timeid.getMonth()
				+ "/" + timeid.getYear()
				+ " " + timeid.getHour()
				+ ":" + timeid.getMinute()
				+ ":" + timeid.getSecond())
				+ "; }";
	}

	public static int compareTo(Person o1, Person o2) {
		return o1.getName().compareTo(o2.getName());
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = result + coord;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		if (!name.equals(person.name)) return false;
		if (coord != person.coord) return false;
		if (!timeid.equals(person.timeid)) return false;
		return true;
	}


	@Override
	public int compareTo(Person o) {
		return name.compareTo(o.name);
	}
}
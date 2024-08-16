
public abstract class Person {
	
	private int id, age;
	private String name, city;
	
	// Constructor
	protected Person(int id, String name, int age, String city) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.city = city;
	}
	
	public int getAge() {		// in order to compare naturally between voters, ListCand., MayorCand.
		return this.age;
	}
	
	public String getCity() {	// in order to enforce legal votes
		return this.city;
	}
	
	public int getId() {		// in order to find a person by ID
		return this.id;			
	}
    
    @Override
    public String toString() {
    	return "Name: " + name + "\t" + "ID: " + id + "\n" + "Age: " + age + "\t" + "City: " + city;
    }

}

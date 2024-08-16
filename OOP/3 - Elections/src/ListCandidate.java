
class ListCandidate extends Voter {

	private static final int MIN_YEARS_IN_CITY = 3;
	private int years_in_city;
	private MunicipalityList ml;
	
	// Constructor
	public ListCandidate(int id, String name, int age, String city, int years_in_city, MunicipalityList ml) {
		super(id, name, age, city);
		if (years_in_city < MIN_YEARS_IN_CITY)
			throw new IllegalArgumentException(
					"A list candidate cannot be a citizen for less than " + MIN_YEARS_IN_CITY + " years");
		this.years_in_city = years_in_city;
		this.ml = ml;
	}
	
	public int getYears() {
		return this.years_in_city;
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "Years in city: " + years_in_city;
	}

}

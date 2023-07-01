package user;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;

import recipe.Recipe;

public class User {

	private String username;
	private String name;
	private String surname;
	private String password;
	private Gender gender;
	private Date birthdate;
	private ArrayList<Recipe> recipes;
	
	public User(String username, String name, String surname, String password,Date birthdate) {
		this(username, name, surname, password, null, birthdate);
	}
	
	public User(String username, String name, String surname, String password, Gender gender) {
		this(username, name, surname, password, gender, null);
	}
	
	public User(String name, String surname, String password, Gender gender, Date birthdate) {
		this(name + "_" + surname, name, surname, password, gender, birthdate);
	}
	
	public User(String username, String name, String surname,
			    String password, Gender gender, Date birthdate) {
		if (name == null || surname == null || password == null || gender == null || birthdate == null) {
			throw new NullPointerException("Credentials required!");
		}
		this.username = username;
		this.name = name;
		this.gender = gender;
		this.surname = surname;
		this.password = password;
		this.birthdate = birthdate;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public Gender getGender() {
		return gender;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public ArrayList<Recipe> getRecipes() {
		return recipes;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void addRecipe(Recipe newRecipe) {
		if (!exists(newRecipe)) {
			recipes.add(newRecipe);
		}
	}

	private boolean exists(Recipe newRecipe) {
		for (Recipe recipe : recipes) {
			if (recipe.getId() == newRecipe.getId()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(gender, name, surname, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return gender == other.gender && Objects.equals(name, other.name) && Objects.equals(surname, other.surname)
				&& Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		return "nine.Exercise.User [username=" + username + ", name=" + name + ", surname=" + surname + ", password=" + password
				+ ", gender=" + gender + ", birthdate=" + birthdate + ", recipes=" + recipes + "]";
	}
	
}
